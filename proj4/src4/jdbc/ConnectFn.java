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
import iris.clojure.lang.Keyword;

import java.sql.Connection;

import java.sql.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>
 * 
   (require ' [ jdbc.alpha :as j ])

 
 (defconstant db-spec { :classname "org.mariadb.jdbc.Driver" 
                       :connection-uri  "jdbc:mariadb://{IPaddress}/{database}user=XXX&password=PPP"    } )
  
  
   
 * 
 * </code>
 * 
 * https://clojure.github.io/java.jdbc/#clojure.java.jdbc/get-connection
 * 
 * https://github.com/clojure/java.jdbc/
 * 
 */
public class ConnectFn extends AFn {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectFn.class);
    
    // a String, the jdbc driver class name
    static Keyword DRIVER_CLASS = Keyword.intern(null , "classname");
    
    // a String Passed directly to DriverManager/getConnection
    static Keyword CON_URI = Keyword.intern(null , "connection-uri");



    
    @Override
    public Object invoke(Object arg1) {

        IPersistentMap map = (IPersistentMap) arg1;
        
        String driverClass = (String) map.valAt(DRIVER_CLASS, null);
        String conUri = (String) map.valAt(CON_URI, null);

        try {
            Class.forName(driverClass);
        } catch (Exception ex) {
            throw new RuntimeException("Can't load driver '" + driverClass + "'", ex);
        }
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(conUri);
        } catch (Exception ex) {
            throw new RuntimeException("Can't load driver '" + driverClass + "'", ex);
        }
        return conn;
    }
    
}
