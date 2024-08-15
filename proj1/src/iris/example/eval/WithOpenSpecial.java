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

import iris.clojure.lang.AFn;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;
import iris.clojure.lang.Seqable;
import iris.clojure.lang.Symbol;
import java.io.Closeable;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class WithOpenSpecial extends AFn {
    
    private static final Logger LOG = LoggerFactory.getLogger(WithOpenSpecial.class);
    
    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {
        
        IPersistentList form = (IPersistentList) arg1;
        LexicalScope oldScope = (LexicalScope) arg2;
        ExampleEvaluator evtor = (ExampleEvaluator) arg3;
        
        ISeq seq = form.seq();
        
        seq = seq.next();
        Object obj = seq.first();
        
        ISeq varAndForm = toSeq(obj);
        Object var = varAndForm.first();
        varAndForm = varAndForm.next();
        Object form2 = varAndForm.first();
        
        Object closable = evtor.eval(form2, oldScope);
        
        LOG.info("42) closable={}", closable);
        
        if (closable == null) {
            return Boolean.FALSE;
        }
        
        LexicalScope.Builder buil = new LexicalScope.Builder(oldScope);
        buil.addBinding((Symbol) var, closable);
        LexicalScope newEnv = buil.build();
        
        
        
        Object ret = RT.EOL;
        try {
            for (seq = seq.next(); seq != RT.EOL; seq = seq.next()) {
                Object toEval = seq.first();
                ret = evtor.eval(toEval, newEnv);
            }
        } finally {
            if (closable instanceof Closeable) {
                
                Closeable cl = (Closeable) closable;
                
                try {
                    cl.close();
                    LOG.info("-------- closed");
                } catch (IOException e) {
                    LOG.error("IOException", e);
                }
            } else {
                LOG.warn("Object is not instanceof Closeable");
            }
        }
        
        return ret;
    }
    
    ISeq toSeq(Object o) {
        
        if (o instanceof ISeq) {
            return (ISeq) o;
        } else if (o instanceof Seqable) {
            Seqable sq = (Seqable) o;
            return sq.seq();
            
        } else {
            throw new RuntimeException("syntax error near with-open");
        }
        
    }
    
}
