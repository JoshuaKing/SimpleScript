package translator;

import handler.TokenIterator;
import translator.Token.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static translator.Token.Type.BitwiseAnd;
import static translator.Token.Type.BitwiseNot;
import static translator.Token.Type.BitwiseOr;
import static translator.Token.Type.BitwiseXor;
import static translator.Token.Type.BooleanAnd;
import static translator.Token.Type.BooleanEquals;
import static translator.Token.Type.BooleanNot;
import static translator.Token.Type.BooleanNotEquals;
import static translator.Token.Type.BooleanOr;
import static translator.Token.Type.CloseBrace;
import static translator.Token.Type.CloseParenthesis;
import static translator.Token.Type.Comma;
import static translator.Token.Type.ConstFloat;
import static translator.Token.Type.ConstInteger;
import static translator.Token.Type.ConstString;
import static translator.Token.Type.Dot;
import static translator.Token.Type.KeywordBoolean;
import static translator.Token.Type.KeywordClass;
import static translator.Token.Type.KeywordFalse;
import static translator.Token.Type.KeywordFloat;
import static translator.Token.Type.KeywordFor;
import static translator.Token.Type.KeywordIf;
import static translator.Token.Type.KeywordImport;
import static translator.Token.Type.KeywordInt;
import static translator.Token.Type.KeywordPackage;
import static translator.Token.Type.KeywordPrivate;
import static translator.Token.Type.KeywordPublic;
import static translator.Token.Type.KeywordStatic;
import static translator.Token.Type.KeywordString;
import static translator.Token.Type.KeywordTrue;
import static translator.Token.Type.KeywordVoid;
import static translator.Token.Type.Name;
import static translator.Token.Type.OpenBrace;
import static translator.Token.Type.OpenParenthesis;
import static translator.Token.Type.OperatorDecrement;
import static translator.Token.Type.OperatorDecrementBy;
import static translator.Token.Type.OperatorDivide;
import static translator.Token.Type.OperatorDivideBy;
import static translator.Token.Type.OperatorEquals;
import static translator.Token.Type.OperatorIncrement;
import static translator.Token.Type.OperatorIncrementBy;
import static translator.Token.Type.OperatorMinus;
import static translator.Token.Type.OperatorModulo;
import static translator.Token.Type.OperatorModuloBy;
import static translator.Token.Type.OperatorMultiply;
import static translator.Token.Type.OperatorMultiplyBy;
import static translator.Token.Type.OperatorPlus;
import static translator.Token.Type.OperatorPower;
import static translator.Token.Type.Semicolon;
import static translator.Variable.VarType.fromTokenType;

/**
 * Created by josking on 3/2/16.
 */
public class Parser {
    TokenIterator tokens;

    List<Token> tree;
    Map<String, Object> rootTable = new HashMap<>();

    public Parser(List<Token> tokens) {
        this.tokens = new TokenIterator(tokens);
        tree = new ArrayList<>();
        while (this.tokens.getIndex() < tokens.size()) {
            if (!handleToken()) this.tokens.increment();
        }
    }

    public List<Token> getTree() {
        return tree;
    }

    private boolean handleToken() {
        Type t = tokens.getType();
        if (check(KeywordPackage)) return handlePackage();
        if (check(KeywordImport)) return handleImport();
        if (check(KeywordClass)) return handleClass();
        return false;
    }

    // 'class' Name '{' ClassStatements '}'
    private boolean handleClass() {
        if (!check(Name)) return false;
        VariableTable.getInstance().addScope(tokens.prevText());
        if (!check(OpenBrace)) return false;
        if (!handleClassStatements()) return false;
        if (!check(CloseBrace)) return false;
        return true;
    }

    // ClassVariableDefinition | ClassMethod
    private boolean handleClassStatements() {
        while (true) {
            int repeat = tokens.getIndex();
            if (handleClassVariableDefinitions()) continue;
            tokens.setIndex(repeat);
            if (handleClassMethods()) continue;
            return false;
        }
    }

    // ReturnType Name '(' Arguments ')' '{' MethodStatements '}'
    private boolean handleClassMethods() {
        Type returnType = handleReturnType();
        if (returnType == null) return false;
        if (!check(Name)) return false;
        String name = tokens.prevText();
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
        while (handleKeywordStatement() || handleExpression(null)) ever = true;
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

    // Type Name [, Type Name]*
    private List<Variable> handleArguments() {
        Type type = handleType();
        if (type == null) return null;
        if (!check(Name)) return null;
        String name = tokens.prevText();
        List<Variable> list = Arrays.asList(new Variable(Variable.Access.Argument, false, name, type));
        if (check(Comma)) {
            List<Variable> append = handleArguments();
            if (append != null) list.addAll(append);
            else return null;
        }
        return list;
    }

    private Type handleReturnType() {
        if (checkOr(KeywordInt, KeywordFloat, KeywordBoolean, KeywordString, KeywordVoid)) return tokens.prev().getType();
        return null;
    }

    private Type handleType() {
        if (checkOr(KeywordInt, KeywordFloat, KeywordBoolean, KeywordString)) return tokens.prev().getType();
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
        String name = handleLongName();
        if (name == null) return false;
        VariableTable.getInstance().importPackage(name);
        return true;
    }

    private boolean handlePackage() {
        String name = handleLongName();
        if (name == null) return false;
        VariableTable.addPackage(name);
        return true;
    }

    // '='  { Expression }
    private boolean handleAssignment(Variable.VarType type) {
        if (!check(OperatorEquals)) return false;
        return handleExpression(type);
    }

    // { 'int', 'float', 'string', 'boolean' } Name Assignment ';'
    private boolean handleVariableDefine(Variable.Access access, boolean isStatic) {
        Token token = checkAny(KeywordInt, KeywordBoolean, KeywordFloat, KeywordString);
        if (token == null) return false;
        if (!check(Name)) return false;
        Variable variable = new Variable(access, isStatic, tokens.prevText(), token.getType());

        if (check(Semicolon)) {
            VariableTable.getInstance().addToScope(variable);
            return true;
        }
        if (!handleAssignment(variable.getType())) return false;
        variable.setValue(tokens.prevText());
        VariableTable.getInstance().addToScope(variable);
        return check(Semicolon);
    }

    // 'if' '(' Expression ')' { Statement , '{' Statements '}' }
    private boolean handleIfStatement() {
        if (!check(OpenParenthesis)) return false;
        if (!handleExpression(Variable.VarType.Boolean)) return false;
        if (!check(CloseParenthesis)) return false;
        return true;
    }

    private boolean check(Type expected) {
        return tokens.check(expected);
    }

    private boolean checkOr(Type... expected) {
        return tokens.checkOr(expected);
    }

    private Token checkAny(Type... expected) {
        return tokens.checkAny(expected);
    }

    // { '(' Expression ')', Statement [Comparator Statement]* }
    private boolean handleExpression(Variable.VarType type) {
        if (check(OpenParenthesis)) {
            return handleExpression(type) && check(CloseParenthesis);
        }
        if (!handleStatement(type)) return false;
        while (handleComparator()) {
            if (!handleStatement(type)) return false;
        }
        return true;
    }

    // { '&&', '||', '==', '!=' }
    private boolean handleComparator() {
        return checkOr(BooleanAnd, BooleanOr, BooleanEquals, BooleanNotEquals);
    }

    //  FunctionCall | InstanceCreation | Constant | Variable | Variable SoloOperator | [!] Variable { DualOperator, Comparator} Expression
    private boolean handleStatement(Variable.VarType type) {
        if (handleMethodCall(type)) return true;
        //TODO: if (handleInstanceCreation()) return true;
        if (handleConstant(type) != null) return true;
        if (handleVariable() != null) return true;
        if (handleSoloOperator()) return true;
        check(BooleanNot);
        if (!handleDualOperator() && !handleComparator()) return false;
        return handleExpression(type);
    }

    // LongName '(' Parameters ')'
    private boolean handleMethodCall(Variable.VarType type) {
        String name = handleLongName();
        if (name == null) return false;
        if (!check(OpenParenthesis)) return false;
        Method method = VariableTable.getInstance().getMethod(name);
        if (method == null) return false;
        if (type != null && !method.getReturnType().equals(type)) {
            System.err.println("Expecting method " + method.getName() + " to return '" + type.name() + "' but returns type '" + method.getReturnType().name() + "'");
            return false;
        }
        System.out.println("Method call to " + method.getName());
        if (!handleParameters(method.arguments)) return false;
        return true;
    }

    // { Name '.' }* Name
    private String handleLongName() {
        if (!check(Name)) return null;
        String name = tokens.prevText();
        while (check(Dot)) {
            if (!check(Name)) return null;
            name += '.' + tokens.prevText();
        }
        return name;
    }

    private boolean handleParameters(List<Variable> parameters) {
        int i = 0;
        for (Variable v : parameters) {
            Token constants = handleConstant(v.getType());
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
    private Token handleConstant(Variable.VarType type) {
        Token token = checkAny(KeywordTrue, KeywordFalse, ConstFloat, ConstInteger, ConstString);
        if (type != null && token != null && !type.equals(Variable.VarType.fromTokenType(token.getType()))) {
            Variable.VarType varType = Variable.VarType.fromTokenType(token.getType());
            System.err.println("Expecting variable to be assigned type '" + type.name() + "' but was assigned a constant (" + token.getText() + ") of type '" + varType.name()+ "'");
            return null;
        }
        return token;
    }

    // Name [ '.' Name ]*
    private Variable handleVariable() {
        String name = handleLongName();
        if (name == null) return null;

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
