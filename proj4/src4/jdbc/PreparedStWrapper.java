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

import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.PersistentVector;
import iris.clojure.lang.RT;
import iris.clojure.lang.Util;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class PreparedStWrapper {
    
    private static final Logger LOG = LoggerFactory.getLogger(PreparedStWrapper.class);
    

    PreparedStatement ps;
    boolean bKeyword;

    PreparedStWrapper(PreparedStatement ps, boolean keyword) {
        this.ps = ps;
        this.bKeyword = keyword;
    }

    IPersistentVector executeQuery() {
        IPersistentVector vec = null;
        try {
            vec = internalExecuteQuery();
        } catch (SQLException se) {
            Util.sneakyThrow(se);
        }
        return vec;
    }

    IPersistentVector internalExecuteQuery() throws SQLException {

        ResultSet rs = ps.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();

        ArrayList<Keyword> listK = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String str = metaData.getColumnName(i);
            list.add(str);
            
            listK.add(  Keyword.intern(null, str)   );
        }
        LOG.info("list {}", list);

        ArrayList<Object> result = new ArrayList<>();
        while (rs.next()) {
            IPersistentMap map = getRecord(rs, list, listK);
            result.add(map);
        }

        rs.close();
        return  new PersistentVector(result);
    }

    
    IPersistentMap getRecord(ResultSet rs, List<String> list, List<Keyword> keyList) throws SQLException {
        
        int len = list.size();
        Object[] init = new Object[len * 2];

        int i = 0;
        for (int j = 0; j < len; j++) {
        
            String name = list.get(j);
            Object v = rs.getObject(name);
            
            if (this.bKeyword) {
                init[i++] = keyList.get(j);
                
            } else {
                init[i++] = name;
            }
            init[i++] = v;
        }
        return RT.map(init);
        
    }
    
    
    
}
