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
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
@ApiStatus.Internal
final class UserThemeSet
        extends AbstractThemeSet
        implements ThemeSet
{

    private boolean builtin;
    private @UnknownNullability Set<BuiltinTheme> whenBuiltin;
    private @UnknownNullability Set<Theme> whenCustom;

    UserThemeSet() {
        this.builtin = true;
        this.whenBuiltin = EnumSet.noneOf(BuiltinTheme.class);
        this.whenCustom = null;
    }

    UserThemeSet(Collection<? extends Theme> src) {
        Objects.requireNonNull(src, "src must not be null");
        if (src instanceof UserThemeSet) {
            UserThemeSet qual = (UserThemeSet) src;
            if (qual.builtin) {
                this.builtin = true;
                this.whenBuiltin = EnumSet.copyOf(qual.whenBuiltin);
                this.whenCustom = null;
            } else {
                this.builtin = false;
                this.whenBuiltin = null;
                this.whenCustom = new HashSet<>(qual.whenCustom);
            }
        } else {
            int expected = src.size();
            Iterator<? extends Theme> iter = src.iterator();
            List<Theme> tmp = new ArrayList<>(expected);
            boolean builtin = true;
            while (iter.hasNext()) {
                Theme next = iter.next();
                tmp.add(next);
                if (!(next instanceof BuiltinTheme)) {
                    builtin = false;
                    break;
                }
            }

            this.builtin = builtin;
            if (builtin) {
                EnumSet<BuiltinTheme> set = EnumSet.noneOf(BuiltinTheme.class);
                for (Theme theme : tmp) set.add((BuiltinTheme) theme);
                this.whenBuiltin = set;
                this.whenCustom = null;
            } else {
                Set<Theme> set = new HashSet<>((int) Math.ceil(expected / (double) 0.75f), 0.75f);
                set.addAll(tmp);
                while (iter.hasNext()) set.add(iter.next());
                this.whenBuiltin = null;
                this.whenCustom = set;
            }
        }
    }

    //

    @Override
    public int size() {
        return this.backing().size();
    }

    @Override
    public boolean contains(Object o) {
        return this.backing().contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.backing().containsAll(c);
    }

    @Override
    public Iterator<Theme> iterator() {
        return new Iter(this.backing().iterator(), false);
    }

    @Override
    public void clear() {
        if (this.builtin) {
            this.whenBuiltin.clear();
        } else {
            this.builtin = true;
            this.whenBuiltin = EnumSet.noneOf(BuiltinTheme.class);
            this.whenCustom = null;
        }
    }

    @Override
    public boolean add(Theme theme) {
        if (!this.builtin) return this.whenCustom.add(theme);
        if (theme instanceof BuiltinTheme) return this.whenBuiltin.add((BuiltinTheme) theme);
        Set<BuiltinTheme> src = this.whenBuiltin;
        Set<Theme> dest = new HashSet<>((int) Math.ceil((src.size() + 1) / (double) 0.75f), 0.75f);
        dest.addAll(src);
        if (!dest.add(theme)) return false;
        this.builtin = false;
        this.whenBuiltin = null;
        this.whenCustom = dest;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Theme)) return false;
        if (this.builtin) return this.whenBuiltin.remove(o);
        // We don't try to upgrade to a builtin set here
        // because doing so would be O(n) and likely to be reversed
        return this.whenCustom.remove(o);
    }

    private Set<? extends Theme> backing() {
        return this.builtin ? this.whenBuiltin : this.whenCustom;
    }

}
