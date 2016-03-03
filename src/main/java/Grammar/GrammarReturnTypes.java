package Grammar;

import classes.Token;

import static classes.Token.Type.*;
import static classes.Token.Type.KeywordVoid;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarReturnTypes extends GrammarRule<Token> {
    @Override
    public Token parseGrammar() throws GrammarException {
        return required(KeywordInt, KeywordFloat, KeywordBoolean, KeywordString, KeywordVoid);
    }
}
