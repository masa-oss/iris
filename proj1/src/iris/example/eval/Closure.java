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

import iris.clojure.lang.LispPrintable;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Objects;

/**
 * This class holds the <b>lexical scope</b> and <b>lambda</b> .
 * 
 * Renamed Function to Closure
 * 
 */
public final class Closure implements LispPrintable, Formattable  {

    private final Object form;
    private final LexicalScope env;

    public Closure(Object form, LexicalScope env) {
        this.form = form;
        this.env = env;
    }

    /**
     * @return the form
     */
    public Object getForm() {
        return form;
    }

    /**
     * @return the scope
     */
    public LexicalScope getScope() {
        return env;
    }

    @Override
    public String getStringForPrint() {

        StringBuilder sb = new StringBuilder();
        sb.append("#<Closure ");

        sb.append(Integer.toHexString(super.hashCode()));

        sb.append(" ");

        sb.append(env.getStringForPrint());

        sb.append(">");
        return sb.toString();
    }

    
    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {

        formatter.format( "<Function %s %s>",  Integer.toHexString(super.hashCode()),  env.getStringForPrint()  );
    }
    
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.form);
        hash = 43 * hash + Objects.hashCode(this.env);
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
        final Closure other = (Closure) obj;
        if (!Objects.equals(this.form, other.form)) {
            return false;
        }
        return Objects.equals(this.env, other.env);
    }



}
