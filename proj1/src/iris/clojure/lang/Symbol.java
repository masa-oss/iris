/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */
/**
 *   Author: Masahito Hemmi
 */
package iris.clojure.lang;

import java.util.Objects;
/**
 * This class is a subset of  <b>clojure.lang.Symbol.java</b>  .
 * 
 * 
 */

public class Symbol extends AFn implements IObj, Comparable<Object>, LispPrintable {

    public final String ns;
    public final String name;
    private final IPersistentMap _meta;

    transient String _str;

    public String getNamespace() {
        return ns;
    }

    public String getName() {
        return name;
    }

    public IPersistentMap getMeta() {
        return _meta;
    }

    public static Symbol intern(String ns, String name) {
        return new Symbol(ns, name);
    }

    public static Symbol intern(String nsname) {
        int i = nsname.indexOf('/');
        if (i == -1 || nsname.equals("/")) {
            return new Symbol(null, nsname);
        } else {
            return new Symbol(nsname.substring(0, i), nsname.substring(i + 1));
        }
    }

    protected final int hashnum;

    protected Symbol(String ns_interned, String name_interned) {
        this.name = name_interned;
        this.ns = ns_interned;
        this._meta = null;
        hashnum = hasheq();
    }

    protected Symbol(String ns_interned, String name_interned, IPersistentMap newMeta) {
        this.name = name_interned;
        this.ns = ns_interned;
        this._meta = newMeta;
        hashnum = hasheq();
    }

    /**
     * Returns a copy of this object with specified metadata.
     * 
     * @param meta
     * @return 
     */
    @Override
    public IObj withMeta(IPersistentMap meta) {
        return new Symbol(ns, name, meta);
    }

    @Override
    public IPersistentMap meta() {
        return _meta;
    }

    @Override
    public String getStringForPrint() {

        if (_str == null) {
            if (ns != null) {
                _str = (ns + "/" + name);
            } else {
                _str = name;
            }
        }
        return _str;
    }

	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
    @Override
    public String toString() {
        if (_str == null) {
            if (ns != null) {
                _str = (ns + "/" + name);
            } else {
                _str = name;
            }
        }
        return _str;
    }

    @Override
    public int hashCode() {

        return hashnum;
    }

    private int hasheq() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.ns);
        hash = 73 * hash + Objects.hashCode(this.name);
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
        final Symbol other = (Symbol) obj;
        if (!Objects.equals(this.ns, other.ns)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int compareTo(Object o) {
        Symbol s = (Symbol) o;
        if (this.equals(o)) {
            return 0;
        }
        if (this.ns == null && s.ns != null) {
            return -1;
        }
        if (this.ns != null) {
            if (s.ns == null) {
                return 1;
            }
            int nsc = this.ns.compareTo(s.ns);
            if (nsc != 0) {
                return nsc;
            }
        }
        return this.name.compareTo(s.name);
    }

}
