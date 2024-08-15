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

/**
 * This class is a subset of  <i>clojure.lang.MapEntry.java</i>  .
 * 
 * 
 */

public final class MapEntry implements IMapEntry, LispPrintable, IPersistentVector, Comparable<Object> {

    private final Object _key;
    private final Object _val;

    public MapEntry(Object key, Object val) {
        this._key = key;
        this._val = val;
    }

    public static MapEntry create(Object key, Object val) {
        return new MapEntry(key, val);
    }

    @Override
    public Object key() {
        return _key;
    }

    @Override
    public Object val() {
        return _val;
    }

    public Object getKey() {
        return key();
    }

    public Object getValue() {
        return val();
    }

    @Override
    public String getStringForPrint() {

        return "MapEntry[" + _key + ", " + _val + "]";
    }

    //-----------------------------------------
    @Override
    public int length() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IPersistentVector assocN(int i, Object val) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IPersistentVector cons(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object get(int i) {
        if (i == 0) {
            return _key;
        } else if (i == 1) {
            return _val;
        } else {
            return null;
        }
    }

    @Override
    public Object nth(int i) {
        if (i == 0) {
            return _key;
        } else if (i == 1) {
            return _val;
        } else {
            return null;
        }
    }

    @Override
    public Object nth(int i, Object notFound) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int count() {
        return 2;
    }

    @Override
    public ISeq seq() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Object o) {
        IPersistentVector v = (IPersistentVector) o;
        if (count() < v.count()) {
            return -1;
        } else if (count() > v.count()) {
            return 1;
        }
        for (int i = 0; i < count(); i++) {
            int c = Util.compare(nth(i), v.nth(i));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

}
