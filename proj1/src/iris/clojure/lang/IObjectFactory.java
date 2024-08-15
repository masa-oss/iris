package iris.clojure.lang;

import java.util.List;
import java.util.Set;

/**
 * This interface is used by each reader macro to create a Lisp object.
 * It was introduced when LispReader's inner class was separated into separate classes.
 *
 */
public interface IObjectFactory {

    Number createBigIntOrLong(String n, int radix, boolean negate, String group8);

    IPersistentList createPersistentList(List<Object> list, int line, int column);

    IPersistentMap createPersistentHashMap(Object[] arr);
    
    IPersistentSet createPersistentHashSet(Set<Object> set);
    
    IPersistentVector createPersistentVector(List<Object> vec);
    
    Object createJavaFalse();

    Object createJavaNull();

    Object createJavaTrue();

    Object matchNumber(String s);

    Number num(long x);
    
    Object createCommonLispNil();
    
    Symbol createSymbol(String name);
}
