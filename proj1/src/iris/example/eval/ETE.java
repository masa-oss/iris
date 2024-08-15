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

import iris.clojure.lang.IObj;
import iris.clojure.lang.IPersistentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to create the EvaluatorException .
 */
public class ETE {

    private static final Logger LOG = LoggerFactory.getLogger(ETE.class);

    public static void throwException(String message, Object errorObj, Object collection) {

        if (message == null) {
            message = "Exception in evalation phase.";
        }

        if (errorObj == null) {
            throw new EvaluatorException(message);
        }

        if (errorObj instanceof IObj) {
            IObj iobj = (IObj) errorObj;

            IPersistentMap meta = iobj.meta();
            throw new EvaluatorException(meta, message, null);
        }

        if (collection instanceof IObj) {
            IObj iobj = (IObj) collection;

            IPersistentMap meta = iobj.meta();
            throw new EvaluatorException(meta, message, null);
        }
        
        throw new EvaluatorException(null, message, null);
    }
    
    public static void throwException(String message, Object errorObj, Object collection, Throwable th) {

        if (message == null) {
            message = "Exception in evalation phase.";
        }

        if (errorObj == null) {
            throw new EvaluatorException(message, th);
        }

        if (errorObj instanceof IObj) {
            IObj iobj = (IObj) errorObj;

            IPersistentMap meta = iobj.meta();
            throw new EvaluatorException(meta, message, null);
        }

        if (collection instanceof IObj) {
            IObj iobj = (IObj) collection;

            IPersistentMap meta = iobj.meta();
            throw new EvaluatorException(meta, message, null);
        }
        
        throw new EvaluatorException(null, message, null);
    }

    
    
    
    /* ==>  Globals
    public static Writer writer;

    static {

        PrintStream out = System.out;
        OutputStreamWriter w = null;
        try {
            w = new OutputStreamWriter(out, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("UnsupportedEncodingException", ex);
        }
        writer = w;
    }
*/
}
