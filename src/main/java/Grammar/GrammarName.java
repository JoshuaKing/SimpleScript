package Grammar;

import static classes.Token.Type.Dot;
import static classes.Token.Type.Name;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarName extends GrammarRule<String> {
    @Override
    public String parseGrammar() throws GrammarException {
        required(Name);
        String name = tokens.prevText();
        while (optional(Dot)) {
            required(Name);
            name += '.' + tokens.prevText();
        }
        return name;
    }
}
