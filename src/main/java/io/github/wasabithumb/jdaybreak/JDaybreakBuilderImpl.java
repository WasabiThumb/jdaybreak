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
package io.github.wasabithumb.jdaybreak;

import io.github.wasabithumb.jdaybreak.theme.Theme;
import io.github.wasabithumb.jdaybreak.theme.ThemeSet;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
@ApiStatus.Internal
final class JDaybreakBuilderImpl implements JDaybreak.Builder {

    private final ThemeSet themes;
    private long interval;

    JDaybreakBuilderImpl() {
        this.themes = ThemeSet.copyOf(ThemeSet.standard());
        this.interval = JDaybreak.DEFAULT_INTERVAL;
    }

    //


    @Override
    public JDaybreakBuilderImpl themes(Collection<? extends Theme> themes) {
        this.themes.clear();
        this.themes.addAll(themes);
        return this;
    }

    @Override
    public JDaybreakBuilderImpl theme(Theme theme) {
        this.themes.add(theme);
        return this;
    }

    @Override
    public JDaybreakBuilderImpl interval(long millis) throws IllegalArgumentException {
        if (millis < 1) throw new IllegalArgumentException("Interval must be at least 1 ms (got " + millis + " ms)");
        this.interval = millis;
        return this;
    }

    @Override
    public JDaybreak build() {
        return new JDaybreakImpl(
                ThemeSet.unmodifiableView(ThemeSet.copyOf(this.themes)),
                this.interval
        );
    }

}
