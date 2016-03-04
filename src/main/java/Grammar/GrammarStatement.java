package Grammar;

import classes.DebugException;
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
        Expression expression = null;
        try {
            expression = test(new GrammarMethodCall(type), new GrammarConstant(type), new GrammarExistingVariable(type), new GrammarSoloOperator(type));
            return expression;
        } catch (DebugException error) {
            optional(BooleanNot);
            boolean isComparison = isComparison();
            try {
                expression = required(new GrammarExpression(type));
            } catch (GrammarException e) {
                throw error.adjust(e, tokens.getIndex(), tokens.tok()).getError();
            }
            if (isComparison && !expression.isConstant()) expression = new Expression(Expression.Type.Expression, Variable.VarType.Boolean);
        }
        return expression;
    }

    private boolean isComparison() throws GrammarException {
        if (notNull(optional(GrammarDualOperator.class))) return false;
        required(GrammarComparison.class);
        return true;
    }
}
