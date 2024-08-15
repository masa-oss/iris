package iris.clojure.nsvar;

import iris.clojure.lang.Resolver;
import iris.clojure.lang.Symbol;


import org.slf4j.LoggerFactory;

/**
 * Simple implementation of resolver .
 */
public class MyResolver implements Resolver {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MyResolver.class);

    Symbol DEFAULT_NS = Symbol.intern(null, "default.ns");

    @Override
    public Symbol currentNS() {

        // Object o = RT.CURRENT_NS.deref();
        Object o = CljCompiler.CURRENT_NS.deref();

        LOG.info("26) {}", o);

        if (o != null) {
            Namespace ns = (Namespace) o;
            return Symbol.intern(null, ns.getName());
        } else {
            return DEFAULT_NS;
        }
    }

    @Override
    public Symbol resolveClass(Symbol sym) {

//        throw new UnsupportedOperationException("Not supported yet.");
        LOG.info("******* resolveClass {}", sym);
        Object o = CljCompiler.CURRENT_NS.deref();

        LOG.info("26) {}", o);

        if (o != null) {
            Namespace ns = (Namespace) o;

         //   Var findInternedVar = ns.findInternedVar(sym);
            
            Object o2 = ns.getMapping(sym);
            if (o2 != null) {
                if (o2 instanceof Class<?>) {
                    String canonicalName = ((Class<?>) o2).getCanonicalName();
                    return Symbol.intern(null, canonicalName);
                } else {
                    return sym;
                }
            } else {
                return sym;
            }
        } else {
            return sym;
        }
    }

    @Override
    public Symbol resolveVar(Symbol sym) {

//        throw new UnsupportedOperationException("Not supported yet.");
        LOG.info("******* resolveVar {}", sym);
        return sym;

    }

    /**
     * ::symbol の時、よばれるみたい
     *
     *
     * @param sym
     * @return
     */
    @Override
    public Symbol resolveAlias(Symbol sym) {

        throw new UnsupportedOperationException("Not supported yet.");
        // LOG.info("resolveClass");
        // return sym;
    }

}
