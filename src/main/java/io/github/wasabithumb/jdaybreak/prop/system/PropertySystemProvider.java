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
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@NullMarked
@ApiStatus.Internal
final class PropertySystemProvider {

    private static @Nullable PropertySystem INSTANCE = null;

    private static final Class<?>[] IMPLS = new Class<?>[] {
            WindowsPropertySystem.class,
            MacosPropertySystem.class,
            GnomePropertySystem.class,
            KdePropertySystem.class,
            XfcePropertySystem.class,
            OpenboxPropertySystem.class
    };

    //

    public static synchronized PropertySystem get() throws IllegalStateException {
        PropertySystem ret = INSTANCE;
        if (ret != null) return ret;
        INSTANCE = ret = initialize();
        return ret;
    }

    private static PropertySystem initialize() throws IllegalStateException {
        PropertySystem candidate = null;
        Throwable cause = null;

        for (Class<?> implClass : IMPLS) {
            PropertySystem next;
            try {
                next = tryInitialize(implClass);
                if (next == null) continue;
            } catch (ThemeQueryException e) {
                if (cause != null) e.addSuppressed(cause);
                cause = e;
                continue;
            }
            if (candidate != null && candidate.priority() >= next.priority()) continue;
            candidate = next;
        }

        if (candidate != null) {
            return candidate;
        }

        throw new IllegalStateException(
                "No supported PropertySystem (tried " + IMPLS.length + " impls)",
                cause
        );
    }

    private static @Nullable PropertySystem tryInitialize(Class<?> implClass) {
        if (!PropertySystem.class.isAssignableFrom(implClass))
            throw new IllegalArgumentException("Class " + implClass.getName() + " does not implement PropertySystem");

        int mod = implClass.getModifiers();
        if (Modifier.isAbstract(mod) || Modifier.isInterface(mod))
            throw new IllegalArgumentException("Cannot instantiate abstract class " + implClass.getName());

        Constructor<? extends PropertySystem> con;
        try {
            con = implClass.asSubclass(PropertySystem.class)
                    .getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + implClass.getName() + " has no primary constructor", e);
        }

        try {
            con.setAccessible(true);
        } catch (Exception ignored) { }

        PropertySystem instance;
        try {
            instance = con.newInstance();
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            throw new IllegalStateException("Constructor for class " + implClass.getName() + " raised a checked exception", cause);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Invariant violated", e);
        }

        if (!instance.isSupported()) return null;
        return instance;
    }

    //

    private PropertySystemProvider() { }

}
