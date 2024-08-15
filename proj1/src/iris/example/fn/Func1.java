/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.fn;

import iris.clojure.lang.AFn;
import iris.clojure.lang.Cons;
import iris.clojure.lang.IObj;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Nil;
import iris.clojure.lang.Numbers;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.PersistentHashSet;
import iris.clojure.lang.PersistentList;
import iris.clojure.lang.PersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.lang.RTPrinter;
import iris.clojure.lang.Util;
import iris.example.eval.ETE;

import iris.example.eval.ExampleEvaluator;
import iris.example.eval.Globals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is just an outer frame for the inner class .
 *
 */
public class Func1 {

    private static final Logger LOG = LoggerFactory.getLogger(Func1.class);

    // Disable construct
    private Func1() {
    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s eq.
     *
     */
    public static class Eq extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (arg1 == arg2) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s eql.
     */
    public static class Eql extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (arg1 == null) {
                if (arg2 == null) {
                    return Boolean.TRUE;
                } else {

                    return Boolean.FALSE;

                }

            } else {
                if (arg1.equals(arg2)) {

                    return Boolean.TRUE;
                } else {

                    return Boolean.FALSE;
                }
            }
        }
    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s cons.
     */
    public static class MyCons extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            ISeq seq = (ISeq) arg2;
            return new Cons(arg1, seq);
        }
    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s funcall.
     */
    public static class Funcall extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return call(arg1, RT.EOL);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return call(arg1, new Cons(arg2, RT.EOL));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            return call(arg1, new Cons(arg2, new Cons(arg3, RT.EOL)));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            return call(arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4, RT.EOL))));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5) {

            return call(arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4,
                    new Cons(arg5, RT.EOL)))));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6) {

            return call(arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4,
                    new Cons(arg5, new Cons(arg6, RT.EOL))))));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6, Object arg7) {

            return call(arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4,
                    new Cons(arg5, new Cons(arg6, new Cons(arg7, RT.EOL)))))));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6, Object arg7, Object arg8) {

            return call(arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4,
                    new Cons(arg5, new Cons(arg6, new Cons(arg7, new Cons(arg8, RT.EOL))))))));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6, Object arg7, Object arg8,
                Object arg9) {

            return call(arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4,
                    new Cons(arg5, new Cons(arg6, new Cons(arg7, new Cons(arg8, new Cons(arg9, RT.EOL)))))))));
        }

        Object call(Object fun, ISeq list) {

            ExampleEvaluator evltor = Globals.getEvaluator();

            Object result = evltor.apply(fun, list);
            return result;
        }

    }

    /**
     * This function is a subset of clojure's <b>first</b>.
     */
    public static class First extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return RT.first(arg1);
        }
    }

    static Object car(Object arg1) {
        
        if (arg1 instanceof Nil) {
            return Nil.INSTANCE;
        }

        if (!(arg1 instanceof Cons)) {
            ETE.throwException("Argument must be a cons.", arg1, null);
        }
        Cons cons = (Cons) arg1;

        return cons.first();
    }

    /**
     * This function is a subset of Common Lisp's <b>car</b>.
     */
    public static class Car extends AFn {

        @Override
        public Object invoke(Object arg1) {
            return car(arg1);
        }
    }

    static Object cdr(Object arg1) {
        
        if (arg1 instanceof Nil) {
            return Nil.INSTANCE;
        }
        
        if (!(arg1 instanceof Cons)) {
            ETE.throwException("Argument must be a cons.", arg1, null);
        }
        Cons cons = (Cons) arg1;

        return cons.next();
    }

    /**
     * This function is a subset of Common Lisp's <b>cdr</b>.
     */
    public static class Cdr extends AFn {

        @Override
        public Object invoke(Object arg1) {
            return cdr(arg1);
        }
    }

    public static class Caar extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return car(car(arg1));
        }
    }

    public static class Cadr extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return car(cdr(arg1));
        }
    }

    public static class Cdar extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return cdr(car(arg1));
        }
    }


    public static class Cddr extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return cdr(cdr(arg1));
        }
    }

    
    
    public static class Caddr extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return car(cdr(cdr(arg1)));
        }
    }
    
    public static class Cdddr extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return cdr(cdr(cdr(arg1)));
        }
    }
    


    /**
     * This function is a subset of clojure's in-ns.
     */
    public static class InNs extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Symbol name = (Symbol) arg1;

            Namespace ns = Namespace.findOrCreate(name);

            LOG.warn("TODO: initialize namespace ....");

            Var curr = CljCompiler.CURRENT_NS;

            curr.bindRoot(ns);

            return ns;
        }
    }

    /**
     * This function is a subset of clojure's meta.
     */
    public static class Meta extends AFn {

        @Override
        public Object invoke(Object arg1) {

            IObj obj = (IObj) arg1;
            return obj.meta();

        }
    }

    /**
     * This function is a subset of clojure's +.
     */
    public static class MyAdd extends AFn {

        @Override
        public Object invoke(Object arg1) {
            return arg1;
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {
            return Numbers.add(arg1, arg2);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {
            return Numbers.add(Numbers.add(arg1, arg2), arg3);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {
            return Numbers.add(Numbers.add(Numbers.add(arg1, arg2), arg3), arg4);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
            return Numbers.add(Numbers.add(Numbers.add(Numbers.add(arg1, arg2), arg3), arg4), arg5);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
            return Numbers.add(Numbers.add(Numbers.add(Numbers.add(Numbers.add(arg1, arg2), arg3), arg4), arg5), arg6);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
            return Numbers.add(Numbers.add(Numbers.add(Numbers.add(Numbers.add(Numbers.add(arg1, arg2), arg3), arg4), arg5), arg6), arg7);
        }

    }

    /**
     * This function is a subset of clojure's concat.
     */
    public static class MyConcat extends AFn {

        private Object makeList(List<Object> list) {

            return PersistentList.create(list);
        }

        private void append(List<Object> list, ISeq from) {

            for (; from != RT.EOL; from = from.next()) {

                Object obj = from.first();
                list.add(obj);
            }
        }

        @Override
        public Object invoke(Object arg1) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            append(list, (IPersistentList) arg4);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            append(list, (IPersistentList) arg4);

            append(list, (IPersistentList) arg5);

            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            append(list, (IPersistentList) arg4);

            append(list, (IPersistentList) arg5);
            append(list, (IPersistentList) arg6);

            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6, Object arg7) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            append(list, (IPersistentList) arg4);

            append(list, (IPersistentList) arg5);
            append(list, (IPersistentList) arg6);
            append(list, (IPersistentList) arg7);

            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6, Object arg7, Object arg8) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            append(list, (IPersistentList) arg4);

            append(list, (IPersistentList) arg5);
            append(list, (IPersistentList) arg6);
            append(list, (IPersistentList) arg7);
            append(list, (IPersistentList) arg8);

            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
                Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {

            ArrayList<Object> list = new ArrayList<>();
            append(list, (IPersistentList) arg1);
            append(list, (IPersistentList) arg2);
            append(list, (IPersistentList) arg3);
            append(list, (IPersistentList) arg4);

            append(list, (IPersistentList) arg5);
            append(list, (IPersistentList) arg6);
            append(list, (IPersistentList) arg7);
            append(list, (IPersistentList) arg8);
            append(list, (IPersistentList) arg9);

            return makeList(list);
        }

    }

    /**
     * This function is a subset of clojure's hash-map.
     */
    public static class MyHashMap extends AFn {

        @Override
        public Object invoke() {
            return PersistentHashMap.EMPTY;
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            HashMap<Object, Object> newMap = new HashMap<>();
            newMap.put(arg1, arg2);
            return new PersistentHashMap(newMap);
        }

        @Override
        public Object invoke(Object arg1, Object arg2,
                Object arg3, Object arg4) {

            HashMap<Object, Object> newMap = new HashMap<>();
            newMap.put(arg1, arg2);
            newMap.put(arg3, arg4);
            return new PersistentHashMap(newMap);
        }

        @Override
        public Object invoke(Object arg1, Object arg2,
                Object arg3, Object arg4,
                Object arg5, Object arg6) {

            HashMap<Object, Object> newMap = new HashMap<>();
            newMap.put(arg1, arg2);
            newMap.put(arg3, arg4);
            newMap.put(arg5, arg6);
            return new PersistentHashMap(newMap);
        }

        @Override
        public Object invoke(Object arg1, Object arg2,
                Object arg3, Object arg4,
                Object arg5, Object arg6,
                Object arg7, Object arg8) {

            HashMap<Object, Object> newMap = new HashMap<>();
            newMap.put(arg1, arg2);
            newMap.put(arg3, arg4);
            newMap.put(arg5, arg6);
            newMap.put(arg7, arg8);
            return new PersistentHashMap(newMap);
        }

        @Override
        public Object invoke(Object arg1, Object arg2,
                Object arg3, Object arg4,
                Object arg5, Object arg6,
                Object arg7, Object arg8,
                Object arg9, Object arg10) {

            HashMap<Object, Object> newMap = new HashMap<>();
            newMap.put(arg1, arg2);
            newMap.put(arg3, arg4);
            newMap.put(arg5, arg6);
            newMap.put(arg7, arg8);
            newMap.put(arg9, arg10);
            return new PersistentHashMap(newMap);
        }

    }

    /**
     * This function is a subset of clojure's hash-set.
     */
    public static class MyHashset extends AFn {

        private Object makeVector(Set<Object> list) {

            return new PersistentHashSet(list);
        }

        @Override
        public Object invoke(Object arg1) {

            HashSet<Object> list = new HashSet<>();
            list.add(arg1);
            return makeVector(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            HashSet<Object> list = new HashSet<>();
            list.add(arg1);
            list.add(arg2);
            return makeVector(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            HashSet<Object> list = new HashSet<>();
            list.add(arg1);
            list.add(arg2);
            list.add(arg3);
            return makeVector(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            HashSet<Object> list = new HashSet<>();
            list.add(arg1);
            list.add(arg2);
            list.add(arg3);
            list.add(arg4);
            return makeVector(list);
        }

    }

    /**
     * This function is a subset of clojure's list.
     */
    public static class MyList extends AFn {

        private Object makeList(List<Object> list) {

            return PersistentList.create(list);
        }

        @Override
        public Object invoke(Object arg1) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            list.add(arg2);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            list.add(arg2);
            list.add(arg3);
            return makeList(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            list.add(arg2);
            list.add(arg3);
            list.add(arg4);
            return makeList(list);
        }
    }

    /**
     * This function is a subset of clojure's seq.
     */
    public static class MySeq extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 == RT.EOL) {
                return arg1;
            }

            return RT.seq(arg1);
        }
    }

    /**
     * This function is a subset of clojure's -.
     */
    public static class MySub extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {
            return Numbers.minus(arg1, arg2);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {
            return Numbers.minus(Numbers.minus(arg1, arg2), arg3);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {
            return Numbers.minus(Numbers.minus(Numbers.minus(arg1, arg2), arg3), arg4);
        }
    }

    /**
     * This function is a subset of clojure's vector.
     */
    public static class MyVector extends AFn {

        private Object makeVector(List<Object> list) {

            return new PersistentVector(list);
        }

        @Override
        public Object invoke(Object arg1) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            return makeVector(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            list.add(arg2);
            return makeVector(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            list.add(arg2);
            list.add(arg3);
            return makeVector(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            list.add(arg2);
            list.add(arg3);
            list.add(arg4);
            return makeVector(list);
        }

    }

    /**
     * This function is a subset of <i>Common Lisp</i>s write.
     */
    public static class MyWrite extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return this.invoke(arg1, arg2, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            return this.invoke(arg1, arg2, arg3, Boolean.FALSE, Boolean.FALSE);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            return this.invoke(arg1, arg2, arg3, arg4, Boolean.FALSE);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {

            Writer writer = getWriter(arg2);

            boolean readably = RT.booleanCast(arg3);
            boolean printMeta = RT.booleanCast(arg4);

            boolean printDup = RT.booleanCast(arg5);
            RTPrinter printer = new RTPrinter(readably, printMeta, printDup);

            try {
                printer.print(arg1, writer);
                writer.flush();
            } catch (IOException ioe) {
                // throw new RuntimeException("IOException", ioe);

                throw Util.sneakyThrow(ioe);

            }
            return Nil.INSTANCE;
        }

        Writer getWriter(Object arg2) {

            if (arg2 instanceof Boolean) {

                return new PrintWriter(System.err, true);
            }
            return (Writer) arg2;
        }

    }

    /**
     * This function is a subset of clojure's next.
     */
    public static class Next extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return RT.next(arg1);
        }
    }

    /**
     * This function is a subset of clojure's with-meta .
     */
    public static class WithMeta extends AFn {

        @Override
        public Object invoke(Object map, Object meta) {

            IObj pmap = (IObj) map;

            PersistentHashMap _meta = (PersistentHashMap) meta;

            return pmap.withMeta(_meta);

        }
    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s <b>apply</b> .
     */
    public static class Apply extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            ISeq list = (ISeq) arg2;

            ExampleEvaluator evltor = Globals.getEvaluator();

            Object result = evltor.apply(arg1, list);
            return result;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq list = (ISeq) arg3;

            return invoke(arg1, new Cons(arg2, list));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            ISeq list = (ISeq) arg4;

            return invoke(arg1, new Cons(arg2, new Cons(arg3, list)));
        }

    }

    /**
     * This function is a subset of clojure's *.
     */
    public static class MyMultiply extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.multiply(arg1, arg2);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            return Numbers.multiply(Numbers.multiply(arg1, arg2), arg3);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {

            return Numbers.multiply(Numbers.multiply(Numbers.multiply(arg1, arg2), arg3), arg4);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {

            return Numbers.multiply(Numbers.multiply(Numbers.multiply(Numbers.multiply(arg1, arg2), arg3), arg4), arg5);
        }
    }

    /**
     * This function is a subset of clojure's /.
     */
    public static class MyDivide extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.divide(arg1, arg2);
        }

    }

    /**
     * This function is a subset of clojure's quotient.
     */
    public static class MyQuotient extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.quotient(arg1, arg2);
        }

    }

    /**
     * This function is a subset of clojure's remainder.
     */
    public static class MyRemainder extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.remainder(arg1, arg2);
        }

    }

    /**
     * This function is a subset of clojure's &lt;.
     */
    public static class MyLt extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.lt(arg1, arg2);
        }

    }

    /**
     * This function is a subset of clojure's &lt;=.
     */
    public static class MyLte extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.lte(arg1, arg2);
        }
    }

    /**
     * This function is a subset of clojure's &gt;.
     */
    public static class MyGt extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.gt(arg1, arg2);
        }
    }

    /**
     * This function is a subset of clojure's &gt;=.
     */
    public static class MyGte extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            return Numbers.gte(arg1, arg2);
        }

    }

    /**
     * This function is a subset of clojure's =.
     */
    public static class MyEquiv extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number n1 = (Number) arg1;
            Number n2 = (Number) arg2;
            return Numbers.equiv(n1, n2);
        }

    }

    /**
     * This function is a subset of clojure's inc.
     */
    public static class MyInc extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return Numbers.inc(arg1);
        }

    }

    /**
     * This function is a subset of clojure's dec.
     */
    public static class MyDec extends AFn {

        @Override
        public Object invoke(Object arg1) {

            return Numbers.dec(arg1);
        }

    }

    /**
     *
     */
    public static class MyDeref extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Var v = (Var) arg1;

            return v.deref();
        }

    }

}
