package iris.example.eval;

import iris.clojure.lang.AFn;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>
 *    Only support
 *    (setf (get x y) value)   --> (common.lisp/*putprop x y value)
 *    
 * </code>
 */
public class SetfMacro extends AFn {
    
    private static final Logger LOG = LoggerFactory.getLogger(SetfMacro.class);
    
    
    static Symbol GET = Symbol.intern(null, "get");
    
    static Symbol PUT_PROP = Symbol.intern("common.lisp", "*putprop");

    @Override
    public Object invoke(Object arg1, Object arg2) {

        ISeq seq = (ISeq) arg1;

        Object o = seq.first();
        if (GET.equals(o)) {
            seq = seq.next();
            Object o1 = seq.first();
            seq = seq.next();
            Object o2 = seq.first();
            
            return RT.list( PUT_PROP,     o1, o2, arg2);
            
        } else {
            throw new EvaluatorException("unsupport setf : " + o);
        }
        
    }


}
