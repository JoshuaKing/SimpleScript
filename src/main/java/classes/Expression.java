package classes;

/**
 * Created by josking on 3/4/16.
 */
public class Expression {

    public enum Type {
        Constant,
        Expression
    }

    Type expression;
    Variable.VarType result;

    public Expression(Type expression, Variable.VarType result) {
        this.expression = expression;
        this.result = result;
    }

    public boolean isConstant() {
        return expression == Type.Constant;
    }

    public Variable.VarType getVariableType() {
        return result;
    }
}
