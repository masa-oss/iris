/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.test;

import iris.clojure.lang.AFn;
import iris.clojure.lang.Cons;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Nil;
import iris.clojure.lang.RT;
import iris.example.eval.ExampleEvaluator;
import iris.example.eval.LexicalScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple test functions.
 */
public class Tests {

    private static final Logger LOG = LoggerFactory.getLogger(Tests.class);

    static ISeq successful = Nil.INSTANCE;

    static ISeq testFailed = Nil.INSTANCE;

    public static class StartTesting extends AFn {

        @Override
        public Object invoke() {

            successful = Nil.INSTANCE;
            testFailed = Nil.INSTANCE;
            return Boolean.TRUE;
        }
    }

    public static class Test extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2, Object arg3) {

            ISeq form = (ISeq) arg1;
            LexicalScope env = (LexicalScope) arg2;
            ExampleEvaluator evtor = (ExampleEvaluator) arg3;

            form = form.next();
            if (form == RT.EOL) {
                throw new RuntimeException("Error in *test");
            }

            String testName = (String) form.first();

            LOG.info("start testname = {}", testName);

            try {
                ISeq seq = form.next();
                if (seq == RT.EOL) {
                    throw new RuntimeException("Error in *test");
                }

                Object eval = seq.first();
                Object result = evtor.eval(eval, env);

                seq = seq.next();
                if (seq == RT.EOL) {
                    throw new RuntimeException("Error in *test");
                }
                Object expected = seq.first();

                boolean b = isEqual(expected, result);
                LOG.info("****************  isEqual={}", b);
                if (b) {

                    successful = new Cons(testName, successful);
                } else {
                    String strExp = RT.printString(expected);

                    LOG.error("Test.expected {}", strExp);
                    
                    String strResult = RT.printString(result);

                    LOG.error("Test.result   {}", strResult);

                    throw new TestException(testName, expected, result);
                }
            } catch (Exception ex) {
                testFailed = new Cons(testName, testFailed);
            }

            return Boolean.TRUE;
        }

        boolean isEqual(Object a, Object b) {
            if (a == null) {
                return (b == null);
            } else {
                return a.equals(b);
            }
        }

    }

    public static class GetSuccessful extends AFn {

        @Override
        public Object invoke() {
            return successful;
        }
    }

    public static class GetTestFailed extends AFn {

        @Override
        public Object invoke() {
            return testFailed;
        }
    }

}
