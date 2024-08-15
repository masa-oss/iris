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

/**
 * 
 * #java.lang.String["foo"]
 * 
 * 
 * DispatchReaderの中から、呼ばれる事がある // コンストラクタ？
 *
 */
public class CtorReader extends AFun {

    @Override
    public Object invoke(Object reader, Object firstChar, Object opts, Object pendingForms,
            IClojureReader cr) {

        PushbackReader r = (PushbackReader) reader;

        pendingForms = cr.ensurePending(pendingForms);
        Object name = cr.read(r, true, null, false, opts, pendingForms);

        return name;
    }
}
