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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is a subset of <b>clojure</b>'s <b>try</b> .
 *
 */
public class TrySpecial extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(TrySpecial.class);

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {

        ISeq form = (ISeq) arg1;
        LexicalScope env = (LexicalScope) arg2;
        ExampleEvaluator evtor = (ExampleEvaluator) arg3;

        form = form.next();
        ISeq seq = form;

        Object ret = null;
        Exception exception = null;
        try {
            for (; seq != RT.EOL; seq = seq.next()) {
                Object first = seq.first();
                if (isCatch(first) || isFinally(first)) {
                    break;
                }
                ret = evtor.eval(first, env);
            }

        } catch (Exception ex) {
            LOG.error("51) -----------  {}", ex.getClass().getName());
            exception = ex;
        }

        if (exception != null) {

            for (seq = seq.next(); seq != RT.EOL; seq = seq.next()) {

                Object first = seq.first();

                if (isFinally(first)) {
                    break;
                }
                if (!isCatch(first)) {
                    continue;
                }


                LOG.info("69) {}", CljCompiler.printString(first));

                if (!(first instanceof ISeq)) {
                    ETE.throwException("syntax error near try", first, null);
                }
                ISeq seq2 = (ISeq) first;
                seq2.first(); // catch
                seq2 = seq2.next();
                Object exceptionClazzName = seq2.first();  // Symbol of RuntimeException , IOException ...
                seq2 = seq2.next();

                LOG.info("80) {}", CljCompiler.printString(exceptionClazzName));

                // Symbol -> Class
                Object exceptionClazz = evtor.resolve((Symbol) exceptionClazzName, env);

                if (exceptionClazz instanceof Class<?>) {

                    Class<?> clazz = (Class<?>) exceptionClazz;

                    LOG.info("84) {}", clazz.getName());

                    boolean b = isMatch(clazz, exception);

                    LOG.info("isMatch={}", b);

                    ret = execOneCatch(seq2, env, evtor, exception);
                    break;
                }
            }
        }

        LOG.info("101) {}", CljCompiler.printString(seq));
        Object car = seq.first();

        while (! isFinally(car)) {
            
            seq = seq.next();
            if (seq == RT.EOL) {
                return ret;
            }
            car = seq.first();
        }

        if (isFinally(car)) {
            
            ISeq fbody = (ISeq)seq.first();

            for (fbody = fbody.next(); fbody != RT.EOL; fbody = fbody.next()) {

                Object finallyExp = fbody.first();
                LOG.info("118) {}", CljCompiler.printString(finallyExp));

                evtor.eval(finallyExp, env);
            }
        }

        return ret;
    }

    boolean isMatch(Class<?> aClass, Object b) {

        if (aClass == null) {
            throw new NullPointerException();
        }
        if (b == null) {
            throw new NullPointerException();
        }
        Class<?> bClass = b.getClass();

        return aClass.isAssignableFrom(bClass);
    }

    Object execOneCatch(ISeq seq, LexicalScope oldEnv, ExampleEvaluator evtor, Exception exception) {

        Object exVar = seq.first();

        LexicalScope.Builder buil = new LexicalScope.Builder(oldEnv);
        buil.addBinding((Symbol) exVar, exception);
        LexicalScope newScope = buil.build();

        Object ret = null;
        for (seq = seq.next(); seq != RT.EOL; seq = seq.next()) {
            Object first = seq.first();

            LOG.info("137) {}", CljCompiler.printString(first));

            ret = evtor.eval(first, newScope);

            LOG.info("140) {}", CljCompiler.printString(ret));
        }
        return ret;

    }

    
    // (car obj) == catch 
    
    boolean isCatch(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ISeq) {
            ISeq seq = (ISeq) obj;
            Object first = seq.first();
            if (CATCH.equals(first)) {
                return true;
            }

        }
        return false;
    }
    
    // (car obj) == finally

    boolean isFinally(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ISeq) {
            ISeq seq = (ISeq) obj;
            Object first = seq.first();
            if (FINALLY.equals(first)) {
                return true;
            }

        }
        return false;
    }
    static Symbol CATCH = Symbol.intern(null, "catch");

    static Symbol FINALLY = Symbol.intern(null, "finally");
}
