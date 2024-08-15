/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
/**
 *   Author: Masahito Hemmi
 */
package iris.clojure.readmacro;

import java.io.PushbackReader;
import java.util.ArrayList;

import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.IMapEntry;
import iris.clojure.lang.IObj;
import iris.clojure.lang.IPersistentCollection;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentSet;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.Nil;
import iris.clojure.lang.PersistentList;
import iris.clojure.lang.PersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Resolver;
import iris.clojure.lang.ResolverEx;
import iris.clojure.lang.Seqable;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyntaxQuoteReaderEx extends AFun {

    private static final Logger LOG = LoggerFactory.getLogger(SyntaxQuoteReaderEx.class);

//    static Symbol SEQ = Symbol.intern("clojure.core", "seq");
    static Symbol SEQ = Symbol.intern("iris.clojure.core", "seq");

//    static Symbol APPLY = Symbol.intern("clojure.core", "apply");
    static Symbol APPLY = Symbol.intern("iris.clojure.core", "apply");

    static Symbol HASHMAP = Symbol.intern("iris.clojure.core", "hash-map");

    static Symbol HASHSET = Symbol.intern("iris.clojure.core", "hash-set");
    static Symbol VECTOR = Symbol.intern("iris.clojure.core", "vector");

//    static Symbol CONCAT = Symbol.intern("clojure.core", "concat");
    static Symbol CONCAT = Symbol.intern("iris.clojure.core", "concat");

//    static Symbol LIST = Symbol.intern("clojure.core", "list");
    static Symbol LIST = Symbol.intern("iris.clojure.core", "list");

    static final Symbol FUNCALL = Symbol.intern("iris.clojure.core", "funcall");

    static final Symbol QUOTE = Symbol.intern(null, "quote");

    // 以下の２つは、別のクラスから参照します
    public static Symbol UNQUOTE = Symbol.intern("iris.clojure.core", "unquote");
    public static Symbol UNQUOTE_SPLICING = Symbol.intern("iris.clojure.core", "unquote-splicing");

    static Symbol WITH_META = Symbol.intern("iris.clojure.core", "with-meta");

    @Override
    public Object invoke(Object reader, Object backquote, Object opts, Object pendingForms,
            IClojureReader cr) {

        PushbackReader r = (PushbackReader) reader;

        Object form = cr.read(r, true, null, true, opts, cr.ensurePending(pendingForms));
        return syntaxQuote(form, cr);
    }

    Object syntaxQuote(Object form, IClojureReader cr) {

        Object ret;

        ResolverEx compilerResolver = cr.getCompilerResolver();

        // if (Compiler.isSpecialEx(form)) {
        if (compilerResolver.isSpecialEx(form)) {

            LOG.info("---------- case1 special");
            // ret = RT.list(Compiler.QUOTE, form);
            ret = RT.list(QUOTE, form);

        } else if (form instanceof Symbol) {

            LOG.info("---------- case2 symbol");
            ret = parseSymbol(cr, form);

        } else if (isUnquote(form)) {
            LOG.info("---------- case3 unquote");
            return RT.second(form);
        } else if (isUnquoteSplicing(form)) {
            LOG.info("---------- case4 unquote splicing");

            throw new IllegalStateException("splice not in list");

        } else if (form instanceof IPersistentCollection) {
            LOG.info("---------- case5 Collection");

            if (form instanceof IPersistentMap) {
                LOG.info("---------- case5-1   Map");
                IPersistentVector keyvals = flattenMap(form);
                ISeq newSeq = sqExpandList(keyvals.seq(), cr);

                //  ret = RT.list(APPLY,
                ret = RT.list(FUNCALL,
                        RT.list(QUOTE, HASHMAP),
                        RT.list(SEQ, RT.cons(CONCAT, newSeq)));

            } else if (form instanceof IPersistentVector) {
                LOG.info("---------- case5-2   []");

                ISeq seq3 = ((IPersistentVector) form).seq();
                ret = RT.list(SEQ, RT.cons(CONCAT, sqExpandList(seq3, cr)));
                ret = RT.list(APPLY,
                        RT.list(QUOTE, VECTOR),
                        ret);

            } else if (form instanceof IPersistentSet) {
                LOG.info("---------- case5-3   Set");

                ISeq seq4 = ((IPersistentSet) form).seq();
                ret = RT.list(SEQ, RT.cons(CONCAT, sqExpandList(seq4, cr)));
                ret = RT.list(APPLY,
                        RT.list(QUOTE, HASHSET),
                        ret);

            } else if (form instanceof ISeq || form instanceof IPersistentList) {
                LOG.info("---------- case5-4   ()  form={}", form.getClass().getName());
                ISeq seq = RT.seq(form);
                LOG.info("---------- case5-4b   {}", seq);
                if (seq == null) {
                    LOG.info("---------- case5-4c   ");
                    ret = RT.cons(LIST, null);
                } else {
                    LOG.info("---------- case5-4d   ");
                    ret = RT.list(SEQ, RT.cons(CONCAT, sqExpandList(seq, cr)));
                }
            } else {
                throw new UnsupportedOperationException("Unknown Collection type");
            }
        } else if (form instanceof Keyword
                || form instanceof Number
                || form instanceof Character
                || form instanceof String) {

            LOG.info("---------- case6   ");
            ret = form;
        } else {
            LOG.info("---------- case7   ");
            // ret = RT.list(Compiler.QUOTE, form);
            ret = RT.list(QUOTE, form);
        }

        if (originalLogic) {
            if (form instanceof IObj && RT.meta(form) != null) {
                //filter line and column numbers
                IPersistentMap newMeta = ((IObj) form).meta().without(RT.LINE_KEY).without(RT.COLUMN_KEY);
                if (newMeta.count() > 0) {
                    return RT.list(WITH_META, ret, syntaxQuote(((IObj) form).meta(), cr));
                }
            }
        } else {
            if (form instanceof IObj) {
                LOG.info("------ CHECK LOGIC !!");
                
               // return RT.list(WITH_META, ret  -- ???
            }
            
        }

        return ret;
    }

    boolean originalLogic = false;  // *******************************
    
    

    private Object parseSymbol(IClojureReader cr, Object form) throws UnsupportedOperationException {
        Object ret;
        Resolver resolver = cr.getReaderResolver();
        Symbol sym = (Symbol) form;
        if (sym.ns == null && sym.name.endsWith("#")) {

            throw new UnsupportedOperationException("Not supported yet.   symbol#");

        } else if (sym.ns == null && sym.name.endsWith(".")) {
            Symbol csym = Symbol.intern(null, sym.name.substring(0, sym.name.length() - 1));
            if (resolver != null) {
                Symbol rc = resolver.resolveClass(csym);
                if (rc != null) {
                    csym = rc;
                }
            } else {
                //  csym = Compiler.resolveSymbol(csym);
                ResolverEx compilerResolver = cr.getCompilerResolver();
                csym = compilerResolver.resolveSymbol(csym);
            }
            sym = Symbol.intern(null, csym.name.concat("."));
        } else if (sym.ns == null && sym.name.startsWith(".")) {
            // Simply quote method names.
        } else if (resolver != null) {
            Symbol nsym = null;
            if (sym.ns != null) {
                Symbol alias = Symbol.intern(null, sym.ns);
                nsym = resolver.resolveClass(alias);
                if (nsym == null) {
                    nsym = resolver.resolveAlias(alias);
                }
            }
            if (nsym != null) {
                // Classname/foo -> package.qualified.Classname/foo
                sym = Symbol.intern(nsym.name, sym.name);
            } else if (sym.ns == null) {
                Symbol rsym = resolver.resolveClass(sym);
                if (rsym == null) {
                    rsym = resolver.resolveVar(sym);
                }
                if (rsym != null) {
                    sym = rsym;
                } else {
                    sym = Symbol.intern(resolver.currentNS().name, sym.name);
                }
            }
            //leave alone if qualified
        } else {
            Object maybeClass = null;
            if (sym.ns != null) {
                //   maybeClass = Compiler.currentNS().getMapping(
                //          Symbol.intern(null, sym.ns));

                ResolverEx compilerResolver = cr.getCompilerResolver();
                maybeClass = compilerResolver.getCurrentNSMapping(Symbol.intern(null, sym.ns));
            }
            if (maybeClass instanceof Class) {
                // Classname/foo -> package.qualified.Classname/foo
                sym = Symbol.intern(
                        ((Class) maybeClass).getName(), sym.name);
            } else {
                // sym = Compiler.resolveSymbol(sym);
                ResolverEx compilerResolver = cr.getCompilerResolver();
                sym = compilerResolver.resolveSymbol(sym);
            }
        }
        // ret = RT.list(Compiler.QUOTE, sym);
        ret = RT.list(QUOTE, sym);
        return ret;
    }

    static boolean isUnquoteSplicing(Object form) {
        
        if (form instanceof Nil) {
            return false;
        }
        return form instanceof ISeq && Util.equals(RT.first(form), UNQUOTE_SPLICING);
    }

    static boolean isUnquote(Object form) {
//        return form instanceof ISeq && Util.equals(RT.first(form), UNQUOTE);

        if (form instanceof Nil) {
            return false;
        }

        if (form instanceof Seqable) {
            ISeq seq = ((Seqable) form).seq();
            Object first = seq.first();         // RT.first(form);
            return Util.equals(first, UNQUOTE);
        } else {
            return false;
        }

    }

    private ISeq sqExpandList(ISeq seq, IClojureReader cr) {
        /*
        PersistentVector ret = PersistentVector.EMPTY;
         */

        ArrayList<Object> list = new ArrayList<>();

        for (; seq != RT.EOL; seq = seq.next()) {
            Object item = seq.first();
            if (isUnquote(item)) {
                // ret = ret.cons(RT.list(LIST, RT.second(item)));
                list.add(RT.list(LIST, RT.second(item)));

            } else if (isUnquoteSplicing(item)) {
                //    ret = ret.cons(RT.second(item));
                list.add(RT.second(item));
            } else {
                //  ret = ret.cons(RT.list(LIST, syntaxQuote(item)));

                Object syntaxQuote = syntaxQuote(item, cr);  // Bug?? Pending

                list.add(RT.list(LIST, syntaxQuote));
            }
        }

//        PersistentVector vec = new PersistentVector(list);  
//        return vec.seq();                                  // Bug !!
        return PersistentList.create(list);  // debug
    }

    private static IPersistentVector flattenMap(Object form) {
        IPersistentVector keyvals = PersistentVector.EMPTY;
        for (ISeq s = RT.seq(form); s != RT.EOL; s = s.next()) {
            IMapEntry e = (IMapEntry) s.first();
            keyvals = (IPersistentVector) keyvals.cons(e.key());
            keyvals = (IPersistentVector) keyvals.cons(e.val());
        }
        return keyvals;
    }

}
