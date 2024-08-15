/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.readmacro;

/**
 *   Author: Masahito Hemmi
 */


import iris.clojure.lang.ILispReaderMacro;

import iris.clojure.lang.Symbol;
import iris.clojure.lang.IReadMacro;

/**
 * Performs reader macro processing for one character. If it starts with #, continue processing with DispachReader .
 * 
 */
public class LispReaderMacro implements ILispReaderMacro {

    private final IReadMacro[] macros = new IReadMacro[256];

    
    public LispReaderMacro() {

        final Symbol QUOTE = Symbol.intern("quote");

        final Symbol DEREF = Symbol.intern("iris.clojure.core", "deref");

        macros['"'] = new StringReaderEx();
        macros[';'] = new CommentReader();

        macros['\''] = new WrappingReader(QUOTE);   // 'x --> (quote x)

        macros['@'] = new WrappingReader(DEREF);

        macros['^'] = new MetaReader();

        macros['`'] = new SyntaxQuoteReaderEx();

        macros['~'] = new UnquoteReader();
        macros['('] = new ListReader();
        macros[')'] = new UnmatchedDelimiterReader();
        macros['['] = new VectorReader();
        macros[']'] = new UnmatchedDelimiterReader();
        macros['{'] = new MapReader();
        macros['}'] = new UnmatchedDelimiterReader();

        macros['\\'] = new CharacterReader();
        
        macros['#'] = new DispatchReader();

    }

    @Override
    public IReadMacro getMacro(int ch) {
        if (ch < macros.length) {
            return macros[ch];
        }
        return null;
    }

    @Override
    public boolean isMacro(int ch) {
        return (ch < macros.length && macros[ch] != null);
    }

    @Override
    public boolean isTerminatingMacro(int ch) {
        return (ch != '#' && ch != '\'' && ch != '%' && isMacro(ch));
    }
}
