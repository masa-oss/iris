package iris.clojure.lang;

/**
 * Nil in <b>Common Lisp</b> .
 * 
 * 
 */
public final class Nil extends Symbol implements IPersistentList {
    
    public static final Nil INSTANCE = new Nil(null, "nil", null);
    
    protected Nil(String ns_interned, String name_interned) {
        super(ns_interned, name_interned);
    }
    
    protected Nil(String ns_interned, String name_interned, IPersistentMap newMeta) {
        super(ns_interned, name_interned, newMeta);
    }    

    @Override
    public Object first() {
        return INSTANCE;
    }

    @Override
    public ISeq next() {
        return INSTANCE;
    }

    @Override
    public ISeq cons(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public ISeq seq() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Since nil is reused, meta manipulation is prohibited.
    @Override
    public IObj withMeta(IPersistentMap meta) {
        return this;
    }

    @Override
    public IPersistentMap meta() {
        return null;
    }
    

    @Override
    public int hashCode() {

        return super.hashnum;
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
        return this.name.equals(super.name);
    }


}
