/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.example.eval;

import iris.clojure.lang.ICommonLispFn;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.LispPrintable;
import iris.clojure.lang.PersistentHashMap;
import iris.clojure.lang.Symbol;
import java.util.ArrayList;

/**
 * Simple implementation of lexical scope .
 *
 */
public final class LexicalScope  implements LispPrintable {
    
    Symbol[] symbols;
    Object[] values;
    
    IPersistentMap localFunctions;
    
    
    private LexicalScope(Symbol[] initSym ,  Object[] initObj, LexicalScope delegate) {
        
        if (initSym == null) throw new NullPointerException();
        if (initObj == null) throw new NullPointerException();
        
        this.symbols = initSym;
        this.values = initObj;
        
        values[0] = delegate;
        if (delegate != null) {
            values[1] = delegate.values;
        } else {
            values[1] = null;
        }
        this.localFunctions = PersistentHashMap.EMPTY;
    }
    
    public LexicalScope() {
        
        this.symbols = new Symbol[2];
        this.values = new Object[2];
        this.localFunctions = PersistentHashMap.EMPTY;
    }    
    
    // １箇所 flet
    public LexicalScope(IPersistentMap localFuns, final LexicalScope old) {

        if (localFuns == null) throw new NullPointerException();
        this.symbols = new Symbol[2];
        this.values = new Object[2];
       
        this.localFunctions = localFuns;
        values[0] = old;
        if (old != null) {
            values[1] = old.values;
        } else {
            values[1] = null;
        }
    }    

    @Override
    public String getStringForPrint() {

        StringBuilder sb = new StringBuilder();
        sb.append("#<Scope ");
        sb.append(Integer.toHexString(super.hashCode()));

        sb.append(" ");

        Object delegate = values[0];
        int nVars = symbols.length -2;
        int nFuncs = localFunctions.count();
        if (nVars == 0 && nFuncs == 0 && delegate == null) {
            sb.append("empty");
        } else {
            String sep = "";
            if (nVars > 0) {
                sb.append(sep).append("#of var=").append(nVars );
                sep = ", ";
            }
            if (nFuncs > 0) {
                sb.append(sep).append("func");
                sep = ", ";
            }
            if (delegate != null) {
                sb.append(sep).append("has chain");
                sep = ", ";
            }
        }

        sb.append(">");
        return sb.toString();
    }



    
    public static class Builder {
        
        private final LexicalScope old;
        
        ArrayList<Symbol> sList = new ArrayList<>();
        ArrayList<Object> oList = new ArrayList<>();

        public Builder(LexicalScope old) {
            this.old = old;
        }
        
        public void addBinding(Symbol sym, Object value) {
            
            if (sList.contains(sym)) {
                throw new EvaluatorException( "Duplicate symbol : " + sym.getStringForPrint()  );
            }
            
            sList.add(sym);
            oList.add(value);
        }   

        public LexicalScope build() {
            
            int n = sList.size();
            Symbol[] symbols = new Symbol[n + 2];
            Object[] values = new Object[n + 2];
            
            for (int i = 0; i < n; i++) {
                symbols[i + 2 ] = sList.get(i);
                values[i + 2 ] = oList.get(i);
            }
            return new LexicalScope(symbols, values, old);
        }
    }
    
    /**
     * Get the location of a variable
     *
     * @param search
     * @return Returns null if not found.
     */
    public int[] getVariableIndex(Symbol search) {

        if (search == null) throw new NullPointerException();
        return getVariableIndex(search, 0);
    }
    
    /**
     * 
     * 
     * @param search
     * @param idx
     * @return   Returns null if not found.
     */
    
    int[] getVariableIndex(Symbol search, int idx) {

        int n = symbols.length;
        for (int i = 2; i < n; i++) {
            Symbol var = symbols[i];
            if (search.equals(var)) {
                int[] arr = new int[2];
                arr[0] = idx;
                arr[1] = i -2;
                return arr;
            }
        }
        LexicalScope del = (LexicalScope) values[0];
        if (del == null) {
            return null;
        }
        return del.getVariableIndex(search, idx+1);
    }    
    
    
    public Object getVariableValue(int m, int n) {
        
        if (m == 0) {
            return values[n+2];
        }
        LexicalScope del = (LexicalScope) values[0];
        if (del == null) {
            return null;
        }
        return del.getVariableValue(m-1, n);
    }
    
    public void setVariableValue(int m, int n, Object newValue) {
        
        if (m == 0) {
            values[n+2] = newValue;
        }
        LexicalScope del = (LexicalScope) values[0];
        if (del == null) {
            return;
        }
        del.setVariableValue(m-1, n, newValue);
    }
    
    
    public ICommonLispFn getLocalFunc(Object key) {
        
        Object ret = localFunctions.valAt(key, null);
        if (ret instanceof FuncDeref) {
            return (ICommonLispFn) ret;
        }
        return null;
    }
    
    



}
