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
import io.github.wasabithumb.jdaybreak.theme.Theme;
import io.github.wasabithumb.jdaybreak.theme.ThemeSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Entry point for {@code JDaybreak}.
 * @see #jDaybreak()
 * @see #builder()
 */
@NullMarked
@ApiStatus.NonExtendable
public interface JDaybreak {

    /**
     * The default amount of time (ms)
     * that the listener daemon should wait
     * between theme queries.
     * @see #registerListener(Consumer, boolean)
     */
    Long DEFAULT_INTERVAL = 200L;

    /**
     * Provides the default instance, semantically
     * identical to {@code builder().build()}.
     */
    @Contract(pure = true)
    static JDaybreak jDaybreak() {
        return JDaybreakImpl.DEFAULT;
    }

    /**
     * Creates a new builder for the purpose of
     * constructing a custom {@link JDaybreak}
     * instance. The builder will be initially configured
     * to contain the {@link ThemeSet#standard() standard themes}
     * and have a daemon interval of {@link #DEFAULT_INTERVAL}.
     */
    @Contract("-> new")
    static Builder builder() {
        return new JDaybreakBuilderImpl();
    }

    //

    /**
     * Provides the immutable set of themes which this
     * instance will attempt to choose from. This
     * is the union of themes provided during initialization,
     * typically the {@link ThemeSet#standard() standard set}.
     */
    @Contract(pure = true)
    @Unmodifiable ThemeSet themes();

    /**
     * Computes the system {@link Theme theme}. This will be exactly one
     * of the themes in the {@link #themes() theme set}.
     * For the {@link #jDaybreak() default instance}, these are the
     * {@link ThemeSet#standard() standard themes}. Therefore, for
     * most use cases, the return value may be adapted to a boolean by
     * checking strict equality with the {@link Theme#light() light}
     * or {@link Theme#dark() dark} theme constant. It is not reasonable to
     * expect this method to block.
     * @throws ThemeException Failed to identify the system theme.
     */
    Theme currentTheme() throws ThemeException;

    /**
     * Sets up the provided callback to receive the new system theme
     * whenever it changes. If there were previously 0 callbacks, a daemon thread
     * will be newly started to poll for changes.
     * @param callback The callback to register.
     * @param fire If true, the callback will also be immediately invoked with the {@link #currentTheme() current theme}.
     * @return true if the callback was not already registered.
     * @throws ThemeException The current theme could not be identified.
     */
    boolean registerListener(Consumer<Theme> callback, boolean fire) throws ThemeException;

    /**
     * Sets up the provided callback to receive the new system theme
     * whenever it changes. If there were previously 0 callbacks, a daemon thread
     * will be newly started to poll for changes. Identical to
     * {@code registerListener(callback, false)}.
     * @param callback The callback to register.
     * @return true if the callback was not already registered.
     * @see #registerListener(Consumer, boolean)
     * @throws ThemeException The current theme could not be identified.
     */
    default boolean registerListener(Consumer<Theme> callback) throws ThemeException {
        return this.registerListener(callback, false);
    }

    /**
     * Unregisters a callback previously registered with {@link #registerListener(Consumer, boolean) registerListener}.
     * If this causes the number of callbacks to become 0, the daemon thread is scheduled to stop.
     * @return true if the callback was not already unregistered.
     */
    boolean unregisterListener(Consumer<Theme> callback);

    //

    /**
     * Facilitates the creation of a new
     * {@link JDaybreak} instance.
     */
    @ApiStatus.NonExtendable
    interface Builder {

        /**
         * Overrides the themes that the instance will be allowed to select from.
         * @see ThemeSet
         */
        @Contract("_ -> this")
        Builder themes(Collection<? extends Theme> themes);

        /**
         * Adds a theme that the instance will be allowed to select from.
         */
        @Contract("_ -> this")
        Builder theme(Theme theme);

        /**
         * Sets the {@link #DEFAULT_INTERVAL interval} in milliseconds for
         * the {@link #registerListener(Consumer, boolean) listener daemon}.
         * @throws IllegalArgumentException Value is less than 1
         */
        @Contract("_ -> this")
        Builder interval(long millis) throws IllegalArgumentException;

        /**
         * Creates an instance matching the configuration
         * of this builder.
         */
        @Contract("-> new")
        JDaybreak build();

    }

}
