/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package jdbc;

import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Namespace;

import static iris.clojure.nsvar.CljCompiler.defun;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * https://clojure.github.io/java.jdbc/#clojure.java.jdbc/get-connection
 *
 * @author hemmi
 */
public class alpha__init {
    
    private static final Logger LOG = LoggerFactory.getLogger(alpha__init.class);
    
    public static void load() {

        Symbol ns1 = Symbol.intern(null, "jdbc.alpha");  //----------- CHANGE
      //  Namespace ns = Namespace.findOrCreate(ns1, false);
        Namespace ns = Namespace.findOrCreate(ns1);
        

        defun(ns, "get-connection", new ConnectFn());

        defun(ns, "query", new QueryFn());

        
    }
    
    static {

        LOG.info("static method start ...");
        
//        try {
            load();
  //      } catch (Exception ex) {
  //          throw ex;
   //     }
        
    }    
    
}
