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
package io.faststream.codegen.core;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Various utility methods for this package.
 *
 * @author Kasper Nielsen
 */
public class CodegenUtil {

    /** A cache of indents */
    private static final String[] INDENT_CACHE;

    /** The String used for indenting a block. */
    public static final String INDENT_STRING = "    ";
    static final Map<String, Integer> KEYWORDS;

    /** The default line separator used. */
    public static final String LS = "\n";

    static {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("public", Modifier.PUBLIC);
        map.put("protected", Modifier.PROTECTED);
        map.put("private", Modifier.PRIVATE);
        map.put("abstract", Modifier.ABSTRACT);
        map.put("static", Modifier.STATIC);
        map.put("final", Modifier.FINAL);
        map.put("synchronized", Modifier.SYNCHRONIZED);
        map.put("native", Modifier.NATIVE);
        KEYWORDS = Collections.unmodifiableMap(map);

        INDENT_CACHE = new String[30];
        INDENT_CACHE[0] = "";
        for (int i = 1; i < INDENT_CACHE.length; i++) {
            INDENT_CACHE[i] = INDENT_CACHE[i - 1] + INDENT_STRING;
        }
    }

    /**
     * Returns an indented string
     *
     * @param blocks
     *            the number of blocks the string should be indented
     * @return an indented string
     */
    public static String indent(int blocks) {
        return INDENT_CACHE[blocks];
    }

    public static String spaces(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String checkValidJavaIdentifier(String s) {
        if (!isJavaIdentifier(s)) {
            throw new IllegalArgumentException(s + " is not a valid Java identifier");
        }
        return s;
    }

    /**
     * Counts occurrences of the specified character in the specified string.
     *
     * @param str
     *            the string to count occurrences in
     * @param c
     *            the char to look for
     * @return the number of occurrences of the specified character
     */
    static int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the default string value, for example <tt>0f</tt> for the specified type.
     *
     * @param type
     *            the type
     * @return the default string value for the specified type.
     */
    public static String defaultValue(Class<?> type) {
        if (type == boolean.class) {
            return "false";
        } else if (type == byte.class) {
            return "(byte) 0";
        } else if (type == char.class) {
            return "(char) 0";
        } else if (type == double.class) {
            return "0d";
        } else if (type == float.class) {
            return "0f";
        } else if (type == int.class) {
            return "0";
        } else if (type == long.class) {
            return "0L";
        } else if (type == short.class) {
            return "(short) 0";
        }
        return "null";
    }

    public static String format(Object o) {
        if (o == null) {
            return "null";
        }
        Class<?> type = o.getClass();
        if (type == Boolean.class || type == Integer.class) {
            return o.toString();
        } else if (type == Byte.class) {
            return "(byte) " + o;
        } else if (type == Character.class) {
            char c = (Character) o;
            return "(char) " + (int) c;
        } else if (type == Double.class) {
            return o + "d";
        } else if (type == Float.class) {
            return o + "f";
        } else if (type == Long.class) {
            return o + "L";
        } else if (type == Short.class) {
            return "(short) " + o;
        } else if (type == String.class) {
            return '"' + o.toString() + '"';
        }
        return null;
    }

    static int getModifier(String name) {
        Integer s = KEYWORDS.get(name);
        return s == null ? -1 : s.intValue();
    }

    public static void main(String[] args) {
        System.out.println(int[].class.getSimpleName());
    }

    static StringBuilder toStringg(StringBuilder sb, Object... objects) {
        for (Object s : objects) {
            if (s instanceof Class) {
                Class<?> cl = (Class<?>) s;
                sb.append(cl.getSimpleName());
                // if (cl.isArray()) {
                // for (int i = 0; i < cl.getModifiers(); i++) {
                //
                // }
                // }
            } else if (s instanceof Object[]) {
                toStringg(sb, (Object[]) s);
            } else {
                sb.append(s);
            }
        }
        return sb;
    }

    static String toStringg(Object... objects) {
        return toStringg(new StringBuilder(), objects).toString();
    }

    public static Object[] flatten(Object... array) {
        boolean needsFlattening = false;
        for (Object o : array) {
            if (o instanceof Object[]) {
                needsFlattening = true;
                break;
            }
        }
        if (needsFlattening) {
            List<Object> l = new ArrayList<>();
            unpackArray(l, array);
            array = l.toArray();
        }
        return array;
    }

    static void unpackArray(List<Object> dst, Object[] array) {
        for (Object o : array) {
            if (o instanceof Object[]) {
                unpackArray(dst, (Object[]) o);
            } else {
                dst.add(o);
            }
        }
    }

    @SafeVarargs
    public static <T> T[] addLast(T last, T... trailing) {
        T[] result = Arrays.copyOf(trailing, trailing.length + 1);
        result[trailing.length] = last;
        return result;
    }

    /**
     * Converts the specified primitive class to the corresponding Object based class. Or returns the specified class if
     * it is not a primitive class.
     *
     * @param type
     *            the class to convert
     * @return the converted class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> boxClass(Class<T> type) {
        // If critically we can put into a array to avoid what look like a lot of branch mispredictions
        if (type == boolean.class) {
            return (Class<T>) Boolean.class;
        } else if (type == byte.class) {
            return (Class<T>) Byte.class;
        } else if (type == char.class) {
            return (Class<T>) Character.class;
        } else if (type == double.class) {
            return (Class<T>) Double.class;
        } else if (type == float.class) {
            return (Class<T>) Float.class;
        } else if (type == int.class) {
            return (Class<T>) Integer.class;
        } else if (type == long.class) {
            return (Class<T>) Long.class;
        } else if (type == short.class) {
            return (Class<T>) Short.class;
        }
        return type;
    }

    /**
     * Converts the specified primitive wrapper class to the corresponding primitive class. Or returns the specified
     * class if it is not a primitive wrapper class.
     *
     * @param type
     *            the class to convert
     * @return the converted class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> unboxClass(Class<T> type) {
        if (type == Boolean.class) {
            return (Class<T>) boolean.class;
        } else if (type == Byte.class) {
            return (Class<T>) byte.class;
        } else if (type == Character.class) {
            return (Class<T>) char.class;
        } else if (type == Double.class) {
            return (Class<T>) double.class;
        } else if (type == Float.class) {
            return (Class<T>) float.class;
        } else if (type == Integer.class) {
            return (Class<T>) int.class;
        } else if (type == Long.class) {
            return (Class<T>) long.class;
        } else if (type == Short.class) {
            return (Class<T>) short.class;
        }
        return type;
    }

    /**
     * Returns a new string where the first letter of the specified string is capitalized.
     *
     * @param str
     *            the string to capitalize
     * @return the string to capitalize
     */
    public static String capitalizeFirstLetter(String str) {
        if (str.length() > 0) {
            return replaceCharAt(str, 0, Character.toUpperCase(str.charAt(0)));
        }
        return str;
    }

    /**
     * Returns a new string where the first letter of the specified string is capitalized.
     *
     * @param str
     *            the string to capitalize
     * @return the string to capitalize
     */
    public static String uncapitalizeFirstLetter(String str) {
        if (str.length() > 0) {
            return replaceCharAt(str, 0, Character.toLowerCase(str.charAt(0)));
        }
        return str;
    }

    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }
    //
    // /**
    // * Returns the contents of this compilation unit appended with line numbers for each line.
    // *
    // * @return the contents of this compilation with line numbers.
    // */
    // public static String toStringWithLinenumbers(List<String> lines) {
    // StringBuilder result = new StringBuilder();
    // int padding = ((int) Math.log10(lines.size())) + 1;
    // int lineNumber = 1;
    // for (String str : lines) {
    // result.append(String.format("%" + padding + "s", lineNumber++)).append(" ").append(str)
    // .append(StringUtil.UNIX_LINE_SEPARATOR);
    // }
    // return result.toString();
    // }

}
