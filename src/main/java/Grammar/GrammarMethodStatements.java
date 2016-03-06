package Grammar;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodStatements extends GrammarRule<Boolean> {
    @Override
    public Boolean parseGrammar() throws GrammarException {
        required(new GrammarStatement(null));
        while (notNull(optional(new GrammarStatement(null))));
        return true;
    }
}
