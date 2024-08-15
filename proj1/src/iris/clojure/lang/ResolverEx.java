package iris.clojure.lang;

/**
 * This interface is there to access Compiler.java from respective Readmacro .
 * 
 * It was introduced when the inner class was separated from  LispReader.java .
 * 
 *   Author: Masahito Hemmi
 *
 */
public interface ResolverEx {
    
    boolean isSpecialEx(Object sym);
    
    
    Symbol resolveSymbol(Symbol sym);

    Object getCurrentNSMapping(Symbol name);

    Object specialsValAt(Object key, Object notFound);

}
