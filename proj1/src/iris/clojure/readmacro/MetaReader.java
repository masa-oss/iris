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

/**
 * Author: Masahito Hemmi
 */
import java.io.PushbackReader;

import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.IMapEntry;

import iris.clojure.lang.IObj;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.LineNumberingPushbackReader;
import iris.clojure.lang.RT;
import iris.clojure.lang.Resolver;
import iris.clojure.lang.ResolverEx;
import iris.clojure.lang.Symbol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MetaReader extends AFun {

    private static final Logger LOG = LoggerFactory.getLogger(MetaReader.class);

    @Override
    public Object invoke(Object reader, Object caret, Object opts, Object pendingForms,
            IClojureReader cr) {

        PushbackReader r = (PushbackReader) reader;
        int line = -1;
        int column = -1;
        if (r instanceof LineNumberingPushbackReader) {
            line = ((LineNumberingPushbackReader) r).getLineNumber();
            column = ((LineNumberingPushbackReader) r).getColumnNumber();
        }

        pendingForms = cr.ensurePending(pendingForms);

        LOG.info("57) pendingForms={}", pendingForms);

        Object meta = cr.read(r, true, null, true, opts, pendingForms);

        LOG.info("61) meta={}", meta.getClass().getName());

        // add from hear
        if (meta instanceof Symbol) {
            //  ResolverEx clr = cr.getCompilerResolver();
            Resolver readerResolver = cr.getReaderResolver();
            Symbol sym = (Symbol) meta;
            sym = readerResolver.resolveClass(sym);
            
            meta = RT.map(RT.TAG_KEY, sym);
            
            
            // add from END

            //     if (meta instanceof Symbol || meta instanceof String) {
        } else if (meta instanceof String) {
            meta = RT.map(RT.TAG_KEY, meta);
        } else if (meta instanceof Keyword) {
            meta = RT.map(meta, RT.T);
        } else if (!(meta instanceof IPersistentMap)) {
            throw new IllegalArgumentException("Metadata must be Symbol,Keyword,String or Map");
        }

        Object o = cr.read(r, true, null, true, opts, pendingForms);
        LOG.info("73) o={}", o.getClass().getName());

        if (o instanceof IObj) {
            if (line != -1 && o instanceof ISeq) {
                meta = RT.assoc(meta, RT.LINE_KEY, RT.get(meta, RT.LINE_KEY, line));
                meta = RT.assoc(meta, RT.COLUMN_KEY, RT.get(meta, RT.COLUMN_KEY, column));
            }
            /*
                if (o instanceof IReference) {
                    ((IReference) o).resetMeta((JsonObject) meta);
                    return o;
                }
             */

 /*   o が持っていた古いmetaに、 metaをマージ */
            Object ometa = RT.meta(o);
            for (ISeq s = RT.seq(meta); s != RT.EOL; s = s.next()) {
                IMapEntry kv = (IMapEntry) s.first();
                ometa = RT.assoc(ometa, kv.key(), kv.val());
            }
            LOG.info("94) {}", ometa.getClass().getName());

            return ((IObj) o).withMeta((IPersistentMap) ometa);

        } else {
            throw new IllegalArgumentException("Metadata can only be applied to IMetas");
        }
    }
}
