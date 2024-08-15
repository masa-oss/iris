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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a subset of  clojure.lang.Keyword.java  .
 * 
 * 
 */

public final class Keyword extends AFn implements Comparable<Object>, LispPrintable {

    private final Symbol sym;
    private final int hasheq;
    transient String _str;

    static final Map<Symbol, Keyword> keywords = Collections.synchronizedMap(new HashMap<>());

    private Keyword(Symbol sym) {
        this.sym = sym;
        hasheq = sym.hashCode() + 0x9e3779b9;
    }

    public String getName() {
        return sym.name;
    }

    public static Keyword intern(String ns, String name) {

        return intern(Symbol.intern(ns, name));
    }

    public static Keyword intern(Symbol sym) {

        Keyword get = keywords.get(sym);
        if (get != null) {
            return get;
        } else {
            Keyword kw = new Keyword(sym);
            keywords.put(sym, kw);
            return kw;
        }
    }

    @Override
    public String getStringForPrint() {

        if (_str == null) {
            _str = ":" + this.sym.getStringForPrint();
        }
        return _str;
    }

    @Override
    public Object invoke(Object arg1) {

        IPersistentMap map = (IPersistentMap) arg1;

        if (map.containsKey(this)) {

            return map.valAt(this);
        }
        return RT.EOL;
    }

    @Override
    public int hashCode() {
        return hasheq;
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
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return sym.compareTo(((Keyword) o).sym);
    }

}
