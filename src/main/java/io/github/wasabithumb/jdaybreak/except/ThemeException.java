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

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Superclass of exceptions
 * raised by {@code JDaybreak}
 * when resolving the system theme.
 * @see ThemeQueryException
 * @see ThemeSelectException
 */
@NullMarked
@ApiStatus.NonExtendable
public abstract class ThemeException extends RuntimeException {

    private static final long serialVersionUID = -934332192202817271L;

    //

    protected ThemeException(String message) {
        super(Objects.requireNonNull(message, "message must not be null"));
    }

    protected ThemeException(String message, Throwable cause) {
        super(
                Objects.requireNonNull(message, "message must not be null"),
                Objects.requireNonNull(cause, "cause must not be null")
        );
    }

    //

    /**
     * Returns the detail message string
     * of this exception, which may
     * <b>NOT</b> be null.
     * @return the detail message string of this exception
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
