package iris.example.test;

import iris.clojure.lang.LispException;

/**
 *
 * @author hemmi
 */
public class TestException extends LispException {
    
    Object expected;
    Object result;
    
    
    public TestException(String message, Object expected, Object result) {
        super(message);
        this.expected = expected;
        this.result = result;
    }
    
}
