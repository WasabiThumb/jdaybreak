# JDaybreak

![License](https://img.shields.io/badge/license-apache--2.0-blue)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.wasabithumb/jdaybreak)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/WasabiThumb/jdaybreak/build.yml)

A lightweight Java 8+ library for querying the system UI theme.
This is a similar project to [Dansoftowner/jSystemThemeDetector](https://github.com/Dansoftowner/jSystemThemeDetector)
with some major structural differences:

- No heavy dependencies (JNA, OSHI)
- No native bindings
- Theme identification process is exposed and configurable
- Support for more Linux environments (Xfce, LXDE/Openbox)

## Usage Examples
### Basic
```java
Theme theme = JDaybreak.jDaybreak().currentTheme();
boolean currentThemeIsDark = Theme.dark().equals(theme);
boolean currentThemeIsLight = Theme.light().equals(theme);
// These values are mutually exclusive, exactly 1 will be true
```

### Listeners
```java
JDayBreak jdb = JDaybreak.builder()
        .interval(50L) // Refresh interval (ms), default is 200
        .build();

jdb.registerListener(
        (Theme theme) -> { /*... */ },
        true // True to also immediately invoke the callback with the current value
);
```

### Custom Themes
```java
public final class AdwaitaDarkTheme implements Theme {

    public static final String NAME = "ADWAITA_DARK";
    public static final AdwaitaDarkTheme INSTANCE = new AdwaitaDarkTheme();
    
    private AdwaitaDarkTheme() { }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean matches(Properties properties) {
        // Theme is active when the system theme name is Adwaita-dark.
        return properties.has(Property.THEME_NAME) &&
                properties.get(Property.THEME_NAME).equals("Adwaita-dark");
    }

    @Override
    public int priority() {
        // Ensures that our custom theme takes priority over the standard dark theme
        return Theme.DEFAULT_PRIORITY + 1;
    }

}
```
```java
JDaybreak jdb = JDaybreak.builder()
        .theme(AdwaitaDarkTheme.INSTANCE)
        // Adds our custom theme in addition to the standard themes
        // Using #themes we can also overwrite the standard themes, however
        // that makes a ThemeSelectException possible when using the API.
        .build();

Theme currentTheme = jdb.currentTheme();
boolean currentThemeIsAdwaitaDark = AdwaitaDarkTheme.INSTANCE.equals(currentTheme);
```

## License
```text
Copyright 2026 Xavier Pedraza

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
