package iris.example.eval;

import iris.clojure.lang.Cons;
import iris.clojure.lang.ISeq;
import iris.clojure.lang.RT;
import iris.clojure.lang.RestFn;
import iris.clojure.lang.Symbol;
import static iris.example.eval.Specials.AND_REST;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hemmi
 */
public class DefunRest extends RestFn {

    private static final Logger LOG = LoggerFactory.getLogger(DefunRest.class);

    boolean macro;

    public DefunRest(boolean macro) {
        this.macro = macro;
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }

    @Override
    protected Object doInvoke(Object arg1, Object arg2, Object rest) {

        String pstring1 = RT.printString(arg1);
        LOG.info("pstring1={}", pstring1);

      //  String pstring2 = RT.printString(arg2);
      //  LOG.info("pstring2={}", pstring2);

      //  String printString = RT.printString(rest);
      //  LOG.info("printString={}", printString);

        Symbol funcName = (Symbol) arg1;  // function-name

        Object argsList = (ISeq) arg2; // argument-list

        checkArgs(argsList); // Check the arguments !!

        ISeq seq = (ISeq) rest;

        Object obj3 = seq.first(); // documentation

        String doc = null;
        if (obj3 instanceof String) {
            doc = (String) obj3;
            seq = seq.next();
        }

        Object block = new Cons(BLOCK, new Cons(funcName, seq));
        Object lambda = RT.list(LAMBDA, argsList, block);

        Object func = RT.list(FUNCTION, lambda);

        Object defn = RT.list(INT_DEFUN,
                        RT.list(QUOTE, funcName),
                        func, doc, macro);
        return defn;
    }

    void checkArgs(Object persistentArgList) {

        HashSet<Symbol> binds = new HashSet<>();
        ISeq vn = (ISeq) persistentArgList; // varNames
        for (; vn != RT.EOL; vn = vn.next()) {

            Symbol varName = (Symbol) vn.first();

            if (AND_REST.equals(varName)) {

                vn = vn.next();
                if (vn == RT.EOL) {
                    ETE.throwException("Syntax error near &rest", varName, persistentArgList);
                }

                Symbol restName = (Symbol) vn.first();
                if (binds.contains(restName)) {
                    throw new RuntimeException("Duplicate var name: " + restName);
                }
                vn = vn.next();
                if (vn != RT.EOL) {
                    throw new RuntimeException("Too many var, near &rest");

                }
                return;
            }

            if (binds.contains(varName)) {
                throw new RuntimeException("Duplicate var name: " + varName);
            }
            binds.add(varName);
        }

    }

    static Symbol LAMBDA = Symbol.intern(null, "lambda");
    static Symbol BLOCK = Symbol.intern(null, "block");

    static Symbol FUNCTION = Symbol.intern(null, "function");
    static Symbol QUOTE = Symbol.intern(null, "quote");

    static Symbol INT_DEFUN = Symbol.intern(null, "*internal-defun");
}
