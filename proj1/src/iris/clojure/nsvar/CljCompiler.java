/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.nsvar;

import iris.clojure.lang.IFn;
import iris.clojure.lang.RTPrinter;

import iris.clojure.lang.Symbol;
import iris.clojure.lang.TransMap;
import iris.clojure.lang.Util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

/**
 * This class is a minimal implementation similar to
 * <b>clojure.lang.Compiler.java</b> .
 *
 *
 * <b>Name changed from Compiler.java to CljCompiler.java .</b>
 * <div>Because the name is the same as java.lang.Compiler</div>
 */
public class CljCompiler {

    //  Common-Lisp
    public static Namespace COMMON_LISP_NS;

    /**
     * Variable to store current namespace .
     */
    public static Var CURRENT_NS;

    // iris.clojure.core
    public static Namespace IRIS_NS;

    // clojure.core
    public static Namespace CLOJURE_NS;
    
    
    
//    public static Var LOADED_LIBS;
    public static Set<Symbol> LOADED_LIBS;
    
    
    public static void addLoadedLibs(Symbol sym) {
        LOADED_LIBS.add(sym);
    }
    
    
    
    
    
    
    

//    final static public Var USE_CONTEXT_CLASSLOADER    = Var.intern(CLOJURE_NS, Symbol.intern("*use-context-classloader*"), T).setDynamic();
 /*
    public static Var USE_CONTEXT_CLASSLOADER;

    static public ClassLoader baseLoader() {
        if (CljCompiler.LOADER.isBound()) {
            return (ClassLoader) CljCompiler.LOADER.deref();
        } else if (RT.booleanCast(USE_CONTEXT_CLASSLOADER.deref())) {
            return Thread.currentThread().getContextClassLoader();
        }
        return CljCompiler.class.getClassLoader();
    }

    static public Class classForName(String name, boolean load, ClassLoader loader) {

        try {
            Class c = null;
            if (!(loader instanceof DynamicClassLoader)) {
                c = DynamicClassLoader.findInMemoryClass(name);
            }
            if (c != null) {
                return c;
            }
            return Class.forName(name, load, loader);
        } catch (ClassNotFoundException e) {
            throw Util.sneakyThrow(e);
        }
    }

    static public Class classForName(String name) {
        return classForName(name, true, baseLoader());
    }
*/
    // =====================================
    public static final Symbol _AMP_ = Symbol.intern("&");

    // rename isSpecial to isSpecialEx
    public static boolean isSpecialEx(Object sym) {

        return specials.containsKey(sym);
    }

    public static final TransMap specials = new TransMap();

    public static Object getSpecial(Object key) {
        return specials.valAt(key);
    }

    public static void putSpecial(final Object key, final Object val) {
        specials.put(key, val);
    }

    public static void defSpecial(final String name, final Object val) {

        Symbol sym = Symbol.intern(null, name);
        specials.put(sym, val);
    }
    
    
    public static Var defun(String name, IFn body) {
        Namespace nsj = IRIS_NS;
        Var v = Var.intern(nsj, Symbol.intern(null, name));
        v.setFunction(body); // for Common Lisp
        //   v.bindRoot(body);
        return v;
    }
    
    public static Var defun(Namespace nsj, String name, IFn body) {
        Var v = Var.intern(nsj, Symbol.intern(null, name));
        v.setFunction(body); // for Common Lisp
        //   v.bindRoot(body);
        return v;
    }
    
    
    

    public static Namespace currentNS() {
        return (Namespace) CURRENT_NS.deref();
    }

    //DynamicClassLoader
    static final public Var LOADER = Var.create().setDynamic();

    public static Symbol resolveSymbol(Symbol sym) {
        //already qualified or classname?
        if (sym.name.indexOf('.') > 0) {
            return sym;
        }

        /*
        if (sym.ns != null) {
            Namespace ns = namespaceFor(sym);
            if (ns == null || (ns.name.name == null ? sym.ns == null : ns.name.name.equals(sym.ns))) {
                return sym;
            }
            return Symbol.intern(ns.name.name, sym.name);
        }
        Object o = currentNS().getMapping(sym);
        if (o == null) {
            return Symbol.intern(currentNS().name.name, sym.name);
        } else if (o instanceof Class) {
            return Symbol.intern(null, ((Class) o).getName());
        } else if (o instanceof Var) {
            Var v = (Var) o;
            return Symbol.intern(v.ns.name.name, v.sym.name);
        }
        return null;
         */
        throw new RuntimeException();
    }

    static public boolean subsumes(Class<?>[] c1, Class<?>[] c2) {
        //presumes matching lengths
        Boolean better = false;
        for (int i = 0; i < c1.length; i++) {
            if (c1[i] != c2[i])// || c2[i].isPrimitive() && c1[i] == Object.class))
            {
                if (!c1[i].isPrimitive() && c2[i].isPrimitive()
                        //|| Number.class.isAssignableFrom(c1[i]) && c2[i].isPrimitive()
                        || c2[i].isAssignableFrom(c1[i])) {
                    better = true;
                } else {
                    return false;
                }
            }
        }
        return better;
    }

    // Moved here from  RT
    public static void print(Object x, Writer w) throws IOException {

        new RTPrinter(true, false).print(x, w);
    }

    public static String printString(Object x) {
        try {
            StringWriter sw = new StringWriter();
            print(x, sw);
            sw.flush();
            return sw.toString();
        } catch (Exception e) {
            throw Util.sneakyThrow(e);
        }
    }

}
