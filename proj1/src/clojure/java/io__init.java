/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package clojure.java;

import iris.clojure.lang.Symbol;
import static iris.clojure.nsvar.CljCompiler.defun;
import iris.clojure.nsvar.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 【参考】
 * 
 * https://qiita.com/hatappo/items/e292bd2132a89cfd6761
 * 
 * Clojureの入出力の関数とIOFactoryプロトコル
 *
 */
public class io__init {
    
    private static final Logger LOG = LoggerFactory.getLogger(io__init.class);

    public static void load() {

        Symbol ns1 = Symbol.intern(null, "clojure.java.io");  //----------- CHANGE
     //   Namespace ns = Namespace.findOrCreate(ns1, false);
        Namespace ns = Namespace.findOrCreate(ns1);

        defun(ns, "reader", new ReaderFn());

        defun(ns, "writer", new WriterFn());

    }
    

    static {

        LOG.info("Static method start ...  `clojure.java.io`");

        try {
            load();
        } catch (Exception ex) {
            throw ex;
        }

    }
}
