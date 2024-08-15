/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.IPersistentVector;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.Keyword;
import iris.clojure.lang.MapEntry;
import iris.clojure.lang.PersistentList;
import iris.clojure.lang.RT;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class ConvToGson {

    private static final Logger LOG = LoggerFactory.getLogger(ConvToGson.class);

    public String toJson(Object sexp) {

        JsonElement je = convert(sexp);
        Gson gson = new Gson();
        return gson.toJson(je);
    }

    public String toPpJson(Object sexp) {

        JsonElement je = convert(sexp);
        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson2.toJson(je);
        return prettyJson;
    }

    public JsonElement convert(Object sexp) {

        if (sexp == null) {
            return JsonNull.INSTANCE;
        } else if (sexp instanceof Boolean) {
            Boolean bl = (Boolean) sexp;
            return new JsonPrimitive(bl);
        } else if (sexp instanceof Number) {
            Number num = (Number) sexp;
            return new JsonPrimitive(num);
        } else if (sexp instanceof IPersistentVector) {

            return convArray((IPersistentVector) sexp);
        } else if (sexp instanceof IPersistentMap) {

            return convObject((IPersistentMap) sexp);
        } else if (sexp instanceof String) {

            return new JsonPrimitive((String) sexp);
        }
        throw new IllegalArgumentException("can't convert " + sexp.getClass().getName());
    }


    JsonObject convObject(IPersistentMap map) {

        JsonObject result = new JsonObject();

        for (ISeq seq = map.seq(); seq != RT.EOL; seq = seq.next()) {

            MapEntry o = (MapEntry) seq.first();
            LOG.info("88) {}", o);

            Object key = o.getKey();
            Object value = o.getValue();
            String strKey = keyToString(key);

            JsonElement elem = convert(value);
            result.add(strKey, elem);

        }
        return result;
    }

    String keyToString(Object key) {

        if (key == null) {
            return "null";
        } else if (key instanceof Keyword) {
            Keyword kw = (Keyword) key;
            return kw.getName();
        } else if (key instanceof String) {
            return (String) key;
        } else {
            return key.toString();
        }
    }

    JsonArray convArray(IPersistentVector vector) {

        int n = vector.length();
        JsonArray result = new JsonArray();
        for (int i = 0; i < n; i++) {
            Object o = vector.get(i);
            JsonElement elem = convert(o);
            result.add(elem);
        }
        return result;
    }

    // ---------
    
    public JsonElement fromStringToJson(String str) {
        
        Gson gson = new Gson();
        JsonElement je = gson.fromJson(str, JsonElement.class);
        return je;
    }


    public Object jsonElementToCollection(JsonElement je) {
        
        if (je.isJsonNull()) {
            
                return null;
        } else if (je.isJsonPrimitive()) {
            
            JsonPrimitive jp = (JsonPrimitive) je;
            if (jp.isBoolean()) {
                return jp.getAsBoolean();
            } else if (jp.isString()) {
                return jp.getAsString();
            } else if (jp.isNumber()) {
                return jp.getAsNumber();
            } else {
                throw new RuntimeException();
            }
        } else if (je.isJsonArray()) {
            
            JsonArray ja = (JsonArray) je;

            ArrayList<Object> list = new ArrayList<>();
            int n = ja.size();
            for (int i = 0; i < n; i++) {
                JsonElement get = ja.get(i);
                
                Object lispObject =  jsonElementToCollection(get);

                list.add(lispObject);
            }
            
            return 
            PersistentList.create(list);
        }

                throw new RuntimeException();
        
    }
    
}
