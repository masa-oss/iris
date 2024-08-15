/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */

package iris.example.eval;

import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.PersistentVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hemmi
 */
public class LexicalScopeUtil {
    

    // -------------- Just for debugging purposes

    /**
     * This method copies and returns the contents of this class's data.
     * This method is provided for learning and debugging purposes.
     * It is not recommended to write processing using the return value of this method.
     * Because the return value of this method may change in the future.
     * 
     * @return 
     */
    public IPersistentVector getChains(LexicalScope sco) {
        
        ArrayList<Object> result = new ArrayList<>();
        result.add(getThisLevelVars(sco));
        
        
        LexicalScope delegate = (LexicalScope)  sco.values[0];
        
        for (LexicalScope p = delegate; p != null  ;   ) {
            result.add(getThisLevelVars(p));
            p = (LexicalScope) p.values[0];
        }
        
        return new PersistentVector ( result);
    }
    
    
    
    
    /**
     * This method copies and returns the contents of this class's data.
     * This method is provided for learning and debugging purposes.
     * It is not recommended to write processing using the return value of this method.
     * Because the return value of this method may change in the future.
     * 
     * @return 
     */
    public IPersistentMap getThisLevelVars(LexicalScope sco) {

        int n = sco.symbols.length;
        if (n <= 2) {
            return PersistentHashMap.EMPTY;
        }
        
        
        Map<Object, Object> map = new HashMap<>();

        
        for (int i = 2; i < n; i++) {
            map.put(sco.symbols[i], sco.values[i]);
        }
        return new PersistentHashMap(map);
    }


}
