package iris.example.eval;

import iris.clojure.lang.AFn;
import iris.clojure.lang.Cons;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.Nil;
import iris.clojure.lang.PersistentList;
import iris.clojure.lang.RT;
import iris.clojure.lang.Seqable;
import iris.clojure.lang.Symbol;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class Macros {

    private static final Logger LOG = LoggerFactory.getLogger(Macros.class);

    public static class IncfMacro extends AFn {

        static Symbol setq = Symbol.intern(null, "setq");

        static Symbol plus = Symbol.intern(null, "+");

        @Override
        public Object invoke(Object arg1) {

            Symbol varName = null;
            if (!(arg1 instanceof Symbol)) {
                ETE.throwException("incf 1st arg must be symbol", arg1, null);
            } else {
                varName = (Symbol) arg1;
            }

            Object add = RT.list(plus, 1, varName);

            Object expanded = RT.list(setq, varName, add);

            LOG.info("macro = {}", expanded);

            return expanded;
        }

    }

    /**
     * A knockoff of clojure's ns macro, I don't quite understand it and am
     * implementing it on my own .
     *
     * <pre>
     * https://qiita.com/athos/items/23146aeee94c9332e12a
     * require vs use
     *
     *
     * 要約
     * Clojure 1.4以降では、基本的にuseを使う必要はありません。requireを使いましょう。
     * </pre>
     *
     * <code>
     * (ns user1.core
     *     (:import [ java.io File ])
     *     (:require [ clojure.string :as s :refer [f] ] )
     * )
     * </code>
     *
     * <pre>
     * (namespace '  foo.ns   ' (:import    [java.io File IOException] )))
     * </pre>
     *
     */
    public static class NamespaceMacro extends AFn {

//    private static final Logger LOG = LoggerFactory.getLogger(NamespaceMacro.class);
        static Keyword REQUIRE = Keyword.intern(null, "require");
        static Keyword IMPORT = Keyword.intern(null, "import");

        @Override
        public Object invoke(Object arg1) {

            return nameSpace((Symbol) arg1, RT.EOL);
        }

        @Override
        public Object invoke(Object arg1, Object arg2) {
            return nameSpace((Symbol) arg1, new Cons(arg2, RT.EOL));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {
            return nameSpace((Symbol) arg1, new Cons(arg2, new Cons(arg3, RT.EOL)));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {
            return nameSpace((Symbol) arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4, RT.EOL))));
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
            return nameSpace((Symbol) arg1, new Cons(arg2, new Cons(arg3, new Cons(arg4, new Cons(arg5, RT.EOL)))));
        }

        Object nameSpace(Symbol sym, ISeq seq) {

            //  Namespace ns = Namespace.findOrCreate(sym);
            ArrayList<Object> list = new ArrayList<>();
            list.add(Symbol.intern(null, "block"));
            list.add(Nil.INSTANCE);

            // TODO:  (quote   ns)  or (in-ns 'ns)
            Object nsns = RT.list(IN_NS, RT.list(QUOTE, sym));
            list.add(nsns);

            for (; seq != RT.EOL; seq = seq.next()) {

                Object first0 = seq.first();
                //    IPersistentList form = (IPersistentList) first0;
                //    ISeq inner = form.seq();
                ISeq inner = getSeq(first0);

                Object first = inner.first();
                if (first == REQUIRE) {
                    ISeq seq2 = inner.next();
                    Object o2 = seq2.first();
                    Object o = macroExpandRequire(REQ, o2);
                    list.add(o);
                } else if (first == IMPORT) {
                    ISeq seq2 = inner.next();
                    Object o2 = seq2.first();
                    Object o = macroExpandRequire(IMP, o2);
                    list.add(o);
                } else {
                    LOG.warn("can not process {}", first.toString());
                }
            }

            return PersistentList.create(list);
        }

        ISeq getSeq(Object obj) {
            if (obj instanceof ISeq) {
                return (ISeq) obj;
            } else if (obj instanceof Seqable) {
                Seqable able = (Seqable) obj;
                return able.seq();
            }
            ETE.throwException("can't convert ISeq", obj, null);
            return null;
        }

        static Symbol QUOTE = Symbol.intern(null, "quote");

        static Symbol IN_NS = Symbol.intern("iris.clojure.core", "in-ns");

        static Symbol REQ = Symbol.intern("iris.clojure.core", "require");
        static Symbol IMP = Symbol.intern("iris.clojure.core", "import");

        Object macroExpandRequire(Object car, Object vec) {
            return RT.list(car, RT.list(QUOTE, vec));
        }

    }

    public static class LambdaMacro extends AFn {

//    private static final Logger LOG = LoggerFactory.getLogger(LambdaMacro.class);
        static Symbol FUNCTION = Symbol.intern(null, "function");
        static Symbol LAMBDA = Symbol.intern(null, "lambda");

        @Override
        public Object invoke(Object arg1, Object arg2) {
            /*
        String str1 = RT.printString(arg1);
        LOG.info("str1={}", str1);
        String str2 = RT.printString(arg2);
        LOG.info("str2={}", str2);
             */
            Object lam = RT.list(LAMBDA, arg1, arg2);
            Object fun = RT.list(FUNCTION, lam);

            String strFun = RT.printString(fun);
            LOG.info("strFun={}", strFun);

            return fun;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {
            /*
        String str1 = RT.printString(arg1);
        LOG.info("str1={}", str1);
        String str2 = RT.printString(arg2);
        LOG.info("str2={}", str2);
        String str3 = RT.printString(arg3);
        LOG.info("str3={}", str3);
             */
            Object lam = RT.list(LAMBDA, arg1, arg2, arg3);
            Object fun = RT.list(FUNCTION, lam);

            String strFun = RT.printString(fun);
            LOG.info("strFun={}", strFun);
            return fun;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {
            /*
        String str1 = RT.printString(arg1);
        LOG.info("str1={}", str1);
        String str2 = RT.printString(arg2);
        LOG.info("str2={}", str2);
        String str3 = RT.printString(arg3);
        LOG.info("str3={}", str3);
        String str4 = RT.printString(arg4);
        LOG.info("str4={}", str4);
             */
            Object lam = RT.list(LAMBDA, arg1, arg2, arg3, arg4);
            Object fun = RT.list(FUNCTION, lam);

            String strFun = RT.printString(fun);
            LOG.info("strFun={}", strFun);
            return fun;
        }

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {

            Object lam = RT.list(LAMBDA, arg1, arg2, arg3, arg4, arg5);
            Object fun = RT.list(FUNCTION, lam);

            String strFun = RT.printString(fun);
            LOG.info("strFun={}", strFun);
            return fun;
        }

    }

    private Macros() {

    }
}
