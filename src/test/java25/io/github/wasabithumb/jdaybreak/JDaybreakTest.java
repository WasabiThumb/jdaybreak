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

import io.github.wasabithumb.jdaybreak.prop.Properties;
import io.github.wasabithumb.jdaybreak.prop.Property;
import io.github.wasabithumb.jdaybreak.prop.system.PropertySystem;
import io.github.wasabithumb.jdaybreak.theme.Theme;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class JDaybreakTest {

    @Test
    void properties() {
        PropertySystem ps = PropertySystem.host();
        System.out.println(ps.getClass().getName());
        Properties props = ps.query();
        assertNotNull(props);
        if (!GraphicsEnvironment.isHeadless()) assertFalse(props.has(Property.HEADLESS));
        System.out.println(props);
    }

    @Test
    void currentTheme() {
        JDaybreak jdb = JDaybreak.jDaybreak();
        Theme theme = assertDoesNotThrow(jdb::currentTheme);
        assertNotNull(theme);
        System.out.println(theme.name());
    }

}