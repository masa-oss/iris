package iris.example.eval;

import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.INamespace;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.IStaticCljNs;

/**
 *
 * @author hemmi
 */
public class StaticNamespaceImpl implements IStaticCljNs {

    @Override
    public Namespace findOrCreate(Symbol name) {
        return Namespace.findOrCreate(name);
    }

 /*
    public Namespace findOrCreate(Symbol name, boolean init) {
        return Namespace.findOrCreate(name, init);
    }
*/
    @Override
    public Namespace find(Symbol name) {
        return Namespace.find(name);
    }

    @Override
    public void addLoadedLibs(Symbol sym) {

        CljCompiler.addLoadedLibs(sym);
    }

    @Override
    public INamespace getCurrentNs() {


        Namespace ns = (Namespace) CljCompiler.CURRENT_NS.deref();
        return ns;
    }

    @Override
    public String printString(Object x) {
        return CljCompiler.printString(x);
    }

    @Override
    public boolean isSpecialEx(Object sym) {
        return CljCompiler.isSpecialEx(sym);
    }
    
}
