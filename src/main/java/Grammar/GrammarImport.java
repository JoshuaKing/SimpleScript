package Grammar;

import compiler.SymbolTable;

import static classes.Token.Type.KeywordImport;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarImport extends GrammarRule<Boolean> {
    @Override
    public Boolean parseGrammar() throws GrammarException {
        required(KeywordImport);
        ensure(SymbolTable.importPackage(required(GrammarName.class)));
        return true;
    }
}
