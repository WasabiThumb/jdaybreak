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
import io.github.wasabithumb.jdaybreak.prop.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

@NullMarked
@ApiStatus.Internal
enum BuiltinTheme implements Theme {
    LIGHT(true),
    DARK(false);

    private static final Pattern DARK_PATTERN = Pattern.compile(".*dark.*", Pattern.CASE_INSENSITIVE);

    //

    private final boolean light;

    BuiltinTheme(boolean light) {
        this.light = light;
    }

    //

    @Override
    public boolean matches(Properties properties) {
        if (properties.has(Property.LIGHT))
            return properties.get(Property.LIGHT) == this.light;

        if (properties.has(Property.DARK))
            return properties.get(Property.DARK) != this.light;

        if (properties.has(Property.THEME_NAME))
            return DARK_PATTERN.matcher(properties.get(Property.THEME_NAME)).matches() != this.light;

        // As a defensive measure, light theme should match
        // when there are NO properties set. This way
        // default behavior logically prohibits
        // ThemeSelectException from being thrown.
        return this.light;
    }

}
