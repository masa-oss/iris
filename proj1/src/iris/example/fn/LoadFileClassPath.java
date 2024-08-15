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
import iris.clojure.lang.IExceptionInfo;
import iris.clojure.lang.ILispReader;
import iris.clojure.lang.LineNumberingPushbackReader;
import iris.clojure.lang.ReaderException;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.Util;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.LispReaderFactory;
import iris.clojure.nsvar.Var;
import iris.example.eval.EvaluatorException;
import iris.example.eval.ExampleEvaluator;
import iris.example.eval.Globals;
import iris.example.eval.LexicalScope;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read file from classpath .
 * 
 * This class is used in <b>Func2.MyRequire</b> . be careful .
 * 
 * Note that the file will not be readable, unless you clean and buld it in NetBeans.
 *
 * (*load-file 'error.test2)
 *
 */
public class LoadFileClassPath extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(LoadFileClassPath.class);
    
    static String SUFFIX = ".lsp";
    

    @Override
    public Object invoke(Object arg1) {

        Symbol sym = null;
        if (arg1 instanceof Symbol) {
            sym = (Symbol) arg1;
        } else {
            throw new EvaluatorException("Error: args must bea a symbol");
        }

        Object ret = null;
        Object oldValue = CljCompiler.CURRENT_NS.deref();
        try {
            
            try {
                procSymbol(sym);
                ret = Boolean.TRUE;
            } catch (EvaluatorException ee) {
                // if file not found
                LOG.warn(" ------- " + ee.getMessage()  );
                // 続きの処理があるので、throw禁止
                ret = Boolean.FALSE;
            } catch (RuntimeException re) {
                LOG.error("67 RuntimeException", re);
                ret = Boolean.FALSE;
            }
            
        } finally {
            Var var = (Var) CljCompiler.CURRENT_NS;
            var.bindRoot(oldValue);
        }
        return ret;
    }

    Object procSymbol(Symbol pkgName) {

        String name = pkgName.getName();

        String fileName = name.replaceAll("\\.", "/") + SUFFIX;

        LOG.info("Loading ... file name = {}", fileName);

        Object ret = null;

        URL url = LoadFileClassPath.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new EvaluatorException("File not found:" + fileName);
        } else {

            try {
                loadFrom(url.openStream());
            } catch (IOException ioe) {
                LOG.error("95 IOException", ioe);
                
                return Util.sneakyThrow(ioe);
                
            }
            
        }
        return ret;
    }

    Object loadFrom(InputStream is) {

        LOG.info("105) InputStream={}", is);
        
        ExampleEvaluator evaluator = Globals.getEvaluator();
        LexicalScope lexical = new LexicalScope();

        final Object eofVal = new Object();

        ILispReader logic = new LispReaderFactory().getLispReader();

        try (InputStream fIStream = is) {
            InputStreamReader iSReader = new InputStreamReader(fIStream, "UTF-8");

            LineNumberingPushbackReader lnpr = new LineNumberingPushbackReader(iSReader);

            try {
                boolean eofIsError = false;
                boolean isRecursive = false;

                Object form = logic.read(lnpr, eofIsError, eofVal, isRecursive);
                while (form != eofVal) {

                    evaluator.eval(form, lexical);

                    //    print(form);
                    LOG.info("Loading ... lineNo={}, columnNo={}", lnpr.getLineNumber(), lnpr.getColumnNumber());

                    form = logic.read(lnpr, eofIsError, eofVal, isRecursive);
                }

            } catch (RuntimeException ex) {

                if (ex instanceof IExceptionInfo) {
                    return Util.sneakyThrow(ex);
                }

                throw new ReaderException(lnpr.getLineNumber(), lnpr.getColumnNumber(), ex);

            }
        } catch (IOException e) {

            return Util.sneakyThrow(e);
        }
        return null;
    }

}
