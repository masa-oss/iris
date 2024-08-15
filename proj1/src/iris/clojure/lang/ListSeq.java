
/**
 *   Author: Masahito Hemmi
 **/
package iris.clojure.lang;

import java.util.List;
/**
 * Simple implementation of ISeq , which use java.util.List .
 * 
 * 
 */
public class ListSeq implements ISeq {

    int idx = 0;
    final List<Object> list;
    final int length;

    public ListSeq(List<Object> it) {
        
      //  if (it != null) throw new RuntimeException("TODO bug here !!");
        
        this.list = it;
        this.length = it.size();
    }


    @Override
    public Object first() {
        if (idx < length) {
            return list.get(idx);
        } else {
            return null;
        }
    }

    @Override
    public ISeq next() {
        idx++;
        if (idx >= length) {
            return RT.EOL;
        } else {
            return this;
        }
    }

    @Override
    public ISeq cons(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int count() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISeq seq() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
