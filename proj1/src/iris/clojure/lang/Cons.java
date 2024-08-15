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

import java.util.Objects;

/**
 *   Author: Masahito Hemmi
 */

/**
 * This class implements Lisp's cons .
 */

public final class Cons extends PersistentList {

    private final Object _first;
    private final ISeq _next;
    private final IPersistentMap _meta;

    public Cons(Object first, ISeq next) {

        this._meta = null;
        this._first = first;
        this._next = next;
    }

    public Cons(IPersistentMap meta, Object first, ISeq _more) {

        this._meta = meta;
        this._first = first;
        this._next = _more;
    }
    
	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
    @Override
    public String toString() {
        return "Cons[meta=" + _meta + "]";
    }
    

    @Override
    public Object first() {
        return _first;
    }

    @Override
    public ISeq next() {
        if (_next == null) {
            return null;
        }
        return _next;
    }

    @Override
    public int count() {
 
//        return 1 + RT.count(_next);

        ISeq seq = _next;
        int n = 0;
        while (seq != RT.EOL) {
            n++;
            seq = seq.next();
        }
        return 1+n;
    }

    @Override
    public Cons withMeta(IPersistentMap meta) {
        if (_meta == meta) {
            return this;
        }
        return new Cons(meta, _first, _next);
    }

    @Override
    public ISeq cons(Object o) {
        return new Cons(o, this);
    }

    @Override
    public ISeq seq() {
        return this;
    }

    @Override
    public IPersistentMap meta() {
        return _meta;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this._first);
        hash = 61 * hash + Objects.hashCode(this._next);
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
        final Cons other = (Cons) obj;
        if (!Objects.equals(this._first, other._first)) {
            return false;
        }
        return Objects.equals(this._next, other._next);
    }




}
