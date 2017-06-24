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

package io.faststream.codegen.janino.compiler;

import java.io.File;
import java.security.ProtectionDomain;

/**
 * A {@link ClassLoader} that, unlike usual {@link ClassLoader}s, does not load byte code, but reads Java&trade; source
 * code and then scans, parses, compiles and loads it into the virtual machine.
 * <p>
 * As with any {@link ClassLoader}, it is not possible to "update" classes after they've been loaded. The way to achieve
 * this is to give up on the {@link AbstractJavaSourceClassLoader} and create a new one.
 */
public abstract class AbstractJavaSourceClassLoader extends ClassLoader {

    protected ProtectionDomainFactory optionalProtectionDomainFactory = null;

    public AbstractJavaSourceClassLoader() {}

    public AbstractJavaSourceClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    /**
     * @param sourcePath
     *            The sequence of directories to search for Java&trade; source files
     */
    public abstract void setSourcePath(File[] sourcePath);

    /**
     * @param optionalCharacterEncoding
     *            if {@code null}, use platform default encoding
     */
    public abstract void setSourceFileCharacterEncoding(String optionalCharacterEncoding);

    /**
     * @param lines
     *            Whether line number debugging information should be generated
     * @param vars
     *            Whether variables debugging information should be generated
     * @param source
     *            Whether source file debugging information should be generated
     */
    public abstract void setDebuggingInfo(boolean lines, boolean vars, boolean source);

    /**
     * @see ClassLoader#defineClass(String, byte[], int, int, ProtectionDomain)
     */
    public final void setProtectionDomainFactory(ProtectionDomainFactory optionalProtectionDomainFactory) {
        this.optionalProtectionDomainFactory = optionalProtectionDomainFactory;
    }

    /**
     * @see AbstractJavaSourceClassLoader#setProtectionDomainFactory
     */
    public interface ProtectionDomainFactory {

        /**
         * @param sourceResourceName
         *            E.g. 'pkg1/pkg2/Outer.java'
         */
        ProtectionDomain getProtectionDomain(String sourceResourceName);
    }

}
