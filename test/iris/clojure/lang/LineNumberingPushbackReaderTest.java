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

import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class LineNumberingPushbackReaderTest {

    public LineNumberingPushbackReaderTest() {
    }

    public void testIncPointer() throws IOException {

        StringReader sr = new StringReader("abcdefghijklmn");
        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);
        System.out.print("index = " + instance.index);
        System.out.print(", line = " + instance.getLineNumber());
        System.out.println(", column = " + instance.getColumnNumber());

        for (int i = 0; i < 10; i++) {
            instance.read();
            //  instance.incPointer();
            System.out.print("index = " + instance.index);
            System.out.print(", line = " + instance.getLineNumber());
            System.out.println(", column = " + instance.getColumnNumber());

        }
    }

    public void testDecPointer() throws IOException {

        StringReader sr = new StringReader("abcdefghijklmn");
        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);
        System.out.println("index = " + instance.index);
      //  System.out.print(", line = " + instance.getLineNumber());
      //  System.out.println(", column = " + instance.getColumnNumber());

        for (int i = 0; i < 10; i++) {
            instance.decPointer();
            System.out.println("index = " + instance.index);
        }
    }
    
    
    @Test
    public void testGetLineNumber() {
        System.out.println("getLineNumber");

        StringReader sr = new StringReader(" abc");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(1, line);
        assertEquals(1, column);
    }

    // @Test
    @Test
    public void testGetLineNumber2() throws IOException {
        System.out.println("getLineNumber2");

        StringReader sr = new StringReader(" abc");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        instance.read();

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(1, line);
        assertEquals(2, column);
    }

    // @Test
    @Test
    public void testGetLineNumber3() throws IOException {
        System.out.println("getLineNumber3");

        StringReader sr = new StringReader("a\nbc");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        instance.read();
        instance.read();

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(2, line);
        assertEquals(1, column);
    }

    // @Test
    @Test
    public void testGetLineNumber4() throws IOException {
        System.out.println("getLineNumber4");

        StringReader sr = new StringReader("a\nbc");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        instance.read();
        instance.read();
        instance.read();

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(2, line);
        assertEquals(2, column);
    }

    // @Test
    @Test
    public void testGetLineNumber5() throws IOException {
        System.out.println("getLineNumber5");

        StringReader sr = new StringReader("a\nbc");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        instance.read();
        instance.read();
        int c1 = instance.read();
        instance.unread(c1);

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(2, line);
        assertEquals(1, column);
    }

    // @Test
    @Test
    public void testGetLineNumber6() throws IOException {
        System.out.println("getLineNumber6(EOF)");

        StringReader sr = new StringReader("ab");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        instance.read();
        instance.read();
        instance.read();

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(1, line);
        assertEquals(3, column);
    }

    @Test
    public void testGetLineNumber7() throws IOException {
        System.out.println("getLineNumber7");

        StringReader sr = new StringReader("a\nbc");

        LineNumberingPushbackReader instance = new LineNumberingPushbackReader(sr);

        instance.read();
        int c2 = instance.read();
        instance.unread(c2);

        int line = instance.getLineNumber();
        int column = instance.getColumnNumber();

        System.out.println("line=" + line + ", col=" + column);

        assertEquals(1, line);
        assertEquals(2, column);
    }
}
