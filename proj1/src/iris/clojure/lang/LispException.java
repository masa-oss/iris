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

/**
 * Base class of ReaderException , EvaluatorException.
 *
 * @author hemmi
 */
public class LispException extends RuntimeException {


    public LispException() {
        super();
    }

    public LispException(String message) {
        super(message);
    }

    public LispException(String message, Throwable cause) {
        super(message, cause);
    }

    public LispException(Throwable cause) {
        super(cause);
    }

}
