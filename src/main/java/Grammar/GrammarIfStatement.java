package Grammar;

import classes.Expression;
import classes.Token;

/**
 * Created by Josh on 6/03/2016.
 */
public class GrammarIfStatement extends GrammarRule<Expression> {
    @Override
    public Expression parseGrammar() throws GrammarException {
        required(Token.Type.KeywordIf);

        required(Token.Type.OpenParenthesis);

        required(new GrammarExpression(null));

        required(Token.Type.CloseParenthesis);

        required(Token.Type.OpenBrace);

        Expression expression = optional(new GrammarStatement(null));

        while (notNull(expression)) {
            expression = optional(new GrammarStatement(null));
        }

        required(Token.Type.CloseBrace);

        return expression;
    }

    @Override
    public String getJavascript() {
        String javascript = "if (" + nextGrammar() + ") {\n";
        while (hasNextGrammar()) {
            javascript += indent(nextGrammar());
        }
        return javascript + "}\n";
    }
}
