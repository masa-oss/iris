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
 * This interpreter does not support anonymous functions, please use lambda .
 */
public class FnReader extends AFun {

    @Override
    public Object invoke(Object reader, Object lparen, Object opts, Object pendingForms,
            IClojureReader cr) {

        throw new IllegalStateException("#()    not impl yet");

        /*
            try {
                Var.pushThreadBindings(
                        RT.map(ARG_ENV, PersistentTreeMap.EMPTY));
                unread(r, '(');
                Object form = read(r, true, null, true, opts, ensurePending(pendingForms));

                PersistentVector args = PersistentVector.EMPTY;
                PersistentTreeMap argsyms = (PersistentTreeMap) ARG_ENV.deref();
                ISeq rargs = argsyms.rseq();
                if (rargs != null) {
                    int higharg = (Integer) ((Map.Entry) rargs.first()).getKey();
                    if (higharg > 0) {
                        for (int i = 1; i <= higharg; ++i) {
                            Object sym = argsyms.valAt(i);
                            if (sym == null) {
                                sym = garg(i);
                            }
                            args = args.cons(sym);
                        }
                    }
                    Object restsym = argsyms.valAt(-1);
                    if (restsym != null) {
                        args = args.cons(Compiler._AMP_);
                        args = args.cons(restsym);
                    }
                }
                return RT.list(Compiler.FN, args, form);
            } finally {
                Var.popThreadBindings();
            }*/
    }
}
