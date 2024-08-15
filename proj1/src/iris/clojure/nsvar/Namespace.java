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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicReference;

import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.ListSeq;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.TransMap;
import iris.clojure.lang.LispPrintable;

/**
 * This class is a subset of <i>clojure.lang.Namespace.java</i> .
 * 
 * In the clojure source code, it was AtomicReference and IPersistentMap.
 * 
 * This source file has been changed to TransMap .
 */
public class Namespace implements LispPrintable, INamespace {

    
    /**
     * 
     * In the Clojure source code, when the Namespace is created, the object registered in Compiler.DEFAULT_IMPORT should be registered.
     * 
     * In this source, When a new instance is created,
     * Execute the logic set in the following variables
     * 
     */
 //   public static DefaultImporter INIT_LOGIC;
    
    
    //  transient final AtomicReference<IPersistentMap> mappings = new AtomicReference<IPersistentMap>();
    //  transient final AtomicReference<IPersistentMap> aliases = new AtomicReference<IPersistentMap>();
    TransMap mappings = new TransMap();
    TransMap aliases = new TransMap();

    final static ConcurrentHashMap<Symbol, Namespace> namespaces = new ConcurrentHashMap<>();
    //   public static ConcurrentHashMap<Symbol, Namespace> namespaces ;

    public Symbol name;

    Namespace(Symbol name) {

        this.name = name;

        if (RT.DEFAULT_IMPORTS != null) {
            mappings.putAll(RT.DEFAULT_IMPORTS);
        }
        //aliases.set(RT.map());
    }

    @Override
    public String getStringForPrint() {

        return "#<ns \"" + name.getStringForPrint() + "\">";
    }

    @Override
    public String getName() {

        return name.getName();
    }
    
    
    public static Namespace findOrCreate(Symbol name /*, boolean init*/) {

        Namespace ns = namespaces.get(name);
        if (ns != null) {
            return ns;
        }
        Namespace newns = new Namespace(name);
/*        
        if (init && (INIT_LOGIC != null)) {
            INIT_LOGIC.setup(newns);
        }
  */      
        ns = namespaces.putIfAbsent(name, newns);
        return ns == null ? newns : ns;
    }

    public static Namespace find(Symbol name) {

        Namespace ns = namespaces.get(name);
        if (ns != null) {
            return ns;
        }
        return null;
    }

    @Override
    public Var findInternedVar(Symbol name) {

        Object valAt = mappings.valAt(name);
        if (valAt instanceof Var) {
            return (Var) valAt;
        }
        return null;
    }

    // Class か Varを取り出す
    @Override
    public Object getMapping(Symbol name) {
        // return mappings.get().valAt(name);
        return mappings.valAt(name);
    }

    public static ISeq all() {

        Collection<Namespace> values = namespaces.values();

        ArrayList<Object> list = new ArrayList<>();
        list.addAll(values);
        return new ListSeq(list);
    }

    @Override
    public IPersistentMap getMappings() {
        return mappings;
    }

    @Override
    public IPersistentMap getAliases() {
        return aliases;
    }

    @Override
    public Var intern(Symbol sym) {

        if (sym.ns != null) {
            throw new IllegalArgumentException("Can't intern namespace-qualified symbol");
        }

        IPersistentMap map = getMappings();
        Object o = map.valAt(sym);

        Var v = null;
        if (o instanceof Var && ((Var) o).ns == this) {
            return (Var) o;
        }

        if (v == null) {
            v = new Var(this, sym);
        }

        mappings.put(sym, v);
        return v;
    }

    // 追加メソッド
    @Override
    public Var internVar(Symbol sym, Var var) {

        mappings.put(sym, var);
        return var;
    }

    // 追加メソッド
    @Override
    public void addAlias(Object key, Object val) {

        aliases.put(key, val);
    }

    Var original_intern(Symbol sym) {

        if (sym.ns != null) {
            throw new IllegalArgumentException("Can't intern namespace-qualified symbol");
        }
        IPersistentMap map = getMappings();

        Object o;
        Var v = null;
        while ((o = map.valAt(sym)) == null) {
            if (v == null) {
                v = new Var(this, sym);
            }
            /*
		IPersistentMap newMap = map.assoc(sym, v);
		mappings.compareAndSet(map, newMap);
             */
            map = getMappings();
        }
        if (o instanceof Var && ((Var) o).ns == this) {
            return (Var) o;
        }

        if (v == null) {
            v = new Var(this, sym);
        }
        /*
	warnOrFailOnReplace(sym, o, v);


	while(!mappings.compareAndSet(map, map.assoc(sym, v)))
		map = getMappings();
         */
        return v;
    }

    @Override
    public Class importClass(Symbol sym, Class<?> c) {
        return referenceClass(sym, c);

    }

    @Override
    public Class importClass(Class<?> c) {
        String n = c.getName();
        return importClass(Symbol.intern(n.substring(n.lastIndexOf('.') + 1)), c);
    }

    public static boolean areDifferentInstancesOfSameClassName(Class<?> cls1, Class<?> cls2) {
        return (cls1 != cls2) && (cls1.getName().equals(cls2.getName()));
    }

    Class<?> referenceClass(Symbol sym, Class<?> val) {
        if (sym.ns != null) {
            throw new IllegalArgumentException("Can't intern namespace-qualified symbol");
        }
        IPersistentMap map = getMappings();
        Class<?> c = (Class<?>) map.valAt(sym);

        while ((c == null) || (areDifferentInstancesOfSameClassName(c, val))) {
//        IPersistentMap newMap = map.assoc(sym, val);

            //  IPersistentMap newMap = map.assocEx(sym, val);
//        mappings.compareAndSet(map, newMap);
            mappings.put(sym, val);

            map = getMappings();
            c = (Class<?>) map.valAt(sym);
        }
        if (c == val) {
            return c;
        }
        throw new IllegalStateException(sym + " already refers to: " + c + " in namespace: " + name);
    }

}
