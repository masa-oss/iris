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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a minimal implementation similar to <b>clojure.lang.RT.java</b>
 * .
 */
public final class RT {

    private static final Logger LOG = LoggerFactory.getLogger(RT.class);

    public static final Boolean T = Boolean.TRUE;
    public static final Boolean F = Boolean.FALSE;

    public static final Keyword TAG_KEY = Keyword.intern(null, "tag");

    public static final Keyword LINE_KEY = Keyword.intern(null, "line");
    public static final Keyword COLUMN_KEY = Keyword.intern(null, "column");

    //simple-symbol->class
    static public IPersistentMap DEFAULT_IMPORTS;

    /**
     * End of list .
     *
     * If Common Lisp then Nil.INSTANCE should be set.
     *
     * If Clojure then null should be set.
     */
    public static final ISeq EOL = Nil.INSTANCE;

//    public static final ISeq EOL =  null;
    

    public static final boolean COMMON_LISP = true;

    // Disable construct
    private RT() {
    }

    public static IPersistentMap map(Object key, Object val) {
        Object[] arr = new Object[2];
        arr[0] = key;
        arr[1] = val;
        return PersistentHashMap.create(arr);
    }

    public static IPersistentMap map(Object key, Object val, Object k2, Object v2) {
        Object[] arr = new Object[4];
        arr[0] = key;
        arr[1] = val;

        arr[2] = k2;
        arr[3] = v2;
        return PersistentHashMap.create(arr);
    }

    // ---------------------------------------------
    public static Object nth(Object coll, int n) {
        if (coll instanceof Indexed) {
            return ((Indexed) coll).nth(n);
        }
        return nthFrom(coll, n);
    }

    static Object nthFrom(Object coll, int n) {
        if (coll == null) {
            return null;
        } else if (coll instanceof CharSequence) {
            return ((CharSequence) coll).charAt(n);
        } else if (coll.getClass().isArray()) {
//		return Reflector.prepRet(coll.getClass().getComponentType(),Array.get(coll, n));
            throw new IllegalStateException("not impl yet");
        } else if (coll instanceof RandomAccess) {
            return ((List) coll).get(n);
        } else if (coll instanceof Matcher) {
            return ((Matcher) coll).group(n);
        } else if (coll instanceof Map.Entry) {
            Map.Entry e = (Map.Entry) coll;
            if (n == 0) {
                return e.getKey();
            } else if (n == 1) {
                return e.getValue();
            }
            throw new IndexOutOfBoundsException();
        } /*
	else if(coll instanceof Sequential) {
		ISeq seq = RT.seq(coll);
		coll = null;
		for(int i = 0; i <= n && seq != RT.EOL; ++i, seq = seq.next()) {
			if(i == n)
				return seq.first();
		}
		throw new IndexOutOfBoundsException();
	} */ else if (coll instanceof Seqable) {
            Seqable able = (Seqable) coll;
            ISeq seq = able.seq();
            coll = null;
            for (int i = 0; i <= n && seq != RT.EOL; ++i, seq = seq.next()) {
                if (i == n) {
                    return seq.first();
                }
            }
            throw new IndexOutOfBoundsException();

        } else {
            throw new UnsupportedOperationException(
                    "nth not supported on this type: " + coll.getClass().getSimpleName());
        }
    }

    public static Object get(Object coll, Object key) {

        if (coll instanceof ILookup) {
            return ((ILookup) coll).valAt(key);
        }
        return getFrom(coll, key);
    }

    private static Object getFrom(Object coll, Object key) {

        if (coll == null) {
            return null;
        } else if (coll instanceof Map) {
            Map m = (Map) coll;
            return m.get(key);
        } else if (coll instanceof IPersistentSet) {
            IPersistentSet set = (IPersistentSet) coll;
            return set.get(key);
        } else if (key instanceof Number && (coll instanceof String || coll.getClass().isArray())) {
            int n = ((Number) key).intValue();
            if (n >= 0 && n < count(coll)) {
                return nth(coll, n);
            }
            return null;
            /*
	}
	else if(coll instanceof ITransientSet) {
		ITransientSet set = (ITransientSet) coll;
		return set.get(key);
             */
        }

        return null;
    }

    public static Object get(Object coll, Object key, Object notFound) {

        if (coll instanceof ILookup) {
            return ((ILookup) coll).valAt(key, notFound);
        }
        return getFrom(coll, key, notFound);
    }

    private static Object getFrom(Object coll, Object key, Object notFound) {

        if (coll == null) {
            return notFound;
        } else if (coll instanceof Map) {
            Map m = (Map) coll;
            if (m.containsKey(key)) {
                return m.get(key);
            }
            return notFound;
        } else if (coll instanceof IPersistentSet) {
            IPersistentSet set = (IPersistentSet) coll;
            if (set.contains(key)) {
                return set.get(key);
            }
            return notFound;
            /*
	}
	else if(key instanceof Number && (coll instanceof String || coll.getClass().isArray())) {
		int n = ((Number) key).intValue();
		return n >= 0 && n < count(coll) ? nth(coll, n) : notFound;
	}
	else if(coll instanceof ITransientSet) {
		ITransientSet set = (ITransientSet) coll;
		if(set.contains(key))
			return set.get(key);
		return notFound;
             */
        }
        return notFound;

    }

    public static ISeq list(Object sym, Object obj) {

        return new Cons(sym, new Cons(obj, EOL));
    }

    public static ISeq list(Object o1, Object o2, Object o3) {

        return new Cons(o1, new Cons(o2, new Cons(o3, EOL)));
    }

    public static ISeq list(Object o1, Object o2, Object o3, Object o4) {

        return new Cons(o1, new Cons(o2, new Cons(o3, new Cons(o4, EOL))));
    }

    public static ISeq list(Object o1, Object o2, Object o3, Object o4, Object o5) {

        return new Cons(o1,
                new Cons(o2, new Cons(o3, new Cons(o4, new Cons(o5, EOL)))));
    }

    public static ISeq list(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {

        return new Cons(o1,
                new Cons(o2,
                        new Cons(o3, new Cons(o4, new Cons(o5, new Cons(o6, EOL))))));
    }

    public static Object first(Object x) {
        if (x instanceof ISeq) {
            return ((ISeq) x).first();
        }
        ISeq seq = seq(x);
        if (seq == null) {
            return null;
        }
        return seq.first();
    }

    public static IPersistentMap assoc(Object coll, Object key, Object val) {

        if (coll == null) {
            //  return new PersistentArrayMap(new Object[]{key, val});
            return PersistentHashMap.create(new Object[]{key, val});
        }
        return ((IPersistentMap) coll).assocEx(key, val);
    }

    public static IPersistentMap meta(Object x) {

        if (x instanceof IObj) {
            return ((IObj) x).meta();
        }
        return null;
    }

    public static Object second(Object x) {
        return first(next(x));
    }

    public static Object third(Object x) {
        return first(next(next(x)));
    }

    public static Object fourth(Object x) {
        return first(next(next(next(x))));
    }

    public static ISeq next(Object x) {

        if (x instanceof ISeq) {
            return ((ISeq) x).next();
        }
        ISeq seq = seq(x);
        if (seq == null) {
            return null;
        }
        return seq.next();
    }

    static public ISeq cons(Object x, Object coll) {

        if (coll == null) {
            return new Cons(x, (ISeq) coll);
        } else if (coll instanceof ISeq) {
            return new Cons(x, (ISeq) coll);
        } else {
            return new Cons(x, seq(coll));
        }
    }

    ////////////// Collections support /////////////////////////////////
    //  private static final int CHUNK_SIZE = 32;
    static Object mapEntry(Object obj) {

        if (obj instanceof java.util.Map.Entry) {

            java.util.Map.Entry me = (java.util.Map.Entry) obj;

            return new MapEntry(me.getKey(), me.getValue());

        } else {
            return obj;
        }

    }

    public static ISeq chunkIteratorSeq(final Iterator iter) {
        if (iter.hasNext()) {

            ArrayList<Object> list = new ArrayList<>();
            list.add(mapEntry(iter.next()));
            while (iter.hasNext()) {
                list.add(mapEntry(iter.next()));
            }
            return PersistentList.create(list);
        }
        return RT.EOL;
    }

    public static ISeq seq(Object coll) {

        if (coll instanceof ISeq) {
            return (ISeq) coll;
        } else {
            return seqFrom(coll);
        }
    }

    // N.B. canSeq must be kept in sync with this!
    private static ISeq seqFrom(Object coll) {

        if (coll instanceof Seqable) {
            return ((Seqable) coll).seq();
        } else if (coll == null) {
            return null;
        } else if (coll instanceof Iterable) {
            return chunkIteratorSeq(((Iterable) coll).iterator());

        } else if (coll.getClass().isArray()) {
            return ArraySeq.createFromObject(coll);

        } else if (coll instanceof CharSequence) {
            //	return StringSeq.create((CharSequence) coll);
            throw new UnsupportedOperationException("I don't want to support this method since it deals with surrogate pairs");

        } else if (coll instanceof Map) {
            return seq(((Map) coll).entrySet());
        } else {
            Class<?> c = coll.getClass();
            //  Class<?> sc = c.getSuperclass();
            throw new IllegalArgumentException("Don't know how to create ISeq from: " + c.getName());
        }
    }

    public static boolean booleanCast(Object x) {

        if (x instanceof Boolean) {
            return ((Boolean) x);
        }
        //  return x != RT.EOL;
        return x != null;
    }

    public static boolean isSymbolOrKeyword(Object x) {

        if (x == null) {
            return false;
        }
        if (x instanceof Symbol) {
            return true;
        }
        if (x instanceof Keyword) {
            return true;
        }
        return false;
    }

    public static int count(Object o) {

        if (o instanceof Nil) {
            return 0;
        } else if (o instanceof IPersistentCollection) {
            return ((IPersistentCollection) o).count();
        }
        return countFrom(o);
    }

    private static int countFrom(Object o) {
        if (o == null) {
            return 0;
        } else if (o instanceof IPersistentCollection) {
            ISeq s = seq(o);
            o = null;
            int i = 0;
            for (; s != RT.EOL; s = s.next()) {
                if (s instanceof IPersistentCollection) {
                    return i + s.count();
                }
                i++;
            }
            return i;
        } else if (o instanceof CharSequence) {
            return ((CharSequence) o).length();
        } else if (o instanceof Collection) {
            return ((Collection) o).size();
        } else if (o instanceof Map) {
            return ((Map) o).size();
        } else if (o instanceof Map.Entry) {
            return 2;
        } else if (o.getClass().isArray()) {
            return Array.getLength(o);
        }

        throw new UnsupportedOperationException("count not supported on this type: " + o.getClass().getSimpleName());
    }

    public static IPersistentMap map(Object... init) {

        if (init == null || init.length == 0) {
            return PersistentHashMap.EMPTY;
        }
        return PersistentHashMap.createWithCheck(init);
    }

    static public Object dissoc(Object coll, Object key) {
        if (coll == null) {
            return null;
        }
        return ((IPersistentMap) coll).without(key);
    }

    public static long longCast(Object x) {
        throw new RuntimeException();
    }

    static public final Object[] EMPTY_ARRAY = new Object[]{};

    static public IPersistentVector vector(Object... init) {
        //    return LazilyPersistentVector.createOwning(init);

        ArrayList<Object> list = new ArrayList<>();
        for (Object o : init) {
            //   LOG.info("417) {}", o);
            list.add(o);
        }
        return new PersistentVector(list);

    }

    static public Object var(String s, String n) {

        throw new UnsupportedOperationException("Please use Ver.ver(s,n) .");
        //   return Var.intern(Symbol.intern(null, s), Symbol.intern(null, n));
    }

    static public Keyword keyword(String s, String n) {
        return Keyword.intern(s, n);
    }

    public static final Comparator<Object> DEFAULT_COMPARATOR = new DefaultComparator();

    private static final class DefaultComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            return Util.compare(o1, o2);
        }
    }

    public static void print(Object x, Writer w) throws IOException {

        new RTPrinter(true, false).print(x, w);
    }

    public static String printString(Object x) {
        try {
            StringWriter sw = new StringWriter();
            print(x, sw);
            sw.flush();
            return sw.toString();
        } catch (Exception e) {
            throw Util.sneakyThrow(e);
        }
    }

    static public int boundedLength(ISeq list, int limit) {
        int i = 0;
        for (ISeq c = list; c != null && i <= limit; c = c.next()) {
            i++;
        }
        return i;
    }

}
