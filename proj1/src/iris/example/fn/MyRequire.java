package iris.example.fn;

import iris.clojure.lang.AFn;
import iris.clojure.lang.IFn;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.MapEntry;
import iris.clojure.lang.Nil;
import iris.clojure.lang.PersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.INamespace;
import iris.clojure.nsvar.Var;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import iris.clojure.nsvar.IStaticCljNs;

/**
 * This function is a subset of clojure's <b>require</b>.
 *
 */
public class MyRequire extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(MyRequire.class);

    Keyword as = Keyword.intern(null, "as");
    Keyword refer = Keyword.intern(null, "refer");
    Keyword all = Keyword.intern(null, "all");

    IFn loadFileFn;

    IStaticCljNs staticNs;

    public MyRequire(IStaticCljNs staticNs, IFn loadFileFn) {
        this.staticNs = staticNs;
        this.loadFileFn = loadFileFn;
    }

    @Override
    public Object invoke(Object arg1) {

        require(arg1);
        return Nil.INSTANCE;
    }

    @Override
    public Object invoke(Object arg1, Object arg2) {

        require(arg1);
        require(arg2);
        return Nil.INSTANCE;
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {

        require(arg1);
        require(arg2);
        require(arg3);
        return Nil.INSTANCE;
    }

    void require(Object arg1) {

        if (arg1 instanceof IPersistentVector) {
            IPersistentVector vec = (IPersistentVector) arg1;
            internalRequire(vec);

        } else if (arg1 instanceof Symbol) {
            ArrayList<Object> list = new ArrayList<>();
            list.add(arg1);
            PersistentVector pv = new PersistentVector(list);
            internalRequire(pv);

        } else {
            LOG.warn("unknown argument, skip={}", arg1);
        }
    }

    void internalRequire(IPersistentVector vec) {

        INamespace importToNs = staticNs.getCurrentNs();

        int n = vec.length();

        int i = 0;
        Object get = vec.get(i++);
        Symbol pkgName = (Symbol) get;

        if (i >= n) {
            doRequire(importToNs, pkgName, null);
            return;
        }

        Symbol preFix = null;
        Object get2 = vec.get(i++);
        if (get2 == as) {              // ex.    :as m

            Object get3 = vec.get(i++);
            preFix = (Symbol) get3;

        }

        doRequire(importToNs, pkgName, preFix);  // move 2024-08-03

        // move 2024-08-03
        if (i >= n) {
            return;
        }

        if (get2 == refer) {

            Object get4 = vec.get(i++);

            if (all.equals(get4)) {

                //   Namespace from2 = Namespace.find(Symbol.intern(null, "iris.clojure.core"));
                INamespace ns = staticNs.find(pkgName);
                
                LOG.info("126)  {} :refer :all", ns);
                if (ns == null) {
                    LOG.warn("Namespace not found : {}", pkgName);
                    return;
                }

                search(ns, importToNs);

            } else {

                IPersistentVector vec2 = (IPersistentVector) get4;

                doRefer(importToNs, pkgName, vec2);
            }
        }
    }

    void search(INamespace from, INamespace to) {
        if (from == null) {
            return;
        }
        IPersistentMap mappings = from.getMappings();
        for (ISeq seq = mappings.seq(); seq != RT.EOL; seq = seq.next()) {

            MapEntry me = (MapEntry) seq.first();

            Object val = me.getValue();
            if (val instanceof Var) {
                Var v = (Var) val;

                boolean aPublic = v.isPublic();

                //    LOG.info("49) key={}, val={}, public={}", me.getKey(), me.getValue(), aPublic);
                if (aPublic) {
                    Symbol sym = (Symbol) me.getKey();
                    to.internVar(sym, v);
                }
            }
        }
    }

    void doRequire(INamespace toNs, Symbol pkgName, Symbol prefix) {

        INamespace ns = staticNs.find(pkgName);
        if (ns == null) {
            LOG.info("Namespace not found : {}", pkgName);

            // Trying to load .lsp from classpath 
            Object ret = loadFileFn.invoke(pkgName);
            Boolean b = (Boolean) ret;
            LOG.info("Load from source, returns : {}", b);

            if (!b) {
                LOG.info("Try loading the class file ...");
                loadJavaLib(pkgName.getName());
            }

            staticNs.addLoadedLibs(Symbol.intern(null, pkgName.getName()));

            ns = staticNs.find(pkgName);
        }

        if (prefix != null) {
            toNs.addAlias(prefix, ns);
            LOG.info("add alias ( {} -> {} )    to {}", prefix, ns.getName(), toNs.getName());
        }
    }

    void loadJavaLib(final String pkg) {

        String className = pkg + "__init";

        LOG.info("loadJavaLib  className={}", className);

        try {
            Class.forName(className);
        } catch (Exception ex) {

            throw new RuntimeException("Exception", ex);
        }
    }

    void doRefer(INamespace toNs, Symbol pkgName, IPersistentVector vec) {

        INamespace ns = staticNs.find(pkgName);
        if (ns == null) {
            LOG.warn("Namespace not found : {}", pkgName);
            return;

        }
        int n = vec.length();

        for (int i = 0; i < n; i++) {
            Object o = vec.get(i);
            Symbol sym = (Symbol) o;
            Object object = ns.getMapping(sym);
            if (object != null) {

                if (object instanceof Var) {
                    Var v = (Var) object;

                    toNs.internVar(sym, v);
                } else {
                    throw new RuntimeException("not impl yet : " + object);
                }

            } else {
                LOG.warn("{} not found in namespace {}", sym.getStringForPrint(), ns.getName());
            }
        }
    }

}
