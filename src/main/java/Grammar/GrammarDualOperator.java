package Grammar;

import classes.Token;

import static classes.Token.Type.*;
import static classes.Token.Type.BitwiseXor;
import static classes.Token.Type.OperatorEquals;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarDualOperator extends GrammarRule<Token> {

    @Override
    public Token parseGrammar() throws GrammarException {
        return required(OperatorDecrementBy, OperatorIncrementBy, OperatorDivideBy, OperatorMultiplyBy, OperatorModuloBy,
                OperatorMinus, OperatorPlus, OperatorDivide, OperatorMultiply, OperatorModulo, OperatorPower,
                BitwiseAnd, BitwiseNot, BitwiseOr, BitwiseXor, OperatorEquals);
    }
}
