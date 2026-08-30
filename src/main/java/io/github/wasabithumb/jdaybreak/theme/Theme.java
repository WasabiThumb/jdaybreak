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

import io.github.wasabithumb.jdaybreak.prop.Properties;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * An object chosen by {@code JDaybreak}
 * when the {@link Properties properties} reported by the
 * host operating system satisfies a
 * given {@link #matches(Properties) rule}.
 * @see ThemeSet
 */
@NullMarked
public interface Theme {

    /**
     * The {@link #priority() priority} used by
     * the standard themes.
     */
    int DEFAULT_PRIORITY = 0;

    /**
     * Returns the standard light theme, applicable for
     * all platforms. Matches eagerly such that
     * this theme is reported when the system properties
     * are inconclusive.
     */
    @Contract(pure = true)
    static Theme light() {
        return BuiltinTheme.LIGHT;
    }

    /**
     * Returns the standard dark theme, applicable for
     * all platforms.
     */
    @Contract(pure = true)
    static Theme dark() {
        return BuiltinTheme.DARK;
    }

    //

    /**
     * A name which identifies this theme.
     * The name for the {@link #light() standard light theme} is {@code LIGHT}
     * and the name for the {@link #dark() standard dark theme} is {@code DARK}.
     */
    String name();

    /**
     * Returns true if the provided host system properties
     * indicate that at most the theme represented by this
     * object is active.
     */
    boolean matches(Properties properties);

    /**
     * Used to distinguish themes in cases where multiple
     * return {@code true} in their {@link #matches(Properties) matches}
     * implementation. All standard themes use the
     * {@link #DEFAULT_PRIORITY default priority}. For a custom
     * theme to take precedence over a standard theme, it should
     * have a priority greater than the default.
     */
    default int priority() {
        return DEFAULT_PRIORITY;
    }

}
