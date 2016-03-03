package Grammar;

import classes.Method;
import classes.Variable;
import compiler.VariableTable;

import static classes.Token.Type.OpenParenthesis;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodCall extends GrammarRule<Boolean> {
    private Variable.VarType expectedReturnType;

    public GrammarMethodCall(Variable.VarType expectedReturnType) {
        this.expectedReturnType = expectedReturnType;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        String name = required(GrammarName.class);
        required(OpenParenthesis);
        Method method = ensure(VariableTable.getInstance().getMethod(name));
        if (!method.getReturnType().equals(expectedReturnType)) {
            except("Expecting method " + method.getName() + " to return '" + expectedReturnType.name()
                    + "' but returns type '" + method.getReturnType().name() + "'");
        }
        System.out.println("Method call to " + method.getName());
        required(new GrammarMethodParameters(method.getArguments()));
        return true;
    }
}
