/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */
package iris.clojure.readmacro;

import java.io.PushbackReader;
import java.io.Reader;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.LineNumberingPushbackReader;
import iris.clojure.lang.ReaderException;
// import iris.clojure.lang.Util;

/**
 *
 * Renamed from StringReader to StringReaderEx
 */
public class StringReaderEx extends AFun {

    @Override
    public Object invoke(Object reader, Object doublequote, Object opts, Object pendingForms,
                      IClojureReader cr ) {
        
        int line = -1;
        int column = -1;
        
        StringBuilder sb = new StringBuilder();
        Reader r = (Reader) reader;

        for (int ch = cr.read1(r); ch != '"'; ch = cr.read1(r)) {
            
            if (r instanceof LineNumberingPushbackReader) {
                line = ((LineNumberingPushbackReader) r).getLineNumber();
                column = ((LineNumberingPushbackReader) r).getColumnNumber();
            }
            
            
            if (ch == -1) {
                throw new ReaderException("EOF while reading string", line, column, null);
                // throw Util.runtimeException("EOF while reading string");
            }
            if (ch == '\\') //escape
            {
                ch = cr.read1(r);
                if (ch == -1) {
                    throw new ReaderException("EOF while reading string", line, column, null);
                    // throw Util.runtimeException("EOF while reading string");
                }
                switch (ch) {
                    case 't':
                        ch = '\t';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case '\\':
                        break;
                    case '"':
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'u': {
                        ch = cr.read1(r);
                        if (Character.digit(ch, 16) == -1) {
                            throw new ReaderException("Invalid unicode escape: \\u" + (char) ch, line, column, null);
                           // throw Util.runtimeException("Invalid unicode escape: \\u" + (char) ch);
                        }
                        ch = cr.readUnicodeChar((PushbackReader) r, ch, 16, 4, true);
                        break;
                    }
                    default: {
                        if (Character.isDigit(ch)) {
                            ch = cr.readUnicodeChar((PushbackReader) r, ch, 8, 3, false);
                            if (ch > 0377) {
                                throw new ReaderException("Octal escape sequence must be in range [0, 377].", line, column, null);
                              //  throw Util.runtimeException("Octal escape sequence must be in range [0, 377].");
                            }
                        } else {
                            throw new ReaderException("Unsupported escape character: \\" + (char) ch, line, column, null);
                          //  throw Util.runtimeException("Unsupported escape character: \\" + (char) ch);
                        }
                    }
                }
            }
            sb.append((char) ch);
        }
        return sb.toString();
    }

}
