/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
/**
 *   Author: Masahito Hemmi
 */
package iris.clojure.lang;

import java.io.PushbackReader;
import java.io.Reader;
import java.util.List;

/**
 * This interface is part of <b>clojure.lang.LispReader.java</b> .
 * This was created when static methods were deprecated.
 */
public interface IClojureReader {

    Object ensurePending(Object pendingForms);

    boolean isWhitespace(int ch);


    Object read(PushbackReader r, boolean eofIsError, Object eofValue, boolean isRecursive, Object opts, Object pendingForms);

    int read1(Reader r);

    List<Object> readDelimitedList(char delim, PushbackReader r, boolean isRecursive, Object opts, Object pendingForms);


    void unread(PushbackReader r, int ch);

    String readToken(PushbackReader r, char initch);

    int readUnicodeChar(String token, int offset, int length, int base);

    int readUnicodeChar(PushbackReader r, int initch, int base, int length, boolean exact);

    Resolver getReaderResolver();

    
    
    // For SyntaxQuoteReaderEx
    ResolverEx getCompilerResolver();
    

    IObjectFactory getObjectFactory();

}
