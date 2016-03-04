package Grammar;

import classes.Expression;
import classes.Method;
import classes.Variable;
import compiler.VariableTable;

import static classes.Token.Type.CloseParenthesis;
import static classes.Token.Type.OpenParenthesis;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodCall extends GrammarRule<Expression> {
    private Variable.VarType expectedReturnType;

    public GrammarMethodCall(Variable.VarType expectedReturnType) throws GrammarException {
        this.expectedReturnType = expectedReturnType;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        String name = required(GrammarName.class);
        required(OpenParenthesis);
        Method method = ensure(VariableTable.getInstance().getMethod(name));
        if (notNull(expectedReturnType) && !expectedReturnType.equals(method.getReturnType())) {
            except("Expecting method " + method.getName() + " to return '" + expectedReturnType.name()
                    + "' but returns type '" + method.getReturnType().name() + "'");
        }
        System.out.println("Method call to " + method.getName());
        required(new GrammarMethodParameters(method.getArguments()));
        required(CloseParenthesis);
        return new Expression(Expression.Type.Expression, method.getReturnType());
    }
}
