/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */
package iris.clojure.lang;

/**
 * <p>
 * A persistent, functional, sequence interface</p>
 * <p>
 * ISeqs are immutable values, i.e. neither first(), nor rest() changes or
 * invalidates the ISeq</p>
 * 
 * 
 * This class is a subset of  <i>clojure.lang.ISeq.java</i>  .
 */

 // public interface ISeq extends Counted , IPersistentCollection {
public interface ISeq extends IPersistentCollection {

    Object first();

    /**
     * Finally, RT.EOL is returned.
     *
     * @return
     */
    ISeq next();



    ISeq cons(Object o);

}
