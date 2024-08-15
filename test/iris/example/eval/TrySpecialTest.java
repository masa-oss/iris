package iris.example.eval;

import iris.clojure.lang.ILispReader;
import iris.util.ExampleEvaluatorForTest;
import iris.util.LispReaderFactoryForTest;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class TrySpecialTest {

    public TrySpecialTest() {
    }

    @Test
    public void testIsMatch() {
        System.out.println("isMatch");
        Object b = new IllegalArgumentException();
        TrySpecial instance = new TrySpecial();

        boolean result = instance.isMatch(RuntimeException.class, b);

        System.out.println("result : " + result);

        assertEquals(true, result);
    }

    @Test
    public void testIsMatch2() {
        System.out.println("isMatch2");
        Object b = new RuntimeException();
        TrySpecial instance = new TrySpecial();

        boolean result = instance.isMatch(IllegalArgumentException.class, b);

        System.out.println("result2 : " + result);

        assertEquals(false, result);
    }


    @Test
    public void testIsCatch() {
        System.out.println("isCatch");
        ILispReader reader = LispReaderFactoryForTest.getInstance();

        Object obj = reader.readString("(catch Exception e (println \"expression 3 throws\")))");

        TrySpecial instance = new TrySpecial();

        boolean result = instance.isCatch(obj);

        System.out.println("isCatch  " + result);

        assertEquals(true, result);
    }

    @Test
    public void testIsCatch2() {
        System.out.println("isCatch2");
        ILispReader reader = LispReaderFactoryForTest.getInstance();

        Object obj = reader.readString("(finally  (println \"expression 3 throws\")))");

        TrySpecial instance = new TrySpecial();

        boolean result = instance.isCatch(obj);

        System.out.println("isCatch2  " + result);

        assertEquals(false, result);
    }

    
    // Error
    @Test
    public void testTryNoexception() {
        
        System.out.println("testTryNoexception");

        ILispReader reader = LispReaderFactoryForTest.getInstance();
        
        //   https://clojuredocs.org/clojure.core/try

        Object obj = reader.readString("(try \"I will not be returned.\" "
                                +          "  \"I will be returned\" "
                                +          "   (catch Exception e (.getMessage e))     "
                                +          "  (finally \"I will also not be returned.\"))  "
        );


        LexicalScope oldEnv = new LexicalScope();
        
        ExampleEvaluatorForTest.Builder buil = new ExampleEvaluatorForTest.Builder();
        buil.add(1, null);
        buil.add(1, null);
        buil.add(1, null);
        ExampleEvaluator evtor = buil.build();
        
        TrySpecial instance = new TrySpecial();
        
        Object result = instance.invoke(obj, oldEnv, evtor);
        
        System.out.println("testTryNoexception : "   + result);
        
        assertEquals("I will be returned", result);
    }
    

    @Test
    public void testTryException() {
        
        System.out.println("testTryException");

        ILispReader reader = LispReaderFactoryForTest.getInstance();
        
        //   https://clojuredocs.org/clojure.core/try

        Object obj = reader.readString("(try (throw (RuntimeException.)) "
                                +          "  \"I will not be returned\" "
                                +          "   (catch Exception e  \"I will be returned\"  )     "
                                +          "  (finally \"I will also not be returned.\"))  "
        );


        LexicalScope oldEnv = new LexicalScope();
        
        
        ExampleEvaluatorForTest.Builder buil = new ExampleEvaluatorForTest.Builder();
        buil.add(2, new RuntimeException("my error"));
        buil.add(1, null);
        buil.add(1, null);
        ExampleEvaluator evtor = buil.build();
        
        TrySpecial instance = new TrySpecial();
        
        Object result = instance.invoke(obj, oldEnv, evtor);
        
        System.out.println("testTryException : "   + result);
        
        assertEquals("I will be returned", result);
    }
    


    @Test
    public void testTryFinally() {
        
        System.err.println("testTryFinally");

        ILispReader reader = LispReaderFactoryForTest.getInstance();
        
        //   https://clojuredocs.org/clojure.core/try

        Object obj = reader.readString("(try (throw (RuntimeException.)) "
                                +          "  \"I will not be returned\" "
                                +          "  (finally \"I will also not be returned.\"  123   ))  "
        );


        LexicalScope oldEnv = new LexicalScope();
        
        
        ExampleEvaluatorForTest.Builder buil = new ExampleEvaluatorForTest.Builder();
        buil.add(2, new RuntimeException("my error"));
        buil.add(1, null);
        buil.add(1, null);
        ExampleEvaluatorForTest evtor = buil.build();
        
        TrySpecial instance = new TrySpecial();
        
        Object result = instance.invoke(obj, oldEnv, evtor);
        
        System.out.println("testTryFinally : "   + result);
        
        
        // The body of finally calls eval in order.
        List calledList = evtor.getCalledList();
        
        System.out.println("size = " + calledList.size()  );
        assertEquals(3, calledList.size());


        System.out.println("" + calledList.toString());
        assertEquals("I will also not be returned.", calledList.get(1));
        assertEquals(123L, calledList.get(2));
    }


}
