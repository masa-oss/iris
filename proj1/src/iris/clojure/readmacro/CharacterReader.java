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
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.Util;

/**
 *
 */
public class CharacterReader extends AFun {

    @Override
    public Object invoke(Object reader, Object backslash, Object opts, Object pendingForms,
            IClojureReader cr) {

        PushbackReader r = (PushbackReader) reader;
        int ch = cr.read1(r);
        if (ch == -1) {
            throw Util.runtimeException("EOF while reading character");
        }
        String token = cr.readToken(r, (char) ch);
        if (token.length() == 1) {
            return Character.valueOf(token.charAt(0));
        } else if (token.equals("newline")) {
            return '\n';
        } else if (token.equals("space")) {
            return ' ';
        } else if (token.equals("tab")) {
            return '\t';
        } else if (token.equals("backspace")) {
            return '\b';
        } else if (token.equals("formfeed")) {
            return '\f';
        } else if (token.equals("return")) {
            return '\r';
        } else if (token.startsWith("u")) {
            char c = (char) cr.readUnicodeChar(token, 1, 4, 16);
            if (c >= '\uD800' && c <= '\uDFFF') // surrogate code unit?
            {
                throw Util.runtimeException("Invalid character constant: \\u" + Integer.toString(c, 16));
            }
            return c;
        } else if (token.startsWith("o")) {
            int len = token.length() - 1;
            if (len > 3) {
                throw Util.runtimeException("Invalid octal escape sequence length: " + len);
            }
            int uc = cr.readUnicodeChar(token, 1, len, 8);
            if (uc > 0377) {
                throw Util.runtimeException("Octal escape sequence must be in range [0, 377].");
            }
            return (char) uc;
        }
        throw Util.runtimeException("Unsupported character: \\" + token);
    }
}
