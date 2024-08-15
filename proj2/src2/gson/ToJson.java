/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package gson;

import iris.clojure.lang.AFn;

/**
 *
 */
public class ToJson extends AFn {
    
    ConvToGson gson = new ConvToGson();
    /**
     * Convert Clojure's collection to com.google.gson objects.
     * 
     * 
     * @param arg1
     * @return 
     */
    @Override
    public Object invoke(Object arg1) {
        
        return gson.toJson(arg1);
    }
}
