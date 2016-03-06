package Grammar;

import classes.Expression;
import classes.Method;
import classes.Token;
import classes.Variable;
import compiler.SymbolTable;

import static classes.Token.Type.CloseParenthesis;

/**
 * Created by Josh on 6/03/2016.
 */
public class GrammarVariableExpression extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarVariableExpression(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        String name = required(GrammarName.class);

        Expression expression = optional(new GrammarSoloOperator(type));
        if (notNull(expression)) return expression;

        expression = optional(new GrammarComparison(), new GrammarDualOperator(type));
        if (notNull(expression)) return required(new GrammarExpression(type));

        if (optional(Token.Type.OpenParenthesis)) {
            Method method = ensure(SymbolTable.getInstance().getMethod(name));
            if (notNull(type) && !type.equals(method.getReturnType())) {
                except("Expecting method " + method.getName() + " to return '" + type.name()
                        + "' but returns type '" + method.getReturnType().name() + "'", true);
            }
            System.out.println("Method call to " + method.getName());
            required(new GrammarMethodParameters(method.getArguments()));
            required(CloseParenthesis);
            return new Expression(Expression.Type.Expression, method.getReturnType());
        }

        return required(new GrammarExistingVariable(type, name));
    }
}
