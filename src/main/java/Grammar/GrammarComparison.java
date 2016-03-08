package Grammar;

import classes.Expression;
import classes.Token;
import classes.Variable;

import static classes.Token.Type.*;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarComparison extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarComparison(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        Token token = required(BooleanAnd, BooleanOr, BooleanEquals, BooleanNotEquals, LessThan, GreaterThan);
        if (token.getType().equals(LessThan) || token.getType().equals(GreaterThan)) {
            if (notNull(type) && type.equals(Variable.VarType.String)) {
                return new Expression(Expression.Type.Expression, Variable.VarType.Integer);
            }
            return new Expression(Expression.Type.Expression, type);
        }
        return new Expression(Expression.Type.Expression, Variable.VarType.Boolean);
    }
}
