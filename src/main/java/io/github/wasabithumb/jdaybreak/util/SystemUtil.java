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
import org.jspecify.annotations.NullMarked;

import java.util.BitSet;

@NullMarked
@ApiStatus.Internal
public final class SystemUtil {

    public static final boolean IS_WINDOWS;
    public static final boolean IS_MAC;
    public static final boolean IS_OTHER;
    static {
        String name = System.getProperty("os.name");
        int match = name == null ? -1 : identify(
                name,
                "windows", // 0
                "mac",              // 1
                "darwin",           // 2
                "linux",            // 3
                "bsd"               // 4
        );
        IS_WINDOWS = match == 0;
        IS_MAC = match == 1 || match == 2;
        IS_OTHER = match < 0 | match > 2;
    }

    private static int identify(String haystack, String... needles) {
        int limit = needles.length;
        int[] positions = new int[limit];

        int hh = 0;
        while (hh < haystack.length()) {
            char c = Character.toLowerCase(haystack.charAt(hh++));
            for (int i = 0; i < limit; i++) {
                String needle = needles[i];
                int len = needle.length();
                int head = positions[i];
                if (head >= len || c != Character.toLowerCase(needle.charAt(head++))) {
                    positions[i] = 0;
                    continue;
                }
                if (head == len) return i;
                positions[i] = head;
            }
        }

        return -1;
    }

    //

    private SystemUtil() { }

}
