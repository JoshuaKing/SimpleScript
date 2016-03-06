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
        Expression expression;
        if (optional(OpenParenthesis)) {
            expression = required(new GrammarExpression(type));
            required(CloseParenthesis);
            return expression;
        }

        expression = optional(new GrammarConstantExpression(type));
        if (notNull(expression)) {
            return expression;
        }
        if (notNull(optional(GrammarVariableTypes.class))){

        }

        return required(new GrammarVariableExpression(type));
    }
}
