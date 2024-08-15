/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.fn;

import iris.clojure.lang.AFn;
import iris.clojure.lang.ILispReader;
import iris.clojure.nsvar.LispReaderFactory;
import iris.example.eval.ETE;
import java.io.PushbackReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is a subset of <i>Clojure</i>'s <b>read</b>.
 *
 */
public class ReadFn extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(ReadFn.class);

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {

        PushbackReader r = null;
        if (arg1 instanceof PushbackReader) {

            r = (PushbackReader) arg1;

        } else if (arg1 instanceof Reader) {

            LOG.info("Create instance of PushbackReader");
            r = new PushbackReader((Reader) arg1);
        } else {
            ETE.throwException("The first argument of read must be java.io.Reader", arg1, null);
        }

        ILispReader lispReader = new LispReaderFactory().getLispReader();

        return lispReader.read(r, false, arg3, false);

    }

}
