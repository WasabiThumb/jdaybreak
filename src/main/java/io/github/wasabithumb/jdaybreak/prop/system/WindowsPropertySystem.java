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
import io.github.wasabithumb.jdaybreak.prop.Property;
import io.github.wasabithumb.jdaybreak.util.SystemExecutable;
import io.github.wasabithumb.jdaybreak.util.SystemUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NullMarked
@ApiStatus.Internal
final class WindowsPropertySystem extends AbstractPropertySystem {

    static final int PRIORITY = 5;
    static final String NAME = "WINDOWS";

    private static final SystemExecutable REG = new SystemExecutable("reg.exe");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\x20*[^\\x20]+\\x20+REG_DWORD\\x20+0x([0-9A-Fa-f]+)");

    //

    WindowsPropertySystem() {
        super(NAME);
    }

    //


    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean isSupported() {
        return SystemUtil.IS_WINDOWS && REG.isPresent();
    }

    @Override
    public Properties query() throws ThemeQueryException {
        ProcessBuilder pb = REG.newProcessBuilder(
                "query", "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                "-v", "AppsUseLightTheme"
        );
        try {
            Process p = pb.start();

            boolean light = false;
            try (InputStream in = p.getInputStream();
                 InputStreamReader r = new InputStreamReader(in, StandardCharsets.US_ASCII);
                 BufferedReader br = new BufferedReader(r)
            ) {
                String line;
                Matcher lineMatcher;
                while ((line = br.readLine()) != null) {
                    lineMatcher = VALUE_PATTERN.matcher(line);
                    if (!lineMatcher.matches()) continue;
                    light = !"0".equals(lineMatcher.group(1));
                }
            }

            finalizeProcess(p);

            return Properties.builder()
                    .set(Property.LIGHT, light)
                    .build();
        } catch (IOException e) {
            throw newQueryException(e);
        }
    }

}
