package iris.example.fn;

import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;
import iris.clojure.lang.RestFn;

/**
 *
 * @author hemmi
 */
public class StrFn extends RestFn {

    @Override
    public int getRequiredArity() {
        return 0;
    }

    @Override
    protected Object doInvoke(Object rest) {
        
        ISeq seq = (ISeq) rest;
        StringBuilder sb = new StringBuilder();
        
        String sep = "";
        for (; seq != RT.EOL  ; seq = seq.next() ) {
            sb.append(sep);
            Object o = seq.first();
            sb.append((String) o);
            sep = " ";
        }
        
        return sb.toString();
        
    }
    
}
