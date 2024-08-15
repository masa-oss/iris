/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/**
 *   Author: Masahito Hemmi
 **/

package iris.clojure.readmacro;

import java.io.Reader;
import java.util.regex.Pattern;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.Util;
/**
 * This class is part of clojure.lang.LispReader.java .
 *
 */

public class RegexReader extends AFun {

    @Override
    public Object invoke(Object reader, Object doublequote, Object opts, Object pendingForms,
            IClojureReader cr) {

        StringBuilder sb = new StringBuilder();
        Reader r = (Reader) reader;
        for (int ch = cr.read1(r); ch != '"'; ch = cr.read1(r)) {
            if (ch == -1) {
                throw Util.runtimeException("EOF while reading regex");
            }
            sb.append((char) ch);
            if (ch == '\\') { //escape

                ch = cr.read1(r);
                if (ch == -1) {
                    throw Util.runtimeException("EOF while reading regex");
                }
                sb.append((char) ch);
            }
        }
        Pattern compiled = Pattern.compile(sb.toString());
        return compiled;
    }
}
