/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.cui;

import iris.clojure.lang.ILispReader;
import iris.clojure.lang.RTPrinter;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.CljCompiler;
import iris.clojure.nsvar.LispReaderFactory;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;
import iris.example.eval.ExampleEvaluator;
import iris.example.eval.LexicalScope;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.io.StringWriter;
import iris.example.eval.ExampleInitializer;
import iris.example.eval.Globals;

/**
 * CUI interpreter.
 *
 * clj/clojure/main.clj Line 368
 *
 */
public class Main {

    public static void main(String args[]) {

        try {
            // 初期化
            ExampleInitializer.initCompiler();
            ExampleInitializer.changeNamespaceToUser();

            InputStream in = System.in;
            InputStreamReader r = new InputStreamReader(in, "UTF-8");
            PushbackReader pbr = new PushbackReader(r);

            PrintStream out = System.out;
            OutputStreamWriter w = new OutputStreamWriter(out, "UTF-8");

            
            String property = System.getProperty("java.version");
            w.append("java.version ");
            w.append(property);
            w.append("\n");
            w.flush();
            
            ILispReader logic = new LispReaderFactory().getLispReader();

            for (;;) {
                try {
                    repl(logic, pbr, w);
                } catch (Exception ee) {
                  //  System.out.println("" + ee.getMessage());
                    System.out.println(ee.toString());
                    System.out.flush();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void repl(ILispReader logic, PushbackReader isr, OutputStreamWriter osw) throws IOException {
        
        
        Var cn = CljCompiler.CURRENT_NS;
        Namespace deref =  (Namespace) cn.deref();
        
        Symbol sym = deref.name;
        
        String prompt = sym.getStringForPrint() + "> ";
        

        System.out.print(prompt);
        System.out.flush();

        ExampleEvaluator evaluator = Globals.getEvaluator();
        LexicalScope lexical = new LexicalScope();

        final Object eofVal = new Object();
        boolean eofIsError = false;
        boolean isRecursive = false;

        Object form = logic.read(isr, eofIsError, eofVal, isRecursive);

        form = evaluator.eval(form, lexical);

        String str = printClojure(form);

        osw.append(str);
        osw.append("\n");
        osw.flush();

    }

    static String printClojure(Object sexp) throws IOException {

        boolean readably = true;
        boolean printMeta = true;
        boolean printDup = false;

        RTPrinter printer = new RTPrinter(readably, printMeta, printDup);
        StringWriter sw = new StringWriter();

        printer.print(sexp, sw);
        sw.flush();
        return sw.toString();
    }

}
