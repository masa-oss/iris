/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.eval;

import iris.clojure.lang.Cons;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.MapEntry;
import iris.clojure.lang.Nil;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author hemmi
 */
public class ExampleEvaluator_BindTest {

    public ExampleEvaluator_BindTest() {
    }

    Symbol x = Symbol.intern(null, "x");
    Symbol y = Symbol.intern(null, "y");
    Symbol z = Symbol.intern(null, "z");
    Symbol rest = Symbol.intern(null, "&rest");

    Long one = 1L;
    Long two = 2L;
    Long three = 3L;

    @Test
    public void testBindLambda_Empty() {

        System.out.println("bindLambda empty");

      //  RT.EOL = Nil.INSTANCE;
        ISeq args = Nil.INSTANCE;
        ISeq varNames = Nil.INSTANCE;

        ExampleEvaluatorImpl instance = new ExampleEvaluatorImpl(null);

        List<MapEntry> result = instance.bindLambda(args, varNames);
        assertEquals(0, result.size());
    }

    @Test
    public void testBindLambda() {

        System.out.println("bindLambda");

     //   RT.EOL = Nil.INSTANCE;

        ISeq args = new Cons(one, new Cons(two, Nil.INSTANCE));
        ISeq varNames = new Cons(x, new Cons(y, Nil.INSTANCE));

        ExampleEvaluatorImpl instance = new ExampleEvaluatorImpl(null);

        List<MapEntry> result = instance.bindLambda(args, varNames);
/*
        for (MapEntry me : result) {
            System.out.println("me = " + me.getStringForPrint());
        }
*/
        assertEquals(2, result.size());

        MapEntry m0 = result.get(0);
        assertEquals(x, m0.getKey());
        assertEquals(one, m0.getValue());

        MapEntry m1 = result.get(1);
        assertEquals(y, m1.getKey());
        assertEquals(two, m1.getValue());
    }

    @Test
    public void testBindLambdaAndRest1() {

        System.out.println("bindLambdaAnd &rest 1");

      //  RT.EOL = Nil.INSTANCE;

        ISeq args = new Cons(one, new Cons(two, new Cons(three, Nil.INSTANCE)));
        ISeq varNames = new Cons(x, new Cons(rest, new Cons(y, Nil.INSTANCE)));

        ExampleEvaluatorImpl instance = new ExampleEvaluatorImpl(null);

        List<MapEntry> result = instance.bindLambda(args, varNames);

        assertEquals(2, result.size());

        MapEntry m0 = result.get(0);
        assertEquals(x, m0.getKey());
        assertEquals(one, m0.getValue());

        MapEntry m1 = result.get(1);
        assertEquals(y, m1.getKey());
        assertTrue(m1.getValue() instanceof Cons);
    }

    @Test
    public void testBindLambdaTooFew() {

        System.out.println("bindLambda too few");

      //  RT.EOL = Nil.INSTANCE;

        ISeq args = new Cons(two, Nil.INSTANCE);
        ISeq varNames = new Cons(x, new Cons(y, Nil.INSTANCE));

        ExampleEvaluatorImpl instance = new ExampleEvaluatorImpl(null);

        Exception excep = null;
        try {

            List<MapEntry> result = instance.bindLambda(args, varNames);

        } catch (Exception ex) {
            excep = ex;
        }

        assertTrue(excep != null);
        assertTrue(excep instanceof RuntimeException);

        String msg = excep.getMessage();
        assertEquals("Too few arguments", msg);
    }

    @Test
    public void testBindLambdaTooMany() {

        System.out.println("bindLambda too many");

     //   RT.EOL = Nil.INSTANCE;

        ISeq args = new Cons(one, new Cons(two, new Cons(three, Nil.INSTANCE)));

        ISeq varNames = new Cons(x, new Cons(y, Nil.INSTANCE));

        ExampleEvaluatorImpl instance = new ExampleEvaluatorImpl(null);

        Exception excep = null;
        try {

            List<MapEntry> result = instance.bindLambda(args, varNames);
        } catch (Exception ex) {
            excep = ex;

           // throw ex;
        }
        assertTrue(excep != null);
        assertTrue(excep instanceof RuntimeException);

        String msg = excep.getMessage();
        assertEquals("Too many arguments", msg);

    }


    @Test
    public void testBindLambdaAndRest2() {

        System.out.println("bindLambdaAnd &rest 2");

     //   RT.EOL = Nil.INSTANCE;

        ISeq args = new Cons(one, new Cons(two, Nil.INSTANCE));
        ISeq varNames = new Cons(x, new Cons(y, new Cons(rest, new Cons(z, Nil.INSTANCE))));

        ExampleEvaluatorImpl instance = new ExampleEvaluatorImpl(null);

        List<MapEntry> result = instance.bindLambda(args, varNames);

        assertEquals(3, result.size());

        MapEntry m0 = result.get(0);
        assertEquals(x, m0.getKey());
        assertEquals(one, m0.getValue());

        MapEntry m1 = result.get(1);
        assertEquals(y, m1.getKey());
        assertEquals(two, m1.getValue());

        MapEntry m2 = result.get(2);
        assertEquals(z, m2.getKey());
        assertTrue(m2.getValue() instanceof Nil);
    }


}
