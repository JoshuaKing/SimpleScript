package Grammar;

import classes.Variable;

import static classes.Token.Type.BooleanNot;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarStatement extends GrammarRule<Boolean> {
    private Variable.VarType type;

    public GrammarStatement(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        //TODO: if (handleMethodCall(type)) return true;
        //TODO: if (handleInstanceCreation()) return true;
        if (required(new GrammarConstant(type)) != null) return true;
        //TODO: if (required(new GrammarExistingVariable(type))) return true;
        if (required(GrammarSoloOperator.class) != null) return true;

        optional(BooleanNot);
        required(GrammarDualOperator.class, GrammarComparison.class);
        required(new GrammarExpression(type));
        return true;
    }
}
