/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */
package iris.clojure.readmacro;

import java.io.PushbackReader;
import java.util.HashSet;
import java.util.List;
import iris.clojure.lang.IClojureReader;
// import iris.clojure.lang.PersistentHashSet;

/**
 *  Read macro for set handles #{ :foo :bar } syntax .
 */
public class SetReader extends AFun {

    @Override
    public Object invoke(Object reader, Object leftbracket, Object opts, Object pendingForms,
                IClojureReader cr) {
        
        PushbackReader r = (PushbackReader) reader;
        List<Object> list = cr.readDelimitedList('}', r, true, opts, cr.ensurePending(pendingForms));

        HashSet<Object> set = new HashSet<>();
        set.addAll(list);
        
        return cr.getObjectFactory().createPersistentHashSet(set);

   //     return  new PersistentHashSet(  set);
    }
}
