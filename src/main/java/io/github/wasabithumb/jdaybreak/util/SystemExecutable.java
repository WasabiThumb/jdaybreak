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
package io.github.wasabithumb.jdaybreak.util;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * Lazily evaluates the actual location on the
 * PATH of a named executable.
 */
@NullMarked
@ApiStatus.Internal
public final class SystemExecutable {

    private static final int S_EVALUATED = 1;
    private static final int S_PRESENT = 2;

    private static @Nullable Path evaluate(String name) {
        String path = System.getenv("PATH");
        if (path == null) return null;

        FileSystem fs = FileSystems.getDefault();
        for (String dirStr : path.split(File.pathSeparator)) {
            Path target;
            try {
                target = fs.getPath(dirStr, name);
            } catch (InvalidPathException ignored) {
                continue;
            }
            if (!Files.isExecutable(target)) continue;
            return target;
        }

        return null;
    }

    //

    private final StampedLock lock;
    private final @NonNls String name;
    private @MagicConstant(flagsFromClass = SystemExecutable.class) int state;
    private @UnknownNullability Path path;

    public SystemExecutable(@NonNls String name) {
        this.lock = new StampedLock();
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.state = 0;
        this.path = null;
    }

    //

    public @NonNls String name() {
        return this.name;
    }

    public boolean isPresent() {
        return this.getInternal() != null;
    }

    public Path get() throws IllegalStateException {
        Path p = this.getInternal();
        if (p == null) throw new IllegalStateException("executable not present");
        return p;
    }

    private @Nullable Path getInternal() {
        long stamp = this.lock.readLock();
        try {
            if ((this.state & S_EVALUATED) == S_EVALUATED) {
                if ((this.state & S_PRESENT) != S_PRESENT) return null;
                return this.path;
            }

            long s2 = this.lock.tryConvertToWriteLock(stamp);
            if (s2 != -1L) {
                stamp = s2;
            } else {
                this.lock.unlock(stamp);
                stamp = this.lock.writeLock();
                if ((this.state & S_EVALUATED) == S_EVALUATED) {
                    if ((this.state & S_PRESENT) != S_PRESENT) return null;
                    return this.path;
                }
            }

            Path p = evaluate(this.name);
            if (p == null) {
                this.path = null;
                this.state = S_EVALUATED;
                return null;
            } else {
                this.path = p;
                this.state = S_EVALUATED | S_PRESENT;
                return p;
            }
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public ProcessBuilder newProcessBuilder(String... args) throws IllegalStateException {
        String[] cmd = new String[args.length + 1];
        cmd[0] = this.get().toString();
        System.arraycopy(args, 0, cmd, 1, args.length);
        return new ProcessBuilder(cmd);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SystemExecutable &&
                this.name.equals(((SystemExecutable) obj).name);
    }

    @Override
    public String toString() {
        return "SystemExecutable{name=" + this.name + "}";
    }

}
