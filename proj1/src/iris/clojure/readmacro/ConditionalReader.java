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

import iris.clojure.lang.IClojureReader;

import iris.clojure.lang.LineNumberingPushbackReader;
import iris.clojure.lang.ReaderException;
import java.io.PushbackReader;

/**
 * This interpreter does not support ConditionalReader .
 *
 * This interpreter has a policy of not calling eval during read.
 *
 */
public class ConditionalReader extends AFun {

    //  final static private Object READ_STARTED = new Object();
    //  final static public Keyword DEFAULT_FEATURE = Keyword.intern(null, "default");
    @Override
    public Object invoke(Object reader, Object eq, Object opts, Object pendingForms,
            IClojureReader cr) {

        int line = -1;
        int column = -1;
        PushbackReader r = (PushbackReader) reader;

        if (r instanceof LineNumberingPushbackReader) {
            line = ((LineNumberingPushbackReader) r).getLineNumber();
            column = ((LineNumberingPushbackReader) r).getColumnNumber();
        }

        throw new ReaderException("ConditionalReader( #? ), not impl yet", line, column, null);

    }

}
