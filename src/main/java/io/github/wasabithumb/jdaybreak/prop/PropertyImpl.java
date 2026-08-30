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

import java.util.Arrays;

@NullMarked
@ApiStatus.Internal
final class PropertyImpl<T> implements Property<T> {

    private static final PropertyImpl<?>[] REGISTRY;
    private static int REGISTRY_HEAD;
    static {
        REGISTRY = new PropertyImpl<?>[Property.class.getDeclaredFields().length];
        REGISTRY_HEAD = 0;
    }

    private static synchronized int register(PropertyImpl<?> instance) {
        final int ordinal = REGISTRY_HEAD++;
        REGISTRY[ordinal] = instance;
        return ordinal;
    }

    @Contract("-> new")
    static synchronized PropertyImpl<?>[] values() {
        return Arrays.copyOf(REGISTRY, REGISTRY_HEAD);
    }

    @Contract("_, _ -> new")
    static <R> PropertyImpl<R> define(String name, Class<R> typeClass) {
        return new PropertyImpl<>(name, typeClass);
    }

    //

    private final int ordinal;
    private final String name;
    private final Class<T> typeClass;

    private PropertyImpl(String name, Class<T> typeClass) {
        this.ordinal = register(this);
        this.name = name;
        this.typeClass = typeClass;
    }

    //

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Class<T> valueType() {
        return this.typeClass;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.ordinal);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Property<?>)) return false;
        return this.ordinal == ((Property<?>) obj).ordinal();
    }

    @Override
    public String toString() {
        return this.name;
    }

}
