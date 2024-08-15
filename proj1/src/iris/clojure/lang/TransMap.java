package iris.clojure.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;

/**
 * 
 * Simple implementation of ITransMap.
 * 
 * This class is not persistent .
 * 
 * This class like a clojure.lang.ATransientMap.java .
 */
public class TransMap implements ITransMap {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TransMap.class);
    
    final ConcurrentHashMap<Object, Object> cmap = new ConcurrentHashMap<>();
    
    public TransMap() {
    }
    
    @Override
    public Object valAt(Object key) {
        Object res = cmap.get(key);
        return res;
    }
    
    @Override
    public Object valAt(Object key, Object notFound) {
        Object res = cmap.get(key);
        if (NULL_VALUE.equals(res)) {
            return notFound;
        }
        
        return res;
        
    }
    
    static String NULL_VALUE = "NULL_VALUE";
    
    @Override
    public void put(Object key, Object val) {
        
        if (key == null) {
            throw new NullPointerException("key must be null");
        }
        
        Object val2 = (val == null) ? NULL_VALUE : val;
        
        cmap.put(key, val2);
    }
    
    public void putAll(IPersistentMap from) {
        
        for (ISeq seq = from.seq(); seq != RT.EOL; seq = seq.next()) {
            
            MapEntry me = (MapEntry) seq.first();
            
            cmap.put(me.getKey(), me.getValue());
        }
    }
    
    @Override
    public boolean containsKey(Object key) {
        
        ConcurrentHashMap.KeySetView<Object, Object> keySet = cmap.keySet();
        
        return keySet.contains(key);
    }
    
    @Override
    public IPersistentMap assocEx(Object key, Object val) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public IPersistentMap without(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*Override
    @Deprecated
    public Iterator<MapEntry> iterator() {
        
        ArrayList<MapEntry> list = new ArrayList<>();
        Set<Map.Entry<Object, Object>> entrySet = cmap.entrySet();
        for ( Map.Entry<Object, Object> ent : entrySet  ) {
        
            MapEntry me = new MapEntry(ent.getKey(), ent.getValue());
            list.add(me);
        
        }
        return list.iterator();
    }*/
    @Override
    public int count() {
        // return cmap.size();
        return cmap.keySet().size();
    }
    
    @Override
    public IMapEntry entryAt(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Associative assoc(Object key, Object val) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ISeq seq() {
        
        Set<Map.Entry<Object, Object>> entrySet = cmap.entrySet();
        
        if (entrySet.isEmpty()) {
            return RT.EOL;     // add 2024-06-15
        }
        
        Iterator<Map.Entry<Object, Object>> it = entrySet.iterator();
        
        List<Object> list = new ArrayList<>();
        
        while (it.hasNext()) {
            
            Map.Entry<Object, Object> next = it.next();
            
            MapEntry ent = new MapEntry(next.getKey(), next.getValue());
            list.add(ent);
            
        }
        
        return new ListSeq(list);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.cmap);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransMap other = (TransMap) obj;
        return Objects.equals(this.cmap, other.cmap);
    }
    
}
