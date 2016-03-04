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
    // MethodCall | NewInstance | { Constant, Variable } [SoloOp]
    public Expression parseGrammar() throws GrammarException {
        Expression expression = null;
        DebugException error = null;
        try {
            try {
                expression = test(new GrammarMethodCall(type));
                if (notNull(expression)) return expression;
            } catch (DebugException e) {
                error = e;
            }
            try {
                expression = test(new GrammarConstant(type), new GrammarExistingVariable(type));
                if (notNull(expression)) return expression;
            } catch (DebugException e) {
                error = e.adjust(error);
            }
            try {
                expression = test(new GrammarSoloOperator(type));
                if (notNull(expression)) return expression;
            } catch (DebugException e) {
                error = e.adjust(error);
            }
            throw error;
        } catch (DebugException err) {
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
        if (notNull(optional(new GrammarDualOperator(type)))) return false;
        required(GrammarComparison.class);
        return true;
    }
}
