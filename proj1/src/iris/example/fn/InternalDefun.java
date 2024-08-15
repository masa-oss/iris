package iris.example.fn;

import iris.clojure.lang.AFn;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class InternalDefun extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(InternalDefun.class);

    @Override
    public Object invoke(final Object arg1, final Object arg2,
            final Object arg3, final Object arg4) {

        Symbol funcName = (Symbol) arg1;
        Object func = arg2;
        String doc = (String) arg3;
        Boolean macro = (Boolean) arg4;

        LOG.info("funcName={}, doc={}, macro={}", funcName, doc, macro);

        Object o = CljCompiler.CURRENT_NS.deref();

        if (o != null) {
            Namespace ns = (Namespace) o;

            Var intern = ns.intern(funcName);
            intern.setFunction(func);
            if (macro) {
                intern.setMacro();
            }

            if (arg3 != null) {
                IPersistentMap map = intern.meta();
                if (map == null) {
                    map = PersistentHashMap.EMPTY;
                }

                IPersistentMap newMap = map.assocEx(FUN_DOC, arg3);

                String pstring1 = RT.printString(newMap);
                LOG.info("newMap={}", pstring1);

                intern.resetMeta(newMap);
            }
        }

        return funcName;
    }

    static Keyword FUN_DOC = Keyword.intern(null, "func-doc");

}
