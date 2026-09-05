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
final class MacosPropertySystem extends AbstractPropertySystem {

    static final String NAME = "MACOS";
    static final int PRIORITY = 5;

    private static final SystemExecutable DEFAULTS = new SystemExecutable("defaults");

    //

    MacosPropertySystem() {
        super(NAME);
    }

    //


    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean isSupported() {
        return SystemUtil.IS_MAC && DEFAULTS.isPresent();
    }

    @Override
    public Properties query() throws ThemeQueryException {
        if (isAwtHeadless()) return headless();
        try {
            Process p = DEFAULTS.newProcessBuilder("read", "-g", "AppleInterfaceStyle").start();
            String style;
            try (InputStream in = p.getInputStream();
                 InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)
            ) {
                style = br.readLine();
            }
            return Properties.builder()
                    .set(Property.THEME_NAME, style)
                    .build();
        } catch (IOException e) {
            throw newQueryException(e);
        }
    }
}
