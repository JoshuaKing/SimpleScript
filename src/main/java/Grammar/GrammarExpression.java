package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.CloseParenthesis;
import static classes.Token.Type.OpenParenthesis;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarExpression extends GrammarRule<Expression> {
    private Variable.VarType type = null;

    public GrammarExpression(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        if (optional(OpenParenthesis)) {
            required(new GrammarExpression(type));
            required(CloseParenthesis);
        }
        Expression expression = required(new GrammarStatement(type));
        while (optional(GrammarComparison.class) != null) {
            required(new GrammarStatement(type));
            expression = new Expression(Expression.Type.Expression, Variable.VarType.Boolean);
        }
        return expression;
    }
}
