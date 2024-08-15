/*
 * Copyright (c) 1996, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package iris.clojure.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Stopped incrementing lineNumber when detecting EOF .
 * 
 *  This file form <i>java.io.LineNumberReader</i> .
 */
public class LineNumberReader2 extends BufferedReader  {
    
    /** Previous character types */
    private static final int NONE = 0; // no previous character
    private static final int CHAR = 1; // non-line terminator
    private static final int EOL = 2; // line terminator
    private static final int EOF  = 3; // end-of-file

    /** The previous character type */
    private int prevChar = NONE;

    /** The current line number */
    private int lineNumber = 0;

    /** The line number of the mark, if any */
    private int markedLineNumber; // Defaults to 0

    /** If the next character is a line feed, skip it */
    private boolean skipLF;

    /** The skipLF flag when the mark was set */
    private boolean markedSkipLF;

    /**
     * Create a new line-numbering reader, using the default input-buffer
     * size.
     *
     * @param  in
     *         A Reader object to provide the underlying stream
     */
    public LineNumberReader2(Reader in) {
        super(in);
    }
    public int getLineNumber() {
        return lineNumber;
    }
    @SuppressWarnings("fallthrough")
    @Override
    public int read() throws IOException {
        synchronized (lock) {
            int c = super.read();
            if (skipLF) {
                if (c == '\n')
                    c = super.read();
                skipLF = false;
            }
            switch (c) {
            case '\r':
                skipLF = true;
            case '\n':          /* Fall through */
                lineNumber++;
                prevChar = EOL;
                return '\n';
            case -1:
                /*
                if (prevChar == CHAR)
                    lineNumber++; */
                prevChar = EOF;
                break;
            default:
                prevChar = CHAR;
                break;
            }
            return c;
        }
    }

    
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }    

    /** Maximum skip-buffer size */
    private static final int maxSkipBufferSize = 8192;

    /** Skip buffer, null until allocated */
    private char skipBuffer[] = null;

    @Override
    public long skip(long n) throws IOException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
}
