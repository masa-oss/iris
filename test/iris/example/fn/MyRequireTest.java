package iris.example.fn;

import iris.clojure.lang.ILispReader;
import iris.clojure.lang.Nil;
import iris.clojure.nsvar.LispReaderFactory;
import iris.util.MockLoadFileFn;
import iris.util.StaticNamespaceForTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class MyRequireTest {
    
    public MyRequireTest() {
    }

    /*
    
(require 'clojure.math)

(require '[ clojure.math] )

(require '[ clojure.math :as m ])

(require '[ clojure.math :refer [sin cos]] )

(require '[ clojure.math]
        '[ clojure.math :as m ])    
    
    */
    


    @Test
    public void testInvoke() {
        
        
        System.out.println("invoke  symbol-1");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("clojure.math");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest();
        MockLoadFileFn b = new MockLoadFileFn(true);   // lspが見つかった
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }


    @Test
    public void testInvoke2() {
        
        
        System.out.println("invoke  symbol-2");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("clojure.math");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest();
        MockLoadFileFn b = new MockLoadFileFn(false);   // lspが見つからない：クラスをロードする
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }


    
    @Test
    
    public void testInvoke10() {
        
        System.out.println("invoke10");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("[ clojure.math]");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest();
        MockLoadFileFn b = new MockLoadFileFn(true);   // ******
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }

    @Test
    public void testInvoke11() {
        
        System.out.println("invoke11");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("[ clojure.math]");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest();
        MockLoadFileFn b = new MockLoadFileFn(false);   // ******
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }

    

    @Test
    public void testInvoke20() {
        
        System.out.println("invoke20");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("[ clojure.math :as m]");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest(true); // ***
        MockLoadFileFn b = new MockLoadFileFn(true);   // ******
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }

    @Test
    public void testInvoke21() {
        
        System.out.println("invoke21");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("[ clojure.math :as m]");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest(true);  //***
        MockLoadFileFn b = new MockLoadFileFn(false);   // ******
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }

    @Test
    public void testInvoke30() {
        
        System.out.println("invoke30");

        LispReaderFactory fact = new LispReaderFactory();
        ILispReader lispReader = fact.getLispReader();


        Object arg1 = lispReader.readString("[ clojure.math :refer  [sin] ]");
        
        StaticNamespaceForTest a = new StaticNamespaceForTest(true); // ***
        MockLoadFileFn b = new MockLoadFileFn(true);   // ******
        
        MyRequire instance = new MyRequire(a, b);
        Object result = instance.invoke(arg1);


        Object expResult = Nil.INSTANCE;

        assertEquals(expResult, result);
    }
    


}
