/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.eval;

import iris.clojure.lang.ICommonLispFn;

/**
 * Preserve CommonLisp functions .
 */
public final class FuncDeref implements ICommonLispFn {
 
    private final Object obj;
    
    public FuncDeref(Object obj) {
        this.obj = obj;
    }


    @Override
    public Object getFunction() {
        return obj;
    }
    
    
}
