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

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class PersistentVectorTest {

    @Test
    public void testWithMeta() {
        System.out.println("withMeta");

        // List containing initial values
        ArrayList<Object> data = new ArrayList<>();
        data.add(1);
        data.add(2);

        // Create PersistentVector
        PersistentVector instance = new PersistentVector(data);

        assertNull(instance.meta());

        IPersistentMap meta = RT.map("abc", 123);
        // Copy the instance, with meta
        IObj result = instance.withMeta(meta);

        assertNotNull(result.meta());
        assertTrue(result instanceof PersistentVector);
        assertTrue(result != instance);

        // Whether meta is present or not, it does not affect equals
        assertTrue(result.equals(instance));

        int h1 = instance.hashCode();
        int h2 = result.hashCode();
        // Whether there is meta or not, hashCode is not affected
        assertEquals(h1, h2);
        
    }

    @Test
    public void testLength() {
        System.out.println("length");

        // List containing initial values
        ArrayList<Object> data = new ArrayList<>();
        data.add(1);
        data.add(2);

        // Create PersistentVector
        PersistentVector instance = new PersistentVector(data);

        int result = instance.length();
        assertEquals(2, result);

        // Add elements to List
        data.add(3);

        // Since it is persistent, the number of elements remains 2.
        assertEquals(2, result);
    }


}
