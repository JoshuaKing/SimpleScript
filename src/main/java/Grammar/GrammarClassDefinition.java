package Grammar;

import classes.Variable;

import static classes.Token.Type.KeywordPrivate;
import static classes.Token.Type.KeywordPublic;
import static classes.Token.Type.KeywordStatic;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarClassDefinition extends GrammarRule<Boolean> {

    @Override
    public Boolean parseGrammar() throws GrammarException {
        required(new GrammarClassVariables());
        return true;
    }

    public class GrammarClassVariables extends GrammarRule<Boolean> {

        @Override
        public Boolean parseGrammar() throws GrammarException {
            Variable.Access access = Variable.Access.Private;
            if (optional(KeywordPrivate)) access = Variable.Access.Private;
            else if (optional(KeywordPublic)) access = Variable.Access.Public;
            boolean isStatic = optional(KeywordStatic);
            required(new GrammarVariableDefinition(access, isStatic));
            return true;
        }
    }
}
