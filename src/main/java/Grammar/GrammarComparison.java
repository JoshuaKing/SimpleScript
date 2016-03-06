package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.*;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarComparison extends GrammarRule<Expression> {
    @Override
    public Expression parseGrammar() throws GrammarException {
        required(BooleanAnd, BooleanOr, BooleanEquals, BooleanNotEquals);
        return new Expression(Expression.Type.Expression, Variable.VarType.Boolean);
    }
}
