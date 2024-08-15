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
import iris.clojure.lang.LispReader;
import iris.clojure.lang.RT;

/**
 *
 * @author Hemmi
 */
public class VarReader extends AFun {

    @Override
    public Object invoke(Object reader, Object quote, Object opts, Object pendingForms,
            IClojureReader cr) {

        PushbackReader r = (PushbackReader) reader;
        Object o = cr.read(r, true, null, true, opts, cr.ensurePending(pendingForms));
        return RT.list(LispReader.THE_VAR, o);
    }
}
