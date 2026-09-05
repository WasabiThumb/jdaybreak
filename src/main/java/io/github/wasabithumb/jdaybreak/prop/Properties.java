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

import java.util.NoSuchElementException;

/**
 * An immutable collection of {@link Property properties}
 * and their associated values if present.
 */
@NullMarked
@ApiStatus.NonExtendable
public interface Properties {

    /**
     * Returns a builder to construct a
     * new {@link Properties} instance.
     */
    @Contract("-> new")
    static Builder builder() {
        return new PropertiesImpl.Builder();
    }

    //

    /**
     * Returns true if the specified property is
     * present in this object, indicating that
     * a call to {@link #get(Property)} with the
     * specified property will succeed.
     */
    @Contract(pure = true)
    boolean has(Property<?> property);

    /**
     * Returns the value associated to the specified
     * property by this object.
     * @throws NoSuchElementException This object does not contain this property.
     * @throws IllegalArgumentException The provided property is {@link Property#isVoid() void type}.
     * @see #has(Property)
     */
    @Contract(pure = true)
    <T> T get(Property<T> property) throws NoSuchElementException;

    //

    /**
     * Used to create new {@link Properties}
     * instances.
     */
    @ApiStatus.NonExtendable
    interface Builder {

        @Contract("_, _ -> this")
        <T> Builder set(Property<T> property, T value);

        @Contract("_ -> this")
        @ApiStatus.AvailableSince("0.2.0")
        Builder set(Property<Void> property);

        @Contract("_ -> this")
        Builder unset(Property<?> property);

        @Contract("-> this")
        Builder clear();

        @Contract("-> new")
        Properties build();

    }

}
