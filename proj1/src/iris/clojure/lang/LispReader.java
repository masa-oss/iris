/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.lang;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of <b>clojure.lang.LispReader.java</b> .
 * 
 * Inner classes are separated and located in <b>iris.clojure.readmacro</b> .
 */
public class LispReader implements IClojureReader, ILispReader {

    private static final Logger LOG = LoggerFactory.getLogger(LispReader.class);

    // moved from RT.java   to Here
    private final Resolver readerResolver;
    
    private final ILispReaderMacro readerMacro;

    private final ResolverEx compilerResolver;
    
    private final IObjectFactory factory;



    public LispReader(Resolver res, ILispReaderMacro lrm, ResolverEx compilerEx, IObjectFactory f) {

        readerResolver = res;
        readerMacro = lrm;
        compilerResolver = compilerEx;
        factory = f;
    }

    @Override
    public IObjectFactory getObjectFactory() {
        return this.factory;
    }
    
    
    @Override
    public Resolver getReaderResolver() {

        return readerResolver;
    }

    
    
    
    /**
     * Swingから呼ばれる
     *
     * @param s
     * @return
     */
    @Override
    public Object readString(String s) {
        PushbackReader r = new PushbackReader(new java.io.StringReader(s));
        return read(r, OPTION_MAP);
    }

    // moved from RT   -- end
    public static final Symbol THE_VAR = Symbol.intern("var");

    static Pattern symbolPat = Pattern.compile("[:]?([\\D&&[^/]].*/)?(/|[\\D&&[^/]][^/]*)");

    @Override
    public boolean isWhitespace(int ch) {
        return Character.isWhitespace(ch) || ch == ',';
    }

    @Override
    public void unread(PushbackReader r, int ch) {
        if (ch != -1)
		try {
            r.unread(ch);
        } catch (IOException e) {
            throw Util.sneakyThrow(e);
        }
    }

    @Override
    public int read1(Reader r) {
        try {
            return r.read();
        } catch (IOException e) {
            throw Util.sneakyThrow(e);
        }
    }

    // Reader opts
    static public final Keyword OPT_EOF = Keyword.intern(null, "eof");

    // EOF special value to throw on eof
    static public final Keyword EOFTHROW = Keyword.intern(null, "eofthrow");

    // public 
    Object read(PushbackReader r, Map<String, Object> opts) {

        boolean eofIsError = true;
        Object eofValue = null;
        if (opts != null) {
            Object eof = opts.getOrDefault(OPT_EOF, EOFTHROW);
            if (!EOFTHROW.equals(eof)) {
                eofIsError = false;
                eofValue = eof;
            }
        }
        return read(r, eofIsError, eofValue, false, opts);
    }

    
    static HashMap<String, Object> OPTION_MAP = new HashMap<>();
    
    // load-file から呼ばれる
    @Override
    public Object read(PushbackReader r, boolean eofIsError, Object eofValue, boolean isRecursive) {
        return read(r, eofIsError, eofValue, isRecursive, OPTION_MAP);
    }

    Object read(PushbackReader r, boolean eofIsError, Object eofValue, boolean isRecursive, Map<String, Object> opts) {
        // start with pendingForms null as reader conditional splicing is not allowed at top level
        return read(r, eofIsError, eofValue, null, null, isRecursive, opts, null, readerResolver);
    }

    @Override
    public Object read(PushbackReader r, boolean eofIsError, Object eofValue, boolean isRecursive, Object opts, Object pendingForms) {
        return read(r, eofIsError, eofValue, null, null, isRecursive, opts, ensurePending(pendingForms), readerResolver);
    }

    @Override
    public Object ensurePending(Object pendingForms) {

        if (pendingForms == null) {
            return new LinkedList();
        } else {
            return pendingForms;
        }
    }

    private Object read(PushbackReader r, boolean eofIsError, Object eofValue, Character returnOn,
            Object returnOnValue, boolean isRecursive, Object opts, Object pendingForms,
            Resolver resolver) {

        int line = -1;
        int column = -1;

        try {
            for (;;) {

                if (pendingForms instanceof List && !((List) pendingForms).isEmpty()) {
                    return ((List) pendingForms).remove(0);
                }

                int ch = read1(r);

                while (isWhitespace(ch)) {
                    ch = read1(r);
                }

                if (ch == -1) {
                    if (eofIsError) {
                        throw new ReaderException("EOF while reading", line, column, null);
                      //  throw Util.runtimeException("EOF while reading");
                    }
                    return eofValue;
                }

                if (returnOn != null && (returnOn.charValue() == ch)) {
                    return returnOnValue;
                }

                if (r instanceof LineNumberingPushbackReader) {
                    line = ((LineNumberingPushbackReader) r).getLineNumber();
                    column = ((LineNumberingPushbackReader) r).getColumnNumber();
                }

                if (Character.isDigit(ch)) {
                    Object n = readNumber(r, (char) ch);
                    return n;
                }

                IReadMacro macroFn = readerMacro.getMacro(ch);
                if (macroFn != null) {
                    //         LOG.info("macroFn="  + macroFn.getClass().getName() );
                    Object ret = macroFn.invoke(r, (char) ch, opts, pendingForms, this);
                    //no op macros return the reader
                    if (ret == r) {
                        continue;
                    }
                    return ret;
                }

                if (ch == '+' || ch == '-') {
                    int ch2 = read1(r);
                    if (Character.isDigit(ch2)) {
                        unread(r, ch2);
                        Object n = readNumber(r, (char) ch);
                        return n;
                    }
                    unread(r, ch2);
                }

                String token = readToken(r, (char) ch);
                Object object = interpretToken(token, resolver);

                if (object instanceof IObj) {
                    if (line != -1) {
                        IObj s = (IObj) object;

                        Associative meta = RT.meta(s);
                        meta = RT.assoc(meta, RT.LINE_KEY, RT.get(meta, RT.LINE_KEY, line));
                        meta = RT.assoc(meta, RT.COLUMN_KEY, RT.get(meta, RT.COLUMN_KEY, column));

                        object = s.withMeta((IPersistentMap) meta);
                    }
                }
                return object;
            }
        } catch (Exception e) {
            if (isRecursive || !(r instanceof LineNumberingPushbackReader)) {
                throw Util.sneakyThrow(e);
            }
            
            if (e instanceof ReaderException) {
                ReaderException re = (ReaderException) e;
                if (re.line > 0 || re.column > 0) {
                    throw re;
                }
            }
            
            LineNumberingPushbackReader rdr = (LineNumberingPushbackReader) r;
            //throw Util.runtimeException(String.format("ReaderError:(%d,1) %s", rdr.getLineNumber(), e.getMessage()), e);

            int ln =rdr.getLineNumber();
            int cn = rdr.getColumnNumber();
            LOG.error("255) line={}, column={}", ln, cn);

            throw new ReaderException(e.getMessage(),  ln, cn, e);
        }
    }

    @Override
    public String readToken(PushbackReader r, char initch) {
        StringBuilder sb = new StringBuilder();
        sb.append(initch);

        for (;;) {
            int ch = read1(r);
            if (ch == -1 || isWhitespace(ch) || readerMacro.isTerminatingMacro(ch)) {
                unread(r, ch);
                return sb.toString();
            }
            sb.append((char) ch);
        }
    }

    Object readNumber(PushbackReader r, char initch) {

        StringBuilder sb = new StringBuilder();
        sb.append(initch);

        for (;;) {
            int ch = read1(r);
            if (ch == -1 || isWhitespace(ch) || readerMacro.isMacro(ch)) {
                unread(r, ch);
                break;
            }
            sb.append((char) ch);
        }

        String s = sb.toString();
        Object n = matchNumber(s);
        if (n == null) {
            throw new NumberFormatException("Invalid number: " + s);
        }
        return n;
    }

    @Override
    public int readUnicodeChar(String token, int offset, int length, int base) {

        if (token.length() != offset + length) {
            throw new IllegalArgumentException("Invalid unicode character: \\" + token);
        }
        int uc = 0;
        for (int i = offset; i < offset + length; ++i) {
            int d = Character.digit(token.charAt(i), base);
            if (d == -1) {
                throw new IllegalArgumentException("Invalid digit: " + token.charAt(i));
            }
            uc = uc * base + d;
        }
        return (char) uc;
    }

    @Override
    public int readUnicodeChar(PushbackReader r, int initch, int base, int length, boolean exact) {

        int uc = Character.digit(initch, base);
        if (uc == -1) {
            throw new IllegalArgumentException("Invalid digit: " + (char) initch);
        }
        int i = 1;
        for (; i < length; ++i) {
            int ch = read1(r);
            if (ch == -1 || isWhitespace(ch) || readerMacro.isMacro(ch)) {
                unread(r, ch);
                break;
            }
            int d = Character.digit(ch, base);
            if (d == -1) {
                throw new IllegalArgumentException("Invalid digit: " + (char) ch);
            }
            uc = uc * base + d;
        }
        if (i != length && exact) {
            throw new IllegalArgumentException("Invalid character length: " + i + ", should be: " + length);
        }
        return uc;
    }

    Object interpretToken(String s, Resolver resolver) {

        if (RT.COMMON_LISP) {
            return interpretTokenCommonLisp(s, resolver);
        } else {
            return interpretTokenClojure(s, resolver);
        }
    }

    Object interpretTokenCommonLisp(String s, Resolver resolver) {

       // LOG.info("352) *********   '{}'", s);
        if (s.equals("NULL")) {

            return factory.createJavaNull();
        } else if (s.equals("nil")) {
            Object n = factory.createCommonLispNil();
          //  LOG.info("359) ====== {} " , n.getClass().getName()   );
            
            return n;
        } else if (s.equals("true")) {

            return factory.createJavaTrue();
        } else if (s.equals("false")) {

            return factory.createJavaFalse();
        }
        Object ret = matchSymbol(s, resolver);
        if (ret != null) {
            return ret;
        }

        throw Util.runtimeException("Invalid token: " + s);
    }

    Object interpretTokenClojure(String s, Resolver resolver) {
/*
        if (s.equals("nil")) {

            return factory.createJavaNull();
        } else if (s.equals("true")) {

            return factory.createJavaTrue();
        } else if (s.equals("false")) {

            return factory.createJavaFalse();
        }

        Object ret = matchSymbol(s, resolver);
        if (ret != null) {
            return ret;
        }*/
        throw Util.runtimeException("Invalid token: " + s);
    }

    Object matchSymbol(String s, Resolver resolver) {

        Matcher m = symbolPat.matcher(s);
        if (m.matches()) {
            int gc = m.groupCount();
            String ns = m.group(1);
            String name = m.group(2);
            if (ns != null && ns.endsWith(":/")
                    || name.endsWith(":")
                    || s.indexOf("::", 1) != -1) {
                return null;
            }
            if (s.startsWith("::")) {
                //  Symbol ks = Symbol.intern(s.substring(2));

                Symbol ks = factory.createSymbol(s.substring(2));

                if (resolver != null) {
                    Symbol nsym;
                    if (ks.ns != null) {
//                        nsym = resolver.resolveAlias(Symbol.intern(ks.ns));

                        nsym = resolver.resolveAlias(factory.createSymbol(ks.ns));

                    } else {
                        nsym = resolver.currentNS();
                    }
                    //auto-resolving keyword
                    if (nsym != null) {
                        return Keyword.intern(nsym.name, ks.name);
                    } else {
                        return null;
                    }
                } else {

                    /*
                    Namespace kns;
                    if (ks.ns != null) {
                        kns = Compiler.currentNS().lookupAlias(Symbol.intern(ks.ns));
                    } else {
                        kns = Compiler.currentNS();
                    }
                    //auto-resolving keyword
                    if (kns != null) {
                        return Keyword.intern(kns.name.name, ks.name);
                    } else {
                        return null;
                    }*/
                    LOG.warn("429) {}", ks);

                    return null;
                }
            }
            boolean isKeyword = s.charAt(0) == ':';
            if (isKeyword) {
                return Keyword.intern(null, s.substring(1));
            } else {
                //  Symbol sym = Symbol.intern(s);

                Symbol sym = factory.createSymbol(s);
                return sym;
            }
        }
        return null;
    }

    Object matchNumber(String s) {
        return factory.matchNumber(s);
    }

    // Sentinel values for reading lists
    private static final Object READ_EOF = new StringBuilder("READ_EOF");
    private static final Object READ_FINISHED = new StringBuilder("READ_FINISHED");

    @Override
    public List<Object> readDelimitedList(char delim, PushbackReader r, boolean isRecursive, Object opts, Object pendingForms) {
        final int firstline
                = (r instanceof LineNumberingPushbackReader)
                        ? ((LineNumberingPushbackReader) r).getLineNumber() : -1;

        ArrayList<Object> a = new ArrayList<>();
        Resolver resolver = readerResolver;

        for (;;) {

            Object form = read(r, false, READ_EOF, delim, READ_FINISHED, isRecursive, opts, pendingForms,
                    resolver);

            if (form == READ_EOF) {
                if (firstline < 0) {
                    throw Util.runtimeException("EOF while reading");
                } else {
                    throw Util.runtimeException("EOF while reading, starting at line " + firstline);
                }
            } else if (form == READ_FINISHED) {
                return a;
            }

            a.add(form);
        }
    }

    @Override
    public ResolverEx getCompilerResolver() {

        return compilerResolver;
    }
}
