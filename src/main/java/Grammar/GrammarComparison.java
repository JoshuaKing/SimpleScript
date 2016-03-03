package Grammar;

import classes.Token;

import static classes.Token.Type.*;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarComparison extends GrammarRule<Token> {
    @Override
    public Token parseGrammar() throws GrammarException {
        return required(BooleanAnd, BooleanOr, BooleanEquals, BooleanNotEquals);
    }
}
