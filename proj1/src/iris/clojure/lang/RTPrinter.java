/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
/**
 *   Author: Masahito Hemmi
 */
package iris.clojure.lang;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;


/**
 * This class has separated the methods that were in clojure.lang.RT.java .
 */
public final class RTPrinter {

    final boolean readably;
    final boolean printMeta;
    final boolean printDup;

    public RTPrinter(boolean readably, boolean printMeta) {
        this.readably = readably;
        this.printMeta = printMeta;
        this.printDup = false;
    }

    public RTPrinter(boolean readably, boolean printMeta, boolean printDup) {
        this.readably = readably;
        this.printMeta = printMeta;
        this.printDup = printDup;
    }

    public void print(Object x, Writer w) throws IOException {

        if (x instanceof IObj) {
            IObj o = (IObj) x;
            if (RT.count(o.meta()) > 0
                    && ((readably && printMeta)
                    || printDup)) {
                IPersistentMap meta = o.meta();
               // w.write("#^");
                w.write("^");
             //   if (meta.count() == 1 && meta.containsKey(TAG_KEY)) {
              //      print(meta.valAt(TAG_KEY), w);
              //  } else {
                    print(meta, w);
              //  }
                w.write(' ');
            }
        }
        if (x == null) {
            
            if (RT.COMMON_LISP) {
                w.write("NULL");
            } else {
                w.write("nil");
            }
        } else if (x instanceof PersistentList.EmptyList) {

            w.write('(');
            w.write(')');

        } else if (x instanceof Nil) {
            w.write("nil");

        } else if (x instanceof ISeq) {
            w.write('(');
            printInnerSeq((ISeq) x, w);
            w.write(')');
        } else if (x instanceof String) {
            String s = (String) x;
            if (!readably) {
                w.write(s);
            } else {
                w.write('"');
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    switch (c) {
                        case '\n':
                            w.write("\\n");
                            break;
                        case '\t':
                            w.write("\\t");
                            break;
                        case '\r':
                            w.write("\\r");
                            break;
                        case '"':
                            w.write("\\\"");
                            break;
                        case '\\':
                            w.write("\\\\");
                            break;
                        case '\f':
                            w.write("\\f");
                            break;
                        case '\b':
                            w.write("\\b");
                            break;
                        default:
                            w.write(c);
                    }
                }
                w.write('"');
            }
        } else if (x instanceof IPersistentMap) {
            w.write('{');
            String sep = "";

            for (ISeq s = RT.seq(x); s != RT.EOL; s = s.next()) {
                w.write(sep);

                IMapEntry e = (IMapEntry) s.first();
                print(e.key(), w);
                w.write(' ');
                print(e.val(), w);
                sep = ", ";
            }
            w.write('}');
        } else if (x instanceof IPersistentVector) {
            IPersistentVector a = (IPersistentVector) x;
            w.write('[');
            for (int i = 0; i < a.count(); i++) {
                print(a.nth(i), w);
                if (i < a.count() - 1) {
                    w.write(' ');
                }
            }
            w.write(']');
        } else if (x instanceof IPersistentSet) {
            w.write("#{");
            String sep = "";
            for (ISeq s = RT.seq(x); s != RT.EOL; s = s.next()) {
                w.write(sep);
                print(s.first(), w);
                sep = " ";
            }
            w.write('}');
        } else if (x instanceof Character) {
            char c = ((Character) x).charValue();
            if (!readably) {
                w.write(c);
            } else {
                w.write('\\');
                switch (c) {
                    case '\n':
                        w.write("newline");
                        break;
                    case '\t':
                        w.write("tab");
                        break;
                    case ' ':
                        w.write("space");
                        break;
                    case '\b':
                        w.write("backspace");
                        break;
                    case '\f':
                        w.write("formfeed");
                        break;
                    case '\r':
                        w.write("return");
                        break;
                    default:
                        w.write(c);
                }
            }
        } else if (x instanceof Class<?>) {
            w.write("#<class ");
            w.write(((Class) x).getName());
            w.write(">");

        } else if (x instanceof BigDecimal && readably) {
            w.write(x.toString());
            w.write('M');
        } else if (x instanceof BigInt && readably) {
            w.write(x.toString());
            w.write('N');
        } else if (x instanceof BigInteger && readably) {
            w.write(x.toString());
            w.write("BIGINT");
            /*
            
            I wanted this class to not depend on Var.
            Since LispPrintable is implemented in Var, it will be processed by the following LispPrintable processing.
            
            
        } else if (x instanceof Var) {
            Var v = (Var) x;
            if (v.ns == null) {
                w.write("#=(var " + v.sym + ")");
            } else {
                w.write("#=(var " + v.ns.name + "/" + v.sym + ")");
            }
            */
        } else if (x instanceof Pattern) {
            Pattern p = (Pattern) x;
            w.write("#\"" + p.pattern() + "\"");
        } else if (x instanceof LispPrintable) {
            LispPrintable kw = (LispPrintable) x;
            w.write(kw.getStringForPrint());
            
        } else if (x instanceof Number) {
            
            if (x instanceof Double) {
                Double d = (Double)x;
                if (d.isNaN()) {
                    w.write("##NaN");
                } else if( d.isInfinite()) {
                    if ( d == Double.NEGATIVE_INFINITY) {
                        w.write("##-Inf");
                        
                    } else {
                        w.write("##Inf");
                    }
                } else {
                    w.write(d.toString());
                }

            } else {
            
                w.write(x.toString());
            }
        } else if (x instanceof Boolean) {
            w.write(x.toString());
        } else {
            w.write("#<instance ");
            
            w.write(x.getClass().getName());
            w.write(">");
        }
    }

    private void printInnerSeq(ISeq x, Writer w) throws IOException {

        String sep = "";

        for (ISeq s = x; s != RT.EOL; s = s.next()) {
            w.write(sep);
            print(s.first(), w);
            sep = " ";
        }
    }

}
