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

/**
 * Exceptions handling return-from etc\. in <i>Common Lisp</i> .
 * 
 *
 */
// public class NonLocalException extends LispException {
public class NonLocalException extends EvaluatorException {
 
    private final Object type;   //   Qblock     Qtagbody   Qcatch   Qtoplevel   Qexit_this_level
    private final Object value;  //   VALUE      Qnil       VALUE    Qnil        VALUE
    private final Object tag;    //   TAG        TAG        TAG      Qnil        Qnil
    private final Object id;     //   FRAME-ID   FRAME-ID   Qnil     CONDITION   Qt/Qnil
    
    public NonLocalException(Object value) {
        this.type = "call/cc";
        this.value = value;
        this.tag = "tag";
        this.id = "id";
    }
    
    public NonLocalException(Object type, Object value, Object tag, Object id) {
        this.type = type;
        this.value = value;
        this.tag = tag;
        this.id = id;
    }

    /**
     * @return the type
     */
    public Object getType() {
        return type;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the tag
     */
    public Object getTag() {
        return tag;
    }

    /**
     * @return the id
     */
    public Object getId() {
        return id;
    }


}
