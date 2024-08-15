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
 * This class is the same as <b>clojure.lang.LispReader.java</b> .
 *
 */
public class ReaderException extends LispException implements IExceptionInfo {

    public final int line;
    public final int column;
    public final Object data;

    final static public String ERR_NS = null;

    final static public Keyword ERR_LINE = Keyword.intern(ERR_NS, "line");

    final static public Keyword ERR_COLUMN = Keyword.intern(ERR_NS, "column");

    public ReaderException(String message, int line, int column, Throwable cause) {
        super(message, cause);
        this.line = line;
        this.column = column;
        this.data = RT.map(ERR_LINE, line, ERR_COLUMN, column);
    }

    public ReaderException(int line, int column, Throwable cause) {
        super(cause);
        this.line = line;
        this.column = column;
        this.data = RT.map(ERR_LINE, line, ERR_COLUMN, column);
    }

    @Override
    public IPersistentMap getData() {
        return (IPersistentMap) data;
    }

	/**
	* The returned string may change in the future.
	* Do not write logic using the returned string.
	*
	*/
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("ReaderException[");
        if (getMessage() != null) {
            sb.append(getMessage());
        }
        if (line >= 0) {
            sb.append(", line=");
            sb.append(line);
        }
        if (column >= 0) {
            sb.append(", column=");
            sb.append(column);
        }
        
        sb.append("]");
        return sb.toString();
    }

}
