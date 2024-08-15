/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.nsvar;

import iris.clojure.lang.Symbol;

/**
 * Namespace and CljCompiler's static methods were extracted to this interface.
 */
public interface IStaticCljNs {

    // access to Namespace
    INamespace findOrCreate(Symbol name);

 //   INamespace findOrCreate(Symbol name, boolean init);

    INamespace find(Symbol name);

    // access to CljCompiler
    void addLoadedLibs(Symbol sym);

    INamespace getCurrentNs();

    String printString(Object x);

    boolean isSpecialEx(Object sym);

    
    
}
