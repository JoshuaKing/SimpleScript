package Grammar;

import classes.Expression;
import classes.Token;
import classes.Variable;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarStatement extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarStatement(Variable.VarType type) {
        this.type = type;
    }

    @Override
    // MethodCall | NewInstance | { Constant, Variable } [SoloOp]
    public Expression parseGrammar() throws GrammarException {
        Expression expression = required(new GrammarExpression(type));
        required(Token.Type.Semicolon);
        return expression;
    }
}
