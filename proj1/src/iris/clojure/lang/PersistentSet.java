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


/**
 *   Author: Masahito Hemmi
 */
import java.util.Objects;
import java.util.Set;
/**
 * <p>Simple implementation of persistent set </p>
 *
 *
 */

public abstract class PersistentSet implements IPersistentSet, IObj {


    protected Set<Object> setset;

    protected IPersistentMap _meta;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.setset);
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
        final PersistentSet other = (PersistentSet) obj;
        return Objects.equals(this.setset, other.setset);
    }



    @Override
    public boolean contains(Object key) {
        return setset.contains(key);
    }

    @Override
    public Object get(Object key) {
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IPersistentMap meta() {
        return _meta;
    }

    @Override
    public int count() {
        return setset.size();
    }

    @Override
    public ISeq seq() {

        if (setset.isEmpty()) {
            return RT.EOL;
        }
        
        Object[] toArray = this.setset.toArray();
        return new ISeqSeq(toArray);
    }

   /**
    * <p>Simple implementation of Iseq </p>
    */
    public static class ISeqSeq implements ISeq {

        Object[] array;
        int idx = 0;

        public ISeqSeq(Object[] array) {
            this.array = array;

        }

        @Override
        public Object first() {
            return array[idx];
        }

        @Override
        public ISeq next() {
            idx++;
            if (idx >= array.length) {
                return RT.EOL;
            }
            return this;
        }

        @Override
        public ISeq cons(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int count() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ISeq seq() {
            return this;
        }
    }
}
