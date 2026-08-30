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
package io.github.wasabithumb.jdaybreak.except;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * An exception occurred while querying
 * properties from the host OS.
 */
@NullMarked
public final class ThemeQueryException extends ThemeException {

    private static final long serialVersionUID = -232781889203286042L;

    //

    public ThemeQueryException(String message, Exception cause) {
        super(message, cause);
    }

    //

    /**
     * Returns the exception which this
     * exception is wrapping.
     * @return the underlying exception
     */
    @Override
    public Exception getCause() {
        return (Exception) super.getCause();
    }

    /**
     * @apiNote Always throws {@code IllegalStateException}
     * as a cause is definitely provided during instantiation.
     */
    @Override
    @Contract("_ -> fail")
    public Throwable initCause(@Nullable Throwable cause) {
        throw new IllegalStateException("may not overwrite cause");
    }

}
