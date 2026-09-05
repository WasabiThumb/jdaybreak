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
import io.github.wasabithumb.jdaybreak.util.CollectionUtil;
import io.github.wasabithumb.jdaybreak.util.SystemExecutable;
import io.github.wasabithumb.jdaybreak.util.SystemUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@NullMarked
@ApiStatus.Internal
final class GnomePropertySystem extends AbstractPropertySystem {

    static final int PRIORITY = 2;
    static final String NAME = "GNOME";

    private static final SystemExecutable GSETTINGS = new SystemExecutable("gsettings");
    private static final Set<String> DARK_SCHEMES = CollectionUtil.newStringSet("prefer-dark");
    private static final Set<String> LIGHT_SCHEMES = CollectionUtil.newStringSet("prefer-light");

    private static String queryInterfacePropertyString(String name) throws ThemeQueryException {
        ProcessBuilder pb = GSETTINGS.newProcessBuilder("get", "org.gnome.desktop.interface", name);
        pb.environment().put("LC_ALL", "C.UTF-8");
        try {
            Process p = pb.start();
            String out;
            try (InputStream in = p.getInputStream();
                 InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(r)
            ) {
                out = readPropertyString(br);
            }
            finalizeProcess(p);
            return out;
        } catch (IOException e) {
            throw newQueryException(e);
        }
    }

    private static String readPropertyString(Reader r) throws IOException {
        int b = r.read();
        if (b == -1) return "";
        if (b != 0x27) throw new IOException("expected string property to begin with single quote (got " + b + ")");

        StringBuilder sb = new StringBuilder();
        while (true) {
            b = r.read();
            if (b == -1) throw new EOFException("expected single quote before end of stream");
            if (b == 0x27) break;
            sb.append((char) b);
        }

        return sb.toString();
    }

    //

    GnomePropertySystem() {
        super(NAME);
    }

    //

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean isSupported() {
        return SystemUtil.IS_OTHER && GSETTINGS.isPresent();
    }

    @Override
    public Properties query() throws ThemeQueryException {
        if (isAwtHeadless()) return headless();
        Properties.Builder ret = Properties.builder();

        String colorScheme = queryInterfacePropertyString("color-scheme");
        if (LIGHT_SCHEMES.contains(colorScheme)) {
            ret.set(Property.LIGHT, true);
        } else if (DARK_SCHEMES.contains(colorScheme)) {
            ret.set(Property.DARK, true);
        }

        String theme = queryInterfacePropertyString("gtk-theme");
        if (!theme.isEmpty()) {
            ret.set(Property.THEME_NAME, theme);
        }

        return ret.build();
    }

}
