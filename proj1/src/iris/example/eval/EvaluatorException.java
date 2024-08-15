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

import iris.clojure.lang.IExceptionInfo;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.LispException;


/**
 * Exceptions during execution phase .
 */
public class EvaluatorException extends LispException implements IExceptionInfo {

    private final IPersistentMap data;
    
    public EvaluatorException() {
        
        this.data = null;
    }

    public EvaluatorException(String message) {
        super(message);
        this.data = null;
    }
    
    public EvaluatorException(String message, Throwable cause) {
        super(message, cause);
        this.data = null;
    }

    public EvaluatorException(IPersistentMap meta, String message, Throwable cause) {
        super(message, cause);
        this.data = meta;
    }

    @Override
    public IPersistentMap getData() {
        return (IPersistentMap) data;
    }
    
    final static  Keyword ERR_LINE = Keyword.intern(null, "line");

    final static  Keyword ERR_COLUMN = Keyword.intern(null, "column");
    
    
	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("EvaluatorException[");
        
//        String message = super.getCause().getMessage();
        String message = super.getMessage();
        sb.append(message);
                
        if (this.data != null) {
            
            Object l = data.valAt(ERR_LINE);
            if (l != null) {
                sb.append(", line ");
                sb.append(l.toString());
            }
            
            Object c = data.valAt(ERR_COLUMN);
            if (c != null) {
                sb.append(", column ");
                sb.append(c.toString());
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    
}
