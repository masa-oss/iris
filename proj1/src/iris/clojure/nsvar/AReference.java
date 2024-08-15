/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package iris.clojure.nsvar;

import iris.clojure.lang.Cons;
import iris.clojure.lang.IFn;
import iris.clojure.lang.IObj;
import iris.clojure.lang.IPersistentMap;
import iris.clojure.lang.ISeq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a subset of clojure.lang.AReference.java .
 */

public class AReference implements IReference {

    private static final Logger LOG = LoggerFactory.getLogger(AReference.class);
    
    private IPersistentMap _meta;

    public AReference() {
        this(null);
    }

    public AReference(IPersistentMap meta) {
        _meta = meta;
    }

    @Override
    synchronized public IPersistentMap meta() {
        return _meta;
    }

    @Override
    synchronized public IPersistentMap alterMeta(IFn alter, ISeq args) {

        LOG.info("alterMeta {}, {}", alter, args);
        _meta = (IPersistentMap) alter.applyTo(new Cons(_meta, args));
        LOG.info("after alterMeta  {}",  _meta);

        return _meta;
    }

    @Override
    synchronized public IPersistentMap resetMeta(IPersistentMap m) {
        _meta = m;
        return m;
    }

    @Override
    public final IObj withMeta(IPersistentMap meta) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
