package Grammar;

import compiler.VariableTable;

import static classes.Token.Type.KeywordPackage;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarPackage extends GrammarRule<VariableTable> {

    @Override
    public VariableTable parseGrammar() throws GrammarException {
        required(KeywordPackage);
        return VariableTable.addPackage(required(GrammarName.class));
    }
}
