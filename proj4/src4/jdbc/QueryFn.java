/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package jdbc;

import iris.clojure.lang.AFn;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;
import iris.example.eval.ETE;
import iris.example.eval.EvaluatorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>
 *   (j/query  db-spec [ "select * from TABLE" ]  )
 * 
 *   (j/query  db-spec [ "select emp_num, name  from employee_num where emp_num like ?"  "24%"]  )
 * 
 * 
 *   (j/query  db-spec [ "select * from TABLE" ]  false)
 * 
 * </code>
 * 
 * [ Warning ]
 * This select retrieves all records, so be careful when selecting from a table with a lot of data.
 * 
 * 
 * 
 * 
 */
public class QueryFn extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(QueryFn.class);
    
    
    ConnectFn fn = new ConnectFn();

    
    @Override
    public Object invoke(Object arg1, Object arg2) {
        
        return invoke(arg1, arg2, Boolean.TRUE);
    }
    
    
    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {

        if (!(arg1 instanceof IPersistentMap)) {
            ETE.throwException("Arg1 must be a map", arg1, null, null);
        }

        if (!(arg2 instanceof IPersistentVector)) {
            ETE.throwException("Arg2 must be a map", arg2, null, null);
        }
        
        boolean bKeyword = true;
        if (arg3 instanceof Boolean) {
            Boolean b = (Boolean)  arg3;
            bKeyword = b;
        }
        
        

        IPersistentMap dbspec = (IPersistentMap) arg1;
        IPersistentVector sqlAndParam = (IPersistentVector) arg2;

        IPersistentVector result = null;
        Connection con = (Connection) fn.invoke(dbspec);
        try {
            try {
                ISeq seq = sqlAndParam.seq();

                String sql = (String) seq.first();
                PreparedStatement ps = con.prepareStatement(sql);

                seq = seq.next();
                for (int i =1 ; seq != RT.EOL; i++) {
                    Object param = seq.first();
                    ps.setObject(i, param);
                    seq = seq.next();
                }
                
                PreparedStWrapper wrap = new PreparedStWrapper(ps, bKeyword);
                result = wrap.executeQuery();
            } finally {
                con.close();
            }
        } catch (SQLException sqle) {
            throw new EvaluatorException("SQLException", sqle);
        } catch (Exception ex) {
            LOG.error("Exception", ex);
        }
        return result;
    }

}
