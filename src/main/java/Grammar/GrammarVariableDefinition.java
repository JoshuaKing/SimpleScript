package Grammar;

import classes.Token;
import classes.Variable;
import compiler.VariableTable;

import static classes.Token.Type.*;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarVariableDefinition extends GrammarRule<Boolean> {
    Variable.Access access;
    boolean isStatic;

    public GrammarVariableDefinition(Variable.Access access, boolean isStatic) throws GrammarException {
        this.access = access;
        this.isStatic = isStatic;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        Token token = required(KeywordInt, KeywordBoolean, KeywordFloat, KeywordString);
        String name = required(GrammarName.class);
        Variable variable = new Variable(access, isStatic, name, token.getType());

        if (optional(Semicolon)) {
            VariableTable.getInstance().addToScope(variable);
            return true;
        }
        required(new GrammarAssignment(variable.getVarType()));
        variable.setValue(tokens.prevText());
        VariableTable.getInstance().addToScope(variable);
        required(Semicolon);
        return true;
    }
}
