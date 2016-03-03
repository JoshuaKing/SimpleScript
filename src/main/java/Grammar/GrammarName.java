package Grammar;

import static classes.Token.Type.Dot;
import static classes.Token.Type.Name;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarName extends GrammarRule<String> {
    @Override
    public String parseGrammar() throws GrammarException {
        String name = required(Name).getText();
        while (optional(Dot)) {
            name += '.' + required(Name).getText();
        }
        return name;
    }
}
