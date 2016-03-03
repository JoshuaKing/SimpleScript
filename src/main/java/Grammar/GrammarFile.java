package Grammar;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarFile extends GrammarRule<Boolean> {

    @Override
    public Boolean parseGrammar() throws GrammarException {
        required(GrammarPackage.class);
        optional(GrammarImport.class);
        required(GrammarClass.class);
        return true;
    }
}
