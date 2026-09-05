/*
 * Copyright 2026 Xavier Pedraza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.wasabithumb.jdaybreak;

import io.github.wasabithumb.jdaybreak.except.ThemeException;
import io.github.wasabithumb.jdaybreak.except.ThemeSelectException;
import io.github.wasabithumb.jdaybreak.prop.Properties;
import io.github.wasabithumb.jdaybreak.prop.system.PropertySystem;
import io.github.wasabithumb.jdaybreak.theme.Theme;
import io.github.wasabithumb.jdaybreak.theme.ThemeSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@NullMarked
@ApiStatus.Internal
final class JDaybreakImpl implements JDaybreak {

    static JDaybreakImpl DEFAULT = new JDaybreakImpl(ThemeSet.standard(), DEFAULT_INTERVAL);

    //

    private final PropertySystem system;
    private final ThemeSet themes;
    private final Daemon daemon;

    JDaybreakImpl(
            ThemeSet themes,
            long interval
    ) {
        this.system = PropertySystem.host();
        this.themes = themes;
        this.daemon = new Daemon(this, interval);
    }

    //

    @Override
    public @Unmodifiable ThemeSet themes() {
        return this.themes;
    }

    @Override
    public Theme currentTheme() throws ThemeException {
        Theme selected = null;
        Properties properties = this.system.query();
        for (Theme candidate : this.themes) {
            if (!candidate.matches(properties)) continue;
            if (selected != null && selected.priority() >= candidate.priority()) continue;
            selected = candidate;
        }
        if (selected != null) return selected;
        throw new ThemeSelectException(
                "none of " +
                this.themes.size() + " theme(s) match system properties: " +
                properties
        );
    }

    @Override
    public boolean registerListener(Consumer<Theme> callback, boolean fire) {
        Theme current = this.currentTheme();
        if (this.daemon.submit(callback, current)) {
            if (fire) callback.accept(current);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregisterListener(Consumer<Theme> callback) {
        return this.daemon.remove(callback);
    }

    //

    private static final class Daemon {

        private final JDaybreakImpl parent;
        private final long interval;
        private final ReadWriteLock lock;
        private final Map<Consumer<Theme>, Listener> map;
        private @Nullable Worker worker;

        Daemon(JDaybreakImpl parent, long interval) {
            this.parent = parent;
            this.interval = interval;
            this.lock = new ReentrantReadWriteLock();
            this.map = new IdentityHashMap<>();
            this.worker = null;
        }

        //

        public boolean submit(Consumer<Theme> callback, Theme initialValue) {
            final Lock lock = this.lock.writeLock();
            lock.lock();
            try {
                if (this.map.containsKey(callback)) return false;
                final boolean open = this.map.isEmpty();
                this.map.put(callback, new Listener(callback, initialValue));
                if (open) this.startWorker();
                return true;
            } finally {
                lock.unlock();
            }
        }

        public boolean remove(Consumer<Theme> callback) {
            final Lock lock = this.lock.writeLock();
            lock.lock();
            try {
                if (this.map.remove(callback) == null) return false;
                if (this.map.isEmpty()) this.stopWorker();
                return true;
            } finally {
                lock.unlock();
            }
        }

        /** MUST BE WRITE LOCKED */
        private void startWorker() {
            Worker w = this.worker;
            if (w != null && w.active) return;
            w = new Worker(this);
            w.start();
            this.worker = w;
        }

        /** MUST BE WRITE LOCKED */
        private void stopWorker() {
            Worker w = this.worker;
            this.worker = null;
            if (w != null && w.active) {
                w.active = false;
                try {
                    w.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while joining worker", e);
                }
            }
        }

        //

        private static final class Worker extends Thread {

            private final Daemon parent;
            volatile boolean active;

            Worker(Daemon parent) {
                super("JDaybreak Query Worker");
                this.setDaemon(true);
                this.setPriority(Thread.NORM_PRIORITY - 1);
                this.parent = parent;
                this.active = true;
            }

            //

            @Override
            public void run() {
                final Logger logger = Logger.getLogger("JDaybreak");
                while (this.active) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(this.parent.interval);
                    } catch (InterruptedException e) {
                        logger.log(Level.WARNING, "JDaybreak worker was interrupted", e);
                        this.active = false;
                        break;
                    }

                    Theme theme;
                    try {
                        theme = this.parent.parent.currentTheme();
                    } catch (ThemeException e) {
                        logger.log(Level.SEVERE, "JDaybreak worker failed to check system theme", e);
                        this.active = false;
                        break;
                    }

                    Lock lock = this.parent.lock.readLock();
                    lock.lock();
                    try {
                        for (Listener l : this.parent.map.values()) {
                            try {
                                l.update(theme);
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "JDaybreak worker callback raised an exception", e);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }

        }

    }

    private static final class Listener {

        private final Consumer<Theme> callback;
        private final StampedLock lock;
        private Theme value;

        Listener(Consumer<Theme> callback, Theme initialValue) {
            this.callback = callback;
            this.lock = new StampedLock();
            this.value = initialValue;
        }

        //

        public void update(Theme newValue) {
            long stamp = this.lock.readLock();
            try {
                if (this.value.equals(newValue)) return;
                long s2 = this.lock.tryConvertToWriteLock(stamp);
                if (s2 != -1L) {
                    stamp = s2;
                } else {
                    this.lock.unlock(stamp);
                    stamp = this.lock.writeLock();
                    if (this.value.equals(newValue)) return;
                }
                this.value = newValue;
                this.callback.accept(newValue);
            } finally {
                this.lock.unlock(stamp);
            }
        }

    }

}
