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

import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.Symbol;

/**
 * Interface of Namespace.
 * Extracted an interface for JUnit test code.
 */
public interface INamespace {

    // 追加メソッド
    void addAlias(Object key, Object val);

    Var findInternedVar(Symbol name);

    IPersistentMap getAliases();

    // Class か Varを取り出す
    Object getMapping(Symbol name);

    IPersistentMap getMappings();

    String getName();

    Class importClass(Symbol sym, Class<?> c);

    Class importClass(Class<?> c);

    Var intern(Symbol sym);

    // 追加メソッド
    Var internVar(Symbol sym, Var var);
    
}
