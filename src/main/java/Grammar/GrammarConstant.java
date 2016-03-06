package Grammar;

import classes.Expression;
import classes.Token;
import classes.Variable;

import static classes.Token.Type.*;
import static classes.Token.Type.ConstString;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarConstant extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarConstant(Variable.VarType type) throws GrammarException {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        Token token = required(KeywordTrue, KeywordFalse, ConstFloat, ConstInteger, ConstString);
        if (notNull(type) && !type.equals(Variable.VarType.fromTokenType(token.getType()))) {
            Variable.VarType varType = Variable.VarType.fromTokenType(token.getType());
            except("Expecting variable to be assigned type '" + type.name() + "' but was assigned a constant (" + token.getText() + ") of type '" + varType.name()+ "'", true);
        }
        return new Expression(Expression.Type.Constant, Variable.VarType.fromTokenType(token.getType()));
    }
}
