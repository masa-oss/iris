/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.nsvar;

import iris.clojure.lang.ResolverEx;
import iris.clojure.lang.Symbol;

import org.slf4j.LoggerFactory;

/**
 * This class was created when we separated LispReader's inner class into an independent class.
 */
public class CompilerResolverEx implements ResolverEx {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CompilerResolverEx.class);
    

    @Override
    public boolean isSpecialEx(Object sym) {

        return CljCompiler.isSpecialEx(sym);
    }

    @Override
    public Symbol resolveSymbol(Symbol sym) {

        LOG.info("***** resolveSymbol  {}", sym);
        return CljCompiler.resolveSymbol(sym);
    }

    @Override
    public Object getCurrentNSMapping(Symbol name) {

        LOG.info("***** getCurrentNSMapping  {}", name);
        Namespace currentNS = CljCompiler.currentNS();
        return currentNS.getMapping(name);
    }

    @Override
    public Object specialsValAt(Object key, Object notFound) {
        
        LOG.info("***** specialsValAt  {}", key);
        return CljCompiler.specials.valAt(key, notFound);
        
    }
    
}
