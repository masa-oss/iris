package iris.example.eval;

import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class LexicalScopeTest {
    
    public LexicalScopeTest() {
    }
    
    static Symbol X = Symbol.intern(null, "x");
    static Symbol Y = Symbol.intern(null, "y");
    static Symbol Z = Symbol.intern(null, "z");

    static Symbol M = Symbol.intern(null, "m");

    @Test
    public void testBuilder() {
        System.out.println("testBuilder");
        
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        buil.addBinding(X, 1L);
        
        LexicalScope instance = buil.build();
        System.out.println("instance=" + instance);
        assertNotNull(instance);
    }


    @Test
    public void testBuilder2() {
        System.out.println("testBuilder2");
        
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        buil.addBinding(X, 1L);
        
        Exception exception = null;
        try {
            buil.addBinding(X, 2L);
        } catch (Exception ex) {
            exception = ex;
        }
        assertTrue(exception instanceof RuntimeException    );
    }


    @Test
    public void testBuilder3() {
        System.out.println("testBuilder3");
        
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        buil.addBinding(X, 1L);
        buil.addBinding(Y, 2L);
        buil.addBinding(Z, 3L);
        
        LexicalScope instance = buil.build();
        System.out.println("instance3=" + instance);
        assertNotNull(instance);
        
        int[] arr = instance.getVariableIndex(Y);
        assertNotNull(arr);
        System.out.println("m="   + arr[0] + ", n =" + arr[1]    );
        
        Object variableValue = instance.getVariableValue(arr[0], arr[1]);
        System.out.println("72)" + variableValue);
        assertEquals(2L, variableValue);
    }


    
    @Test
    public void testBuilder4() {
        System.out.println("testBuilder4");
        
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        buil.addBinding(X, 1L);
        buil.addBinding(Y, 2L);
        buil.addBinding(Z, 3L);
        
        LexicalScope instance = buil.build();
        System.out.println("instance4=" + instance);
        assertNotNull(instance);
        
        int[] arr = instance.getVariableIndex(M);
        assertNull(arr);
    }
    

    @Test
    public void testBuilder5() {
        System.out.println("testBuilder5");
        
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        LexicalScope instance = buil.build();
        System.out.println("instance5=" + instance);
        assertNotNull(instance);
        
        int[] arr = instance.getVariableIndex(M);
        assertNull(arr);
        
        
    }


    @Test
    public void testBuilder6() {
        System.out.println("testBuilder6");
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        buil.addBinding(X, 1L);
        buil.addBinding(Y, 9L);
        
        LexicalScope instance = buil.build();
        assertNotNull(instance);


        LexicalScope.Builder builD = new LexicalScope.Builder(instance);
        LexicalScope instance2 = builD.build();


        
        int[] arr = instance2.getVariableIndex(Y);
        
        System.out.println("m="   + arr[0] + ", n =" + arr[1]    );
        
        assertNotNull(arr);
        
        Object variableValue = instance2.getVariableValue(arr[0], arr[1]);
        System.out.println("139)" + variableValue);
        assertEquals(9L, variableValue);
        
        
    }

    
    @Test
    public void testBuilder7() {
        System.out.println("testBuilder7");
        
        LexicalScope.Builder buil = new LexicalScope.Builder(null);
        buil.addBinding(X, 1L);
        buil.addBinding(Y, 9L);
        
        LexicalScope instance = buil.build();
        assertNotNull(instance);


        LexicalScope.Builder builD = new LexicalScope.Builder(instance);
        builD.addBinding(Y, 99L);
        LexicalScope instance2 = builD.build();


        
        int[] arr = instance2.getVariableIndex(Y);
        
        System.out.println("m="   + arr[0] + ", n =" + arr[1]    );
        
        assertNotNull(arr);
        
        Object variableValue = instance2.getVariableValue(arr[0], arr[1]);
        System.out.println("171)" + variableValue);
        assertEquals(99L, variableValue);
        
        LexicalScopeUtil util = new LexicalScopeUtil();
        
        IPersistentVector chains = util.getChains(instance2);
        
        String str = RT.printString(chains);
        System.out.println("178) " + str);
    }



    
}
