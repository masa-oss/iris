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
 * This class is the super class of each ReadMacro .
 * 
 * It was introduced when the inner class was separated from  LispReader.java .
 * 
 *   Author: Masahito Hemmi
 */
package iris.clojure.readmacro;

import iris.clojure.lang.ArityException;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.IReadMacro;

public abstract class AFun implements IReadMacro {

    /*
    public Object invoke(Object arg1, Object arg2) {
        return throwArity(2);
    }
*/
    
    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, IClojureReader arg5) {
        return throwArity(5);
    }


    public Object throwArity(int n) {
        String name = getClass().getName();
        throw new ArityException(n, name);
    }
}
