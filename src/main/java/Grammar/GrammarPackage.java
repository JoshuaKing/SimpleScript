package Grammar;

import compiler.SymbolTable;

import static classes.Token.Type.KeywordPackage;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarPackage extends GrammarRule<SymbolTable> {

    @Override
    public SymbolTable parseGrammar() throws GrammarException {
        required(KeywordPackage);
        SymbolTable table = SymbolTable.addPackage(required(GrammarName.class));
        optional(GrammarImport.class);
        required(GrammarClass.class);
        return table;
    }

    @Override
    public String getJavascript() {
        return indent("var _PACKAGE['" + nextGrammar() + "'] = {") + indent(nextGrammar()) + indent("}");
    }
}
