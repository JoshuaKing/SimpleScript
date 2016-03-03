package Grammar;

import classes.Token;

import static classes.Token.Type.OperatorDecrement;
import static classes.Token.Type.OperatorIncrement;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarSoloOperator extends GrammarRule<Token> {

    @Override
    public Token parseGrammar() throws GrammarException {
        return required(OperatorDecrement, OperatorIncrement);
    }
}
