package Grammar;

import static classes.Token.Type.Semicolon;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodStatements extends GrammarRule<Boolean> {
    @Override
    public Boolean parseGrammar() throws GrammarException {

        required(new GrammarExpression(null));
        required(Semicolon);
        while (notNull(optional(new GrammarExpression(null)))) required(Semicolon);
        return true;
    }
}
