package Grammar;

import classes.Token;
import classes.Variable;
import compiler.VariableTable;

import static classes.Token.Type.*;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarVariableDefinition extends GrammarRule<Boolean> {
    Variable.Modifier modifier;
    boolean isStatic;

    public GrammarVariableDefinition(Variable.Modifier modifier, boolean isStatic) throws GrammarException {
        this.modifier = modifier;
        this.isStatic = isStatic;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        Token token = required(GrammarVariableTypes.class);
        String name = required(GrammarName.class);
        Variable variable = new Variable(modifier, isStatic, name, token.getType());

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
