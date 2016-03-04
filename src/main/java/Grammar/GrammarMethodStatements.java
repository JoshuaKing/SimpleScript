package Grammar;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodStatements extends GrammarRule<Boolean> {
    @Override
    public Boolean parseGrammar() throws GrammarException {
        repeatable(new GrammarExpression(null));
        return true;
    }
}
