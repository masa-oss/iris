/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.lang;

import iris.util.LispReaderFactoryForTest;
import iris.util.TestInitializer;
import java.io.PushbackReader;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class LispReaderTest {

    public LispReaderTest() {
    }

    public void testReadString() {
        System.out.println("readString");
        String s = "";
        ILispReader instance = LispReaderFactoryForTest.getInstance();
        Object expResult = null;
        Object result = instance.readString(s);
        assertEquals(expResult, result);
    }

    // @Test
    @Test
    public void testRead_Long() {
        System.out.println("read Long");

        PushbackReader r = new PushbackReader(new java.io.StringReader("123"));

        boolean eofIsError = false;
        Object eofValue = new Object();
        boolean isRecursive = false;
        ILispReader instance = LispReaderFactoryForTest.getInstance();

        Object result = instance.read(r, eofIsError, eofValue, isRecursive);

        System.out.println("result = " + result);
        assertTrue(result instanceof Long);
        assertEquals(123L, result);
    }

    @Test
    public void testRead_123N() {
        System.out.println("read 123N");

        PushbackReader r = new PushbackReader(new java.io.StringReader("123N"));

        boolean eofIsError = false;
        Object eofValue = new Object();
        boolean isRecursive = false;
        ILispReader instance = LispReaderFactoryForTest.getInstance();

        Object result = instance.read(r, eofIsError, eofValue, isRecursive);

        System.out.println("result = " + result.getClass().getName());
        assertTrue(result instanceof BigInt);

        assertEquals(BigInt.fromLong(123L), result);
    }

    @Test
    public void testRead_123M() {
        System.out.println("read 123M");

        PushbackReader r = new PushbackReader(new java.io.StringReader("123M"));

        boolean eofIsError = false;
        Object eofValue = new Object();
        boolean isRecursive = false;
        ILispReader instance = LispReaderFactoryForTest.getInstance();

        Object result = instance.read(r, eofIsError, eofValue, isRecursive);

        System.out.println("result = " + result.getClass().getName());
        assertTrue(result instanceof BigDecimal);

        BigDecimal b = new BigDecimal("123");

        assertEquals(b, result);
    }


    @Test
    public void testRead_True() {
        System.out.println("read true");

        PushbackReader r = new PushbackReader(new java.io.StringReader("true"));

        boolean eofIsError = false;
        Object eofValue = new Object();
        boolean isRecursive = false;
        ILispReader instance = LispReaderFactoryForTest.getInstance();

        Object result = instance.read(r, eofIsError, eofValue, isRecursive);

        System.out.println("result = " + result.getClass().getName());
        assertTrue(result instanceof Boolean);

        Boolean b =  Boolean.TRUE;

        assertEquals(b, result);
    }


    @Test
    public void testRead_False() {
        System.out.println("read false");

        PushbackReader r = new PushbackReader(new java.io.StringReader("false"));

        boolean eofIsError = false;
        Object eofValue = new Object();
        boolean isRecursive = false;
        ILispReader instance = LispReaderFactoryForTest.getInstance();

        Object result = instance.read(r, eofIsError, eofValue, isRecursive);

        System.out.println("result = " + result.getClass().getName());
        assertTrue(result instanceof Boolean);

        Boolean b =  Boolean.FALSE;

        assertEquals(b, result);
    }

    @Test
    public void testRead_NULL() {
        System.out.println("read NULL");
        
        TestInitializer.initRT01();

        PushbackReader r = new PushbackReader(new java.io.StringReader("NULL"));

        boolean eofIsError = false;
        Object eofValue = new Object();
        boolean isRecursive = false;
        ILispReader instance = LispReaderFactoryForTest.getInstance();

        Object result = instance.read(r, eofIsError, eofValue, isRecursive);
        
        System.out.println("result=" + result);

        assertTrue(result == null);

    }




}
