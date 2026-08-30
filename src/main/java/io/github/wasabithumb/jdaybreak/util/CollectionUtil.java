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
package io.github.wasabithumb.jdaybreak.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NullMarked
@ApiStatus.Internal
public final class CollectionUtil {

    @Contract("_ -> new")
    public static @Unmodifiable Set<String> newStringSet(String... src) {
        if (src.length == 0) return Collections.emptySet();
        if (src.length == 1) return Collections.singleton(src[0]);
        Set<String> ret = new HashSet<>((int) Math.ceil(src.length / (double) 0.75f), 0.75f);
        ret.addAll(Arrays.asList(src));
        return Collections.unmodifiableSet(ret);
    }

    //

    private CollectionUtil() { }

}
