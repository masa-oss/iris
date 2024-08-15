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
 * */
package iris.clojure.lang;

import java.util.List;

/**
 * <p>Simple implementation of persistent list </p>
 *
 * <p>Note that instances of this class are constant values
 * i.e. add/remove etc return new values</p>
*/


public abstract class PersistentList implements IPersistentList, IObj {

    public static IPersistentList create(List<Object> init) {

        ISeq ret =  RT.EOL;

        int n = init.size();
        // Rotate in reverse order
        for (int i = n -1; i >=0 ; i--) {
            Object o = init.get(i);
            ret = new Cons(o, ret);
        }
        
        if (( ! RT.COMMON_LISP) && (ret == RT.EOL)) {
            return EMPTY;
        }
        return (IPersistentList) ret;
    }



    // **********************************************
    
    public static final EmptyList EMPTY = new EmptyList(null);

    // static class EmptyList extends Obj implements IPersistentList, List, ISeq, Counted, IHashEq {
    public static class EmptyList implements IPersistentList, IObj {

        static final int HASHEQ = 236790; //   Murmur3.hashOrdered(Collections.EMPTY_LIST);

        IPersistentMap _meta = null;

        @Override
        public int hashCode() {
            return 1;
        }

        public int hasheq() {
            return HASHEQ;
        }

	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
        @Override
        public String toString() {
            return "()";
        }

        @Override
        public boolean equals(Object o) {
            // return (o instanceof Sequential || o instanceof List) && RT.seq(o) == null;
            return (o instanceof List) && RT.seq(o) == null;
        }

        public boolean equiv(Object o) {
            return equals(o);
        }

        EmptyList(IPersistentMap meta) {
            _meta = meta;
        }

        @Override
        public Object first() {
            return null;
        }

        @Override
        public ISeq next() {
            return null;
        }

        public ISeq more() {
            return this;
        }

        @Override
        public PersistentList cons(Object o) {
//            return new PersistentList(meta(), o, null, 1);
            throw new UnsupportedOperationException();
        }

        public IPersistentCollection empty() {
            return this;
        }

        @Override
        public EmptyList withMeta(IPersistentMap meta) {
            if (meta != meta()) {
                return new EmptyList(meta);
            }
            return this;
        }

        public Object peek() {
            return null;
        }

        public IPersistentList pop() {
            throw new IllegalStateException("Can't pop empty list");
        }

        @Override
        public int count() {
            return 0;
        }

        @Override
        public ISeq seq() {
            return null;
        }

        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public boolean contains(Object o) {
            return false;
        }

        /*

        public boolean addAll(int index, Collection c) {
            throw new UnsupportedOperationException();
        }
         */
        @Override
        public IPersistentMap meta() {
            return _meta;
        }
    }

}
