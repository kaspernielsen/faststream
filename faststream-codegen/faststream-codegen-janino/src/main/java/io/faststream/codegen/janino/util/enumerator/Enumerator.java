/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package io.faststream.codegen.janino.util.enumerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that represents an enumerated value. Its main features are its {@link #toString()} and
 * {@link #fromString(String, Class)} method, which map names to values and vice versa.
 * <p>
 * To use this class, derive from it and define one or more <code>public static final</code> fields, as follows:
 * 
 * <pre>
 * public final class Suit extends Enumerator {
 * 
 *     // Exactly N instances of &quot;Suit&quot; exist to represent the N possible values.
 *     public static final Suit CLUBS = new Suit(&quot;clubs&quot;);
 *     public static final Suit DIAMONDS = new Suit(&quot;diamonds&quot;);
 *     public static final Suit HEARTS = new Suit(&quot;hearts&quot;);
 *     public static final Suit SPADES = new Suit(&quot;spades&quot;);
 * 
 *     // Optional, if you want to use EumeratorSet arithmetics.
 *     public static final EnumeratorSet NONE = new EnumeratorSet(Suit.class).setName(&quot;none&quot;);
 *     public static final EnumeratorSet ALL = new EnumeratorSet(Suit.class, true).setName(&quot;all&quot;);
 * 
 *     // These MUST be declared exactly like this:
 *     private Suit(String name) {
 *         super(name);
 *     }
 * 
 *     public static Suit fromString(String name) throws EnumeratorFormatException {
 *         return (Suit) Enumerator.fromString(name, Suit.class);
 *     }
 * }
 * </pre>
 * 
 * @see <a href="http://java.sun.com/developer/Books/effectivejava/Chapter5.pdf">Effective Java, Item 21</a>
 */
public abstract class Enumerator {
    /* package */final String name;

    /**
     * Class enumeratorClass => Map: String name => Enumerator
     */
    private static final Map instances = Collections.synchronizedMap(new HashMap());

    /**
     * Initialize the enumerator to the given value.
     */
    protected Enumerator(String name) {
        if (name == null)
            throw new NullPointerException();
        this.name = name;

        Enumerator.getInstances(this.getClass()).put(name, this);
    }

    /**
     * Equality is reference identity.
     */
    public final boolean equals(Object that) {
        return this == that;
    }

    /**
     * Enforce {@link Object}'s notion of {@link Object#hashCode()}.
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a mapping of name to Enumerator for the given enumeratorClass.
     */
    /* package */static Map getInstances(Class enumeratorClass) {
        Map m = (Map) Enumerator.instances.get(enumeratorClass);
        if (m != null)
            return m;

        // The map need not be synchronized because it is modified only during initialization
        // of the Enumerator.
        m = new HashMap();
        Enumerator.instances.put(enumeratorClass, m);
        return m;
    }

    /**
     * Initialize an {@link Enumerator} from a string.
     * <p>
     * The given string is converted into a value by looking at all instances of the given type created so far.
     * <p>
     * Derived classes should invoke this method as follows:
     * 
     * <pre>
     * public class Suit extends Enumerator {
     *     ...
     *     public static Suit fromString(String name) throws EnumeratorFormatException {
     *         return (Suit) Enumerator.fromString(name, Suit.class);
     *     }
     * }
     * </pre>
     * 
     * @throws EnumeratorFormatException
     *             if the string cannot be identified
     */
    protected static final Enumerator fromString(String name, Class enumeratorClass) throws EnumeratorFormatException {
        Enumerator value = (Enumerator) Enumerator.getInstances(enumeratorClass).get(name);
        if (value == null)
            throw new EnumeratorFormatException(name);
        return value;
    }

    /**
     * Returns the <code>name</code> passed to {@link #Enumerator(String)}.
     */
    public String toString() {
        return this.name;
    }
}
