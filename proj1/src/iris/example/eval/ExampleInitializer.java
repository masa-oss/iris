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

import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;

import iris.clojure.lang.IFn;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.PersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;

import iris.example.fn.Func1;
import iris.example.fn.Func2;
import iris.example.fn.Func3;
import iris.example.fn.LoadFile;
import iris.example.fn.LoadFileClassPath;
import iris.example.fn.ReadFn;
import iris.example.fn.MyRequire;

import iris.example.test.Tests;

import static iris.clojure.nsvar.CljCompiler.defSpecial;
import static iris.clojure.nsvar.CljCompiler.defun;

import iris.example.fn.InternalDefun;
import iris.example.fn.SampleRest;

import iris.example.fn.StrFn;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.TreeSet;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialize RT.java's RT.DEFAULT_IMPORTS .
 * And add built-in functions for this lisp
 * interpreter .
 *
 */
public class ExampleInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleInitializer.class);

    public static final String USER_CORE = "user.core";

    /**
     *
     */
    public static void initCompiler() {

        CljCompiler.CLOJURE_NS = Namespace.findOrCreate(Symbol.intern("clojure.core"));
        CljCompiler.COMMON_LISP_NS = Namespace.findOrCreate(Symbol.intern("common.lisp"));

        //  initialize RT.DEFAULT_IMPORTS
        RT.DEFAULT_IMPORTS = RT.map(
                Symbol.intern("System"), System.class,
                Symbol.intern("String"), String.class,
                Symbol.intern("StringBuilder"), StringBuilder.class,
                Symbol.intern("Math"), Math.class,
                Symbol.intern("Integer"), Integer.class,
                Symbol.intern("Long"), Long.class,
                Symbol.intern("Double"), Double.class,
                Symbol.intern("Exception"), Exception.class,
                Symbol.intern("RuntimeException"), RuntimeException.class,

                Symbol.intern("URI"), URI.class,
                Symbol.intern("URL"), URL.class,
                Symbol.intern("File"), File.class,
                Symbol.intern("FileInputStream"), FileInputStream.class,
                Symbol.intern("ByteArrayInputStream"), ByteArrayInputStream.class,
                Symbol.intern("NonLocalException"), NonLocalException.class
        );

        CljCompiler.IRIS_NS = Namespace.findOrCreate(Symbol.intern("iris.clojure.core"));

        TreeSet<Symbol> ts = new TreeSet<>(new SymComp());
        ts.add(Symbol.intern(null, "iris.clojure.core"));
        ts.add(Symbol.intern(null, "clojure.core"));
        ts.add(Symbol.intern(null, "common.lisp"));
        CljCompiler.LOADED_LIBS = ts;

        // declare specials
        defSpecial("quote", new Specials.QuoteSpecial());

        defSpecial("if", new Specials.IfSpecial());

        defSpecial("block", new Specials.BlockSpecial());

        defSpecial("new", new NewNewSpecial());

        defSpecial("dolist", new Specials.DoListSpecial());

        // deprecated
        defSpecial("s*defun", new Specials.DefunSpecial());

        // deprecated
        defSpecial("s*defmacro", new Specials.DefmacroSpecial());

        defSpecial("defconstant", new Specials.DefconstantSpecial());

        defSpecial("var", new Specials.VarSpecial());

        defSpecial("let", new Specials.LetSpecial());

        defSpecial("cond", new Specials.CondSpecial());

        defSpecial("function", new Specials.FunctionSpecial());

        defSpecial("flet", new Specials.FletSpecial());

        defSpecial("with-open-file", new Specials.WithOpenFileSpecial());

        defSpecial("with-open", new WithOpenSpecial());

        defSpecial("try", new TrySpecial());   // ------------------------

        defSpecial("setq", new Specials.SetqSpecial());
        defSpecial("set!", new Specials.SetqSpecial());

        defSpecial("while", new Specials.WhileSpecial());

        defSpecial("progn", new Specials.PrognSpecial());

        defSpecial("defonce", new Specials.DefonceSpecial());

        defSpecial("call/cc", new Specials.CallCcSpecial());

        defSpecial("return-from", new Specials.ReturnFromSpecial());

        defSpecial("*test", new Tests.Test());

        // The following is not special-form s. ## reader macros.
        // ##Inf   (reader macro)
        defSpecial("Inf", Double.POSITIVE_INFINITY);

        // ##-Inf   (reader macro)
        defSpecial("-Inf", Double.NEGATIVE_INFINITY);

        // ##NaN   (reader macro)
        defSpecial("NaN", Double.NaN);

        // ##undef   (reader macro)
        defSpecial("undef", new Var.Unbound(null));   // ##undef

        CljCompiler.CURRENT_NS = Var.intern(CljCompiler.IRIS_NS, Symbol.intern("*ns*"), CljCompiler.IRIS_NS).setDynamic();

        // Clojure's history
        Globals.HISTORY1 = Var.intern(CljCompiler.IRIS_NS, Symbol.intern("*1"), null).setDynamic();
        Globals.HISTORY2 = Var.intern(CljCompiler.IRIS_NS, Symbol.intern("*2"), null).setDynamic();
        Globals.HISTORY3 = Var.intern(CljCompiler.IRIS_NS, Symbol.intern("*3"), null).setDynamic();

        {
            // Common Lisp's history , but * not support
            Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();
            ns.internVar(Symbol.intern(null, "**"), Globals.HISTORY2);
            ns.internVar(Symbol.intern(null, "***"), Globals.HISTORY3);
        }

        defun("in-ns", new Func1.InNs());

        defun("load-file", new LoadFile());

        defun("*load-file", new LoadFileClassPath()); 

        defun("+", new Func1.MyAdd());
        defun("-", new Func1.MySub());

        defun("*", new Func1.MyMultiply());
        defun("/", new Func1.MyDivide());

        defun("quotient", new Func1.MyQuotient());
        defun("remainder", new Func1.MyRemainder());

        defun("<", new Func1.MyLt());
        defun("<=", new Func1.MyLte());

        defun(">", new Func1.MyGt());
        defun(">=", new Func1.MyGte());

        defun("=", new Func1.MyEquiv());

        defun("inc", new Func1.MyInc());
        defun("dec", new Func1.MyDec());
        defun("deref", new Func1.MyDeref());

        defun("even?", new Func2.EvenQ());

        defun("boolean?", new Func3.IsBoolean());
        defun("char?", new Func3.IsChar());
        defun("characterp", new Func3.IsChar());

        defun("cons?", new Func3.IsCons());
        defun("consp", new Func3.IsCons());

        defun("list?", new Func3.IsList());
        defun("listp", new Func3.IsList());

        defun("map?", new Func3.IsMap());
        defun("set?", new Func3.IsSet());
        defun("vector?", new Func3.IsVector());
        defun("string?", new Func3.IsString());
        defun("stringp", new Func3.IsString());
        defun("number?", new Func3.IsNumber());
        defun("numberp", new Func3.IsNumber());

        defun("instance?", new Func3.IsInstance());

        defun("seq", new Func1.MySeq());
        defun("concat", new Func1.MyConcat());

        defun("hash-map", new Func1.MyHashMap());

        defun("vector", new Func1.MyVector());
        defun("hash-set", new Func1.MyHashset());

        defun("with-meta", new Func1.WithMeta());
        defun("meta", new Func1.Meta());

        defun("first", new Func1.First());
        defun("car", new Func1.Car());
        defun("next", new Func1.Next());
        defun("cdr", new Func1.Cdr());

        defun("caar", new Func1.Caar());
        defun("cadr", new Func1.Cadr());

        defun("cdar", new Func1.Cdar());
        defun("cddr", new Func1.Cddr());

        defun("caddr", new Func1.Caddr());
        defun("cdddr", new Func1.Cdddr());

        defun("filter", new Func2.FilterFn());

        defun("loaded-libs", new Func2.LoadedLibs());

        defun("import", new Func2.MyImport());
        defun("require", new MyRequire(new StaticNamespaceImpl(), new LoadFileClassPath()));

        defun("class", new Func2.MyClass());

        // https://tnoda-clojure.tumblr.com/post/134976322091/cheatsheet-literals
        defun("byte", new Func2.MyByte());
        defun("short", new Func2.MyShort());

        defun("int", new Func2.MyInt());
        defun("float", new Func2.MyFloat());
        defun("double", new Func2.MyDouble());

        defun("byte-array", new Func2.ByteArray());

        defun("aset-byte", new Func2.AsetByte());
        defun("aget-byte", new Func2.AgetByte());

        defun("keys", new Func2.MyKeys());

        defun("vals", new Func2.MyVals());

        defun("contains?", new Func2.MyContains());

        defun("range", new Func2.MyRange());

        defun("split-at", new Func2.SplitAt());

        defun("sort", new Func2.MySort());

        defun("merge", new Func2.Merge());

        defun("union", new Func2.MyUnion());

        defun("intersection", new Func2.MyIntersection());

        defun("select-keys", new Func2.MySelectKeys());

        // https://qiita.com/hatappo/items/e292bd2132a89cfd6761
        //     defun("reader", new ReaderFn());
        defun("read", new ReadFn());

        defun("dissoc", new Func2.MyDissoc());

        defun("throw", new Func3.MyThrow());

        defun("gensym", new Func3.GenSym());

        defun("str", new StrFn());

        defun("*vec", new Func3.ScopeFn());

        // ------ Common Lisp like functions.
        defun("*internal-defun", new InternalDefun());

        defun("eq", new Func1.Eq());

        defun("eql", new Func1.Eql());

        defun("list", new Func1.MyList());

        defun("funcall", new Func1.Funcall());

        defun("cons", new Func1.MyCons());

        defun("write", new Func1.MyWrite());

        defun("pr", new Func3.MyPrn(false, true));
        defun("prin1", new Func3.MyPrn(false, true));

        defun("terpri", new Func3.Terpri());

        defun("newline", new Func3.Terpri());

        defun("prn", new Func3.MyPrn(true, true));   // = common lisp print

        defun("println", new Func3.MyPrn(true, false));

        defun("princ", new Func3.MyPrn(false, false));

        defun("apply", new Func1.Apply());

        // ------
        defun("mapcar", new Func2.MapCar());

        defun("symbol-function", new Func2.SymbolFunction());

        defun("symbolp", new Func2.SymbolpFn());

        defun("null", new Func2.MyNull());
        defun("not", new Func2.MyNot());

        defun("quit", new Func2.Quit());
        defun("exit", new Func2.Quit());

        defun("append", new Func3.Append());

        defun("macroexpand-1", new Func3.MacroExpand1Fn());

        defun("length", new Func3.Length());

        defun("ns-map", new Func3.NsMappingFn());

        defun("ns-aliases", new Func3.NsAliasesFn());

        defun("nth", new Func3.NthFn());

        defun("*env", new Func3.EnvFn());

        //------
        defun("*class", new Func3.JavaClazz());

        defun("*start-testing", new Tests.StartTesting());

        defun("*successful", new Tests.GetSuccessful());
        defun("*test-failed", new Tests.GetTestFailed());

        defun("sample-rest", new SampleRest());
        
        
        defmacro(CljCompiler.IRIS_NS, "incf", new Macros.IncfMacro());

        defmacro(CljCompiler.IRIS_NS, "ns", new Macros.NamespaceMacro());

        defmacro(CljCompiler.IRIS_NS, "lambda", new Macros.LambdaMacro());

        defmacro(CljCompiler.IRIS_NS, "setf", new SetfMacro());

        defmacro(CljCompiler.IRIS_NS, "defun", new DefunRest(false));
        defmacro(CljCompiler.IRIS_NS, "defmacro", new DefunRest(true));

        //------ clojure.core
        defun(CljCompiler.CLOJURE_NS, "assoc", new Func2.MyAssoc());

        defun(CljCompiler.CLOJURE_NS, "get", new Func2.MyGet());

        defun(CljCompiler.CLOJURE_NS, "print", new Func3.MyPrn(false, false));

        // --- common lisp
        defun(CljCompiler.COMMON_LISP_NS, "get", new Func3.GetPropFn());

        defun(CljCompiler.COMMON_LISP_NS, "*putprop", new Func3.PutPropFn());

        defun(CljCompiler.COMMON_LISP_NS, "assoc", new Func3.CommonAssoc());

        defun(CljCompiler.COMMON_LISP_NS, "print", new Func3.MyPrn(true, true));   // = common lisp print

        defun(CljCompiler.COMMON_LISP_NS, "atom", new Func2.Atom());

        //------
        // Create a 'user.core' after registering the initialization process .
     //   Namespace.INIT_LOGIC = new SearchPublicFn();

        Var currNs = CljCompiler.CURRENT_NS;
        Namespace user = Namespace.findOrCreate(Symbol.intern(USER_CORE));
        currNs.bindRoot(user);

        //    putVariableValue(Symbol.intern(null, "undef"), null);


        // *****************
        changeNamespaceToUser();
        
        LOG.info("418) {}",  CljCompiler.CURRENT_NS.deref()  );
        
        List<Object> reqArg = new ArrayList<>();
        reqArg.add( Symbol.intern(null, "iris.clojure.core")  );
        reqArg.add( Keyword.intern( Symbol.intern(null, "refer")   )  );
        reqArg.add( Keyword.intern( Symbol.intern(null, "all")   )  );
        
        PersistentVector vec = new PersistentVector(reqArg);
        IFn reqFn = new MyRequire(new StaticNamespaceImpl(), new LoadFileClassPath());

        reqFn.invoke(vec);

    }

    public static Var defmacro(Namespace nsj, String name, IFn body) {
        Var v = Var.intern(nsj, Symbol.intern(null, name));
        v.setMacro();
        v.setFunction(body);
        return v;
    }

    /*
    static Object putVariableValue(Symbol sym, Object val) {

        if (sym.getNamespace() != null) {
            throw new RuntimeException("getNamespace() != null :  " + sym.getName());
        }

        Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();

        Var findInternedVar = ns.findInternedVar(sym);
        if (findInternedVar != null) {
            throw new RuntimeException("already exists  :  " + sym.getName());

        }

        Var v = ns.intern(sym);
        v.bindRoot(val);
        return sym;
    }
     */
    public static void changeNamespaceToUser() {

//        CljCompiler.IRIS_NS = Namespace.findOrCreate(Symbol.intern(USER_CORE));

        Namespace user = Namespace.findOrCreate(Symbol.intern(USER_CORE));
        CljCompiler.CURRENT_NS.bindRoot( user);

    }

    static class SymComp implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {

            Symbol s1 = (Symbol) o1;
            Symbol s2 = (Symbol) o2;
            String a1 = s1.getStringForPrint();
            String a2 = s2.getStringForPrint();

            return a1.compareTo(a2);
        }

    }
}
