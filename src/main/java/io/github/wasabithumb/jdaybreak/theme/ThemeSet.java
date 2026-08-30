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
package io.github.wasabithumb.jdaybreak.theme;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * An optimized {@link Set} of {@link Theme themes}
 * for use in configuring {@code JDaybreak}.
 * @see #standard()
 */
@NullMarked
@ApiStatus.NonExtendable
public interface ThemeSet extends Set<Theme> {

    /**
     * Creates a new empty mutable {@link ThemeSet}.
     */
    @Contract("-> new")
    static ThemeSet of() {
        return new UserThemeSet();
    }

    /**
     * Creates a new mutable {@link ThemeSet} consisting of
     * the provided themes.
     */
    @Contract("_ -> new")
    static ThemeSet of(Theme... values) {
        return new UserThemeSet(Arrays.asList(values));
    }

    /**
     * Creates a new mutable {@link ThemeSet} containing all themes
     * within the provided collection.
     */
    @Contract("_ -> new")
    static ThemeSet copyOf(Collection<? extends Theme> src) {
        return new UserThemeSet(src);
    }

    /**
     * Returns an unmodifiable set containing all standard (builtin) themes,
     * namely {@link Theme#light() light} and {@link Theme#dark() dark}.
     * Unless there are significant developments in platform technology,
     * these will be the only 2 themes to ever be included in this library.
     */
    @Contract(pure = true)
    static @Unmodifiable ThemeSet standard() {
        return UnmodifiableViewThemeSet.STANDARD;
    }

    /**
     * Returns an unmodifiable set of {@link Theme themes} which
     * reads through to the provided set.
     */
    static @UnmodifiableView ThemeSet unmodifiableView(Set<? extends Theme> src) {
        return UnmodifiableViewThemeSet.of(src);
    }

}
