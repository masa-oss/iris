/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package iris.clojure.readmacro;

import java.io.PushbackReader;
import java.util.Iterator;
import java.util.List;
import iris.clojure.lang.IClojureReader;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.RT;
import iris.clojure.lang.Resolver;
import iris.clojure.lang.Symbol;
import iris.clojure.lang.Util;

/**
 *
 * @author Hemmi
 */
    // :a.b{:c 1} => {:a.b/c 1}
    // ::{:c 1}   => {:a.b/c 1}  (where *ns* = a.b)
    // ::a{:c 1}  => {:a.b/c 1}  (where a is aliased to a.b)

public class NamespaceMapReader extends AFun  {
    
        @Override
        public Object invoke(Object reader, Object colon, Object opts, Object pendingForms,
                IClojureReader cr) {
            PushbackReader r = (PushbackReader) reader;

            boolean auto = false;
            int autoChar = cr.read1(r);
            if (autoChar == ':') {
                auto = true;
            } else {
                cr.unread(r, autoChar);
            }

            Object sym = null;
            int nextChar = cr.read1(r);
            if (cr.isWhitespace(nextChar)) {  // the #:: { } case or an error
                if (auto) {
                    while (cr.isWhitespace(nextChar)) {
                        nextChar = cr.read1(r);
                    }
                    if (nextChar != '{') {
                        cr.unread(r, nextChar);
                        throw Util.runtimeException("Namespaced map must specify a namespace");
                    }
                } else {
                    cr.unread(r, nextChar);
                    throw Util.runtimeException("Namespaced map must specify a namespace");
                }
            } else if (nextChar != '{') {  // #:foo { } or #::foo { }
                cr.unread(r, nextChar);
                sym = cr.read(r, true, null, false, opts, pendingForms);
                nextChar = cr.read1(r);
                while (cr.isWhitespace(nextChar)) {
                    nextChar = cr.read1(r);
                }
            }
            if (nextChar != '{') {
                throw Util.runtimeException("Namespaced map must specify a map");
            }

            // Resolve autoresolved ns
            String ns;
            if (auto) {
                Resolver resolver = cr.getReaderResolver();
                if (sym == null) {
                    if (resolver != null) {
                        ns = resolver.currentNS().name;
                    } else {
                        //      ns = Compiler.currentNS().getName().getName();
                        throw new RuntimeException("resolver was null");
                    }
                } else if (!(sym instanceof Symbol) || ((Symbol) sym).getNamespace() != null) {
                    throw Util.runtimeException("Namespaced map must specify a valid namespace: " + sym);
                } else {
                    Symbol resolvedNS;
                    if (resolver != null) {
                        resolvedNS = resolver.resolveAlias((Symbol) sym);
                    } else {
                        /*
                        Namespace rns = Compiler.currentNS().lookupAlias((Symbol) sym);
                        resolvedNS = rns != null ? rns.getName() : null;
                         */
                        resolvedNS = null;
                    }

                    if (resolvedNS == null) {
                        throw Util.runtimeException("Unknown auto-resolved namespace alias: " + sym);
                    } else {
                        ns = resolvedNS.getName();
                    }
                }
            } else if (!(sym instanceof Symbol) || ((Symbol) sym).getNamespace() != null) {
                throw Util.runtimeException("Namespaced map must specify a valid namespace: " + sym);
            } else {
                ns = ((Symbol) sym).getName();
            }

            // Read map
            List<Object> kvs = cr.readDelimitedList('}', r, true, opts, cr.ensurePending(pendingForms));
            if ((kvs.size() & 1) == 1) {
                throw Util.runtimeException("Namespaced map literal must contain an even number of forms");
            }

            // Construct output map
            Object[] a = new Object[kvs.size()];
            Iterator iter = kvs.iterator();
            for (int i = 0; iter.hasNext(); i += 2) {
                Object key = iter.next();
                Object val = iter.next();

                if (key instanceof Keyword) {
                    Keyword kw = (Keyword) key;
                    /*
                    if (kw.getNamespace() == null) {
                        key = Keyword.intern(ns, kw.getName());
                    } else if (kw.getNamespace().equals("_")) {
                        key = Keyword.intern(null, kw.getName());
                    }
                     */
                    throw new RuntimeException("737");
                } else if (key instanceof Symbol) {
                    Symbol s = (Symbol) key;
                    if (s.getNamespace() == null) {
                        key = Symbol.intern(ns, s.getName());
                    } else if (s.getNamespace().equals("_")) {
                        key = Symbol.intern(null, s.getName());
                    }
                }
                a[i] = key;
                a[i + 1] = val;
            }
            return RT.map(a);
        }


}
