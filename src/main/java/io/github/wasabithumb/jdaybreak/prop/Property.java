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
package io.github.wasabithumb.jdaybreak.prop;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import static io.github.wasabithumb.jdaybreak.prop.PropertyImpl.define;

/**
 * A variable extracted from the host operating
 * system that may or may not have an associated value.
 * The value and presence of known properties is the
 * information permitted to be used to determine
 * the system theme.
 */
@NullMarked
@ApiStatus.NonExtendable
public interface Property<T> {

    /**
     * Property that is set if the system
     * properties were not queried because
     * the system was determined to be
     * running in a headless environment.
     * There is no value associated with
     * this property.
     */
    @ApiStatus.AvailableSince("0.2.0")
    Property<Void> HEADLESS = define("HEADLESS");

    /**
     * Property that is set if the operating system
     * makes an affirmative claim about whether ({@code true})
     * or not ({@code false}) the system uses a light theme.
     */
    Property<Boolean> LIGHT = define("LIGHT", Boolean.class);

    /**
     * Property that is set if the operating system
     * makes an affirmative claim about whether ({@code true})
     * or not ({@code false}) the system uses a dark theme.
     */
    Property<Boolean> DARK = define("DARK", Boolean.class);

    /**
     * Property that is set if the operating system
     * definitely provides a name of an installed theme
     * that is set as the system default, containing
     * the name of that theme as a string.
     */
    Property<String> THEME_NAME = define("THEME_NAME", String.class);

    /**
     * Returns a newly allocated array
     * of all property constants from
     * least to greatest {@link #ordinal() ordinal}.
     */
    @Contract("-> new")
    static Property<?>[] values() {
        return PropertyImpl.values();
    }

    //

    /**
     * Returns the ordinal of this property
     * constant, for purposes similar to
     * {@link Enum#ordinal()}.
     */
    @Contract(pure = true)
    int ordinal();

    /**
     * Returns the name of this property
     * constant, identical to the name
     * of the static field in the {@code Property}
     * class with the same value as this object.
     */
    @Contract(pure = true)
    String name();

    /**
     * Returns the class instance describing
     * the type of values associated with this property.
     */
    @Contract(pure = true)
    Class<T> valueType();

    /**
     * Returns {@code true} if the {@link #valueType() value type}
     * of this property is {@link Void}. This may be
     * faster than {@code Void.TYPE.equals(property.valueType())}.
     */
    @Contract(pure = true)
    boolean isVoid();

}
