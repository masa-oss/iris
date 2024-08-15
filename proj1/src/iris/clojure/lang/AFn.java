/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 * */
/**
 *   Author: Masahito Hemmi
 */
package iris.clojure.lang;

/**
 * Base class representing functions.
 *
 * This class is a subset of clojure.lang.AFn.
 *
 * @author hemmi
 */
public abstract class AFn implements IFn {

    @Override
    public Object call() {
        return invoke();
    }

    @Override
    public void run() {
        invoke();
    }

    @Override
    public Object invoke() {
        return throwArity(0);
    }

    @Override
    public Object invoke(Object arg1) {
        return throwArity(1);
    }

    @Override
    public Object invoke(Object arg1, Object arg2) {
        return throwArity(2);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3) {
        return throwArity(3);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) {
        return throwArity(4);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return throwArity(5);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return throwArity(6);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return throwArity(7);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
            Object arg8) {
        return throwArity(8);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
            Object arg8, Object arg9) {
        return throwArity(9);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
            Object arg8, Object arg9, Object arg10) {
        return throwArity(10);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
            Object arg8, Object arg9, Object arg10, Object arg11) {
        return throwArity(11);
    }

    @Override
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
            Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return throwArity(12);
    }

    /*
public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13)
		{
	return throwArity(13);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14)
		{
	return throwArity(14);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15) {
	return throwArity(15);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16) {
	return throwArity(16);
}

     */

 /*
    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
            Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
            Object arg15, Object arg16, Object arg17, Object arg18, Object arg19, Object arg20,
            Object... args) {
        return throwArity(21);
    }
     */
    public Object throwArity(int n) {
        String name = getClass().getName();
        throw new ArityException(n, name);
    }

    @Override
    public Object applyTo(ISeq arglist) {

        return applyToHelper(this, arglist);
    }

    static public Object applyToHelper(IFn ifn, ISeq arglist) {
//	switch(RT.boundedLength(arglist, 20))
        switch (arglist.count()) {
            case 0:
                //  arglist = null;
                return ifn.invoke();
            case 1:
                return ifn.invoke(Util.ret1(arglist.first(), arglist = null));
            case 2:
                return ifn.invoke(arglist.first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 3:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 4:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 5:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 6:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 7:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 8:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            case 9:
                return ifn.invoke(arglist.first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        (arglist = arglist.next()).first(),
                        Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );

            case 10:
                return ifn.invoke(arglist.first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         (arglist = arglist.next()).first(),
                         Util.ret1((arglist = arglist.next()).first(), arglist = null)
                );
            default:
                throw new UnsupportedOperationException();
        }
    }

}
