/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */

package iris.clojure.readmacro;

import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.ResolverEx;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.Util;
// import static iris.clojure.nsvar.CljCompiler.specials;

import java.io.PushbackReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class SymbolicValueReader extends AFun {
    
    private static final Logger LOG = LoggerFactory.getLogger(SymbolicValueReader.class);
    

    @Override
    public Object invoke(Object reader, Object hash, Object opts, final Object pendingForms,
            IClojureReader cr) {

        PushbackReader r = (PushbackReader) reader;
        Object o = cr.read(r, true, null, true, opts, cr.ensurePending(pendingForms));

        if (!(o instanceof Symbol)) {
            throw Util.runtimeException("Invalid token: ##" + o);
        }
        
        LOG.info("38) {}" ,o);
/*
        if (!(specials.containsKey(o))) {
        throw Util.runtimeException("Unknown symbolic value: ##" + o);
        }
        return specials.valAt(o);
         */
        

        ResolverEx compilerResolver = cr.getCompilerResolver();
        
        Object ret = compilerResolver.specialsValAt(o, null);
        if (ret == null) {
            throw Util.runtimeException("Unknown symbolic value: ##" + o);
        }
        return ret;
    }

}
