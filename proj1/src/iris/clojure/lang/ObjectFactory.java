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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class used by LispReader to create objects .
 */
public class ObjectFactory implements IObjectFactory {

    static Pattern intPat
            = Pattern.compile(
                    "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?");

    static Pattern floatPat = Pattern.compile("([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?");

    static Pattern ratioPat = Pattern.compile("([-+]?[0-9]+)/([0-9]+)");

    @Override
    public Object matchNumber(String s) {
        Matcher m = intPat.matcher(s);
        if (m.matches()) {
            if (m.group(2) != null) {
                if (m.group(8) != null) {
                    return BigInt.ZERO;   // ****
                }
//                return Numbers.num(0);
                return this.num(0);
            }
            boolean negate = (m.group(1).equals("-"));
            String n;
            int radix = 10;
            if ((n = m.group(3)) != null) {
                radix = 10;
            } else if ((n = m.group(4)) != null) {
                radix = 16;
            } else if ((n = m.group(5)) != null) {
                radix = 8;
            } else if ((n = m.group(7)) != null) {
                radix = Integer.parseInt(m.group(6)); //****
            }
            if (n == null) {
                return null;
            }

            return this.createBigIntOrLong(n, radix,
                    negate, m.group(8));

        }
        m = floatPat.matcher(s);
        if (m.matches()) {
            if (m.group(4) != null) {
                return new BigDecimal(m.group(1));
            }
            return Double.parseDouble(s);
        }
        m = ratioPat.matcher(s);
        if (m.matches()) {
            String numerator = m.group(1);
            if (numerator.startsWith("+")) {
                numerator = numerator.substring(1);
            }
            
            return Numbers.divide(Numbers.reduceBigInt(BigInt.fromBigInteger(new BigInteger(numerator))),
                    Numbers.reduceBigInt(BigInt.fromBigInteger(new BigInteger(m.group(2)))));
             
        }
        return null;
    }

    @Override
    public Number num(long x) {
        return Long.valueOf(x);
    }

    @Override
    public Number createBigIntOrLong(String n, int radix,
            boolean negate, String group8) {

        BigInteger bn = new BigInteger(n, radix);

        if (negate) {
            bn = bn.negate();
        }

        if (group8 != null) {
            return BigInt.fromBigInteger(bn);
        }
        return bn.bitLength() < 64
                ? num(bn.longValue())
                : BigInt.fromBigInteger(bn);
    }

    @Override
    public Object createJavaNull() {
        return null;
    }

    @Override
    public Object createJavaTrue() {
        return Boolean.TRUE;
    }

    @Override
    public Object createJavaFalse() {
        return Boolean.FALSE;
    }

    @Override
    public IPersistentList createPersistentList(List<Object> list, int line, int column) {

        if (list.isEmpty()) {
            if (RT.COMMON_LISP) {
                return Nil.INSTANCE;
            } else {
                return PersistentList.EMPTY;
            }
        }
        IObj s = (IObj) PersistentList.create(list);
        if (line != -1) {
            Associative meta = RT.meta(s);
            meta = RT.assoc(meta, RT.LINE_KEY, RT.get(meta, RT.LINE_KEY, line));
            meta = RT.assoc(meta, RT.COLUMN_KEY, RT.get(meta, RT.COLUMN_KEY, column));
            
            IObj o2 = s.withMeta((IPersistentMap) meta);
            
            return (IPersistentList) o2;
            
        } else {
            return (IPersistentList) s;
        }

    }

    @Override
    public Object createCommonLispNil() {
        
        return Nil.INSTANCE;
    }

    @Override
    public Symbol createSymbol(String name) {
        
        if ("nil".equals(name)) {
            
            return Nil.INSTANCE;
        }
        
        return Symbol.intern(name);
    }

    @Override
    public IPersistentSet createPersistentHashSet(Set<Object> set) {
        
        return new PersistentHashSet(set);
    }

    @Override
    public IPersistentVector createPersistentVector(List<Object> list) {

        return new PersistentVector(list);
    }

    @Override
    public IPersistentMap createPersistentHashMap(Object[] arr) {
        return RT.map(arr);
    }

}
