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

import java.lang.reflect.Array;

/**
 * Utility for handling Java arrays with clojure . Supports only byte[] .
 */
public class ArraySeq implements ISeq {

    public final Object[] array;
    final int i;

    ArraySeq(Object array, int i) {
        this.i = i;
        this.array = (Object[]) array;
    }

    static public ArraySeq create(Object... array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return new ArraySeq(array, 0);
    }

    public static ISeq createFromObject(Object array) {

        if (array == null || Array.getLength(array) == 0) {
            return null;
        }
        Class aclass = array.getClass();
        if (aclass == int[].class) {
            // return new ArraySeq_int(null, (int[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        if (aclass == float[].class) {
            // return new ArraySeq_float(null, (float[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        if (aclass == double[].class) {
            //  return new ArraySeq_double(null, (double[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        if (aclass == long[].class) {
            //  return new ArraySeq_long(null, (long[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        if (aclass == byte[].class) {
            return new ArraySeq_byte(null, (byte[]) array, 0);
        }
        if (aclass == char[].class) {
            // return new ArraySeq_char(null, (char[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        if (aclass == short[].class) {
            // return new ArraySeq_short(null, (short[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        if (aclass == boolean[].class) {
            //  return new ArraySeq_boolean(null, (boolean[]) array, 0);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        // return new ArraySeq(array, 0);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object first() {
        if (array != null) {
            return array[i];
        }
        return null;
    }

    @Override
    public ISeq next() {
        if (array != null && i + 1 < array.length) {
            return new ArraySeq(array, i + 1);
        }
        return null;
    }

    @Override
    public int count() {
        if (array != null) {
            return array.length - i;
        }
        return 0;
    }

    public int index() {
        return i;
    }

    @Override
    public ISeq cons(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISeq seq() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // -----------------------------------------
    /**
     * ISeq containing bytearray in Java .
     */
    public static class ArraySeq_byte implements ISeq {

        IPersistentMap _meta;
        final byte[] array;
        final int i;

        public ArraySeq_byte(IPersistentMap meta, byte[] array, int i) {

            this._meta = meta;
            this.array = array;
            this.i = i;
        }

        @Override
        public Object first() {
            return array[i];
        }

        @Override
        public ISeq next() {
            if (i + 1 < array.length) {
                return new ArraySeq_byte(_meta, array, i + 1);
            }
            return RT.EOL;

        }

        @Override
        public ISeq cons(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int count() {
            return array.length - i;
        }

        @Override
        public ISeq seq() {
            return this;
        }

    }

}
