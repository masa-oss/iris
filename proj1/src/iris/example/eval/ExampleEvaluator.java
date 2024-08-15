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

import iris.clojure.lang.ISeq;
import iris.clojure.lang.Symbol;

/**
 * Lisp interpreter (eval) that behaves like Common Lisp .
 *
 *
 *
 *
 */
public interface ExampleEvaluator {

    Object eval(Object sexp, LexicalScope env);

    Object apply(final Object first, final ISeq args);

    Object macroexpand1(final ISeq sexp);

    Object resolve(Symbol sym, LexicalScope env);

}
