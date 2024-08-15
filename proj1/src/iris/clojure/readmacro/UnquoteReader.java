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
import iris.clojure.lang.RT;
import iris.clojure.lang.Util;

/**
 *
 * @author Hemmi
 */
public class UnquoteReader extends AFun {

    @Override
    public Object invoke(Object reader, Object comma, Object opts, Object pendingForms,
            IClojureReader cr) {
        PushbackReader r = (PushbackReader) reader;
        int ch = cr.read1(r);
        if (ch == -1) {
            throw Util.runtimeException("EOF while reading character");
        }
        pendingForms = cr.ensurePending(pendingForms);
        if (ch == '@') {
            Object o = cr.read(r, true, null, true, opts, pendingForms);
            return RT.list(SyntaxQuoteReaderEx.UNQUOTE_SPLICING, o);
        } else {
            cr.unread(r, ch);
            Object o = cr.read(r, true, null, true, opts, pendingForms);
            return RT.list(SyntaxQuoteReaderEx.UNQUOTE, o);
        }
    }
}
