package Grammar;

import classes.Expression;
import classes.Variable;

import static classes.Token.Type.BitwiseAnd;
import static classes.Token.Type.BitwiseNot;
import static classes.Token.Type.BitwiseOr;
import static classes.Token.Type.BitwiseXor;
import static classes.Token.Type.OperatorDecrementBy;
import static classes.Token.Type.OperatorDivide;
import static classes.Token.Type.OperatorDivideBy;
import static classes.Token.Type.OperatorEquals;
import static classes.Token.Type.OperatorIncrementBy;
import static classes.Token.Type.OperatorMinus;
import static classes.Token.Type.OperatorModulo;
import static classes.Token.Type.OperatorModuloBy;
import static classes.Token.Type.OperatorMultiply;
import static classes.Token.Type.OperatorMultiplyBy;
import static classes.Token.Type.OperatorPlus;
import static classes.Token.Type.OperatorPower;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarDualOperator extends GrammarRule<Expression> {

    private final Variable.VarType type;

    public GrammarDualOperator(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        required(OperatorDecrementBy, OperatorIncrementBy, OperatorDivideBy, OperatorMultiplyBy, OperatorModuloBy,
                OperatorMinus, OperatorPlus, OperatorDivide, OperatorMultiply, OperatorModulo, OperatorPower,
                BitwiseAnd, BitwiseNot, BitwiseOr, BitwiseXor, OperatorEquals);
        return new Expression(Expression.Type.Expression, type);
    }
}
