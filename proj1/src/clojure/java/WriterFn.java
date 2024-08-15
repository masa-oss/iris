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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class WriterFn extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(WriterFn.class);

    @Override
    public Object invoke(Object arg1) {

        if (arg1 instanceof String) {
            return writeToFile(new File((String) arg1));
        }

        throw new EvaluatorException("Argument must be String  : " + arg1);
    }

    Object writeToFile(File f) {
        
        BufferedWriter ret = null;
        try {
            FileOutputStream fos = new FileOutputStream(f);

            OutputStreamWriter isw = new OutputStreamWriter(fos, "UTF-8");

            ret = new BufferedWriter(isw);

        } catch (IOException fne) {

            throw new EvaluatorException(null, fne.getMessage(), fne);
        }
        return ret;
    }


}
