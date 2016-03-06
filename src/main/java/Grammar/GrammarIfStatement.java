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

        Expression expression;

        while (notNull(expression = optional(new GrammarStatement(null))));

        required(Token.Type.CloseBrace);

        return expression;
    }
}
