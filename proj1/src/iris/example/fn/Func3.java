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
import iris.clojure.lang.IPersistentCollection;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentSet;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Nil;
import iris.clojure.lang.RT;
import iris.clojure.lang.RTPrinter;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.Util;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.Namespace;
import iris.example.eval.ETE;
import iris.example.eval.EvaluatorException;
import iris.example.eval.ExampleEvaluator;
import iris.example.eval.Globals;
import iris.example.eval.LexicalScope;
import iris.example.eval.LexicalScopeUtil;
import iris.example.eval.NonLocalException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public final class Func3 {

    private static final Logger LOG = LoggerFactory.getLogger(Func3.class);

    // Disable construct
    private Func3() {
    }

    public static class IsBoolean extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Boolean) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    public static class IsChar extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Character) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    public static class IsCons extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Cons) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    public static class IsInstance extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (!(arg1 instanceof Class<?>)) {
                ETE.throwException("Arg1 of instance? must Class", arg1, null);
            }

            if (arg2 == null) {
                return Boolean.FALSE;
            }

            Class<?> clazz = (Class<?>) arg1;

            Class<?> clazz2 = arg2.getClass();

            if (clazz.isAssignableFrom(clazz2)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    public static class IsList extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof IPersistentList) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    public static class IsMap extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof IPersistentMap) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    public static class IsNumber extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Number) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    public static class IsSet extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof IPersistentSet) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    public static class IsString extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof String) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    public static class IsVector extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof IPersistentVector) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    }

    public static class Length extends AFn {

        @Override
        public Object invoke(Object arg1) {

            int len = RT.count(arg1);
            long lon = len;
            return lon;
        }

    }

    public static class Append extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (!(arg1 instanceof ISeq)) {
                ETE.throwException("The first argument of append must be a list", arg1, null);
            }
            if (!(arg2 instanceof ISeq)) {
                ETE.throwException("The second argument of append must be a list", arg2, null);
            }

            ISeq seq1 = (ISeq) arg1;
            ISeq seq2 = (ISeq) arg2;

            return append2(seq1, seq2);

        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            if (!(arg1 instanceof ISeq)) {
                ETE.throwException("The first argument of append must be a list", arg1, null);
            }
            if (!(arg2 instanceof ISeq)) {
                ETE.throwException("The second argument of append must be a list", arg2, null);
            }
            if (!(arg3 instanceof ISeq)) {
                ETE.throwException("The 3rd argument of append must be a list", arg3, null);
            }

            ISeq seq1 = (ISeq) arg1;
            ISeq seq2 = (ISeq) arg2;
            ISeq seq3 = (ISeq) arg3;

            return append2(seq1, append2(seq2, seq3));

        }

        ISeq append2(ISeq seq1, ISeq seq2) {

            if (seq1 instanceof Nil) {
                return seq2;
            }
            return new Cons(seq1.first(), append2(seq1.next(), seq2));

        }

    }

    /**
     * This function is a subset of <i>Clojure</i>'s prn.
     *
     * Note that it is not flushed when pr is used, so it will be output in the
     * next prn.
     *
     */
    public static class MyPrn extends AFn {

        private final RTPrinter printer;

        final boolean newline;

        public MyPrn(boolean newline, boolean readably) {

            this.newline = newline;

            //  boolean readably = true;
            boolean printMeta = false;
            boolean printDup = false;
            printer = new RTPrinter(readably, printMeta, printDup);
        }

        @Override
        public Object invoke() {

            try {
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1) {

            try {
                pr(arg1);
                if (newline) {
                    Globals.writer.write("\n");
                }
                // Common Lisp is probably flushing .
                // In Clojure, it is flushed with newline .

                // In this interpreter, I decided to flush according to Common Lisp .
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                Globals.writer.write(" ");

                pr(arg5);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                Globals.writer.write(" ");

                pr(arg5);
                Globals.writer.write(" ");

                pr(arg6);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                Globals.writer.write(" ");

                pr(arg5);
                Globals.writer.write(" ");

                pr(arg6);
                Globals.writer.write(" ");

                pr(arg7);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                Globals.writer.write(" ");

                pr(arg5);
                Globals.writer.write(" ");

                pr(arg6);
                Globals.writer.write(" ");

                pr(arg7);
                Globals.writer.write(" ");

                pr(arg8);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                Globals.writer.write(" ");

                pr(arg5);
                Globals.writer.write(" ");

                pr(arg6);
                Globals.writer.write(" ");

                pr(arg7);
                Globals.writer.write(" ");

                pr(arg8);
                Globals.writer.write(" ");

                pr(arg9);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
            try {
                pr(arg1);
                Globals.writer.write(" ");

                pr(arg2);
                Globals.writer.write(" ");

                pr(arg3);
                Globals.writer.write(" ");

                pr(arg4);
                Globals.writer.write(" ");

                pr(arg5);
                Globals.writer.write(" ");

                pr(arg6);
                Globals.writer.write(" ");

                pr(arg7);
                Globals.writer.write(" ");

                pr(arg8);
                Globals.writer.write(" ");

                pr(arg9);
                Globals.writer.write(" ");

                pr(arg10);
                if (newline) {
                    Globals.writer.write("\n");
                }
                Globals.writer.flush();
            } catch (IOException ex) {

                return Util.sneakyThrow(ex);
            }

            return Nil.INSTANCE;
        }

        private void pr(Object obj) throws IOException {
            printer.print(obj, Globals.writer);
        }

    }

    /**
     * This function is a subset of Clojure's <b>macroexpand-1</b> .
     *
     */
    public static class MacroExpand1Fn extends AFn {

        ExampleEvaluator evltor = Globals.getEvaluator();

        @Override
        public Object invoke(Object arg1) {

            ISeq seq = (ISeq) arg1;

            return evltor.macroexpand1(seq);

            // Memo: Common lisp probably returns multiple values
        }

    }

    /**
     * <div>(*class "uDate" "java.util.Date")</div>
     * <div>(*class "sDate" "java.sql.Date")</div>
     *
     */
    public static class JavaClazz extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(JavaClazz.class);

        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (!(arg1 instanceof String)) {
                ETE.throwException("Arg1 must be a string", arg1, null, null);
            }
            if (!(arg2 instanceof String)) {
                ETE.throwException("Arg2 must be a string", arg2, null, null);
            }
            String name = (String) arg1;
            Symbol symName = Symbol.intern(null, name);
            String clazzName = (String) arg2;

            Namespace importToNs = (Namespace) iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();

            Class<?> clazz = null;

            try {
                clazz = Class.forName(clazzName);

                importToNs.importClass(symName, clazz);

            } catch (ClassNotFoundException ex) {
                LOG.warn("ClassNotFoundException", ex);
            }

            return Boolean.TRUE;
        }

    }

    /**
     * This function is a subset of <i>Clojure</i>'s <b>throw</b>.
     *
     */
    public static class MyThrow extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (arg1 instanceof Exception) {
                Exception e = (Exception) arg1;

                return Util.sneakyThrow(e);

            } else {

                Object type = "type";
                Object value = arg1;
                Object tag = "tag";
                Object id = 123;

                throw new NonLocalException(type, value, tag, id);

            }
        }

    }

    /**
     * Common Lisp's <b>get</b>
     *
     * <code>
     *    (require '[ common.lisp :refer [ get ]])
     * </code>
     */
    public static class GetPropFn extends AFn {

        //   private static final Logger LOG = LoggerFactory.getLogger(GetPropFn.class);
        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (!(arg1 instanceof Symbol)) {
                throw new EvaluatorException("");
            }
            if (!(arg2 instanceof Symbol)) {
                throw new EvaluatorException("");
            }
            Symbol sym = (Symbol) arg1;
            Symbol propname = (Symbol) arg2;

            Symbol sym2 = null;

            if (sym.getNamespace() == null) {
                Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();

                String nsName = ns.getName();
                sym2 = Symbol.intern(nsName, sym.getName());

            } else {
                sym2 = sym;
            }

            LOG.info("40) sym2={}", sym2.getStringForPrint());

            Object obj = Globals.getProp(sym2, propname);
            if (obj == null) {
                return Nil.INSTANCE;
            }
            return obj;
        }

    }

    /**
     * Common Lisp's <b>putprop>/b> .
     */
    public static class PutPropFn extends AFn {

//    private static final Logger LOG = LoggerFactory.getLogger(PutPropFn.class);
        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            if (!(arg1 instanceof Symbol)) {
                throw new EvaluatorException("");
            }
            if (!(arg2 instanceof Symbol)) {
                throw new EvaluatorException("");
            }
            if (arg3 == null) {
                throw new NullPointerException("");
            }
            Symbol sym = (Symbol) arg1;
            Symbol propname = (Symbol) arg2;

            Symbol sym2 = null;

            if (sym.getNamespace() == null) {
                Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();

                String nsName = ns.getName();
                sym2 = Symbol.intern(nsName, sym.getName());

            } else {
                sym2 = sym;
            }

            LOG.info("40) sym2={}", sym2.getStringForPrint());

            Globals.putProp(sym2, propname, arg3);
            return arg3;
        }

    }

    /**
     * <code>
     *  (require '[common.lisp :refer [assoc]] )
     *
     *  (assoc 'b '((a 1 ) (b 2)))
     * </code>
     */
    public static class CommonAssoc extends AFn {

        @Override
        public Object invoke(final Object arg1, final Object arg2) {

            if (arg1 == null) {
                throw new NullPointerException();
            }
            if (arg2 == null) {
                throw new NullPointerException();
            }

            Object key = arg1;
            if (!(arg2 instanceof ISeq)) {
                ETE.throwException("Arg2 must be a list", arg2, null);
            }

            for (ISeq seq = (ISeq) arg2; seq != RT.EOL; seq = seq.next()) {
                Object o = seq.first();

                Object aa = Func1.car(o);
                if (arg1.equals(aa)) {
                    return o;
                }
            }

            return Nil.INSTANCE;
        }

    }

    /**
     * Clojure's like <b>ns-aliases</b> .
     */
    public static class NsAliasesFn extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (!(arg1 instanceof Namespace)) {
                ETE.throwException("Arg1 must be a namespace", arg1, null);
            }
            Namespace ns = (Namespace) arg1;

            return ns.getAliases();

        }

    }

    /**
     * Clojure's like <b>ns-mapping</b> .
     */
    public static class NsMappingFn extends AFn {

        @Override
        public Object invoke(Object arg1) {

            if (!(arg1 instanceof Namespace)) {
                ETE.throwException("Arg1 must be a namespace", arg1, null);
            }
            Namespace ns = (Namespace) arg1;

            return ns.getMappings();

        }

    }

    /**
     * This interpreter only <b>*scope</b>
     */
    public static class ScopeFn extends AFn {

        LexicalScopeUtil util = new LexicalScopeUtil();

        @Override
        public Object invoke(Object arg1) {

            if (!(arg1 instanceof LexicalScope)) {
                ETE.throwException("Arg1 must be a lexical scope", arg1, null, null);
            }
            LexicalScope lc = (LexicalScope) arg1;
            return util.getChains(lc);
        }

    }

    /**
     *
     * This interpreter only. access java's System.getProperties
     *
     */
    public static class EnvFn extends AFn {

        @Override
        public Object invoke() {

            Properties property = System.getProperties();

            Set<Map.Entry<Object, Object>> entrySet = property.entrySet();

            int n = entrySet.size();
            Object[] init = new Object[n * 2];

            int i = 0;
            for (Map.Entry<Object, Object> entry : entrySet) {

                init[i++] = entry.getKey();
                init[i++] = entry.getValue();
            }

            return RT.map(init);
        }

        @Override
        public Object invoke(Object arg1) {

            if (!(arg1 instanceof String)) {
                ETE.throwException("Arg1 must be a string", arg1, null);
            }
            String key = (String) arg1;

            String property = System.getProperty(key);
            return property;
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {

            if (!(arg1 instanceof String)) {
                ETE.throwException("Arg1 must be a string", arg1, null);
            }
            if (!(arg2 instanceof String)) {
                ETE.throwException("Arg1 must be a string", arg2, null);
            }
            String key = (String) arg1;
            String def = (String) arg2;

            String property = System.getProperty(key, def);
            return property;
        }
    }

    /**
     * Clojure's <b>nth</b>
     */
    public static class NthFn extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            IPersistentCollection coll = (IPersistentCollection) arg1;

            Number num = (Number) arg2;
            int n = num.intValue();

            return RT.nth(coll, n);

        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     *
     * Lisp's <b>gensym</b>
     */
    public static class GenSym extends AFn {

        @Override
        public Object invoke() {

            int nextInt = Globals.getNextInt();
            String name = "Gen_" + nextInt;
            return Symbol.intern(null, name);
        }

    }

    /**
     *
     * Common Lisp's <b>terpri</b> .
     */
    public static class Terpri extends AFn {

        @Override
        public Object invoke() {

            try {
                Globals.writer.write("\n");
                Globals.writer.flush();
            } catch (IOException ex) {
                return Util.sneakyThrow(ex);
            }
            return Nil.INSTANCE;
        }

    }

}
