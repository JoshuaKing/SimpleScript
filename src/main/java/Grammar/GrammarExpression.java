package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.BooleanNot;
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
    // '(' Expression ')' | Statement [{DualOp, Comp} Expression]* | ['!'] Expression
    public Expression parseGrammar() throws GrammarException {
        Expression expression;
        if (optional(OpenParenthesis)) {
            expression = required(new GrammarExpression(type));
            required(CloseParenthesis);
            return expression;
        }

        if (optional(BooleanNot)) {
            return required(new GrammarExpression(type));
        }

        expression = required(new GrammarStatement(type));

        while (notNull(optional(GrammarComparison.class))) {
            required(new GrammarStatement(type));
            expression = new Expression(Expression.Type.Expression, Variable.VarType.Boolean);
        }
        return expression;
    }
}
