package Grammar;

import classes.Token;
import classes.Variable;

import java.util.Arrays;
import java.util.List;

import static classes.Token.Type.Comma;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarArguments extends GrammarRule<List<Variable>> {

    @Override
    public List<Variable> parseGrammar() throws GrammarException {
        Token token = required(GrammarVariableTypes.class);
        String name = required(GrammarName.class);
        List<Variable> list = Arrays.asList(new Variable(Variable.Modifier.Argument, false, name, token.getType()));
        if (optional(Comma)) {
            token = required(GrammarVariableTypes.class);
            name = required(GrammarName.class);
            list.add(new Variable(Variable.Modifier.Argument, false, name, token.getType()));
        }
        return list;
    }

    @Override
    public String getJavascript() {
        String javascript = "";
        while (hasNextGrammar()) {
            javascript += nextGrammar() + " " + nextGrammar();
            javascript += hasNextGrammar() ? ", " : "";
        }
        return javascript;
    }
}
