package Grammar;

import classes.Variable;

import java.util.List;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarMethodParameters extends GrammarRule<Boolean> {
    private List<Variable> arguments;

    public GrammarMethodParameters(List<Variable> arguments) {
        this.arguments = arguments;
    }

    @Override
    public Boolean parseGrammar() throws GrammarException {
        return null;
    }
}
