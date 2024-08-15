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
import iris.clojure.lang.Associative;
import iris.clojure.lang.Cons;
import iris.clojure.lang.IMapEntry;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentSet;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.MapEntry;
import iris.clojure.lang.Nil;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.PersistentHashSet;
import iris.clojure.lang.PersistentList;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;
import iris.example.eval.ExampleEvaluator;
import iris.example.eval.Globals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is just an outer frame for the inner class .
 */
public final class Func2 {

    private static final Logger LOG = LoggerFactory.getLogger(Func2.class);

    // Disable construct
    private Func2() {
    }

    /**
     * This function is a subset of Clojure's <b>byte</b>.
     */
    public static class MyByte extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number lng = (Number) arg1;

            return lng.byteValue();
        }
    }

    /**
     * This function is a subset of Clojure's <b>short</b>.
     */
    public static class MyShort extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number lng = (Number) arg1;

            return lng.shortValue();
        }

    }

    /**
     * This function is a subset of clojure's int.
     */
    public static class MyInt extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number lng = (Number) arg1;

            return lng.intValue();
        }

    }

    /**
     * This function is a subset of clojure's float.
     */
    public static class MyFloat extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number lng = (Number) arg1;

            return lng.floatValue();
        }
    }

    /**
     * This function is a subset of clojure's double.
     */
    public static class MyDouble extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number lng = (Number) arg1;

            return lng.doubleValue();
        }
    }

    /**
     * This function is a subset of clojure's keys.
     */
    public static class MyKeys extends AFn {

        @Override
        public Object invoke(Object arg1) {

            IPersistentMap map = (IPersistentMap) arg1;

            ISeq result = Nil.INSTANCE;
            for (ISeq seq = map.seq(); seq != RT.EOL; seq = seq.next()) {

                MapEntry me = (MapEntry) seq.first();

                result = new Cons(me.getKey(), result);
            }

            return result;
        }

    }

    /**
     * This function is a subset of clojure's vals.
     */
    public static class MyVals extends AFn {

        @Override
        public Object invoke(Object arg1) {

            IPersistentMap map = (IPersistentMap) arg1;

            ISeq result = Nil.INSTANCE;
            for (ISeq seq = map.seq(); seq != RT.EOL; seq = seq.next()) {

                MapEntry me = (MapEntry) seq.first();

                result = new Cons(me.getValue(), result);
            }

            return result;
        }

    }

    /**
     * This function is a subset of clojure's byte-array.
     */
    public static class ByteArray extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Long lng = (Long) arg1;
            int size = lng.intValue();
            byte[] arr = new byte[size];

            return arr;
        }

    }

    /**
     * This function is a subset of clojure's aset-byte.      <code>
     * (aset-byte  (byte-array 10) 2  99)
     *
     * </code>
     *
     */
    public static class AsetByte extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            byte[] arr = (byte[]) arg1;

            Number n2 = (Number) arg2;
            int idx = n2.intValue();

            Number num = (Number) arg3;

            arr[idx] = num.byteValue();

            return Boolean.TRUE;
        }

    }

    /**
     * This function is a subset of Clojure's <b>aget-byte</b>.
     */
    public static class AgetByte extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            byte[] arr = (byte[]) arg1;

            Number n2 = (Number) arg2;
            int idx = n2.intValue();

            return arr[idx];
        }

    }

    /**
     * This function is a subset of Clojure's <b>assoc</b>.
     */
    public static class MyAssoc extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentMap assoc = (IPersistentMap) arg1;

            return assoc.assocEx(arg2, arg3);
        }

    }

    /**
     * This function is a subset of clojure's dissoc.
     */
    public static class MyDissoc extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentMap assoc = (IPersistentMap) arg1;

            return assoc.without(arg2);
        }
    }

    /**
     * This function is a subset of clojure's contains.
     */
    public static class MyContains extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Associative assoc = (Associative) arg1;

            return assoc.containsKey(arg2);
        }
    }

    /**
     * This function is a subset of clojure's import.
     */
    public static class MyImport extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(MyImport.class);

        @Override
        public Object invoke(Object arg1) {

            LOG.info("arg1");
            Namespace importToNs = (Namespace) iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();

            return importClass(importToNs, arg1);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            LOG.info("arg1={}, arg2={}", arg1, arg2);
            Namespace importToNs = (Namespace) iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();

            importClass(importToNs, arg1);
            return importClass(importToNs, arg2);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            LOG.info("arg1={}, arg2={}, arg3={}", arg1, arg2, arg3);
            Namespace importToNs = (Namespace) iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();

            importClass(importToNs, arg1);
            importClass(importToNs, arg2);
            return importClass(importToNs, arg3);
        }

        Object importClass(Namespace importToNs, Object arg1) {

            ISeq seq0 = RT.seq(arg1);

            if (seq0 == RT.EOL) {
                return Nil.INSTANCE;
            }

            Symbol prefix = (Symbol) seq0.first();

            Class<?> clazz = null;

            for (ISeq seq = seq0.next(); seq != RT.EOL; seq = seq.next()) {

                Symbol className = (Symbol) seq.first();

                LOG.info("MyImport {},  {}", prefix, className);

                String clazzName = prefix.getName() + "." + className.getName();

                try {
                    clazz = Class.forName(clazzName);

                    importToNs.importClass(clazz);

                } catch (ClassNotFoundException ex) {
                    LOG.warn("ClassNotFoundException", ex);
                }
            }

            return clazz;
        }
    }

    /**
     * This function is a subset of clojure's loaded-libs.
     *
     * <code>
     * (loaded-libs)
     * </code>
     *
     */
    public static class LoadedLibs extends AFn {

        @Override
        public Object invoke() {
            Set<Symbol> set = iris.clojure.nsvar.CljCompiler.LOADED_LIBS;

            HashSet<Object> newSet = new HashSet<>();
            newSet.addAll(set);

            return new PersistentHashSet(newSet);
        }
    }

    /**
     * This function is a subset of clojure's sort.
     *
     */
    public static class MySort extends AFn {

        @Override
        public Object invoke(final Object arg1) {

            ISeq seq = RT.seq(arg1);
            return sort(seq);
        }

        Object sort(final ISeq _seq) {

            List<Object> list = new ArrayList<>();

            for (ISeq seq = _seq; seq != RT.EOL; seq = seq.next()) {
                Object obj = seq.first();
                list.add(obj);
            }

            Collections.sort(list, RT.DEFAULT_COMPARATOR);

            return PersistentList.create(list);
        }

    }

    /**
     * This function is a subset of clojure's merge.
     *
     */
    public static class Merge extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(Merge.class);

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentMap map1 = (IPersistentMap) arg1;
            IPersistentMap map2 = (IPersistentMap) arg2;

            ISeq m1 = map1.seq();
            ISeq m2 = map2.seq();

            HashMap<Object, Object> map = new HashMap<>();
            add(map, m1);
            add(map, m2);

            int size = map.size();

            LOG.info("map.size = {}", size);

            BiFun biFun = new BiFun(size);

            map.forEach(biFun);

            return PersistentHashMap.createWithCheck(biFun.getArray());

        }

        static class BiFun implements BiConsumer<Object, Object> {

            final Object[] arr;
            int idx = 0;

            BiFun(int size) {
                arr = new Object[size * 2];
            }

            @Override
            public void accept(Object k, Object v) {

                LOG.info(" key = {}, val = {}", k, v);
                arr[idx++] = k;
                arr[idx++] = v;
            }

            public Object[] getArray() {
                return arr;
            }
        }

        void add(Map<Object, Object> map, ISeq seq) {

            for (; seq != RT.EOL; seq = seq.next()) {

                MapEntry me = (MapEntry) seq.first();

                map.put(me.getKey(), me.getValue());

            }
        }
    }

    /**
     * This function is a subset of clojure's union.
     *
     */
    public static class MyUnion extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentSet s1 = (IPersistentSet) arg1;
            IPersistentSet s2 = (IPersistentSet) arg2;

            HashSet<Object> hashSet = new HashSet<>();

            for (ISeq seq = s1.seq(); seq != RT.EOL; seq = seq.next()) {
                Object obj = seq.first();
                hashSet.add(obj);
            }

            for (ISeq seq = s2.seq(); seq != RT.EOL; seq = seq.next()) {
                Object obj = seq.first();
                hashSet.add(obj);
            }

            return new PersistentHashSet(hashSet);
        }

    }

    /**
     * This function is a subset of clojure's get.
     *
     * (get map key) (get map key not-found
     *
     */
    public static class MyGet extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentMap assoc = (IPersistentMap) arg1;

            return assoc.valAt(arg2);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentMap assoc = (IPersistentMap) arg1;

            return assoc.valAt(arg2, arg3);
        }
    }

    /**
     * This function is a subset of clojure's range.
     *
     * This interpreter does not perform <b>lazy</b> processing.
     *
     */
    public static class MyRange extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;
            long n = num.longValue();
            List<Object> list = range(0, n, 1);
            return PersistentList.create(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number num = (Number) arg1;
            long n = num.longValue();

            Number num2 = (Number) arg2;
            long s = num2.longValue();

            List<Object> list = range(n, s, 1);
            return PersistentList.create(list);
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            Number num = (Number) arg1;
            long n = num.longValue();

            Number num2 = (Number) arg2;
            long s = num2.longValue();

            Number num3 = (Number) arg3;
            long p = num3.longValue();

            List<Object> list = range(n, s, p);
            return PersistentList.create(list);
        }

        public List<Object> range(final long start, final long end, final long step) {

            if (step <= 0) {
                throw new IllegalArgumentException("step");
            }

            List<Object> list = new ArrayList<>();
            for (long n = start; n < end; n += step) {
                list.add(n);
            }
            return list;
        }

    }

    /**
     * This function is a subset of clojure's split-at.
     *
     */
    public static class SplitAt extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number n = (Number) arg1;
            int ln = n.intValue();

            ISeq seq = (ISeq) arg2;

            List<Object> list = new ArrayList<>();
            for (; seq != RT.EOL; seq = seq.next()) {
                if (ln <= 0) {
                    break;
                }
                ln--;
                Object obj = seq.first();
                list.add(obj);
            }

            IPersistentList create = PersistentList.create(list);

            return RT.vector(create, seq);
        }

    }

    /**
     * This function is a subset of clojure's intersection.
     *
     */
    public static class MyIntersection extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentSet s1 = (IPersistentSet) arg1;
            IPersistentSet s2 = (IPersistentSet) arg2;

            HashSet<Object> hashSet = new HashSet<>();

            for (ISeq seq = s2.seq(); seq != RT.EOL; seq = seq.next()) {
                Object obj = seq.first();

                if (s1.contains(obj)) {

                    hashSet.add(obj);
                }
            }

            return new PersistentHashSet(hashSet);
        }
    }

    /**
     * This function is a subset of clojure's class.
     *
     */
    public static class MyClass extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 == null) {
                return null;
            }

            return arg1.getClass();
        }

    }

    /**
     * This function is a subset of clojure's even?.
     *
     */
    public static class EvenQ extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            long lng = num.longValue();
            if ((lng & 1) == 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    /**
     * This function is a subset of clojure's select-keys.
     *
     * (select-keys map [ :a :b ])
     *
     */
    public static class MySelectKeys extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentMap map = (IPersistentMap) arg1;

            HashMap<Object, Object> resultMap = new HashMap<>();

            for (ISeq seq = RT.seq(arg2); seq != RT.EOL; seq = seq.next()) {

                Object key = seq.first();
                IMapEntry me = map.entryAt(key);

                resultMap.put(me.key(), me.val());
            }

            return new PersistentHashMap(resultMap);
        }

    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s <b>symbol-function</b>.
     *
     */
    public static class SymbolFunction extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Symbol sym = (Symbol) arg1;

            Namespace ns = (Namespace) iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();

            Var findInternedVar = ns.findInternedVar(sym);
            if (findInternedVar != null) {

                return findInternedVar.getFunction();

            }

            return Nil.INSTANCE;
        }

    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s <b>symbolp</b>.
     *
     */
    public static class SymbolpFn extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Symbol) {
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }
    }

    public static Object nullp(Object arg1) {

        if (arg1 == null) {
            return Boolean.TRUE;
        }
        if (arg1 instanceof Nil) {
            return Boolean.TRUE;
        }

        return Nil.INSTANCE;
    }

    /**
     * This function is a subset of Common Lisp's <b>null</b>.
     *
     */
    public static class MyNull extends AFn {

        @Override
        public Object invoke(Object arg1) {
            return nullp(arg1);
        }

    }


    /**
     * This function is a subset of Common Lisp's <b>not</b>.
     *
     */
    public static class MyNot extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Boolean) {
                Boolean b = (Boolean)arg1;
                return ! b;
            }

            return nullp(arg1);
        }

    }


    /**
     * This function is a subset of Common Lisp's <b>atom</b>.
     *
     */
    public static class Atom extends AFn {

        @Override
        public Object invoke(Object arg1) {
            if (arg1 == null) {
                return Nil.INSTANCE;
            }
            if (arg1 instanceof Cons) {
                return Boolean.TRUE;
            }

            return Nil.INSTANCE;
        }

    }

    /**
     * This function terminates the interpreter .
     *
     */
    public static class Quit extends AFn {

        @Override
        public Object invoke() {

            System.exit(0);
            return Boolean.TRUE;
        }

    }

    /**
     * This function is a subset of Common Lisp's <b>mapcar</b>.
     *
     */
    public static class MapCar extends AFn {

        @Override
        public Object invoke(Object fn, Object arg2) {

            ExampleEvaluator evl = Globals.getEvaluator();

            ArrayList<Object> list = new ArrayList<>();

            for (ISeq arglist = (ISeq) arg2; arglist != RT.EOL; arglist = arglist.next()) {

                Object car = arglist.first();

                Object o = evl.apply(fn, new Cons(car, RT.EOL));

                list.add(o);

            }

            return PersistentList.create(list);
        }

    }

    /**
     * This function is a subset of Clojure's <b>filter</b>.
     *
     */
    public static class FilterFn extends AFn {

        //   private static final Logger LOG = LoggerFactory.getLogger(FilterFn.class);
        @Override
        public Object invoke(Object fn, Object arg2) {

            ExampleEvaluator evl = Globals.getEvaluator();

            ArrayList<Object> list = new ArrayList<>();

            for (ISeq arglist = RT.seq(arg2); arglist != RT.EOL; arglist = arglist.next()) {

                Object car = arglist.first();

                Object o = evl.apply(fn, new Cons(car, RT.EOL));

                if (!isFalse(o)) {
                    list.add(car);
                }
            }

            return PersistentList.create(list);
        }

        boolean isFalse(Object x) {
            if (x == null) {
                return true;
            }
            if (x instanceof Boolean) {
                return !((Boolean) x);
            }
            return false;
        }

    }

}
