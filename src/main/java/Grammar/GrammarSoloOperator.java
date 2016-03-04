package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.OperatorDecrement;
import static classes.Token.Type.OperatorIncrement;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarSoloOperator extends GrammarRule<Expression> {

    private final Variable.VarType type;

    public GrammarSoloOperator(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        required(OperatorDecrement, OperatorIncrement);
        return new Expression(Expression.Type.Expression, type);
    }
}
