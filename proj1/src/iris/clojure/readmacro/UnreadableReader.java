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

import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.LineNumberingPushbackReader;
import iris.clojure.lang.ReaderException;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read #< ...
 */
public class UnreadableReader extends AFun {

    private static final Logger LOG = LoggerFactory.getLogger(UnreadableReader.class);
    
    
    @Override
    public Object invoke(Object reader, Object leftangle, Object opts, Object pendingForms,
            IClojureReader cr) {

        Reader r = (Reader) reader;
        int line = -1;
        int column = -1;

        
        LOG.info("36) r={}", r.getClass().getName());
        
        if (r instanceof LineNumberingPushbackReader) {
            line = ((LineNumberingPushbackReader) r).getLineNumber();
            column = ((LineNumberingPushbackReader) r).getColumnNumber();
        }
        
        LOG.info("43) {}, {}", line, column);
        
        throw new ReaderException("Unreadable form", line, column, null);
    }
}
