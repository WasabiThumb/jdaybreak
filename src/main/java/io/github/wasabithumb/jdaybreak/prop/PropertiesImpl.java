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
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

@NullMarked
@ApiStatus.Internal
final class PropertiesImpl implements Properties {

    private static final Property<?>[] UNIVERSE = Property.values();

    @Contract("null -> fail")
    private static int unwrap(@Nullable Property<?> property) {
        return Objects.requireNonNull(property, "property must not be null")
                .ordinal();
    }

    //

    private final @Nullable Object[] values;

    private PropertiesImpl(@Nullable Object[] values) {
        this.values = values;
    }

    //

    @Override
    public boolean has(Property<?> property) {
        return null != this.values[unwrap(property)];
    }

    @Override
    public <T> T get(Property<T> property) throws NoSuchElementException {
        Object value = this.values[unwrap(property)];
        if (null == value) throw new NoSuchElementException("property " + property.name() + " is not set");
        return property.valueType().cast(value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PropertiesImpl)) return false;
        return Arrays.equals(this.values, ((PropertiesImpl) obj).values);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Properties{");
        boolean sep = false;
        for (int i = 0; i < UNIVERSE.length; i++) {
            Property<?> key = UNIVERSE[i];
            Object value = this.values[i];
            if (value == null) continue;
            if (sep) sb.append(", ");
            sb.append(key.name());
            sb.append('=');
            sb.append(value);
            sep = true;
        }
        sb.append('}');
        return sb.toString();
    }

    //

    static final class Builder implements Properties.Builder {

        private final @Nullable Object[] values;

        Builder() {
            Object[] values = new Object[UNIVERSE.length];
            Arrays.fill(values, null);
            this.values = values;
        }

        //

        @Override
        public <T> Builder set(Property<T> property, T value) {
            this.values[unwrap(property)] = value;
            return this;
        }

        @Override
        public Builder unset(Property<?> property) {
            this.values[unwrap(property)] = null;
            return this;
        }

        @Override
        public Builder clear() {
            Arrays.fill(this.values, null);
            return this;
        }

        @Override
        public PropertiesImpl build() {
            return new PropertiesImpl(Arrays.copyOf(this.values, UNIVERSE.length));
        }

    }

}
