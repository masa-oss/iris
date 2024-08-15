package iris.util;

import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;
import iris.clojure.lang.Symbol;
import iris.example.eval.ExampleEvaluator;
import iris.example.eval.LexicalScope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class ExampleEvaluatorForTest implements ExampleEvaluator {
    
    private static final Logger LOG = LoggerFactory.getLogger(ExampleEvaluatorForTest.class);
    
    static int MODE_RET = 1;
    static int MODE_THROW = 2;

    List<Integer> modeList;
    List<Object> retList;
    int idx = 0;
    
    ExampleEvaluatorForTest(List<Integer> modeList, List<Object> retList) {
        this.modeList = modeList;
        this.retList = retList;
    }
    
    public static class Builder {
        
        ArrayList<Integer> modeList = new ArrayList<>();
        ArrayList<Object> retList = new ArrayList<>();
        
        public Builder() {
        }
        
        public void add(int mode, Object retObj) {
            modeList.add(mode);
            retList.add(retObj);
        }
        
        public ExampleEvaluatorForTest build() {
            return new ExampleEvaluatorForTest(
                    Collections.unmodifiableList(modeList),
                    Collections.unmodifiableList(retList)
            );
        }
        
    }
    
    
    ArrayList<Object> calledList = new ArrayList<>();
    
    
    @Override
    public Object eval(Object sexp, LexicalScope env) {
        
        calledList.add(sexp);
        
        String str = RT.printString(sexp);
        
        LOG.info("MockEval : {}, {}", str, env.toString());
        
        int mode = modeList.get(idx);
        Object ret = retList.get(idx);
        idx++;
        
        
        if (mode == MODE_RET) {
            return sexp;
        } else if (mode == MODE_THROW) {
            
            throw (RuntimeException) ret;
        }
        
        return ret;
    }
    
    
    public List getCalledList() {
        return calledList;
    }
    
    
    
    @Override
    public Object apply(Object first, ISeq args) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object macroexpand1(ISeq sexp) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object resolve(Symbol sym, LexicalScope env) {
        
        if (EXCEPTION.equals(sym)) {
            return Exception.class;
        }
        
        LOG.error("Unknown {}", sym.toString());
        return null;
    }
    
    static Symbol EXCEPTION = Symbol.intern(null, "Exception");
    
}
