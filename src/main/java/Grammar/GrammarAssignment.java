package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.OperatorEquals;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarAssignment extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarAssignment(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        required(OperatorEquals);
        return required(new GrammarExpression(type));
    }
}
