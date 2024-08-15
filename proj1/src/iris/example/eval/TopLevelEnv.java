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

import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.Namespace;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Var;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If a variable is not found in LexicalScope, look for its value in current namespace .
 */
public final class TopLevelEnv {

    private static final Logger LOG = LoggerFactory.getLogger(TopLevelEnv.class);

    // disable construct
    private TopLevelEnv() {
    }

    public static Object getVariableValue(Symbol sym) {

        Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();

        Var findInternedVar = ns.findInternedVar(sym);
        if (findInternedVar != null) {
            return findInternedVar.deref();
        }
        return null;
    }

    public static Object putVariableValue(Symbol sym, Object val) {

        if (sym.getNamespace() != null) {
            throw new RuntimeException("getNamespace() != null :  " + sym.getName());
        }

        Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();

        Var findInternedVar = ns.findInternedVar(sym);
        if (findInternedVar != null) {
            throw new RuntimeException("already exists  :  " + sym.getName());

        }

        Var v = ns.intern(sym);

        v.bindRoot(val);

        return sym;
    }

}
