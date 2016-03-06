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
        return SymbolTable.addPackage(required(GrammarName.class));
    }

    @Override
    public String getJavascript(int indent) {
        return indent(indent, "var " + nextGrammar().getJavascript(0) + " = {\n" + nextGrammar().getJavascript(indent + 1) + "}");
    }
}
