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
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Handles extraction of {@link Properties properties}
 * from the host operating system for a specific
 * named environment kind
 */
@NullMarked
@ApiStatus.NonExtendable
public interface PropertySystem {

    /**
     * Retrieves the canonical {@link PropertySystem}
     * for the host operating system.
     * @throws IllegalStateException An instance could not be chosen.
     */
    static PropertySystem host() throws IllegalStateException {
        return PropertySystemProvider.get();
    }

    //

    /**
     * A name which uniquely identifies
     * this system singleton.
     */
    String name();

    /**
     * Arbitrary integer used to signal
     * preference for one system over a set
     * of other systems when multiple systems
     * signal host support.
     */
    @ApiStatus.Internal
    int priority();

    /**
     * Returns true if the system is usable
     * on this host OS.
     * @throws ThemeQueryException Support could not be determined,
     * callers may treat this identically to returning false.
     */
    boolean isSupported() throws ThemeQueryException;

    /**
     * Queries the host OS for theme properties.
     * This should only be called when {@link #isSupported()} returns
     * true non-exceptionally.
     * @throws ThemeQueryException Properties could not be queried.
     * This is unexpected behavior and should bubble to API call sites.
     */
    Properties query() throws ThemeQueryException;

}
