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

import iris.clojure.lang.ResolverEx;
import iris.clojure.lang.Symbol;

/**
 *
 * @author hemmi
 */
public class ResolverExForTest implements ResolverEx {

    @Override
    public boolean isSpecialEx(Object sym) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Symbol resolveSymbol(Symbol sym) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getCurrentNSMapping(Symbol name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object specialsValAt(Object key, Object notFound) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
