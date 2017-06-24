/*
 * Copyright (c) 2008 Kasper Nielsen.
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
package io.faststream.sisyphus.util;

import static java.lang.reflect.Array.newInstance;

import java.io.File;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author Kasper Nielsen
 */
public class ClassTestUtil {

    /**
     * Returns a set of all types that <strong>all</strong> elements in specified iterable has in common. This includes
     * both interfaces and super classes.
     * 
     * @param elements
     *            the elements to find common types from
     * @return a set of all types thet all elements in the specified iterable has in common
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Set<Class<?>> commonPublicTypesOfObjects(Iterable<?> types) {
        Iterator<?> iter = types.iterator();
        Object o = null;
        while (o == null && iter.hasNext()) {
            o = iter.next();
        }
        if (o == null) {
            return (Set) Collections.singleton(Object.class);
        }
        Set<Class<?>> common = findAllInterfacesAndSuperClasses(o.getClass());
        Set<Class<?>> processed = new LinkedHashSet<>();
        processed.add(o.getClass());
        while (iter.hasNext()) {
            o = iter.next();
            if (o != null) {
                Class<?> t = o.getClass();
                if (!processed.contains(t)) {
                    common.retainAll(findAllInterfacesAndSuperClasses(t));
                    processed.add(t);
                }
            }
        }
        for (Iterator<Class<?>> i = common.iterator(); i.hasNext();) {
            if (!Modifier.isPublic(i.next().getModifiers())) {
                i.remove();
            }
        }
        return common;
    }

    /**
     * Returns a list of all classes on the classpath in the specified package.
     * 
     * @param packageName
     *            the name of the package
     * @return a list of all classes
     * @throws ClassNotFoundException
     * @throws Exception
     *             the list of classes in the specified package could not be obtained
     */
    public static List<Class<?>> findAllClassesInPackage(String packageName) throws Exception {
        // System.out.println("Looking for package " + packageName);
        // TODO fix what if we have multiple packages getResource only returns one
        String pName = packageName.replace(".", "/");
        List<URL> urls = Collections.list(ClassTestUtil.class.getClassLoader().getResources(pName));
        if (urls.isEmpty()) {
            throw new IllegalArgumentException("Could not find file or folder: " + pName);
        }
        ArrayList<Class<?>> names = new ArrayList<>();
        for (URL url : urls) {
            if (url.getProtocol().equals("jar")) {
                String fileName = URLDecoder.decode(url.getFile(), "UTF-8");
                // System.out.println(fileName);

                try (JarFile file = new JarFile(fileName.substring(5, fileName.indexOf("!")));) {
                    Enumeration<JarEntry> entries = file.entries();
                    while (entries.hasMoreElements()) {
                        String e = entries.nextElement().getName();
                        e = e.replace('/', '.');
                        // System.out.println(e);
                        if (e.startsWith(packageName) && e.endsWith(".class")) {
                            e = e.substring(packageName.length() + 1, e.length() - 6);
                            if (!e.contains("/")) {
                                names.add(Class.forName(packageName + "." + e.replace(".class", "")));
                            }
                        }
                    }
                }
            } else {
                File folder = new File(url.getFile());
                for (File f : folder.listFiles()) {
                    String name = f.getName();
                    if (f.isFile() && name.endsWith(".class")) {
                        names.add(Class.forName(packageName + "." + name.replace(".class", "")));
                    }
                }
            }
        }
        return names;
    }

    public static Set<Class<?>> findAllInterfacesAndSuperClasses(Class<?> clazz) {
        LinkedHashSet<Class<?>> result = new LinkedHashSet<>();
        findAllInterfacesAndSuperClasses0(clazz, result);
        result.add(Object.class);
        return result;
    }

    private static void findAllInterfacesAndSuperClasses0(Class<?> clazz, LinkedHashSet<Class<?>> addTo) {
        while (clazz != null) {
            if (!addTo.contains(clazz)) {
                addTo.add(clazz);
                for (Class<?> i : clazz.getInterfaces()) {
                    findAllInterfacesAndSuperClasses0(i, addTo);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public static boolean isInterComparable(Iterable<?> objects) {
        Iterator<?> iter = objects.iterator();
        if (!iter.hasNext()) {
            return true;
        }
        Object o = iter.next();
        if (o == null || !(o instanceof Comparable)) {
            return false;
        }
        while (iter.hasNext()) {
            Object oo = iter.next();
            if (oo == null || o.getClass() != oo.getClass()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the underlying class for the specified type, or null if the type is a wildcard or variable type.
     * 
     * @param type
     *            the type
     * @return the underlying class for the specified type
     */
    static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            return componentClass == null ? null : newInstance(componentClass, 0).getClass();
        }
        return null; // wildcard type or typevariable
    }

    static List<Class<?>> getTypeOfArguments(Class<?> baseClass, Class<?> child) {
        if (child.isInterface()) {
            throw new IllegalArgumentException();
        }
        HashMap<Type, Type> resolvedTypes = new HashMap<>();
        Type type = child;
        while (!getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                // Cannot use a raw type for anything, just move on to the super class
                type = ((Class<?>) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }
                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass, determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments = type instanceof Class ? ((Class<?>) type).getTypeParameters() : ((ParameterizedType) type).getActualTypeArguments();
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }

    public static Class<?> getTypeOfArgument(Class<?> baseClass, Class<?> childClass, int parameterIndex) {
        return getTypeOfArguments(baseClass, childClass).get(parameterIndex);
    }
}
