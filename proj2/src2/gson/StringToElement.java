package gson;

import iris.clojure.lang.AFn;

/**
 *
 * @author hemmi
 */
public class StringToElement extends AFn {
    
    ConvToGson gson = new ConvToGson();
    
    @Override
    public Object invoke(Object arg1) {
        
        String str = (String ) arg1;
        return gson.fromStringToJson(str);
    }
}
