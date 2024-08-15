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
import java.util.List;
import java.util.Objects;

/**
 * <p>Simple implementation of persistent vector </p>
 *
 *
 */


public final class PersistentVector implements IPersistentVector, IObj, LispPrintable {

    public static PersistentVector EMPTY = new PersistentVector(new ArrayList<>());

    final List<Object> aList;
    final int length;

    final IPersistentMap _meta;

    public PersistentVector(List<Object> list) {

        if (list == null) {
            throw new NullPointerException("list");
        }
        
        int siz = list.size();

        ArrayList<Object> newList = new ArrayList<>(siz);
        newList.addAll(list);
        this.aList = newList;
        
        this.length = list.size();
        this._meta = null;
    }

    private PersistentVector(List<Object> list, IPersistentMap meta) {

        if (list == null) {
            throw new NullPointerException("list");
        }

        ArrayList<Object> newList = new ArrayList<>();
        newList.addAll(list);
        this.aList = newList;

        this.length = list.size();
        this._meta = meta;
    }

    @Override
    public IObj withMeta(IPersistentMap meta) {

        return new PersistentVector(this.aList, meta);
    }

    @Override
    public IPersistentMap meta() {
        return _meta;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public IPersistentVector assocN(int i, Object val) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IPersistentVector cons(Object o) {

        ArrayList<Object> newList = new ArrayList<>();
        newList.addAll(this.aList);
        newList.add(o);
        return new PersistentVector(newList);
    }

    @Override
    public Object get(int i) {
        return aList.get(i);
    }

    @Override
    public String getStringForPrint() {
        return "PersistentVector[" + length + "]";
    }

    @Override
    public int count() {
        return length;
    }

    @Override
    public Object nth(int i) {
        return aList.get(i);
    }

    @Override
    public Object nth(int i, Object notFound) {
        return aList.get(i);
    }

    @Override
    public ISeq seq() {

        if (aList.isEmpty()) {
            return RT.EOL;
        }

        return new ListSeq(aList);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.aList);
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
        final PersistentVector other = (PersistentVector) obj;
        if (this.length != other.length) {
            return false;
        }
        return Objects.equals(this.aList, other.aList);
    }
    
}
