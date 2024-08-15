/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
/**
 *   Author: Masahito Hemmi
 */
package iris.clojure.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;
/**
 * <p>Simple implementation of persistent map </p>
 *
 *
 */
public abstract class PersistentMap extends AFn implements IObj, IPersistentMap {
    
    protected IPersistentMap _meta;
    protected Map<Object, Object> map;

    final private static Object NOT_FOUND = Nil.INSTANCE;

    @Override
    public IPersistentMap meta() {

        return _meta;
    }



    @Override
    public boolean containsKey(Object key) {

        return map.containsKey(key);
    }

    @Override
    public IMapEntry entryAt(Object key) {

        Object get = map.get(key);
        if (get == null) {
            return null;
        }

        return new MapEntry(key, get);
    }

    @Override
    public ISeq seq() {

        Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        
        if (entrySet.isEmpty()) {
            return RT.EOL;     // add 2024-06-15
        }
        

        Iterator<Map.Entry<Object, Object>> it = entrySet.iterator();

        List<Object> list = new ArrayList<>();

        while (it.hasNext()) {

            Map.Entry<Object, Object> next = it.next();

            MapEntry ent = new MapEntry(next.getKey(), next.getValue());
            list.add(ent);

        }

        return new ListSeq(list);
    }

    @Override
    public Object valAt(Object key) {

        if (map.containsKey(key)) {

            Object get = map.get(key);
            return get;
        } else {
            return NOT_FOUND;
        }
    }

    @Override
    public Object valAt(Object key, Object notFound) {

        if (map.containsKey(key)) {
            Object get = map.get(key);
            return get;
        } else {
            return notFound;
        }
    }

}
