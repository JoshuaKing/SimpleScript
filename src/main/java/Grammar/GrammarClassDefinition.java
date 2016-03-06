package Grammar;

import classes.Method;
import classes.Token;
import classes.Variable;
import compiler.SymbolTable;

import java.util.List;

import static classes.Token.Type.*;
import static classes.Token.Type.CloseBrace;
import static classes.Variable.VarType.fromTokenType;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarClassDefinition extends GrammarRule<Boolean> {

    @Override
    public Boolean parseGrammar() throws GrammarException {
        while (notNull(optional(new GrammarClassVariable()))) return true;
        return required(new GrammarClassMethod());
    }

    public class GrammarClassVariable extends GrammarRule<Boolean> {

        @Override
        public Boolean parseGrammar() throws GrammarException {
            Variable.Modifier modifier = Variable.Modifier.Private;
            if (optional(KeywordPrivate)) modifier = Variable.Modifier.Private;
            else if (optional(KeywordPublic)) modifier = Variable.Modifier.Public;
            boolean isStatic = optional(KeywordStatic);
            required(new GrammarVariableDefinition(modifier, isStatic));
            required(Semicolon);
            return true;
        }
    }

    public class GrammarClassMethod extends GrammarRule<Boolean> {

        @Override
        public Boolean parseGrammar() throws GrammarException {
            Token token = required(GrammarReturnTypes.class);
            String name = required(GrammarName.class);
            required(OpenParenthesis);
            if (!optional(CloseParenthesis)) {
                List<Variable> args = required(GrammarArguments.class);
                if (args == null) except("Could not retrieve parameters for method '" + name + "'", true);
                SymbolTable.getInstance().addNewScope(name);
                SymbolTable.getInstance().addToScope(args);
                SymbolTable.getInstance().addMethod(new Method(fromTokenType(token.getType()), name, args));
                required(CloseParenthesis);
            }
            required(OpenBrace);
            required(new GrammarStatement(null));
            while (notNull(optional(new GrammarStatement(null))));
            required(CloseBrace);
            return true;
        }
    }
}
