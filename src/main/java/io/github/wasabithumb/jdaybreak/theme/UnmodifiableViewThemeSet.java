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

import java.util.*;

@NullMarked
@ApiStatus.Internal
final class UnmodifiableViewThemeSet
        extends AbstractThemeSet
        implements ThemeSet
{

    static final UnmodifiableViewThemeSet STANDARD = new UnmodifiableViewThemeSet(EnumSet.allOf(BuiltinTheme.class));

    static UnmodifiableViewThemeSet of(Set<? extends Theme> backing) {
        if (backing instanceof UnmodifiableViewThemeSet) return (UnmodifiableViewThemeSet) backing;
        return new UnmodifiableViewThemeSet(backing);
    }

    //

    private final Set<? extends Theme> backing;

    private UnmodifiableViewThemeSet(Set<? extends Theme> backing) {
        this.backing = backing;
    }

    //

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public Iterator<Theme> iterator() {
        return new Iter(this.backing.iterator(), true);
    }

    @Override
    public boolean contains(Object o) {
        return this.backing.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.backing.containsAll(c);
    }

}
