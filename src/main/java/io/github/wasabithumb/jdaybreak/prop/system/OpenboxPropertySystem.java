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
import io.github.wasabithumb.jdaybreak.util.SystemUtil;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@NullMarked
@ApiStatus.Internal
final class OpenboxPropertySystem extends AbstractPropertySystem {

    static final int PRIORITY = 0;
    static final String NAME = "OPENBOX";

    private static @Nullable Path getLocalRc() {
        try {
            FileSystem fs = FileSystems.getDefault();
            Path userHome = fs.getPath(System.getProperty("user.home"));
            Path rc = userHome.resolve(".config/openbox/rc.xml");
            if (!Files.exists(rc)) return null;
            return rc;
        } catch (InvalidPathException ignored) {
            return null;
        }
    }

    private static @Nullable Path getGlobalRc() {
        try {
            FileSystem fs = FileSystems.getDefault();
            Path rc = fs.getPath("/etc/xdg/openbox/rc.xml");
            if (!Files.exists(rc)) return null;
            return rc;
        } catch (InvalidPathException ignored) {
            return null;
        }
    }

    @Contract("null -> null")
    private static @Nullable String extractThemeName(@Nullable Path rc) throws ThemeQueryException {
        if (rc == null) return null;
        try {
            ThemeNameHandler h = new ThemeNameHandler();
            XMLReader reader = new ParserAdapter();
            reader.setContentHandler(h);
            try (InputStream in = Files.newInputStream(rc)) {
                reader.parse(new InputSource(in));
            }
            return h.name();
        } catch (SAXException | IOException e) {
            throw newQueryException(e);
        }
    }

    //

    OpenboxPropertySystem() {
        super(NAME);
    }

    //

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public boolean isSupported() {
        return SystemUtil.IS_OTHER &&
                (null != getLocalRc() || null != getGlobalRc());
    }

    @Override
    public Properties query() throws ThemeQueryException {
        Properties.Builder builder = Properties.builder();
        String themeName = extractThemeName(getLocalRc());
        if (themeName == null) {
            themeName = extractThemeName(getGlobalRc());
            if (themeName == null) return builder.build();
        }
        builder.set(Property.THEME_NAME, themeName);
        return builder.build();
    }

    //

    private static final class ThemeNameHandler extends DefaultHandler {

        private static final int S_PRE_THEME = 0;
        private static final int S_PRE_THEME_NAME = 1;
        private static final int S_IN_THEME_NAME = 2;
        private static final int S_POST_THEME_NAME = 3;
        private static final int S_POST_THEME = 4;

        //

        private final StringBuilder nameBuilder;
        private @MagicConstant(valuesFromClass = ThemeNameHandler.class) int state;

        ThemeNameHandler() {
            this.nameBuilder = new StringBuilder();
            this.state = S_PRE_THEME;
        }

        //

        public @Nullable String name() {
            if (this.state < S_POST_THEME) return null;
            return this.nameBuilder.toString();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            switch (this.state) {
                case S_PRE_THEME:
                    if ("theme".equals(qName)) this.state = S_PRE_THEME_NAME;
                    break;
                case S_PRE_THEME_NAME:
                    if ("name".equals(qName)) this.state = S_IN_THEME_NAME;
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            switch (this.state) {
                case S_IN_THEME_NAME:
                    if ("name".equals(qName)) this.state = S_POST_THEME_NAME;
                    break;
                case S_POST_THEME_NAME:
                    if ("theme".equals(qName)) this.state = S_POST_THEME;
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (this.state != S_IN_THEME_NAME) return;
            this.nameBuilder.append(ch, start, length);
        }

    }

}
