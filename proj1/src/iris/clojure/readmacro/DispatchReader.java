/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */
package iris.clojure.readmacro;

import java.io.PushbackReader;
import java.io.Reader;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.Util;
import iris.clojure.lang.IReadMacro;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;

/**
 *
 */
public class DispatchReader extends AFun {

    IReadMacro[] dispatchMacros = new IReadMacro[256];

    public DispatchReader() {
        dispatchMacros['^'] = new MetaReader();
        dispatchMacros['#'] = new SymbolicValueReader();

        if (RT.COMMON_LISP) {
            Symbol func= Symbol.intern(null, "function");
            dispatchMacros['\''] = new WrappingReader(func);
            
        } else {
            dispatchMacros['\''] = new VarReader();
        }

        dispatchMacros['"'] = new RegexReader();
        dispatchMacros['('] = new FnReader();
        dispatchMacros['{'] = new SetReader();
        dispatchMacros['='] = new EvalReader();
        dispatchMacros['!'] = new CommentReader();
        dispatchMacros['<'] = new UnreadableReader();
        dispatchMacros['_'] = new DiscardReader();
        dispatchMacros['?'] = new ConditionalReader();
        dispatchMacros[':'] = new NamespaceMapReader();

        if (RT.COMMON_LISP) {
            dispatchMacros['\\'] = new CharacterReader();
        }

    }

    private IReadMacro ctorReader = new CtorReader();
    
    @Override
    public Object invoke(Object reader, Object hash, Object opts, final Object pendingForms,
            IClojureReader cr) {

        int ch = cr.read1((Reader) reader);
        if (ch == -1) {
            throw Util.runtimeException("EOF while reading character");
        }
        IReadMacro fn = dispatchMacros[ch];

        // Try the ctor reader first
        if (fn == null) {
            cr.unread((PushbackReader) reader, ch);
            final Object pendingForm2 = cr.ensurePending(pendingForms);

//            IFun ctor = cr.getCtorReader();
            IReadMacro ctor = ctorReader;

            Object result = ctor.invoke(reader, ch, opts, pendingForm2, cr);

            if (result != null) {
                return result;
            } else {
                throw Util.runtimeException(String.format("No dispatch macro for: %c", (char) ch));
            }
        }
        return fn.invoke(reader, ch, opts, pendingForms, cr);
    }
}
