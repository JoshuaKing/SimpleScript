package Grammar;

import classes.Variable;

import static classes.Token.Type.OperatorEquals;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarAssignment extends GrammarRule<Boolean> {
    private Variable.VarType type;

    public GrammarAssignment(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        required(OperatorEquals);
        return required(new GrammarExpression(type));
    }
}
