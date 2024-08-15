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

import iris.clojure.lang.AFn;

/**
 *
 */
public final class MathFunctions {

    // disavle construct
    private MathFunctions() {

    }

    public static class IEEEremainder extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number n1 = (Number) arg1;
            Number n2 = (Number) arg2;

            return Math.IEEEremainder(n1.doubleValue(), n2.doubleValue());
        }
    }

    public static class AddExact extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number n1 = (Number) arg1;
            Number n2 = (Number) arg2;

            return Math.addExact(n1.longValue(), n2.longValue());
        }
    }
    
    public static class Asin extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number n = (Number) arg1;

            return Math.asin( n.doubleValue()  );
        }
    }

    public static class Acos extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.acos(num.doubleValue());
        }
    }
    
    public static class Atan extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.atan(num.doubleValue());
        }
    }

    public static class Atan2 extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number n1 = (Number) arg1;
            Number n2 = (Number) arg2;

            return Math.atan2(n1.doubleValue(), n2.doubleValue());
        }
    }
    
    public static class Cbrt extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.cbrt(num.doubleValue());
        }
    }
    
    public static class Ceil extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number n1 = (Number) arg1;

            return Math.ceil(n1.doubleValue());
        }
    }

    public static class CopySign extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number n1 = (Number) arg1;
            Number n2 = (Number) arg2;

            return Math.copySign(n1.doubleValue(), n2.doubleValue());
        }
    }

    public static class Cos extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.cos(num.doubleValue());
        }
    }


    public static class Cosh extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.cosh(num.doubleValue());
        }
    }

    public static class DecrementExact extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.decrementExact(num.longValue());
        }
    }
    
    public static class Exp extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.exp(num.doubleValue());
        }
    }

    public static class Expm1 extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.expm1(num.doubleValue());
        }
    }

    public static class Floor extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.floor(num.doubleValue());
        }
    }

    public static class FloorDiv extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number num = (Number) arg1;
            Number num2 = (Number) arg2;

            return Math.floorDiv(num.longValue(), num2.longValue());
        }
    }

    public static class FloorMod extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number num = (Number) arg1;
            Number num2 = (Number) arg2;

            return Math.floorMod(num.longValue(), num2.longValue());
        }
    }
    
    public static class GetExponent extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.getExponent(dbl.doubleValue());
        }
    }
    
    public static class Hypot extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;
            Number dbl2 = (Number) arg2;

            return Math.hypot(dbl.doubleValue(), dbl2.doubleValue());
        }
    }
    
    public static class IncrementExact extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.incrementExact(num.longValue());
        }
    }
    
    public static class Log extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.log(dbl.doubleValue());
        }
    }
    
    public static class Log10 extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.log10(dbl.doubleValue());
        }
    }

    public static class Log1p extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.log1p(dbl.doubleValue());
        }
    }

    public static class MultiplyExact extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number num = (Number) arg1;
            Number num2 = (Number) arg2;

            return Math.multiplyExact(num.longValue(), num2.longValue());
        }
    }

    public static class NagateExact extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number num = (Number) arg1;

            return Math.negateExact(num.longValue());
        }
    }

    public static class NextAfter extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;
            Number dbl2 = (Number) arg2;

            return Math.nextAfter(dbl.doubleValue(), dbl2.doubleValue());
        }
    }
    
    public static class NextDown extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;

            return Math.nextDown(dbl.doubleValue());
        }
    }
    
    public static class NextUp extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;

            return Math.nextUp(dbl.doubleValue());
        }
    }
    
    public static class Pow extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;
            Number dbl2 = (Number) arg2;

            return Math.pow(dbl.doubleValue(), dbl2.doubleValue());
        }
    }

    public static class Random extends AFn {

        @Override
        public Object invoke() {

            return Math.random();
        }
    }
    
    public static class Rint extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;

            return Math.rint(dbl.doubleValue());
        }
    }
    
    public static class Round extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;

            return Math.round(dbl.doubleValue());
        }
    }
    
    public static class Scalb extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;
            Number it = (Number) arg2;

            return Math.scalb(dbl.doubleValue(), it.intValue());
        }
    }

    public static class Signum extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number dbl = (Number) arg1;

            return Math.signum(dbl.doubleValue());
        }
    }
    
    public static class Sin extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number n = (Number) arg1;

            return Math.sin( n.doubleValue() );
        }
    }

    public static class Sinh extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.sinh(dbl.doubleValue());
        }
    }

    public static class Sqrt extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.sqrt(dbl.doubleValue());
        }
    }

    public static class SubtractExact extends AFn {

        @Override
        public Object invoke(Object arg1, Object arg2) {

            Number lng = (Number) arg1;
            Number lng2 = (Number) arg2;

            return Math.subtractExact(lng.longValue(), lng2.longValue());
        }
    }
    
    public static class Tan extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.tan(dbl.doubleValue());
        }
    }

    public static class Tanh extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.tanh(dbl.doubleValue());
        }
    }

    public static class ToDegrees extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.toDegrees(dbl.doubleValue());
        }
    }

    public static class ToRadians extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.toRadians(dbl.doubleValue());
        }
    }

    public static class Ulp extends AFn {

        @Override
        public Object invoke(Object arg1) {

            Number dbl = (Number) arg1;

            return Math.ulp(dbl.doubleValue());
        }
    }

}
