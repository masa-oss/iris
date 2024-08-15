/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */
package iris.clojure.lang;

import java.io.PushbackReader;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a PushbackReader that wraps a LineNumberReader.
 *
 *
 *
 */
public class LineNumberingPushbackReader extends PushbackReader {

    private static final Logger LOG = LoggerFactory.getLogger(LineNumberingPushbackReader.class);

// This class is a PushbackReader that wraps a LineNumberReader. The code
// here to handle line terminators only mentions '\n' because
// LineNumberReader collapses all occurrences of CR, LF, and CRLF into a
// single '\n'.
    
    
    private static final int NEWLINE = (int) '\n';

    private int _columnNumber = 1;

    // Ring Buffer
    final int[] arrLines = new int[]{1, 0, 0, 0, 0, 0, 0, 0};
    final int[] arrColumns = new int[]{1, 0, 0, 0, 0, 0, 0, 0};

    int index = 0;

    public LineNumberingPushbackReader(Reader r) {
        super(new LineNumberReader2(r));
    }

    void incPointer() {

        index = (index + 1) & 7;
    }

    void decPointer() {

        index = (index - 1) & 7;
    }

    public int getLineNumber() {
        return arrLines[index];
    }

    public int getColumnNumber() {
        return arrColumns[index];
    }

    @Override
    public int read() throws IOException {
        int c = super.read();

        if (c == NEWLINE) {
            _columnNumber = 1;
        } else if (c != -1) {
            _columnNumber++;
        }

        incPointer();
        int lineNum = ((LineNumberReader2) in).getLineNumber() + 1;
        arrLines[index] = lineNum;
        arrColumns[index] = _columnNumber;

        if (debug) {

            String str = "EOF";
            if (c == NEWLINE) {
                str = "newline";
            } else if (c == '\t') {
                str = "tab";
            } else if (c == ' ') {
                str = "SPC";
            } else if (c != -1) {
                char[] buf = new char[1];
                buf[0] = (char) c;
                str = new String(buf);
            }

   //         LOG.info("read()  line {}, column {}  str {}", this.getLineNumber(), this.getColumnNumber(), str);
        }
        return c;
    }

    final boolean debug = true;

    @Override
    public void unread(int c) throws IOException {

        super.unread(c);

        _columnNumber--;
        
        this.decPointer();;

   //     LOG.info("unread()  line {}, column {}", this.getLineNumber(), this.getColumnNumber());

    }


    @Override
    public long skip(long n) throws IOException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean ready() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unread(char[] cbuf) throws IOException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void unread(char[] cbuf, int off, int len) throws IOException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        throw new UnsupportedOperationException();
    }

    @Override
    public long transferTo(Writer out) throws IOException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int read(char[] cbuf) throws IOException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int read(CharBuffer target) throws IOException {

        throw new UnsupportedOperationException();
    }
}
