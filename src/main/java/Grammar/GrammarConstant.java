package Grammar;

import classes.Token;
import classes.Variable;

import static classes.Token.Type.*;
import static classes.Token.Type.ConstString;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarConstant extends GrammarRule<Token> {
    private Variable.VarType type;

    public GrammarConstant(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Token parseGrammar() throws GrammarException {
        Token token = required(KeywordTrue, KeywordFalse, ConstFloat, ConstInteger, ConstString);
        if (type != null && !type.equals(Variable.VarType.fromTokenType(token.getType()))) {
            Variable.VarType varType = Variable.VarType.fromTokenType(token.getType());
            except("Expecting variable to be assigned type '" + type.name() + "' but was assigned a constant (" + token.getText() + ") of type '" + varType.name()+ "'");
        }
        return token;
    }
}
