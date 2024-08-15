/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.readmacro;

import java.io.PushbackReader;
import java.util.List;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.IObjectFactory;
import iris.clojure.lang.IPersistentList;
import iris.clojure.lang.LineNumberingPushbackReader;

/**
 *
 * @author hemmi
 */
public class ListReader extends AFun {
/*
    final IObjectFactory factory;


    public ListReader(IObjectFactory factory) {
        this.factory = factory;
    }
*/
    @Override
    public Object invoke(Object reader, Object leftparen, Object opts, Object pendingForms,
            IClojureReader cr) {
        
        PushbackReader r = (PushbackReader) reader;
        int line = -1;
        int column = -1;
        if (r instanceof LineNumberingPushbackReader) {
            line = ((LineNumberingPushbackReader) r).getLineNumber();
            column = ((LineNumberingPushbackReader) r).getColumnNumber();
        }
        List<Object> list = cr.readDelimitedList(')', r, true, opts, cr.ensurePending(pendingForms));

        
        
        IPersistentList c =  cr.getObjectFactory() .createPersistentList(list, line, column);
        return c;

        /*            
            if (list.isEmpty()) {
                return PersistentList.EMPTY;
            }
            IObj s = (IObj) PersistentList.create(list);
            if (line != -1) {
                Object meta = RT.meta(s);
                meta = RT.assoc(meta, RT.LINE_KEY, RT.get(meta, RT.LINE_KEY, line));
                meta = RT.assoc(meta, RT.COLUMN_KEY, RT.get(meta, RT.COLUMN_KEY, column));
                return s.withMeta((JsonObject) meta);
            } else {
                return s;
            }
         */
    }

}
