/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.lang;

import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class PersistentHashMapTest {
    

    @Test
    public void testCount() {
        System.out.println("count");
        
        HashMap<Object, Object> initMap = new HashMap<>();
        initMap.put("a", 1);
        initMap.put("b", 2);

        PersistentHashMap instance = new PersistentHashMap(initMap);
        int result = instance.count();
        assertEquals(2, result);
        
        
        // Add elements to Map
        initMap.put("c", 3);
        
        // Since it is persistent, the number of elements remains 2.

        int result2 = instance.count();
        assertEquals(2, result2);
    }


    @Test
    public void testWithMeta() {
        System.out.println("withMeta");
        
        
        HashMap<Object, Object> initMap = new HashMap<>();
        initMap.put("a", 1);
        initMap.put("b", 2);

        PersistentHashMap instance = new PersistentHashMap(initMap);
        
        assertNull(instance.meta());

        IPersistentMap meta = RT.map("abc", 123);
        // Copy the instance, with meta
        IObj result = instance.withMeta(meta);

        assertNotNull(result.meta());
        assertTrue(result instanceof PersistentHashMap);
        assertTrue(result != instance);
        
        
        // Whether meta is present or not, it does not affect equals
        assertTrue(result.equals(instance));

        int h1 = instance.hashCode();
        int h2 = result.hashCode();
        // Whether there is meta or not, hashCode is not affected
        assertEquals(h1, h2);
        
    }


    
}
