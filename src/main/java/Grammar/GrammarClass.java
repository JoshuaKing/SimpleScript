package Grammar;

import compiler.SymbolTable;

import static classes.Token.Type.CloseBrace;
import static classes.Token.Type.KeywordClass;
import static classes.Token.Type.OpenBrace;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarClass extends GrammarRule<Boolean> {
    @Override
    public Boolean parseGrammar() throws GrammarException {
        required(KeywordClass);
        String name = required(GrammarName.class);
        ensure(SymbolTable.getInstance().addNewScope(name));

        required(OpenBrace);
        repeatable(GrammarClassDefinition.class);
        required(CloseBrace);
        return true;
    }

    @Override
    public String getJavascript() {
        String javascript = indent("var " + nextGrammar() + " = class {\n");
        while (hasNextGrammar()) javascript += indent(nextGrammar());
        javascript += indent("}\n");
        return javascript;
    }
}
