package translator;

import translator.Token.Type;

import java.util.*;

import static translator.Token.Type.*;
import static translator.Variable.VarType.fromTokenType;

/**
 * Created by josking on 3/2/16.
 */
public class GrammarTree {
    int index = 0;
    List<Token> tokens;

    List<Token> tree;
    Map<String, Object> rootTable = new HashMap<>();

    public GrammarTree (List<Token> tokens) {
        this.tokens = tokens;
        tree = new ArrayList<>();
        for (; index < this.tokens.size();) {
            if (!handleToken()) index++;
        }
    }

    public List<Token> getTree() {
        return tree;
    }

    private boolean handleToken() {
        Type t = tokens.get(index).getType();
        if (check(KeywordPackage)) return handlePackage();
        if (check(KeywordImport)) return handleImport();
        if (check(KeywordClass)) return handleClass();
        return false;
    }

    // 'class' Name '{' ClassStatements '}'
    private boolean handleClass() {
        if (!check(Name)) return false;
        VariableTable.getInstance().addScope(prev().getText());
        if (!check(OpenBrace)) return false;
        if (!handleClassStatements()) return false;
        if (!check(CloseBrace)) return false;
        return true;
    }

    private boolean handleClassStatements() {
        int repeat = index;
        if (!handleClassVariableDefinitions()) {
            index = repeat;
            if (!handleClassMethods()) return false;
        } else {
            if (!handleClassMethods()) return false;
        }
        return true;
    }

    // ReturnType Name '(' Arguments ')' '{' MethodStatements '}'
    private boolean handleClassMethods() {
        Type returnType = handleReturnType();
        if (returnType == null) return false;
        if (!check(Name)) return false;
        String name = prev().getText();
        if (!check(OpenParenthesis)) return false;
        if (!check(CloseParenthesis)) {
            List<Variable> args = handleArguments();
            if (args == null) return false;
            VariableTable.getInstance().addScope(name);
            VariableTable.getInstance().addToScope(args);
            VariableTable.getInstance().addMethod(new Method(fromTokenType(returnType), name, args));
            if (!check(CloseParenthesis)) return false;
        }
        if (!check(OpenBrace)) return false;
        if (!handleMethodStatements()) return false;
        if (!check(CloseBrace)) return false;
        return true;
    }

    // { KeyWordStatement, Command }+
    private boolean handleMethodStatements() {
        boolean ever = false;
        while (handleKeywordStatement() || handleExpression()) ever = true;
        return ever;
    }

    // Keyword '(' Expression ')' '{' Commands '}'
    private boolean handleKeywordStatement() {
        Token token = checkAny(KeywordIf, KeywordFor);
        if (token == null) return false;
        switch(token.getType()) {
            case KeywordIf:
                return handleIfStatement();
            case KeywordFor:
                //return handleFor;
                break;
            default:
                break;
        }
        return false;
    }

    private Token prev() {
        return tokens.get(index - 1);
    }

    // Type Name [, Type Name]*
    private List<Variable> handleArguments() {
        Type type = handleType();
        if (type == null) return null;
        if (!check(Name)) return null;
        String name = prev().getText();
        List<Variable> list = Arrays.asList(new Variable(Variable.Access.Argument, false, name, type));
        if (check(Comma)) {
            List<Variable> append = handleArguments();
            if (append != null) list.addAll(append);
            else return null;
        }
        return list;
    }

    private Type handleReturnType() {
        if (checkOr(KeywordInt, KeywordFloat, KeywordBoolean, KeywordString, KeywordVoid)) return prev().getType();
        return null;
    }

    private Type handleType() {
        if (checkOr(KeywordInt, KeywordFloat, KeywordBoolean, KeywordString)) return prev().getType();
        return null;
    }

    // [{ public, private }] [static] Assignment
    private boolean handleClassVariableDefinitions() {
        Variable.Access access = Variable.Access.Private;
        if (check(KeywordPrivate)) access = Variable.Access.Private;
        else if (check(KeywordPublic)) access = Variable.Access.Public;
        boolean isStatic = check(KeywordStatic);
        return handleVariableDefine(access, isStatic);
    }

    private boolean handleImport() {
        if (!check(Name)) return false;
        String name = prev().getText();
        while (check(Dot)) {
            if (!check(Name)) return false;
            name += '.' + prev().getText();
        }
        VariableTable.getInstance().importPackage(name);
        return true;
    }

    private boolean handlePackage() {
        if (!check(Name)) return false;
        String name = prev().getText();
        while (check(Dot)) {
            if (!check(Name)) return false;
            name += '.' + prev().getText();
        }
        VariableTable.addPackage(name);
        return true;
    }

    private Variable handleInt(Variable.Access access, boolean isStatic) {
        if (!check(Name)) return null;
        return new Variable(access, isStatic, prev().getText(), 0, Variable.VarType.Integer);
    }

    // {'=', '+=', '-=', '*=', '/=', '**', '&', '|', '~', '^'} { Expression } | {'++','--'}
    private boolean handleAssignment(Variable variable) {
        if (!check(OperatorEquals)) return false;
        if (checkOr(ConstInteger, ConstFloat)) {
            variable.setValue(prev().getText());
        } else if (variable.getVarType().equals(Variable.VarType.String)) {
            variable.setValue(prev().getText());
        } else if (variable.getVarType().equals(Variable.VarType.Boolean)) {
            variable.setValue(prev().getText());
        } else {
            return false;
        }
        return true;
    }

    // { 'int', 'float', 'string' } Name Assignment ';'
    private boolean handleVariableDefine(Variable.Access access, boolean isStatic) {
        Token token = checkAny(KeywordInt, KeywordBoolean, KeywordFloat, KeywordString);
        if (token == null) return false;
        if (!check(Name)) return false;
        Variable variable = new Variable(access, isStatic, prev().getText(), token.getType());

        if (check(Semicolon)) {
            VariableTable.getInstance().addToScope(variable);
            return true;
        }
        if (!handleAssignment(variable)) return false;
        VariableTable.getInstance().addToScope(variable);
        return check(Semicolon);
    }

    // 'if' '(' Expression ')' { Statement , '{' Statements '}' }
    private boolean handleIfStatement() {
        if (!check(OpenParenthesis)) return false;
        if (!handleExpression()) return false;
        if (!check(CloseParenthesis)) return false;
        return true;
    }

    private boolean check(Type expected) {
        while (tokens.get(index).getType().equals(WhiteSpace) || tokens.get(index).getType().equals(LineComment)) index++;
        if (tokens.get(index).getType().equals(expected)) {
            tree.add(tokens.get(index));
            index++;
            return true;
        }
        return false;
    }

    private boolean checkOr(Type... expected) {
        while (tokens.get(index).getType().equals(WhiteSpace) || tokens.get(index).getType().equals(LineComment)) index++;
        for (Type exp : expected) {
            if (tokens.get(index).getType().equals(exp)) {
                tree.add(tokens.get(index));
                index++;
                return true;
            }
        }
        return false;
    }

    private Token checkAny(Type... expected) {
        while (tokens.get(index).getType().equals(WhiteSpace) || tokens.get(index).getType().equals(LineComment)) index++;
        for (Type exp : expected) {
            if (tokens.get(index).getType().equals(exp)) {
                tree.add(tokens.get(index));
                index++;
                return prev();
            }
        }
        return null;
    }

    // { '(' Expression ')', Statement [Comparator Statement]* }
    private boolean handleExpression() {
        if (check(OpenParenthesis)) {
            return handleExpression() && check(CloseParenthesis);
        }
        if (!handleStatement()) return false;
        while (handleComparator()) {
            if (!handleStatement()) return false;
        }
        return true;
    }

    // { '&&', '||', '==', '!=' }
    private boolean handleComparator() {
        return checkOr(BooleanAnd, BooleanOr, BooleanEquals, BooleanNotEquals);
    }

    //  FunctionCall | InstanceCreation | Constant | Variable | Variable SoloOperator | [!] Variable { DualOperator, Comparator} Statement
    private boolean handleStatement() {
        if (handleMethodCall()) return true;
        //if (handleInstanceCreation()) return true;
        if (handleConstant() != null) return true;
        if (handleVariable() != null) return true;
        if (handleSoloOperator()) return true;
        check(BooleanNot);
        if (!handleDualOperator() && !handleComparator()) return false;
        return handleStatement();
    }

    // { Name '.'}* Name '(' Parameters ')'
    private boolean handleMethodCall() {
        if (!check(Name)) return false;
        String name = prev().getText();
        while (check(Dot)) {
            if (!check(Name)) return false;
            name += '.' + prev().getText();
        }
        if (!check(OpenParenthesis)) return false;
        Method method = VariableTable.getInstance().getMethod(name);
        if (method == null) return false;
        System.out.println("Method call to " + method.getName());
        if (!handleParameters(method.arguments)) return false;
        return true;
    }

    private boolean handleParameters(List<Variable> parameters) {
        int i = 0;
        for (Variable v : parameters) {
            Token constants = handleConstant();
            Variable.VarType type = null;
            String name = "";
            if (constants != null) {
                name = constants.getText();
                type = fromTokenType(constants.getType());
            } else {
                Variable var = handleVariable();
                if (var == null) return false;
                name = var.getName();
                type = var.getType();
            }
            if (!v.getType().equals(type)) {
                System.err.println("Parameter #" + i + " with value '" + name + "' of type '" + type.name() + "' does not match Argument '" + v.getName() + "' of type '" + v.getType().name() + "'");
                return false;
            }
            i++;
        }
        return true;
    }

    // '++', '--'
    private boolean handleSoloOperator() {
        return checkOr(OperatorDecrement, OperatorIncrement);
    }

    // '-=', '+=', '/=', '*=', '%=', '-', '+', '/', '*', '%', '**', '&', '~', '|', '^', '='
    private boolean handleDualOperator() {
        return checkOr(OperatorDecrementBy, OperatorIncrementBy, OperatorDivideBy, OperatorMultiplyBy, OperatorModuloBy,
                        OperatorMinus, OperatorPlus, OperatorDivide, OperatorMultiply, OperatorModulo, OperatorPower,
                        BitwiseAnd, BitwiseNot, BitwiseOr, BitwiseXor, OperatorEquals);
    }

    // { integer, float, 'true', 'false', ".*", '.*'}
    private Token handleConstant() {
        return checkAny(KeywordTrue, KeywordFalse, ConstFloat, ConstInteger, ConstString);
    }

    // Name [ '.' Name ]*
    private Variable handleVariable() {
        if (!check(Name)) return null;
        String name = prev().getText();
        while (check(Dot)) {
            if (!check(Name)) return null;
            name += '.' + prev().getText();
        }

        Variable variable = VariableTable.getInstance().get(name);
        if (variable == null) {
            System.err.println(name + " does not exist. ");
            return null;
        } else {
            System.out.println(name + " is currently " + variable.getValue());
        }

        return variable;
    }


}
