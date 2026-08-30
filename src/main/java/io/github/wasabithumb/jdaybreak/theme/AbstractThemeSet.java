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

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.AbstractSet;
import java.util.Iterator;

@NullMarked
@ApiStatus.Internal
abstract class AbstractThemeSet extends AbstractSet<Theme> {

    protected static final class Iter implements Iterator<Theme> {

        private final Iterator<? extends Theme> backing;
        private final boolean protect;

        Iter(
                Iterator<? extends Theme> backing,
                boolean protect
        ) {
            this.backing = backing;
            this.protect = protect;
        }

        //

        @Override
        public boolean hasNext() {
            return this.backing.hasNext();
        }

        @Override
        public Theme next() {
            return this.backing.next();
        }

        @Override
        public void remove() {
            if (this.protect) throw new UnsupportedOperationException("set is unmodifiable");
            this.backing.remove();
        }

    }

}
