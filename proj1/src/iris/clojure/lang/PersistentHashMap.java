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

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Objects;

/**
 * <p>
 * Simple implementation of persistent map </p>
 *
 * <p>
 * Note that instances of this class are constant values i.e. add/remove etc
 * return new values</p>
 *
 * <p>
 * The key must implement the hashCode method</p>
 *
 */
public class PersistentHashMap extends PersistentMap {

    public static final PersistentHashMap EMPTY = create(new Object[0]);

    public PersistentHashMap(final Map<Object, Object> map) {

        if (map == null) {
            throw new NullPointerException();
        }

        int siz = map.size();

        HashMap<Object, Object> newMap = new HashMap<>(siz);
        newMap.putAll(map);

        super.map = Collections.unmodifiableMap(newMap);
       // this.count = newMap.keySet().size();
        this.count = siz;
        
        
        super._meta = null;
    }

    private PersistentHashMap(final Map<Object, Object> map, final IPersistentMap meta) {

        if (map == null) {
            throw new NullPointerException();
        }
        super.map = map;

        this.count = map.keySet().size();
        super._meta = meta;
    }

    final int count;

    private PersistentHashMap(final Map<Object, Object> map, int count) {

        super.map = map;

        this.count = count;
        super._meta = null;
    }

    @Override
    public int count() {
        return count;
    }

    public static PersistentHashMap create(Object[] init) {

        HashMap<Object, Object> result = new HashMap<>();

        for (int i = 0; i < init.length; i += 2) {
            Object key = init[i];
            Object val = init[i + 1];
            Object x = result.get(key);
            if (x != null) {
                throw new IllegalArgumentException("Duplicate key: " + init[i]);
            }
            result.put(key, val);
        }
        Map<Object, Object> unmodifiableMap = Collections.unmodifiableMap(result);

        int count = unmodifiableMap.keySet().size();
        return new PersistentHashMap(unmodifiableMap, count);
    }

    public static PersistentHashMap createWithCheck(Object[] init) {

        HashMap<Object, Object> result = new HashMap<>();

        for (int i = 0; i < init.length; i += 2) {
            Object key = init[i];
            Object val = init[i + 1];
            Object x = result.get(key);
            if (x != null) {
                throw new IllegalArgumentException("Duplicate key: " + init[i]);
            }
            result.put(key, val);
        }
        Map<Object, Object> unmodifiableMap = Collections.unmodifiableMap(result);
        return new PersistentHashMap(unmodifiableMap);
    }

    /**
     * Returns a copy of this object with specified metadata.
     *
     * @param meta
     * @return
     */
    @Override
    public IObj withMeta(IPersistentMap meta) {

        if (meta == _meta) {
            return this;
        }
        return new PersistentHashMap(map, meta);
    }

    @Override
    public IPersistentMap assocEx(Object key, Object val) {

        Map<Object, Object> newMap = new HashMap<>();
        newMap.putAll(map);
        newMap.put(key, val);
        return new PersistentHashMap(newMap);
    }

    @Override
    public IPersistentMap without(Object key) {

        Map<Object, Object> newMap = new HashMap<>();
        newMap.putAll(map);
        newMap.remove(key);

        return new PersistentHashMap(newMap);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.map);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersistentHashMap other = (PersistentHashMap) obj;
        return Objects.equals(this.map, other.map);
    }
}
