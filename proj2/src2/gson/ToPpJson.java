package gson;

import iris.clojure.lang.AFn;

/**
 *
 * @author hemmi
 */
public class ToPpJson extends AFn {
    
    ConvToGson gson = new ConvToGson();
    
    @Override
    public Object invoke(Object arg1) {
        
        return gson.toPpJson(arg1);
    }
}
