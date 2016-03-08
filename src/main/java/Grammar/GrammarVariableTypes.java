package Grammar;

import classes.Token;

import static classes.Token.Type.*;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarVariableTypes extends GrammarRule<Token> {
    @Override
    public Token parseGrammar() throws GrammarException {
        return required(KeywordInt, KeywordFloat, KeywordBoolean, KeywordString);
    }

    @Override
    public String getJavascript() {
        return "var";
    }
}
