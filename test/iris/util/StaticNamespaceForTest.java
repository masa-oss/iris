package iris.util;

import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.INamespace;
import iris.clojure.nsvar.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import iris.clojure.nsvar.IStaticCljNs;

/**
 *
 * @author hemmi
 */
public class StaticNamespaceForTest implements IStaticCljNs {

    private static final Logger LOG = LoggerFactory.getLogger(StaticNamespaceForTest.class);

    final boolean callFlag;

    public StaticNamespaceForTest() {
        callFlag = false;
    }

    public StaticNamespaceForTest(boolean call) {
        callFlag = call;
    }

    @Override
    public INamespace findOrCreate(Symbol name) {

        throw new UnsupportedOperationException();
    }
/*
    public INamespace findOrCreate(Symbol name, boolean init) {

        throw new UnsupportedOperationException();
    }
*/
    @Override
    public INamespace find(Symbol name) {

        if (callFlag) {

            Namespace ret = Namespace.find(name);

            LOG.info("find {} --> {}", name, ret);
            return ret;

        } else {
            LOG.warn("find = {}", name);
            return null;
        }
    }

    Symbol loadedName;

    // access to CljCompiler
    @Override
    public void addLoadedLibs(Symbol sym) {

        if (callFlag) {
            
            CljCompiler.addLoadedLibs(sym);

            LOG.info("addLoadedLibs = {}", sym);
            
        } else {

            loadedName = sym;
            LOG.warn("addLoadedLibs = {}", sym);
        }
    }

    @Override
    public INamespace getCurrentNs() {

        return ns;
    }

    static Namespace ns = Namespace.findOrCreate(Symbol.intern(null, "test"));

    @Override
    public String printString(Object x) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isSpecialEx(Object sym) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
