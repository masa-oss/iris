package iris.example.fn;

import iris.clojure.lang.AFn;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.MapEntry;
import iris.clojure.lang.RT;
import iris.clojure.lang.Seqable;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.INamespace;
import iris.clojure.nsvar.IStaticCljNs;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;
import iris.example.eval.ETE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This function like clojure's  <b>refer-clojure</b> .
 *
 * <code>
 *   (refer-iris)
 *
 *   (refer-iris :only [ns])
 * </code>
 */
public class ReferIris extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(ReferIris.class);

    static Keyword ONLY = Keyword.intern(null, "only");

    IStaticCljNs staticNs;

    public ReferIris(IStaticCljNs staticNs) {

        this.staticNs = staticNs;
    }

    @Override
    public Object invoke() {

        INamespace currentNs = staticNs.getCurrentNs();
        INamespace from2 = staticNs.find(Symbol.intern(null, "iris.clojure.core"));

        search(from2, currentNs);
        return Boolean.TRUE;
    }

    @Override
    public Object invoke(Object arg1, Object arg2) {

        if (ONLY.equals(arg1)) {

            ISeq seq2 = getSeq(arg2);
            INamespace currentNs = staticNs.getCurrentNs();
            INamespace from2 = staticNs.find(Symbol.intern(null, "iris.clojure.core"));
            referOnly(from2, currentNs, seq2);

            return Boolean.TRUE;
        } else if (arg1 instanceof Namespace) {
            
            Namespace ns1 = (Namespace) arg1;
            ISeq seq2 = getSeq(arg2);
            
            INamespace from2 = staticNs.find(Symbol.intern(null, "iris.clojure.core"));
            referOnly(from2, ns1, seq2);
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }

    void referOnly(INamespace from, INamespace toNs, ISeq seq) {

        for (; seq != RT.EOL; seq = seq.next()) {

            Object o = seq.first();

            LOG.info("73) symbol={}", o);

            Symbol sym = (Symbol) o;
            Object object = from.getMapping(sym);
            if (object != null) {
                if (object instanceof Var) {
                    Var v = (Var) object;

                    toNs.internVar(sym, v);
                } else {
                    throw new RuntimeException("Unknown object : " + object);
                }

            }
        }
    }

    ISeq getSeq(Object obj) {
        if (obj instanceof ISeq) {
            return (ISeq) obj;
        } else if (obj instanceof Seqable) {
            Seqable able = (Seqable) obj;
            return able.seq();
        }
        ETE.throwException("can't convert ISeq", obj, null);
        return null;
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

}
