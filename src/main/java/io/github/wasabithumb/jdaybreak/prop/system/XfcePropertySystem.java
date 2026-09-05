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
final class XfcePropertySystem extends AbstractPropertySystem {

    static final int PRIORITY = 1;
    static final String NAME = "XFCE";

    private static final SystemExecutable XFCONF_QUERY = new SystemExecutable("xfconf-query");
    private static final Pattern THEME_NAME_PATTERN = Pattern.compile("/Net/ThemeName\\s*(\\S+)");

    //

    XfcePropertySystem() {
        super(NAME);
    }

    //

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean isSupported() {
        return SystemUtil.IS_OTHER && XFCONF_QUERY.isPresent();
    }

    @Override
    public Properties query() throws ThemeQueryException {
        if (isAwtHeadless()) return headless();
        ProcessBuilder pb = XFCONF_QUERY.newProcessBuilder(
                "-c", "xsettings",
                "-p", "/Net",
                "-l",
                "-v"
        );
        pb.environment().put("LC_ALL", "C.UTF-8");

        try {
            Process p = pb.start();

            String themeName = null;
            try (InputStream in = p.getInputStream();
                 InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(r)
            ) {
                String line;
                Matcher lineMatcher;
                while ((line = br.readLine()) != null) {
                    lineMatcher = THEME_NAME_PATTERN.matcher(line);
                    if (!lineMatcher.matches()) continue;
                    themeName = lineMatcher.group(1);
                    break;
                }
            }

            finalizeProcess(p);

            Properties.Builder builder = Properties.builder();
            if (themeName != null && !themeName.isEmpty()) builder.set(Property.THEME_NAME, themeName);
            return builder.build();
        } catch (IOException e) {
            throw newQueryException(e);
        }
    }

}
