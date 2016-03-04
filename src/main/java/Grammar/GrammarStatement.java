package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.BooleanNot;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarStatement extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarStatement(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        Expression expression = test(new GrammarMethodCall(type));
        if (notNull(expression)) return expression;
        //TODO: if (handleInstanceCreation()) return true;
        expression = test(new GrammarConstant(type));
        if (notNull(expression)) return expression;
        expression = test(new GrammarExistingVariable(type));
        if (notNull(expression)) return expression;
        expression = test(new GrammarSoloOperator(type));
        if (notNull(expression)) return expression;

        optional(BooleanNot);
        boolean isComparison = isComparison();
        expression = required(new GrammarExpression(type));
        if (isComparison && !expression.isConstant()) return new Expression(Expression.Type.Expression, Variable.VarType.Boolean);
        return expression;
    }

    private boolean isComparison() throws GrammarException {
        if (notNull(optional(GrammarDualOperator.class))) return false;
        required(GrammarComparison.class);
        return true;
    }
}
