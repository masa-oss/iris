/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.gui;

import java.io.PrintWriter;

import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.LispPrintable;
import iris.clojure.lang.MapEntry;
import iris.clojure.nsvar.Namespace;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Var;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Hemmi
 */
public class Const {

    public static final String HR = "HR";

    public static final double PI = 3.141592;

    public static void foo() {
        System.out.println("----------はろ");
    }

    public static void printNamespacesToConsole(PrintWriter pw) {

        for (ISeq seq = Namespace.all(); seq != RT.EOL; seq = seq.next()) {
            Object o = seq.first();
            if (o instanceof Namespace) {
                Namespace ns = (Namespace) o;
                pw.println("Namespace[" + ns.getName() + "]");
            }

        }
    }

    public static void printToConsole(Namespace targetNS) {

        PrintWriter pw = new PrintWriter(System.out, true);

        printToConsole(targetNS, pw);
    }
    
    static class SortOpe implements Comparator<MapEntry> {

        @Override
        public int compare(MapEntry o1, MapEntry o2) {
            
            Symbol s1 = (Symbol) o1.getKey();
            Symbol s2 = (Symbol) o2.getKey();
            
            String str1 = s1.getStringForPrint();
            String str2 = s2.getStringForPrint();
            return str1.compareTo(str2);
        }
        
    }
    
    
    static List<MapEntry> sortEntry(Namespace targetNS) {

        ArrayList<MapEntry> list = new ArrayList<>();
        
        IPersistentMap mappings = targetNS.getMappings();
        for (ISeq seq = mappings.seq(); seq != RT.EOL; seq = seq.next()) {
            
            MapEntry me = (MapEntry) seq.first();
            list.add(me);
        }
        
        Collections.sort(list, new SortOpe());
        return list;
    }
    
    // sort by name asc order
    public static void printToConsoleAsc(Namespace targetNS, PrintWriter pw) {
        
        List<MapEntry> list = sortEntry(targetNS);
        for (MapEntry me : list) {
            
            Object varOrClass = me.getValue();

            String str = dump(varOrClass);

            pw.println("" + me.getKey() + "\t" + str);
        }
    }
    
    public static void printToConsole(Namespace targetNS, PrintWriter pw) {

        IPersistentMap mappings = targetNS.getMappings();
        for (ISeq seq = mappings.seq(); seq != RT.EOL; seq = seq.next()) {

            MapEntry me = (MapEntry) seq.first();

            Object varOrClass = me.getValue();

            String str = dump(varOrClass);

            pw.println("" + me.getKey() + "\t" + str);
        }
    }

    static String dump(Object varOrClass) {

        if (varOrClass == null) {
            return "null";
        } else if (varOrClass instanceof Class) {
            Class<?> clazz = (Class<?>) varOrClass;
            return "Class=" + clazz.getCanonicalName();
        } else if (varOrClass instanceof Var) {
            Var v = (Var) varOrClass;
            Namespace ns = v.ns;
            Symbol sy = v.sym;
            
            boolean macro = v.isMacro();

            StringBuilder sb = new StringBuilder( "Var*[" + ns.getName() + ", " + sy.getStringForPrint() + ", " );
            
            Object root = v.getRawRoot();
            if (root == null) {
                sb.append( "null" );
            } else if (root instanceof LispPrintable) {
                LispPrintable lp = (LispPrintable) root;
                sb.append(
                lp.getStringForPrint());
                
            } else {
                sb.append( root.toString() );
            }
            
            
            if (macro) {
                sb.append(",     ** MACRO **");
            }
            
            boolean aPublic = v.isPublic();
            if (!aPublic) {
                sb.append(",    ** private **");
                
            }
            
            sb.append("]");
            return sb.toString();

        } else {
            return varOrClass.getClass().getName();
        }
    }

    /*
    public static void printToConsole() {
        
        PrintWriter pw = new PrintWriter(System.out, true);

      //  Object deref = RT.CURRENT_NS.deref();
        Object deref = Compiler.CURRENT_NS.deref();
        
        
        pw.println("current-ns=" + deref);

        Namespace CLOJURE_NS = Compiler.CLOJURE_NS;

        IPersistentMap mappings = CLOJURE_NS.getMappings();

        Iterator iterator = mappings.iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();

            pw.println("" + o.getClass().getName());

            MapEntry me = (MapEntry) o;

            pw.println("" + me.getKey() + "," + me.getValue());
        }

    }



    public static void boo() {
        System.out.println("------------boo");
        Namespace CLOJURE_NS = Compiler.CLOJURE_NS;

        IPersistentMap mappings = CLOJURE_NS.getMappings();

        try {
            FileOutputStream fos = new FileOutputStream("clojure-sym.txt");
            try {
                OutputStreamWriter s = new OutputStreamWriter(fos, "UTF-8");
                try {
                    Iterator iterator = mappings.iterator();

                    while (iterator.hasNext()) {
                        Object o = iterator.next();

                        System.out.println("" + o.getClass().getName());

                        MapEntry me = (MapEntry) o;

                        s.write("" + me.getKey() + "," + me.getValue());
                        s.write("\r\n");

                    }

                } finally {
                    s.close();
                }

            } finally {
                fos.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("------------boo end");
    }
     */
}
