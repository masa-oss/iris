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

import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Var;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class Globals {

    private static final Logger LOG = LoggerFactory.getLogger(Globals.class);

    // Read-Eval-Print-loop
    public static Var HISTORY1;
    public static Var HISTORY2;
    public static Var HISTORY3;

    public static OutputStreamWriter writer;

    static {
        PrintStream out = System.out;
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("UnsupportedEncodingException", ex);
        }
    }

    // ================================
    // Common Lisp's   (setf (get sym) value) emulation .
    static HashMap<Symbol, Object> PROP_EMU = new HashMap<>();

    public static void putProp(Symbol sym, Symbol propName, Object value) {

        String ns = sym.getNamespace();
        if (ns == null) {
            throw new EvaluatorException("Namespace must be not null : " + sym.getStringForPrint());
        }

        Map<Symbol, Object> propMap = (Map<Symbol, Object>) PROP_EMU.get(sym);

        if (propMap == null) {

            HashMap<Symbol, Object> newMap = new HashMap<>();

            PROP_EMU.put(sym, newMap);
            propMap = newMap;
        }

        propMap.put(propName, value);
    }

    public static Object getProp(Symbol sym, Symbol propName) {

        String ns = sym.getNamespace();
        if (ns == null) {
            throw new EvaluatorException("Namespace must be not null : " + sym.getStringForPrint());
        }
        Map<Symbol, Object> propMap = (Map<Symbol, Object>) PROP_EMU.get(sym);

        if (propMap == null) {
            return null;
        }

        return propMap.get(propName);
    }
    
    // -------------
    
    public static ExampleEvaluator getEvaluator() {
        
        return new ExampleEvaluatorImpl(NS_UTIL);
    }
    
    static StaticNamespaceImpl NS_UTIL = new StaticNamespaceImpl();

    
    // -------------

    public synchronized static int getNextInt() {
        return next++;
    }
    
    private static int next = 10000;
    
    
    // disable construct
    private Globals() {
    }

}
