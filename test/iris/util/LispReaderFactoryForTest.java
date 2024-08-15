/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.util;

import iris.clojure.lang.ILispReader;
import iris.clojure.lang.LispReader;
import iris.clojure.lang.ObjectFactory;
import iris.clojure.readmacro.LispReaderMacro;

/**
 *
 * @author hemmi
 */
public class LispReaderFactoryForTest {

    public static ILispReader getInstance() {
        
        LispReaderMacro lrm = new LispReaderMacro();

        ResolverExForTest res2 = new ResolverExForTest();

        LispReader target = new LispReader(new ResolverForTest(), lrm, res2, new ObjectFactory());
        return target;
        
        
    }

    
}
