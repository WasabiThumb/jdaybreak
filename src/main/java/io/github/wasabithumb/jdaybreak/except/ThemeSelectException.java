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

import org.jspecify.annotations.NullMarked;

/**
 * Raised when no theme could be selected
 * with respect to the host OS properties.
 */
@NullMarked
public final class ThemeSelectException extends ThemeException {

    private static final long serialVersionUID = 8215863669734802042L;

    //

    public ThemeSelectException(String message) {
        super(message);
    }

}
