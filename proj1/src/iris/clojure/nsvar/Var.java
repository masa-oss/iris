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

/**
 * Author: Masahito Hemmi
 */

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import iris.clojure.lang.AFn;
import iris.clojure.lang.Associative;
import iris.clojure.lang.ICommonLispFn;
import iris.clojure.lang.IDeref;
import iris.clojure.lang.IFn;
import iris.clojure.lang.IMapEntry;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.LispPrintable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a subset of <i>clojure.lang.Var.java</i> .
 */
public final class Var extends AReference implements IDeref, LispPrintable, ICommonLispFn, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(Var.class);

    /**
     * Inner class, not used in this interpreter .
     */
    public static class TBox {

        volatile Object val;
        final Thread thread;

        public TBox(Thread t, Object val) {
            this.thread = t;
            this.val = val;
        }
    }

    /**
     * A class that represents unbound .
     */
    public static class Unbound extends AFn {

        final public Var v;

        public Unbound(Var v) {
            this.v = v;
        }

	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
        @Override
        public String toString() {
//		return "Unbound: " + v;    // It will be an infinite loop
            return "Unbound[]";
        }

        @Override
        public Object throwArity(int n) {
            throw new IllegalStateException("Attempting to call unbound fn: " + v);
        }
    }

    static class Frame {

        final static Frame TOP = new Frame(PersistentHashMap.EMPTY, null);
        //Var->TBox
        Associative bindings;
        Frame prev;

        public Frame(Associative bindings, Frame prev) {
            this.bindings = bindings;
            this.prev = prev;
        }

        @Override
        protected Object clone() {

            return new Frame(this.bindings, null);
        }
    }

    static final ThreadLocal<Frame> dvals = new ThreadLocal<Frame>() {

        @Override
        protected Frame initialValue() {
            return Frame.TOP;
        }
    };

    static public volatile int rev = 0;

    static Keyword macroKey = Keyword.intern(null, "macro");

    volatile Object root;

    volatile boolean dynamic = false;
    transient final AtomicBoolean threadBound;
    public final Symbol sym;
    public final Namespace ns;

	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Var[");

        if (sym != null) {
            sb.append(sym.getStringForPrint());
        } else {
            sb.append("sym=null");
        }

        sb.append(", root=");
        if (root != null) {
            if (root instanceof LispPrintable) {
                LispPrintable hr = (LispPrintable) root;

                sb.append(hr.getStringForPrint());

            } else {
                sb.append(root.toString());
            }

        } else {
            sb.append("null");
        }

        if (dynamic) {
            sb.append(", dynamic");
        }
        boolean mac = isMacro();
        if (mac) {
            sb.append(", macro");
        }

        if (threadBound.get()) {
            sb.append(", threadBound");
        }

        sb.append("]");
        return sb.toString();
    }

    //IPersistentMap _meta;
    public static Object getThreadBindingFrame() {
        return dvals.get();
    }

    public static Object cloneThreadBindingFrame() {
        return dvals.get().clone();
    }

    public static void resetThreadBindingFrame(Object frame) {
        dvals.set((Frame) frame);
    }

    public Var setDynamic() {
        this.dynamic = true;
        return this;
    }

    public Var setDynamic(boolean b) {
        this.dynamic = b;
        return this;
    }

    public final boolean isDynamic() {
        return dynamic;
    }

    public static Var intern(Namespace ns, Symbol sym, Object root) {

        return intern(ns, sym, root, true);
    }

    public static Var intern(Namespace ns, Symbol sym, Object root, boolean replaceRoot) {

        Var dvout = ns.intern(sym);
        if (!dvout.hasRoot() || replaceRoot) {
            dvout.bindRoot(root);
        }
        return dvout;
    }

    public static Var var(String s, String n) {
        return Var.intern(Symbol.intern(null, s), Symbol.intern(null, n));
    }

    // rename by Hemmi
    public String toStringOriginal() {
        if (ns != null) {
            return "#'" + ns.name + "/" + sym;
        }
        return "#<Var: " + (sym != null ? sym.toString() : "--unnamed--") + ">";
    }

    public static Var find(Symbol nsQualifiedSym) {
        if (nsQualifiedSym.ns == null) {
            throw new IllegalArgumentException("Symbol must be namespace-qualified");
        }
        Namespace ns = Namespace.find(Symbol.intern(nsQualifiedSym.ns));
        if (ns == null) {
            throw new IllegalArgumentException("No such namespace: " + nsQualifiedSym.ns);
        }
        return ns.findInternedVar(Symbol.intern(nsQualifiedSym.name));
    }

    public static Var intern(Symbol nsName, Symbol sym) {

        Namespace ns = Namespace.findOrCreate(nsName);
        return intern(ns, sym);
    }

    public static Var internPrivate(String nsName, String sym) {

        Namespace ns = Namespace.findOrCreate(Symbol.intern(nsName));
        Var ret = intern(ns, Symbol.intern(sym));
        ret.setMeta(PRIVATE_META);
        return ret;
    }

    public static Var intern(Namespace ns, Symbol sym) {
        return ns.intern(sym);
    }

    public static Var create() {
        return new Var(null, null);
    }

    public static Var create(Object root) {
        return new Var(null, null, root);
    }

    Var(Namespace ns, Symbol sym) {
        this.ns = ns;
        this.sym = sym;
        this.threadBound = new AtomicBoolean(false);
        this.root = new Unbound(this);
        setMeta(PersistentHashMap.EMPTY);
    }

    Var(Namespace ns, Symbol sym, Object root) {
        this(ns, sym);
        this.root = root;
        ++rev;
    }

    public boolean isBound() {
        return hasRoot() || (threadBound.get() && dvals.get().bindings.containsKey(this));
    }

    final public Object get() {
        if (!threadBound.get()) {
            return root;
        }
        return deref();
    }

    @Override
    public final Object deref() {
        TBox b = getThreadBinding();
        if (b != null) {
            return b.val;
        }
        return root;
    }
/*
    public void setValidator(IFn vf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object alter(IFn fn, ISeq args) {
        throw new UnsupportedOperationException("Not supported yet.");
        
	//set(fn.applyTo(RT.cons(deref(), args)));
	//return this;
    }
*/
    public Object set(Object val) {
//	validate(getValidator(), val);
        TBox b = getThreadBinding();
        if (b != null) {
            if (Thread.currentThread() != b.thread) {
                throw new IllegalStateException(String.format("Can't set!: %s from non-binding thread", sym));
            }
            return (b.val = val);
        }
        LOG.info("Var(248) {}", this);
        throw new IllegalStateException(String.format("Can't change/establish root binding of: %s with set", sym));
    }

    public Object doSet(Object val) {
        return set(val);
    }

    public Object doReset(Object val) {
        bindRoot(val);
        return val;
    }

    public void setMeta(IPersistentMap m) {
        //ensure these basis keys
//    resetMeta(m.assoc(nameKey, sym).assoc(nsKey, ns));

        super.resetMeta(m);
    }

    public void setMacro() {
        alterMeta(assoc, RT.list(macroKey, RT.T));
    }

    public boolean isMacro() {
        //    return RT.booleanCast(meta().valAt(macroKey));
        final String NOT_FOUND = "*NOT-FOUND*";
        IPersistentMap meta = meta();

        Object valAt = meta.valAt(macroKey, NOT_FOUND);

        if (NOT_FOUND.equals(valAt)) {
            return false;
        }
        return RT.booleanCast(valAt);
    }

//public void setExported(boolean state){
//	_meta = _meta.assoc(privateKey, state);
//}
    final static Keyword privateKey = Keyword.intern(null, "private");

//    static IPersistentMap privateMeta = new PersistentArrayMap(new Object[]{privateKey, Boolean.TRUE});
    public static IPersistentMap PRIVATE_META = PersistentHashMap.create(new Object[]{privateKey, Boolean.TRUE});

    public boolean isPublic() {

        final String NOT_FOUND = "*NOT-FOUND*";

        IPersistentMap meta = meta();

        Object valAt = meta.valAt(privateKey, NOT_FOUND);

        if (NOT_FOUND.equals(valAt)) {
            return true;
        }

        //   LOG.info("isPublic  map.class={}  valAt={}", meta.getClass().getName(), valAt);
        boolean b = !RT.booleanCast(valAt);

        //   LOG.info("isPublic  returns : {}", b);
        return b;
    }

    final public Object getRawRoot() {
        return root;
    }

    public Object getTag() {
        return meta().valAt(RT.TAG_KEY);
    }

    public void setTag(Symbol tag) {
        alterMeta(assoc, RT.list(RT.TAG_KEY, tag));
    }

    final public boolean hasRoot() {
        return !(root instanceof Unbound);
    }

    //binding root always clears macro flag
    synchronized public void bindRoot(Object root) {
        /*
	validate(getValidator(), root);
	Object oldroot = this.root;
         */
        this.root = root;
        /*
	++rev;
//        alterMeta(dissoc, RT.list(macroKey));  // remove hemmi
    notifyWatches(oldroot,this.root);
         */
    }

    /*
    synchronized void swapRoot(Object root) {
            
	validate(getValidator(), root);
	Object oldroot = this.root;
	this.root = root;
	++rev;
    notifyWatches(oldroot,root);
        
    }*/

    synchronized public void unbindRoot() {
        this.root = new Unbound(this);
        ++rev;
    }
/*
    synchronized public void commuteRoot(IFn fn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    synchronized public Object alterRoot(IFn fn, ISeq args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
*/
    
    
//    public static void pushThreadBindings(Associative bindings) {
    public static void pushThreadBindings(IPersistentMap bindings) {
        Frame f = dvals.get();
        //   Associative bmap = f.bindings;
        IPersistentMap bmap = (IPersistentMap) f.bindings;

        for (ISeq bs = bindings.seq(); bs != RT.EOL; bs = bs.next()) {
            IMapEntry e = (IMapEntry) bs.first();
            Var v = (Var) e.key();
            if (!v.dynamic) {
                throw new IllegalStateException(String.format("Can't dynamically bind non-dynamic var: %s/%s", v.ns, v.sym));
            }
            //	v.validate(v.getValidator(), e.val());
            v.threadBound.set(true);
            //  bmap = bmap.assoc(v, new TBox(Thread.currentThread(), e.val()));
            bmap = bmap.assocEx(v, new TBox(Thread.currentThread(), e.val()));
        }
        dvals.set(new Frame(bmap, f));
    }

    public static void popThreadBindings() {
        Frame f = dvals.get().prev;
        if (f == null) {
            throw new IllegalStateException("Pop without matching push");
        } else if (f == Frame.TOP) {
            dvals.remove();
        } else {
            dvals.set(f);
        }
    }

    public static Associative getThreadBindings() {
        Frame f = dvals.get();
        IPersistentMap ret = PersistentHashMap.EMPTY;
        for (ISeq bs = f.bindings.seq(); bs != RT.EOL; bs = bs.next()) {
            IMapEntry e = (IMapEntry) bs.first();
            Var v = (Var) e.key();
            TBox b = (TBox) e.val();
            //ret = ret.assoc(v, b.val);
            ret = ret.assocEx(v, b.val);
        }
        return ret;
    }

    public final TBox getThreadBinding() {
        if (threadBound.get()) {
            IMapEntry e = dvals.get().bindings.entryAt(this);
            if (e != null) {
                return (TBox) e.val();
            }
        }
        return null;
    }

    final public IFn fn() {
        return (IFn) deref();
    }

    public Object call() {
        return invoke();
    }

    public void run() {
        invoke();
    }

    public Object invoke() {
        return fn().invoke();
    }

    static IFn assoc = new AFn() {
        @Override
        public Object invoke(Object m, Object k, Object v) {
            return RT.assoc(m, k, v);
        }
    };

    static IFn dissoc = new AFn() {
        @Override
        public Object invoke(Object c, Object k) {
            return RT.dissoc(c, k);
        }
    };

    /**
     * *
     * Note - serialization only supports reconnecting the Var identity on the
     * deserializing end Neither the value in the var nor any of its properties
     * are serialized
     *
     *
    private Object writeReplace() throws ObjectStreamException {
        throw new RuntimeException("not impl yet.");
    }
     */
    
    

    // common lisp
    private Object function;

    public void setFunction(Object fun) {
        this.function = fun;
    }

    @Override
    public Object getFunction() {
        return this.function;
    }

    @Override
    public String getStringForPrint() {

        Var v = this;
        if (v.ns == null) {
           // return ("#=(var " + v.sym + ")");
            return ("(var " + v.sym + ")");
        } else {
           // return ("#=(var " + v.ns.name + "/" + v.sym + ")");
            return ("(var " + v.ns.name + "/" + v.sym + ")");
        }
    }

}
