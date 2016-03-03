package Grammar;

import classes.Variable;

import static classes.Token.Type.CloseParenthesis;
import static classes.Token.Type.OpenParenthesis;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarExpression extends GrammarRule<Boolean> {
    private Variable.VarType type;

    public GrammarExpression(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        if (optional(OpenParenthesis)) {
            required(new GrammarExpression(type));
            required(CloseParenthesis);
        }
        required(new GrammarStatement(type));
        while (optional(GrammarComparison.class) != null) {
            required(new GrammarStatement(type));
        }
        return true;
    }
}
