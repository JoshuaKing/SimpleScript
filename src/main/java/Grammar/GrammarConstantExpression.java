package Grammar;

import classes.Expression;
import classes.Variable;

/**
 * Created by Josh on 6/03/2016.
 */
public class GrammarConstantExpression extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarConstantExpression(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        Expression constant = required(new GrammarConstant(type));

        Expression expression = optional(new GrammarSoloOperator(type));
        if (notNull(expression)) return expression;

        expression = optional(new GrammarComparison(), new GrammarDualOperator(type));
        if (notNull(expression)) return required(new GrammarExpression(type));

        return constant;
    }
}
