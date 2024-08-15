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

import iris.clojure.lang.IClojureReader;

/**
 * This interpreter does not support EvalReader .
 * 
 * This interpreter has a policy of not calling eval during read.
 *
 */
public class EvalReader extends AFun {

    @Override
    public Object invoke(Object reader, Object eq, Object opts, Object pendingForms,
            IClojureReader cr) {

        throw new IllegalStateException("not impl yet");
    }
}
