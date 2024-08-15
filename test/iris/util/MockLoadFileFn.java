package iris.util;

import iris.clojure.lang.AFn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class MockLoadFileFn extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(MockLoadFileFn.class);
    
    
    boolean ret;
    
    public MockLoadFileFn(boolean b) {
        this.ret = b;
    }
    
    
    Object savedArg1;
    
    
    @Override
    public Object invoke(Object arg1) {

        savedArg1 = arg1;
        LOG.info("MockLoadFileFn  {}  ==> {}", arg1, ret);
        return ret;
    }
    
}
