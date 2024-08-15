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
import iris.clojure.lang.Cons;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.Nil;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is just an outer frame for the inner class .
 *
 */
public class Specials {

    private static final Logger LOG = LoggerFactory.getLogger(Specials.class);

    static Symbol AND_REST = Symbol.intern(null, "&rest");

    /**
     * This function is a subset of <i>Common Lisp</i>'s block.
     *
     * <pre>
     * block name form ...
     * </pre>
     *
     * @author Hemmi
     */
    public static class BlockSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.next();
            Object blockName = seq.first();

            if (!RT.isSymbolOrKeyword(blockName)) {
                throw new IllegalArgumentException("blockName=" + blockName);
            }

            LOG.info("blockName={}", blockName);

            Object result = Nil.INSTANCE;
            try {
                for (ISeq body = seq.next(); body != RT.EOL; body = body.next()) {

                    Object first = body.first();

                    result = evtor.eval(first, env);
                }

            } catch (NonLocalException re) {

                Object type = re.getType();

                Object tag = re.getTag();

                Object value = re.getValue();

                LOG.info("NonLocalException  type={}, tag={}, value={}", type, tag, value);

                if (type != block) {
                    LOG.info("62 -------");
                    throw re;
                }
                if (blockName.equals(tag)) {
                    result = value;
                } else {
                    LOG.info("68 -------");
                    throw re;

                }

            }
            return result;
        }

        static Keyword block = Keyword.intern(null, "block");
    }

    /**
     * This function is a subset of <i>Common Lisp</i>'s cond.
     *
     * <code>
     * (cond ((= 1 2 ) 2 3) ((= 1 1) 4 5) )
     * </code>
     *
     * @author hemmi
     */
    public static class CondSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq cond = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            Object ret = Nil.INSTANCE;
            for (ISeq form = cond.next(); form != RT.EOL; form = form.next()) {

                ISeq list = (ISeq) form.first();
                Object pred = list.first();
                Object result = evtor.eval(pred, env);
                if (!isFalse(result)) {
                    for (ISeq body = list.next(); body != RT.EOL; body = body.next()) {
                        Object exec = body.first();
                        ret = evtor.eval(exec, env);
                    }
                    break;
                }
            }
            return ret;
        }

        boolean isFalse(Object arg) {

            if (arg == null) {
                return true;
            }
            if (arg instanceof Boolean) {
                Boolean b = (Boolean) arg;

                return !b;
            }
            if (arg instanceof Nil) {
                return true;
            }
            return false;
        }
    }

    /**
     * This function is a subset of Common-lisp's defconstant.
     */
    public static class DefconstantSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.seq();

            seq = seq.next();
            Object obj = seq.first();
            Symbol sym = (Symbol) obj;  // variable-name
            LOG.info("variable name={}", sym);

            seq = seq.next();
            Object obj2 = seq.first(); // value

            Object eval = evtor.eval(obj2, env);

            LOG.info("TODO impl .   ={}", eval);

            TopLevelEnv.putVariableValue(sym, eval);

            return sym;
        }
    }

    /**
     * This function is a subset of Common-lisp's defmacro.
     * 
     * Deprecated
     */
    public static class DefmacroSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.seq();

            seq = seq.next();
            Object obj = seq.first();
            Symbol sym = (Symbol) obj;  // function-name

            seq = seq.next();
            Object obj2 = seq.first(); // argument-list

            seq = seq.next();
            Object obj3 = seq.first(); // documentation

            String doc = null;
            if (obj3 instanceof String) {
                doc = (String) obj3;
                seq = seq.next();
            }

            Object lambda = new Cons(LAMBDA, new Cons(obj2, seq));

            String printString = iris.clojure.nsvar.CljCompiler.printString(lambda);

            LOG.info("lambda={}", printString);

            LOG.info("func name={}", sym);

            LOG.info("documentation={}", doc);

            Object o = iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();
            if (o != null) {
                Namespace ns = (Namespace) o;

                Var intern = ns.intern(sym);
                intern.setFunction(lambda);
                intern.setMacro();
            }

            return sym;
        }

        static Symbol LAMBDA = Symbol.intern(null, "lambda");

    }

    /**
     * This function is a subset of Common-lisp's <b>defun</b>.
     * 
     * Deprecated
     * 
     */
    public static class DefunSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.seq();

            seq = seq.next();
            Object obj = seq.first();
            Symbol funcName = (Symbol) obj;  // function-name

            seq = seq.next();
            Object argsList = seq.first(); // argument-list

            checkArgs(argsList); // Check the arguments !!

            seq = seq.next();
            Object obj3 = seq.first(); // documentation

            String doc = null;
            if (obj3 instanceof String) {
                doc = (String) obj3;
                seq = seq.next();
            }
            //  Object lambda = new Cons(LAMBDA, new Cons(argsList, seq));

            Object block = new Cons(BLOCK, new Cons(funcName, seq));
            Object lambda = RT.list(LAMBDA, argsList, block);

            String printString = iris.clojure.nsvar.CljCompiler.printString(lambda);

            LOG.info("lambda={}", printString);

            Closure func = new Closure(lambda, env);

            LOG.info("func name={}", funcName);

            LOG.info("documentation={}", doc);

            Object o = iris.clojure.nsvar.CljCompiler.CURRENT_NS.deref();
            if (o != null) {
                Namespace ns = (Namespace) o;

                Var intern = ns.intern(funcName);
                intern.setFunction(func);
            }

            return funcName;
        }

        void checkArgs(Object persistentArgList) {

            HashSet<Symbol> binds = new HashSet<>();
            ISeq vn = (ISeq) persistentArgList; // varNames
            for (; vn != RT.EOL; vn = vn.next()) {

                Symbol varName = (Symbol) vn.first();

                if (AND_REST.equals(varName)) {

                    vn = vn.next();
                    if (vn == RT.EOL) {
                        ETE.throwException("Syntax error near &rest", varName, persistentArgList);
                        //  throw new RuntimeException("Syntax error near &rest");
                    }

                    Symbol restName = (Symbol) vn.first();
                    if (binds.contains(restName)) {
                        throw new RuntimeException("Duplicate var name: " + restName);
                    }
                    vn = vn.next();
                    if (vn != RT.EOL) {
                        throw new RuntimeException("Too many var, near &rest");

                    }
                    return;
                }

                if (binds.contains(varName)) {
                    throw new RuntimeException("Duplicate var name: " + varName);
                }
                binds.add(varName);
            }

        }

        static Symbol LAMBDA = Symbol.intern(null, "lambda");
        static Symbol BLOCK = Symbol.intern(null, "block");
    }

    /**
     * This function is a subset of Common-lisp's if.
     */
    public static class IfSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();
            if (form == RT.EOL) {
                throw new IllegalArgumentException();
            }
            Object pred = form.first();
            form = form.next();
            if (form == RT.EOL) {
                throw new IllegalArgumentException();
            }

            Object result = evtor.eval(pred, env);
            if (!isFalse(result)) {
                Object then = form.first();

                Object result2 = evtor.eval(then, env);
                return result2;

            } else {
                form = form.next();
                if (form == RT.EOL) {
                    return Nil.INSTANCE;
                }
                Object _else = form.first();

                Object result3 = evtor.eval(_else, env);
                return result3;
            }
        }

        boolean isFalse(Object arg) {

            if (arg == null) {
                return true;
            }
            if (arg instanceof Boolean) {
                Boolean b = (Boolean) arg;

                return !b;
            }
            if (arg instanceof Nil) {
                return true;
            }
            return false;
        }
    }

    /**
     * This function is a subset of Common-lisp's let.
     */
    public static class LetSpecial extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(LetSpecial.class);

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope oldScope = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.next();
            ISeq list = (ISeq) seq.first();

            LOG.info("first={}", list);

            LexicalScope.Builder buil = new LexicalScope.Builder(oldScope);
            //  ArrayList<MapEntry> binds = new ArrayList<>();

            for (ISeq pt = list; pt != RT.EOL; pt = pt.next()) {

                ISeq list2 = (ISeq) pt.first();
                Symbol var = (Symbol) list2.first();
                list2 = list2.next();
                Object form2 = list2.first();

                Object eval = evtor.eval(form2, oldScope);
                //  MapEntry me = new MapEntry(var, eval);
                //  binds.add(me);

                buil.addBinding(var, eval);
            }

            //  LOG.info("binds={}", binds);
            Object result = Nil.INSTANCE;
            //  LexicalScope newScope = new LexicalScope(binds, oldScope);
            LexicalScope newScope = buil.build();

            for (ISeq body = seq.next(); body != RT.EOL; body = body.next()) {

                Object first = body.first();

                result = evtor.eval(first, newScope);
            }

            return result;
        }
    }

    /**
     * This function is a subset of Common-lisp's quote.
     */
    public static class QuoteSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            Object obj = RT.second(form);
            return obj;
        }
    }

    /**
     * This function is a subset of Common-lisp's return-from.
     *
     * <pre>
     * (return-from name form )
     * </pre>
     *
     * @author Hemmi
     */
    public static class ReturnFromSpecial extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(ReturnFromSpecial.class);

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.next();
            Object blockName = (Object) seq.first();

            if (!RT.isSymbolOrKeyword(blockName)) {
                throw new IllegalArgumentException("tag=" + blockName);
            }

            LOG.info("return-from name={}", blockName);

            seq = seq.next();
            Object first = seq.first();

            Object result = evtor.eval(first, env);
            throw new NonLocalException(block, result, blockName, null);
        }

        static Keyword block = Keyword.intern(null, "block");

    }

    public static class VarSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq next = form.next();
            if (next == RT.EOL) {
                throw new IllegalArgumentException();
            }
            Symbol sym = (Symbol) next.first();

            Object obj = evtor.resolve(sym, env);

            if (obj instanceof Var) {
                return obj;
            } else {
                ETE.throwException("Unable to resolve var: " + sym.toString() + " in this context",
                        sym, form);
                return null;
            }
        }
    }

    /**
     * Poor  implementation of Common Lisp's with-open-file
     * <code>
     * (with-open-file ( f "foo.txt" :output) body)
     *
     * (with-open-file ( f "foo.txt" :input) body)
     * </code>
     */
    public static class WithOpenFileSpecial extends AFn {

        //     private static final Logger LOG = LoggerFactory.getLogger(WithOpenFileSpecial.class);
        static Keyword INPUT = Keyword.intern(Symbol.intern(null, "input"));

        static Keyword OUTPUT = Keyword.intern(Symbol.intern(null, "output"));

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            IPersistentList form = (IPersistentList) arg1;
            LexicalScope oldScope = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            ISeq seq = form.seq();

            seq = seq.next();
            Object obj = seq.first();
            ISeq list = (ISeq) obj;  // ( f "foo.txt" :output) 

            Symbol var;
            String fileName;
            Keyword dir;
            {
                var = (Symbol) list.first();
                list = list.next();
                fileName = (String) list.first();
                list = list.next();
                dir = (Keyword) list.first();

                if (!((dir == INPUT) || (dir == OUTPUT))) {
                    throw new IllegalArgumentException("Sorry. :input or :output  supported");
                }

                LOG.info("variable name={}", var);
                LOG.info("file name={}", fileName);
            }

            if (dir == OUTPUT) {

                output(fileName, var, oldScope, seq, evtor);
            } else if (dir == INPUT) {

                input(fileName, var, oldScope, seq, evtor);
            }

            return Boolean.TRUE;
        }

        void input(String fileName, Symbol var, LexicalScope oldScope, ISeq seq, ExampleEvaluator evtor) {

            try {

                FileInputStream fos = new FileInputStream(fileName);
                try {
                    InputStreamReader osw = new InputStreamReader(fos);
                    /*
                    ArrayList<MapEntry> binds = new ArrayList<>();
                    binds.add(new MapEntry(var, osw));
                    LexicalScope newScope = new LexicalScope(binds, oldScope);
                     */

                    LexicalScope.Builder buil = new LexicalScope.Builder(oldScope);
                    buil.addBinding(var, osw);
                    LexicalScope newScope = buil.build();

                    for (ISeq body = seq.next(); body != RT.EOL; body = body.next()) {

                        Object form2 = body.first(); // value
                        LOG.info("body={}", form2);
                        evtor.eval(form2, newScope);
                    }

                } finally {
                    fos.close();
                }

            } catch (IOException ioe) {

                throw new RuntimeException("IOException", ioe);
            }
        }

        void output(String fileName, Symbol var, LexicalScope oldScope, ISeq seq, ExampleEvaluator evtor) {

            try {

                FileOutputStream fos = new FileOutputStream(fileName);
                try {
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    /*
                    ArrayList<MapEntry> binds = new ArrayList<>();
                    binds.add(new MapEntry(var, osw));
                    LexicalScope newScope = new LexicalScope(binds, oldScope);
                     */

                    LexicalScope.Builder buil = new LexicalScope.Builder(oldScope);
                    buil.addBinding(var, osw);
                    LexicalScope newScope = buil.build();

                    for (ISeq body = seq.next(); body != RT.EOL; body = body.next()) {

                        Object form2 = body.first(); // value
                        LOG.info("body={}", form2);
                        evtor.eval(form2, newScope);
                    }

                    osw.flush();
                } finally {
                    fos.close();
                }

            } catch (IOException ioe) {

                throw new RuntimeException("IOException", ioe);
            }
        }

    }

    /**
     * This function is a subset of Common Lisp's <b>function</b> .
     *
     */
    public static class FunctionSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            Object first = form.first(); // function

            form = form.next();

            Object second = form.first(); // (lambda (x ... ) ....)

            if (second instanceof ISeq) {

                boolean b = ExampleEvaluatorImpl.isLambda(second);

                if (!b) {
                    String toPrint = RT.printString(second);
                    LOG.warn("677) not lambda = {}", toPrint);

                    throw new RuntimeException("Syntax error near function");
                }
                return new Closure(second, env);

            } else if (second instanceof Symbol) {
                return new Closure(second, env);
            } else {
                throw new RuntimeException("Syntax error near function");
            }
        }

    }

    /**
     * This function is a subset of <b>Common Lisp</b>'s progn .
     *
     * <code>
     *
     * (progn body)
     *
     * </code>
     *
     */
    public static class PrognSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq seq = (ISeq) arg1;
            LexicalScope scope = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            Object ret = Nil.INSTANCE;

            for (ISeq sq = seq.next(); sq != RT.EOL; sq = sq.next()) {

                Object o = sq.first();
                ret = evtor.eval(o, scope);
            }

            return ret;
        }

    }

    /**
     * This function is a subset of <b>Common Lisp</b>'s setq .
     *
     * <code>
     *
     * (let ((a 1)) (setq a 234) a)
     *
     * </code>
     *
     */
    public static class SetqSpecial extends AFn {

        //    private static final Logger LOG = LoggerFactory.getLogger(SetqSpecial.class);
        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope scope = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();
            Object varName = form.first();  // varName

            if (!(varName instanceof Symbol)) {
                throw new EvaluatorException("Setq : Variable name must be symbol");
            }
            Symbol sym = (Symbol) varName;

            form = form.next();
            if (form == RT.EOL) {
                throw new EvaluatorException("Setq needs 2 argument");
            }

            Object expression = form.first();
            Object value = evtor.eval(expression, scope);

            int[] arr = scope.getVariableIndex(sym);

            if (arr == null) {
                throw new EvaluatorException("This interpreter's setq only supports lexical scoping. : " + sym.toString()
                        + ", When using global variables, please use defconstant ."
                );
            }
            scope.setVariableValue(arr[0], arr[1], value);

            return value;
        }

    }

    /**
     * This function is a subset of Common Lisp's <b>while</b> .
     *
     * <code>
     *
     * (while pred body)
     *
     * </code>
     *
     */
    public static class WhileSpecial extends AFn {

        //      private static final Logger LOG = LoggerFactory.getLogger(WhileSpecial.class);
        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq seq = (ISeq) arg1;
            LexicalScope scope = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            seq = seq.next();
            if (seq == RT.EOL) {
                throw new EvaluatorException("while needs 1 argument");
            }
            ISeq rest = seq;

            for (;;) {
                Object pred = rest.first();  // pred
                Object value = evtor.eval(pred, scope);

                LOG.info("pred = {}", value);

                if (isFalse(value)) {
                    LOG.info("53) ------ break");
                    break;
                }

                for (ISeq sq = rest.next(); sq != RT.EOL; sq = sq.next()) {

                    Object o = sq.first();
                    LOG.info("60) body = {}", o);
                    evtor.eval(o, scope);
                }
            }

            return Nil.INSTANCE;
        }

        boolean isFalse(Object obj) {

            if (obj == null) {
                return true;
            }
            if (obj instanceof Boolean) {
                Boolean b = (Boolean) obj;
                return !b;
            }

            return false;
        }

    }

    /**
     * This function is a subset of Common Lisp's <b>flet</b> .
     *
     */
    public static class FletSpecial extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope oldEnv = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();
            Object first = form.first();
            ISeq funDefs = (ISeq) first;
            IPersistentMap funMap = defLocalFuns(funDefs, oldEnv);

            LexicalScope newEnv = new LexicalScope(funMap, oldEnv);
            Object ret = Nil.INSTANCE;

            for (form = form.next(); form != RT.EOL; form = form.next()) {

                Object sexp = form.first();

                ret = evtor.eval(sexp, newEnv);

            }

            return ret;
        }

        Symbol LAMBDA = Symbol.intern(null, "lambda");

        IPersistentMap defLocalFuns(ISeq defs, LexicalScope scope) {

            HashMap<Object, Object> newMap = new HashMap<>();
            for (ISeq seq = defs; seq != RT.EOL; seq = seq.next()) {

                Object first = seq.first();

                if (first instanceof ISeq) {
                    ISeq seq2 = (ISeq) first;

                    Object funcName = seq2.first();
                    if (!(funcName instanceof Symbol)) {
                        throw new RuntimeException("Syntax error near flet");
                    }
                    ISeq rest = seq2.next();
                    Cons lambda = new Cons(LAMBDA, rest);
                    //    newMap.put(funcName, new FuncDeref(lambda));

                    Closure func = new Closure(lambda, scope);

                    newMap.put(funcName, new FuncDeref(func));

                } else {
                    throw new RuntimeException("Syntax error near flet");
                }
            }

            LOG.info("map = {}", newMap);

            return new PersistentHashMap(newMap);
        }

    }

    /**
     * This function is a subset of Clojure's <b>defonce</b> .
     *
     *
     */
    public static class DefonceSpecial extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(DefonceSpecial.class);

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope scope = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();
            Object varName = form.first();  // varName

            if (!(varName instanceof Symbol)) {
                throw new EvaluatorException("Defonce : Variable name must be symbol");
            }
            Symbol sym = (Symbol) varName;

            form = form.next();
            if (form == RT.EOL) {
                throw new EvaluatorException("Defonce needs 2 argument");
            }

            Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();

            Var findInternedVar = ns.findInternedVar(sym);
            if (findInternedVar != null) {

                LOG.info("defonce : {} already had a value .", sym);
                return Nil.INSTANCE;

            } else {

                Object expression = form.first();
                Object value = evtor.eval(expression, scope);

                Var v = ns.intern(sym);

                v.bindRoot(value);
                return v;
            }
        }

    }

    /**
     * Poor implementation of scheme's <b>call/cc<b> .
     *
     * It only supports global escapes.
     *
     * <code>
     *
     * (defun foo (x)
     * (call/cc (lambda (cc)
     * (if (< x 0)
     * x
     * (cc 99)))))
     *
     *
     * </code>
     */
    public static class CallCcSpecial extends AFn {

        private static final Logger LOG = LoggerFactory.getLogger(CallCcSpecial.class);

        static Symbol TRY = Symbol.intern(null, "try");

        static Symbol FLET = Symbol.intern(null, "flet");

        static Symbol THROW = Symbol.intern(null, "throw");

        static Symbol PARAM = Symbol.intern(null, "param");

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();

            Object maybeLambda = form.first();

            Object expanded = changeTry((ISeq) maybeLambda);

            // return expanded;
            Object ret = evtor.eval(expanded, env);
            return ret;
        }

        /**
         * (lambda (cc) (if (< x 0) x (cc 99)))
         *
         * V
         *
         * (try <below> (catch NonLocalException e (.getValue e) ) )
         *
         * V
         * (flet ((cc (param) (throw (NonLocalException. param)))) (if (< x 0) x
         * (cc 99)))
         *
         *
         * @param list
         * @return
         */
        static Symbol CATCH = Symbol.intern(null, "catch");
        static Symbol GET_VAL = Symbol.intern(null, ".getValue");
        static Symbol E = Symbol.intern(null, "e");
        static Symbol NON_LOCAL2 = Symbol.intern(null, "NonLocalException");

        Object changeTry(ISeq list) {

            ISeq inner = new Cons(GET_VAL, new Cons(E, Nil.INSTANCE));

            ISeq catch2 = RT.list(CATCH, NON_LOCAL2, E, inner);

//        ISeq catch = new Cons(CATCH, new Cons(NON_LOCAL2, new Cons(E, new Cons (inner, Nil.INSTANCE))));
            Object oo = changeFlet(list);

            return RT.list(TRY, oo, catch2);
        }

        Object changeFlet(ISeq list) {

            Object lambda = list.first();

            list = list.next();

            Object varList = list.first();

            list = list.next();

            ISeq binds = makeBind((ISeq) varList);

            return new Cons(FLET, new Cons(binds, list));
        }

        static Symbol NON_LOCAL = Symbol.intern(null, "NonLocalException.");

        ISeq makeBind(ISeq varList) {

            Object var = varList.first();

            ISeq inner = new Cons(NON_LOCAL, new Cons(PARAM, Nil.INSTANCE));
            ISeq body = new Cons(THROW, new Cons(inner, Nil.INSTANCE));
            ISeq paramList = new Cons(PARAM, Nil.INSTANCE);

            ISeq one = new Cons(var, new Cons(paramList, new Cons(body, Nil.INSTANCE)));
            return new Cons(one, Nil.INSTANCE);
        }
        /*
        Object wrapTry(ISeq form) {

            return new Cons(TRY, form);
        }
         */
    }

    /**
     * This function is a subset of Common Lisp's <b>dolist</b> .
     *
     */
    public static class DoListSpecial extends AFn {

//    private static final Logger LOG = LoggerFactory.getLogger(DoListSpecial.class);
        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();
            if (form == RT.EOL) {
                ETE.throwException("Syntax error near dolist", arg1, null);
            }
            Object form2 = form.first();
            if (!(form2 instanceof ISeq)) {
                ETE.throwException("Syntax error near dolist", form2, arg1);
            }
            ISeq seq2 = (ISeq) form2;
            Object ovar = seq2.first();
            if (!(ovar instanceof Symbol)) {
                ETE.throwException("Syntax error near dolist, the variable must be a symbol", ovar, arg1);

            }
            Symbol var = (Symbol) ovar;
            seq2 = seq2.next();
            if (seq2 == RT.EOL) {
                ETE.throwException("Syntax error near dolist", form2, arg1);
            }
            Object values = seq2.first();
            Object forLoop = evtor.eval(values, env);

            String str = RT.printString(forLoop);
            LOG.info("68) str={}", str);

            form = form.next();
            if (form == RT.EOL) {
                LOG.info("66) form was nil");
                // no body
                return Nil.INSTANCE;
            }

            for (ISeq vals = (ISeq) forLoop; vals != RT.EOL; vals = vals.next()) {

                Object first = vals.first();

                LOG.info("75) {}", first);

                LexicalScope.Builder bui = new LexicalScope.Builder(env);
                bui.addBinding(var, first);
                LexicalScope newScope = bui.build();

                for (ISeq body = form; body != RT.EOL; body = body.next()) {
                    Object toEval = body.first();
                    evtor.eval(toEval, newScope);
                }
            }

            return Nil.INSTANCE;
        }

    }

}
