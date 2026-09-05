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

@NullMarked
@ApiStatus.Internal
final class KdePropertySystem extends AbstractPropertySystem {

    static final int PRIORITY = 1;
    static final String NAME = "KDE";

    private static final SystemExecutable KREADCONFIG = new SystemExecutable("kreadconfig5");

    //

    KdePropertySystem() {
        super(NAME);
    }

    //

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean isSupported() {
        return SystemUtil.IS_OTHER && KREADCONFIG.isPresent();
    }

    @Override
    public Properties query() throws ThemeQueryException {
        if (isAwtHeadless()) return headless();
        try {
            ProcessBuilder pb = KREADCONFIG.newProcessBuilder(
                    "--file", "kdeglobals",
                    "--group", "General",
                    "--key", "ColorScheme"
            );
            pb.environment().put("LC_ALL", "C.UTF-8");
            Process p = pb.start();

            String theme;
            try (InputStream in = p.getInputStream();
                 InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(r)
            ) {
                theme = br.readLine();
            }
            finalizeProcess(p);

            return Properties.builder()
                    .set(Property.THEME_NAME, theme)
                    .build();
        } catch (IOException e) {
            throw newQueryException(e);
        }
    }

}
