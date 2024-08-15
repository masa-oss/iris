/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package clojure.java;

import iris.clojure.lang.AFn;
import iris.example.eval.EvaluatorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class ReaderFn extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(ReaderFn.class);

    @Override
    public Object invoke(Object arg1) {

        if (arg1 instanceof String) {
            return readFromFile(new File((String) arg1));

        }
        if (arg1 instanceof URL) {
            return readFrom((URL) arg1);

        }
        if (arg1 instanceof byte[]) {
            byte[] buf = (byte[]) arg1;
            return readFromByteArray(buf);
        }

        throw new EvaluatorException("Argument must be String or URL or byte[] : " + arg1);
    }

    Object readFromFile(File f) {
        BufferedReader ret = null;
        try {
            FileInputStream fos = new FileInputStream(f);

            InputStreamReader isw = new InputStreamReader(fos, "UTF-8");

            ret = new BufferedReader(isw);

        } catch (IOException fne) {

            throw new EvaluatorException(null, fne.getMessage(), fne);
        }
        return ret;
    }

    Object readFromByteArray(byte[] buf) {

        BufferedReader ret = null;
        try {
            String str = new String(buf, "UTF-8");
            StringReader sr = new StringReader(str);
            
            ret = new BufferedReader(sr);
            
        } catch (UnsupportedEncodingException fne) {

            throw new EvaluatorException(null, fne.getMessage(), fne);
        }
        return ret;
    }

    Object readFrom(URL url) {
        BufferedReader ret = null;
        try {
            InputStream fos = url.openStream();

            InputStreamReader isw = new InputStreamReader(fos);

            ret = new BufferedReader(isw);

        } catch (IOException fne) {

            throw new EvaluatorException(null, fne.getMessage(), fne);
        }
        return ret;
    }

}
