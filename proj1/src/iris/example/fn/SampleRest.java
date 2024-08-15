package iris.example.fn;

import iris.clojure.lang.RT;
import iris.clojure.lang.RestFn;

/**
 *
 */
public class SampleRest extends RestFn {

    @Override
    public int getRequiredArity() {
        return 2;
    }

    @Override
    protected Object doInvoke(Object arg1, Object arg2, Object args) {
        
        return RT.list(arg1, arg2, args);
    }
    
}
