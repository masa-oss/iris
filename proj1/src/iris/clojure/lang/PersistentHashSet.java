/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.lang;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Masahito Hemmi
 */
/**
 * <p>
 * Simple implementation of persistent set </p>
 *
 * <p>
 * Note that instances of this class are constant values i.e. add/remove etc
 * return new values</p>
 *
 * <p>
 * Elements must implement the hashCode method </p>
 *
 */
public class PersistentHashSet extends PersistentSet {

    public static PersistentHashSet EMPTY = new PersistentHashSet(new HashSet<>());

    
    
    public PersistentHashSet(Set<Object> init) {
        
        if (init == null) throw new NullPointerException();
        
        int siz = init.size();

        HashSet<Object> newValue = new HashSet<>(siz);
        newValue.addAll(init);

        this.setset = Collections.unmodifiableSet(newValue);
        this._meta = null;
    }

    private PersistentHashSet(Set<Object> newValue, IPersistentMap newMeta) {

        this.setset = newValue;
        this._meta = newMeta;
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
        return new PersistentHashSet(setset, meta);
    }

    @Override
    public IPersistentSet disjoin(Object key) {

        HashSet<Object> newSet = new HashSet<>();
        newSet.addAll(this.setset);
        newSet.remove(key);

        return new PersistentHashSet(newSet, _meta);
    }




}
