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
import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;

import iris.clojure.lang.Symbol;

import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.Namespace;

import java.util.ArrayList;
import org.apache.commons.beanutils.ConstructorUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is a subset of <b>Clojure</b>'s new .
 * 
 * Call Java using commons-beanutils .
 */
public class NewNewSpecial extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(NewNewSpecial.class);

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {

        ISeq form = (ISeq) arg1;
        LexicalScope env = (LexicalScope) arg2;
        ExampleEvaluator evtor = (ExampleEvaluator) arg3;

        form = form.next();
        
        Symbol sym = (Symbol) form.first();

        if (sym.getNamespace() != null) {
            throw new IllegalArgumentException("symbol's namespace must be null");
        }

        String clazzName = sym.getName();

        LOG.info("new className={}", clazzName);
        
        ArrayList<Object> list = new ArrayList<>();
        for (ISeq seq = form.next(); seq != RT.EOL; seq = seq.next() ) {
            
            Object first = seq.first();
            Object obj = evtor.eval(first, env);
            list.add(obj);
        }
        int size = list.size();
        Object[] args = new Object[size];
    //    Class<?>[] params = new Class<?>[size];
        for (int i = 0; i < size; i++) {
            Object obj = list.get(i);
            args[i] = obj;
     //       params[i] = (obj != null) ? obj.getClass() : Object.class;
        }
        
        
        Namespace ns =  (Namespace)  CljCompiler.CURRENT_NS.deref();
        
        Object varOrClass = ns.getMapping(sym);
        
        if (varOrClass == null) {
            throw new IllegalArgumentException("Class not found " + clazzName);
            
        } else if (varOrClass instanceof Class<?> ) {
            Class<?> clazz = (Class<?>) varOrClass;
        
            Object instance = null;
            try {
                instance = ConstructorUtils.invokeConstructor(clazz, args);
            } catch (Exception ex) {
                throw new RuntimeException("Can't create '" + clazz.getName() + "'", ex  );
            }
            return instance;
        } else {
            throw new IllegalArgumentException("Class not found " + clazzName);
            
        }
        
        
    }

}
