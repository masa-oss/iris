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
 * Author: Masahito Hemmi
 */
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a minimal implementation similar to
 * <b>clojure.lang.Util.java</b> .
 *
 */
public class Util {

    static public RuntimeException runtimeException(String s) {
        return new RuntimeException(s);
    }

    static public RuntimeException runtimeException(String s, Throwable e) {
        return new RuntimeException(s, e);
    }

    /**
     * Throw even checked exceptions without being required to declare them or
     * catch them.Suggested idiom:
     * <p>
     * <code>throw sneakyThrow( some exception );</code>
     *
     * @param t
     * @return
     */
    static public RuntimeException sneakyThrow(Throwable t) {
        // http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html
        if (t == null) {
            throw new NullPointerException();
        }
        Util.<RuntimeException>sneakyThrow0(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    static private <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

    static public boolean equals(Object k1, Object k2) {
        if (k1 == k2) {
            return true;
        }
        return k1 != null && k1.equals(k2);
    }

    static public <K, V> void clearCache(ReferenceQueue rq, ConcurrentHashMap<K, Reference<V>> cache) {
        //cleanup any dead entries
        if (rq.poll() != null) {
            while (rq.poll() != null)
			;
            for (Map.Entry<K, Reference<V>> e : cache.entrySet()) {
                Reference<V> val = e.getValue();
                if (val != null && val.get() == null) {
                    cache.remove(e.getKey(), val);
                }
            }
        }
    }

    static public Object ret1(Object ret, Object nil) {
        return ret;
    }

    static public ISeq ret1(ISeq ret, Object nil) {
        return ret;
    }

    @SuppressWarnings("unchecked")
    static public int compare(Object k1, Object k2) throws ClassCastException {
        if (k1 == k2) {
            return 0;
        }
        if (k1 != null) {
            if (k2 == null) {
                return 1;
            }
            if (k1 instanceof Number) {
                return Numbers.compare((Number) k1, (Number) k2);
            }

            //    if (k1 instanceof Comparable<Object>) {
            Comparable<Object> comp1 = (Comparable<Object>) k1;
            return comp1.compareTo(k2);
            //  }
        }
        return -1;
    }

}
