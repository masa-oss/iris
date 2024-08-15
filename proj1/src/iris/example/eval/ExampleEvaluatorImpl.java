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

import iris.clojure.lang.Cons;
import iris.clojure.lang.ICommonLispFn;
import iris.clojure.lang.IFn;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentSet;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.MapEntry;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.PersistentList;
import iris.clojure.lang.PersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.Nil;
import iris.clojure.lang.PersistentHashSet;
import iris.clojure.lang.ReaderException;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.IStaticCljNs;
import iris.clojure.nsvar.INamespace;
import iris.clojure.nsvar.Var;

import java.util.ArrayList;
import java.util.HashSet;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.beanutils.MethodUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lisp interpreter (eval) that behaves like Common Lisp .
 *
 *
 *
 */
public class ExampleEvaluatorImpl implements ExampleEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleEvaluatorImpl.class);

    int level;
    IStaticCljNs scn;

    public ExampleEvaluatorImpl(IStaticCljNs scn) {
        this.level = 0;
        this.scn = scn;
    }

    String _printString(Object form) {
        return CljCompiler.printString(form);

    }

    @Override
    public Object eval(Object sexp, LexicalScope env) {

        String cls = "";
        Object o = null;
        level++;
        IPersistentMap meta = RT.meta(sexp);
        try {

            if (LOG.isDebugEnabled()) {
                cls = (sexp == null) ? "null" : sexp.getClass().getName();
                String printString = _printString(sexp);

                LOG.debug("Before eval ({}) {} - {}", level, cls, printString);
            }

            o = internalEval(sexp, env);

            if (LOG.isDebugEnabled()) {

                LOG.debug("After  eval ({}) {} => {}", level, cls, o);
            }

        } catch (EvaluatorException ee) {
            throw ee;
        } catch (RuntimeException re) {

            if (meta != null) {
                if (re instanceof ReaderException) {
                    String msg = re.toString();
                    throw new EvaluatorException(meta, msg, re);
                } else {
                    throw new EvaluatorException(meta, re.getMessage(), re);
                }
            } else {
                throw re;
            }

        } finally {
            level--;
        }
        return o;
    }

    Object internalEval(Object sexp, LexicalScope env) {

        if (sexp == null) {
            return null;
        } else if (sexp instanceof Number) {
            return sexp;
        } else if (sexp instanceof Boolean) {
            return sexp;

        } else if (sexp instanceof String) {
            return sexp;
        } else if (sexp instanceof Character) {
            return sexp;

        } else if (sexp instanceof Nil) {
            return sexp;

        } else if (sexp instanceof Keyword) {
            return sexp;

        } else if (sexp instanceof PersistentList.EmptyList) {
            return sexp;

        } else if (sexp instanceof IPersistentVector) {

            return evalVector((IPersistentVector) sexp, env);

        } else if (sexp instanceof IPersistentMap) {

            return evalMap((IPersistentMap) sexp, env);

        } else if (sexp instanceof IPersistentSet) {

            return evalSet((IPersistentSet) sexp, env);

        } else if (sexp instanceof Var) {

            throw new RuntimeException("can't eval " + sexp);
        } else if (sexp instanceof ISeq) {

            return evalList((ISeq) sexp, env);

        } else if (sexp instanceof Symbol) {
            return evalSymbol((Symbol) sexp, env);
        }

        return sexp;
    }

    public Object evalSet(IPersistentSet pset, LexicalScope env) {

        HashSet<Object> newSet = new HashSet<>();
        for (ISeq seq = pset.seq(); seq != RT.EOL; seq = seq.next()) {

            Object obj = seq.first();
            Object evaled = this.eval(obj, env);
            newSet.add(evaled);
        }
        return new PersistentHashSet(newSet);
    }

    public Object evalMap(IPersistentMap map, LexicalScope env) {

        ArrayList<Object> list = new ArrayList<>();
        for (ISeq seq = map.seq(); seq != RT.EOL; seq = seq.next()) {

            Object obj = seq.first();
            MapEntry me = (MapEntry) obj;

            Object key = this.eval(me.getKey(), env);
            Object val = this.eval(me.getValue(), env);
            list.add(key);
            list.add(val);
        }
        Object[] toArray = list.toArray();
        PersistentHashMap create = PersistentHashMap.create(toArray);
        return create;
    }

    public Object evalVector(IPersistentVector vec, LexicalScope env) {

        ArrayList<Object> list = new ArrayList<>();

        int size = vec.count();
        for (int i = 0; i < size; i++) {
            Object obj = vec.get(i);
            Object res = this.eval(obj, env);
            list.add(res);
        }

        return new PersistentVector(list);
    }

    static Symbol T = Symbol.intern(null, "t");
    static Symbol ELSE = Symbol.intern(null, "else");
    
    public Object evalSymbol(Symbol sym, LexicalScope env) {
/* 2024-08-10
        boolean b = CljCompiler.isSpecialEx(sym);
        if (b) {
            return sym;
        }
*/

        if (T.equals(sym) || ELSE.equals(sym)) {
            return sym;
        }
        
        int[] arrs = env.getVariableIndex(sym);
        if (arrs != null) {
            return env.getVariableValue( arrs[0]  , arrs[1]);
        }
        
        
        
        
        String ns = sym.getNamespace();
        if (ns != null) {
            // When there is a namespace
            INamespace found =  this.scn   .find(Symbol.intern(null, ns));
            if (found == null) {

                INamespace cur = scn.getCurrentNs();
                
                
                IPersistentMap aliases = cur.getAliases();
                Object valAt = aliases.valAt(Symbol.intern(null, ns), null);
                LOG.info("214) alias = {}", valAt);
                if (valAt == null) {
                    return getFieldValue(sym);
                } else {
                    if (valAt instanceof INamespace) {

                        INamespace aliNs = (INamespace) valAt;
                        Var va  = aliNs.findInternedVar(Symbol.intern(null, sym.getName()));
                        if (va  != null) {
                            return va.deref();
                        } else {
                            throw new RuntimeException("Variable not found: " + sym);
                        }

                    } else {
                        throw new RuntimeException("Variable not found: " + sym);
                    }
                }

            }
            String name = sym.getName();

            Object val = found.getMapping(Symbol.intern(null, name));

            LOG.info("141) {}", val);

            if (val instanceof Class<?>) {
                return val;
            } else if (val instanceof Var) {
                Var v = (Var) val;
                return v.deref();
            } else {
                throw new RuntimeException("Object is not  Class or Var");

            }
        }
        return searchCurrentNs(sym);
    }

    Object searchCurrentNs(Symbol sym) {

        if (sym.getNamespace() != null) {
            throw new RuntimeException("getNamespace() != null :  " + sym.getName());
        }

        INamespace ns = scn.getCurrentNs();
        
        

        Var findInternedVar = ns.findInternedVar(sym);
        if (findInternedVar != null) {

            // add 24-7-31
            if (findInternedVar.hasRoot()) {

                return findInternedVar.deref();
            } else {
                ETE.throwException("UNBOUND-VARIABLE " + sym.toString(), sym, null);
            }
        }

        Object mapping = ns.getMapping(sym);
        if (mapping != null) {
            return mapping;
        }

        ETE.throwException("UNBOUND-VARIABLE " + sym.toString(), sym, null);
        return Boolean.FALSE;
    }

    // Javaのstatic のfield
    Object getFieldValue(Symbol sym) {

        String ns = sym.getNamespace();

        INamespace curr = scn.getCurrentNs();

        Object obj = curr.getMapping(Symbol.intern(null, ns));
        if (obj instanceof Class<?>) {
            String name = sym.getName();
            // LOG.info("240) search field ..{}", name);

            Class<?> clazz = (Class<?>) obj;
            Object ret = null;
            try {
                Field declaredField;
                declaredField = clazz.getDeclaredField(name);

                ret = declaredField.get(null);
            } catch (Exception ex) {
                throw new RuntimeException("Exception", ex);
            }

            return ret;
        }
        throw new RuntimeException("Exception");
    }

    @Override
    public Object resolve(Symbol sym, LexicalScope env) {

        String ns = sym.getNamespace();
        if (ns != null) {
            
            // When a namespace is specified
            
            INamespace found = scn.find(Symbol.intern(null, ns));
            
            if (found == null) {

                INamespace curr = (INamespace) CljCompiler.CURRENT_NS.deref();
                //  LOG.info("Search alias ... {}", curr.getName());

                Object f2 = curr.getAliases().valAt(Symbol.intern(null, ns));
                LOG.info("alias={}", f2);
                found = (INamespace) f2;

                if (f2 == null) {

                    Object obj = curr.getMapping(Symbol.intern(null, ns));
                    LOG.info("281) obj = {}", obj);
                    if (obj != null) {
                        return obj;
                    }

                }

            } else {

                Object o2 = found.getMapping(Symbol.intern(null, sym.getName()));
                if (o2 != null) {
                    return o2;
                }
                return getFieldValue(sym);
            }

            if (found == null) {
                throw new RuntimeException("can't eval " + sym.getStringForPrint());
            }

            String name = sym.getName();

            Object val = found.getMapping(Symbol.intern(null, name));

            LOG.info("60) {}", val);

            if (val == null) {

                ETE.throwException("not found", sym, null);

            }
            return val;
        } else {

            // When Namespace is not specified

            ICommonLispFn localFunc = env.getLocalFunc(sym);
            if (localFunc != null) {
                return localFunc;
            }
            
            // If Namespace is not specified, search from current

            INamespace curr = scn.getCurrentNs();

            Object val2 = curr.getMapping(sym);
            return val2;
        }
    }

    @Override
    public Object macroexpand1(final ISeq sexp) {

        final LexicalScope env = new LexicalScope();
        int length = sexp.count();
        if (length == 0) {
            return sexp;
        }
        Object first = sexp.first(); // First in the list is the function name
        LOG.info("375) first={}", first);

        if (first instanceof Symbol) {

            return macroOrFuncation1(first, sexp, env);
        }
        return Nil.INSTANCE;
    }

    public Object evalList(final ISeq sexp, final LexicalScope env) {

        int length = sexp.count();
        if (length == 0) {
            return sexp;
        }
        Object first = sexp.first(); // First in the list is the function name
        LOG.info("375) first={}", first);

        Object special = CljCompiler.getSpecial(first);
        if (special != null) {
            LOG.info("379) first={}, special", first, special);

            IFn specFn = (IFn) special;
            Object res = specFn.invoke(sexp, env, this);
            return res;
        }
        LOG.info("385) {} is not special ", first);

        if (first instanceof Symbol) {

            Object answer = null;
            try {
                answer = macroOrFuncation(first, sexp, env);
                
            } catch(InvocationTargetException ite) {
                LOG.info("InvocationTargetException", ite);
                String errmsg = (ite.getCause() != null) ? ite.getCause().getMessage() : "Exception in evalation phase.";
                
                ETE.throwException(errmsg, sexp, null, ite);
                
            } catch(Exception ex) {
                LOG.info("Exception", ex);
                
                ETE.throwException(ex.getMessage(), sexp, null, ex);
            }
            return answer;

        } else if (first instanceof Keyword) {

            ISeq args = sexp.next();
            ISeq sexp2 = evalArguments(args, env);  // eval each args - first
            IFn fnc = (IFn) first;

            // I threw away the second one and onwards.
            Object map = sexp2.first();

            if (LOG.isDebugEnabled()) {
                String clz = (map == null) ? "null" : map.getClass().getName();
                String printString = _printString(map);
                LOG.debug("382) args.print {}, {}", clz, printString);
            }

            return fnc.invoke(map);
        } else if (isLambda(first)) {

            ISeq lambdaForm = (ISeq) first;
            lambdaForm = lambdaForm.next();
            ISeq lambdaList = (ISeq) lambdaForm.first();
            lambdaForm = lambdaForm.next();

            ISeq args = sexp.next();
            ISeq sexp2 = evalArguments(args, env);  // eval each args - first

            LOG.info("402) before lambda  {}", first.getClass().getName());

            return invokeLambda(sexp2, lambdaList, lambdaForm, env);

        } else {
            throw new RuntimeException("Function not supported : " + first + ", env=" + env);
        }
    }

    Object methodCall(String className, String methodName, ISeq arglist) throws Exception {

        Object instance = arglist.first();
        ISeq rest = arglist.next();

        LOG.info("429) className={}, methodName={}, instance.class={}", className, methodName, instance.getClass().getName());

        List<Object> list = new ArrayList<>();
        for (ISeq seq = rest; seq != RT.EOL; seq = seq.next()) {
            Object first = seq.first();
            list.add(first);
        }

        LOG.info("437) list={}", list);

        Object[] argArr = new Object[list.size()];

        // It may not work well when the argument is null
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Object o = list.get(i);
            argArr[i] = o;
        }

        LOG.info("376) args={}", Arrays.toString(argArr));

        Object o3 = MethodUtils.invokeMethod(instance, methodName, argArr);
        return o3;

    }

    static Symbol NEW = Symbol.intern(null, "new");

    Object macroOrFuncation1(Object first, final ISeq sexp, final LexicalScope env) throws RuntimeException {
        Symbol sym = (Symbol) first;
        Object maybeVar = resolve(sym, env);
        if (!(maybeVar instanceof ICommonLispFn)) {
            throw new RuntimeException("The function definition could not be found : " + first + ", env=" + env);
        }
        if (isMacro(maybeVar)) {

            Var v = (Var) maybeVar;
            Object macro = v.getFunction(); // When using Common Lisp, get it from the function field
            if (macro instanceof IFn) {
                IFn ifn = (IFn) macro;

                //   macro expand ->   eval
                ISeq arglist = sexp.next();

                Object r = ifn.applyTo(arglist);

                String rintString = this._printString(r);
                LOG.debug("351) marcro => {}", rintString);

                return r;

            } else {
                if (isLambda(macro)) {

                    Object r = apply(macro, sexp.next());
                    return r;
                }

                throw new RuntimeException("The function definition could not be found : " + first + ", env=" + env);
            }
        }
        return Nil.INSTANCE;
    }

    Object macroOrFuncation(Object first, final ISeq sexp, final LexicalScope env) throws Exception {

        Symbol sym = (Symbol) first;
        Object maybeVar = resolve(sym, env);

        if (maybeVar == null) {
            String name = sym.getName();
            if (name.startsWith(".") && name.length() > 1) {

                // method call
                ISeq args = sexp.next();
                ISeq sexp2 = evalArguments(args, env);  // eval each args - first
                Object fi = sexp2.first();
                String clsName = fi.getClass().getName();
                LOG.info("404) clsName={}", clsName);
                return methodCall(clsName, name.substring(1), sexp2);

            } else if (name.endsWith(".") && name.length() > 1) {

                int len = name.length();
                String className = name.substring(0, len - 1);

                LOG.info("className={}", className);

                // macro expand
                Symbol classSym = Symbol.intern(null, className);
                Object expanded = new Cons(NEW, new Cons(classSym, sexp.next()));

                String rintString = this._printString(expanded);
                LOG.debug("438) marcro => {}", rintString);

                return eval(expanded, env);

            } else {
                String symName = sym.getStringForPrint();
                throw new RuntimeException("Function '" + symName + "' is not supported by this interpreter.");
            }
        }

        if (maybeVar instanceof Class<?>) {

            Class<?> clazz = (Class<?>) maybeVar;
            return callStatic(clazz, sym);

        } else if (!(maybeVar instanceof ICommonLispFn)) {
            throw new RuntimeException("The function definition could not be found : " + first + ", env=" + env);
        }

        if (isMacro(maybeVar)) {

            Var v = (Var) maybeVar;
            Object macro = v.getFunction(); // When using Common Lisp, get it from the function field
            if (macro instanceof IFn) {
                IFn ifn = (IFn) macro;

                //   macro expand ->   eval
                ISeq arglist = sexp.next();

                Object r = ifn.applyTo(arglist);

                String rintString = this._printString(r);
                LOG.debug("351) marcro => {}", rintString);

                return eval(r, env);

            } else {
                if (isLambda(macro)) {

                    Object r = apply(macro, sexp.next());
                    return eval(r, env);
                }

                throw new RuntimeException("The function definition could not be found : " + first + ", env=" + env);
            }
        }

        Object root = null;
        if (maybeVar instanceof ICommonLispFn) {

            ICommonLispFn cfn = (ICommonLispFn) maybeVar;
            root = cfn.getFunction();

        }

        if (root instanceof IFn) {
            IFn ifn = (IFn) root;

            //  evalArguments ->   apply 
            ISeq args = sexp.next();

            if (LOG.isTraceEnabled()) {
                String clz = (args == null) ? "null" : args.getClass().getName();
                String printString = _printString(args);
                LOG.trace("570) args.print {}, {}", clz, printString);
            }
            ISeq sexp2 = evalArguments(args, env);  // eval each args - first

            if (LOG.isTraceEnabled()) {
                LOG.trace("575) before funcall  {}", ifn.getClass().getName());
            }
            Object r = ifn.applyTo(sexp2);

            return r;

        } else if (isLambda(root)) {

            ISeq lambdaForm = (ISeq) root;
            lambdaForm = lambdaForm.next();
            ISeq lambdaList = (ISeq) lambdaForm.first();
            lambdaForm = lambdaForm.next();

            ISeq args = sexp.next();
            ISeq sexp2 = evalArguments(args, env);  // eval each args - first

            LOG.info("637) before lambda  {}", root.getClass().getName());

            return invokeLambda(sexp2, lambdaList, lambdaForm, env);

        } else if (root instanceof Closure) {
            Closure func = (Closure) root;
            ISeq lambdaForm = (ISeq) func.getForm();

            ISeq args = sexp.next();
            ISeq sexp2 = evalArguments(args, env);  // eval each args - first
            lambdaForm = lambdaForm.next();
            ISeq lambdaList = (ISeq) lambdaForm.first();
            lambdaForm = lambdaForm.next();

            return invokeLambda(sexp2, lambdaList, lambdaForm, func.getScope());

        } else {
            throw new RuntimeException("The function definition could not be found : " + first + ", env=" + env);
        }
    }

    boolean isMacro(Object maybeVar) {

        if (maybeVar instanceof Var) {
            Var v = (Var) maybeVar;

            return v.isMacro();
        }
        return false;
    }

    Object callStatic(Class<?> clazz, Symbol sym) {

        String methodName = sym.getName();

        Class<?>[] parameterTypes = new Class<?>[0];

        Method declaredMethod = null;
        try {

            declaredMethod = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            throw new RuntimeException("getDeclaredMethod not found : " + sym, ex);

        }

        Object[] args = new Object[0];

        Object instance = null;

        Object ret = null;
        try {
            ret = declaredMethod.invoke(instance, args);
        } catch (Exception ex) {
            throw new RuntimeException("invoke error : " + sym, ex);

        }
        return ret;
    }

    static final Symbol LAMBDA = Symbol.intern(null, "lambda");

    static boolean isLambda(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj instanceof ISeq) {
            Object first = ((ISeq) obj).first();
            if (LAMBDA.equals(first)) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    LexicalScope empty = new LexicalScope();

    @Override
    public Object apply(final Object first, final ISeq args) {

        final Object fn = first;

        if (first instanceof Symbol) {

            Symbol sym = (Symbol) first;
            Object varOrClazz = resolve(sym, empty);

            if (varOrClazz instanceof Var) {

                Var v = (Var) varOrClazz;
                if (v.isMacro()) {
                    throw new RuntimeException("Macro not supported : " + first);
                }
                Object root = v.getFunction(); // When using common-lisp, get it from the function field

                if (root == null) {
                    root = v.deref();
                }

                if (root instanceof IFn) {
                    IFn ifn = (IFn) root;

                    return invokeIFn(ifn, args);

                }
            }
        }

        if (isLambda(fn)) {
            ISeq lambdaForm = (ISeq) fn;
            lambdaForm = lambdaForm.next();
            ISeq lambdaList = (ISeq) lambdaForm.first(); // variable name list
            lambdaForm = lambdaForm.next();

            LOG.info("658) before lambda  {}", fn.getClass().getName());

            LexicalScope env = new LexicalScope();
            return invokeLambda(args, lambdaList, lambdaForm, env);

        } else if (fn instanceof Closure) {
            Closure function = (Closure) fn;
            Object maybeLambda = function.getForm();
            if (isLambda(maybeLambda)) {

                ISeq lambdaForm = (ISeq) maybeLambda;
                lambdaForm = lambdaForm.next();
                ISeq lambdaList = (ISeq) lambdaForm.first(); // variable name list
                lambdaForm = lambdaForm.next();

                LOG.info("674) before lambda  {}", fn.getClass().getName());

                return invokeLambda(args, lambdaList, lambdaForm, function.getScope());

            } else if (maybeLambda instanceof Symbol) {

                return apply(maybeLambda, args);

            } else {
                throw new RuntimeException("can not execute:" + maybeLambda);
            }
        }
        throw new RuntimeException("Function(apply) not supported : " + fn);
    }

    Object invokeLambda(ISeq argsEvaled, ISeq varNames, ISeq bodyForEval, LexicalScope oldScope) {

        List<MapEntry> binds = bindLambda(argsEvaled, varNames);

        LOG.info("binds={}", binds);

        Object result = Nil.INSTANCE;
        
        LexicalScope.Builder buil = new LexicalScope.Builder(oldScope);
        for (MapEntry ma : binds) {
            buil.addBinding((Symbol)  ma.getKey()  , ma.getValue());
        }
        LexicalScope newScope = buil.build();
        

        for (; bodyForEval != RT.EOL; bodyForEval = bodyForEval.next()) {

            Object form = bodyForEval.first();

            result = this.eval(form, newScope);
        }
        return result;
    }

    static Symbol AND_REST = Symbol.intern(null, "&rest");

    List<MapEntry> bindLambda(final ISeq arguments, final ISeq varNames) {

        ArrayList<MapEntry> binds = new ArrayList<>();
        ISeq args = arguments;
        ISeq vn = varNames;
        for (; vn != RT.EOL; args = args.next(), vn = vn.next()) {

            Object varName = vn.first();

            if (AND_REST.equals(varName)) {

                vn = vn.next();
                if (vn == RT.EOL) {
                    throw new RuntimeException("Syntax error near &rest");
                }

                Object restName = vn.first();

                ISeq restArg = argRest(args);

                MapEntry me2 = new MapEntry(restName, restArg);
                addWithCheck(binds, me2);

                return binds;
            }

            if (args == RT.EOL) {
                throw new RuntimeException("Too few arguments");
            }
            Object value = args.first();

            MapEntry me = new MapEntry(varName, value);
            addWithCheck(binds, me);

        }

        if (args != RT.EOL) {
            throw new RuntimeException("Too many arguments");
        }
        return binds;
    }

    void addWithCheck(List<MapEntry> binds, MapEntry me) {

        for (MapEntry pair : binds) {

            if (pair.getKey().equals(me.getKey())) {
                throw new RuntimeException("Duplicate var name: " + me.getKey());
            }
        }
        binds.add(me);
    }

    ISeq argRest(ISeq seq) {

        if (seq == RT.EOL) {
            return RT.EOL;
        }
        Object first = seq.first();
        return new Cons(first, argRest(seq.next()));
    }

    IPersistentList evalArguments(ISeq seq, LexicalScope env) {

        ArrayList<Object> list = new ArrayList<>();
        for (; seq != RT.EOL; seq = seq.next()) {
            Object arg = seq.first();
            Object evaled = this.eval(arg, env);
            list.add(evaled);
        }
        return PersistentList.create(list);
    }

    Object invokeIFn(IFn ifn, ISeq sexp) {

        LOG.info("before invoke {}", ifn.getClass().getName());

        Object o = ifn.applyTo(sexp);

        LOG.info("retun {}  - {}", ifn.getClass().getName(), o);
        return o;
    }

}
