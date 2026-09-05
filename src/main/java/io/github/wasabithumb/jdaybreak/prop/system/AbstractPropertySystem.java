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
package io.github.wasabithumb.jdaybreak.prop.system;

import io.github.wasabithumb.jdaybreak.except.ThemeQueryException;
import io.github.wasabithumb.jdaybreak.prop.Properties;
import io.github.wasabithumb.jdaybreak.prop.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ApiStatus.Internal
abstract class AbstractPropertySystem implements PropertySystem {

    @Contract("_ -> new")
    protected static ThemeQueryException newQueryException(Exception exception) {
        return new ThemeQueryException("Theme query failed unexpectedly", exception);
    }

    protected static boolean isAwtHeadless() {
        return java.awt.GraphicsEnvironment.isHeadless();
    }

    @Contract("-> new")
    protected static Properties headless() {
        return Properties.builder()
                .set(Property.HEADLESS)
                .build();
    }

    protected static void finalizeProcess(Process p) throws ThemeQueryException {
        try {
            int ex = p.waitFor();
            if (ex == 0) return;
            throw newQueryException(new IllegalStateException("Process exited with status code " + ex));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw newQueryException(e);
        }
    }

    //

    protected final String name;

    AbstractPropertySystem(String name) {
        this.name = name;
    }

    //


    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PropertySystem &&
                this.name.equals(((PropertySystem) obj).name());
    }

    @Override
    public String toString() {
        return "PropertySystem{name=" + this.name + "}";
    }

}
