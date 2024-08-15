/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/**
 *   Author: Masahito Hemmi
 **/
package iris.clojure.readmacro;

import java.io.PushbackReader;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.Util;

public class MapReader extends AFun {

    @Override
    public Object invoke(Object reader, Object leftparen, Object opts, Object pendingForms,
            IClojureReader cr) {
        
        PushbackReader r = (PushbackReader) reader;
        Object[] a = cr.readDelimitedList('}', r, true, opts, cr.ensurePending(pendingForms)).toArray();
        if ((a.length & 1) == 1) {
            throw Util.runtimeException("Map literal must contain an even number of forms");
        }
        
       return cr.getObjectFactory().createPersistentHashMap(a);
     //   cr.getObjectFactory().createPersistentHashSet(set)
      //  return RT.map(a);
    }
}
