/**
 *   Copyright (c) Masahito Hemmi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
package clojure;

import iris.clojure.lang.Symbol;
import iris.clojure.nsvar.Namespace;
import iris.clojure.nsvar.Var;

import static iris.clojure.nsvar.CljCompiler.defun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (require '[ clojure.math :as m ])
 *
 *
 * @author hemmi
 */
public class math__init {

    private static final Logger LOG = LoggerFactory.getLogger(math__init.class);

    public static void load() {

        Symbol ns1 = Symbol.intern(null, "clojure.math");  //----------- CHANGE
     //   Namespace ns = Namespace.findOrCreate(ns1, false);
        Namespace ns = Namespace.findOrCreate(ns1);

        {
            Symbol sym = Symbol.intern(null, "PI");
            Object root = Math.PI;
            boolean replaceRoot = true;
            Var.intern(ns, sym, root, replaceRoot);
        }

        {
            Symbol sym = Symbol.intern(null, "E");
            Object root = Math.E;
            boolean replaceRoot = true;
            Var.intern(ns, sym, root, replaceRoot);
        }

        defun(ns, "IEEE-remainder", new MathFunctions.IEEEremainder());

        defun(ns, "acos", new MathFunctions.Acos());
        defun(ns, "add-exact", new MathFunctions.AddExact());

        defun(ns, "asin", new MathFunctions.Asin());

        defun(ns, "atan", new MathFunctions.Atan());
        defun(ns, "atan2", new MathFunctions.Atan2()); // TODO test

        defun(ns, "cbrt", new MathFunctions.Cbrt());

        defun(ns, "ceil", new MathFunctions.Ceil());

        defun(ns, "copy-sign", new MathFunctions.CopySign());

        defun(ns, "cos", new MathFunctions.Cos());

        defun(ns, "cosh", new MathFunctions.Cosh());

        defun(ns, "decrement-exact", new MathFunctions.DecrementExact());

        defun(ns, "exp", new MathFunctions.Exp());

        defun(ns, "expm1", new MathFunctions.Expm1());

        defun(ns, "floor", new MathFunctions.Floor());

        defun(ns, "floor-div", new MathFunctions.FloorDiv()); // TODO test
        defun(ns, "floor-mod", new MathFunctions.FloorMod()); // TODO test

        defun(ns, "get-exponent", new MathFunctions.GetExponent());

        defun(ns, "hypot", new MathFunctions.Hypot());

        defun(ns, "increment-exact", new MathFunctions.IncrementExact());
        defun(ns, "log", new MathFunctions.Log());

        defun(ns, "log10", new MathFunctions.Log10());

        defun(ns, "log1p", new MathFunctions.Log1p());

        defun(ns, "multiply-exact", new MathFunctions.MultiplyExact());

        defun(ns, "negate-exact", new MathFunctions.NagateExact());

        defun(ns, "next-after", new MathFunctions.NextAfter());

        defun(ns, "next-down", new MathFunctions.NextDown());
        defun(ns, "next-up", new MathFunctions.NextUp());

        defun(ns, "pow", new MathFunctions.Pow());
        
        defun(ns, "random", new MathFunctions.Random());

        defun(ns, "rint", new MathFunctions.Rint());

        defun(ns, "round", new MathFunctions.Round());
        
        defun(ns, "scalb", new MathFunctions.Scalb());
        
        defun(ns, "sin", new MathFunctions.Sin());

        defun(ns, "sinh", new MathFunctions.Sinh());

        defun(ns, "sqrt", new MathFunctions.Sqrt());

        defun(ns, "subtract-exact", new MathFunctions.SubtractExact());

        defun(ns, "tan", new MathFunctions.Tan());

        defun(ns, "tanh", new MathFunctions.Tanh());

        
        defun(ns, "to-degrees", new MathFunctions.ToDegrees());
        
        defun(ns, "to-radians", new MathFunctions.ToRadians());
        
        defun(ns, "ulp", new MathFunctions.Ulp());
    }

    static {

        LOG.info("static method start ...");

        try {
            load();
        } catch (Exception ex) {
            throw ex;
        }

    }

}
