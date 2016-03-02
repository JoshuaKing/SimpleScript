package translator;

import translator.Token.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static translator.Token.Type.ArithmeticEquals;
import static translator.Token.Type.BooleanAnd;
import static translator.Token.Type.BooleanEquals;
import static translator.Token.Type.BooleanNot;
import static translator.Token.Type.BooleanNotEquals;
import static translator.Token.Type.BooleanOr;
import static translator.Token.Type.CloseBrace;
import static translator.Token.Type.CloseParenthesis;
import static translator.Token.Type.Dot;
import static translator.Token.Type.KeywordStatic;
import static translator.Token.Type.TypeFloat;
import static translator.Token.Type.TypeInteger;
import static translator.Token.Type.KeywordClass;
import static translator.Token.Type.KeywordFalse;
import static translator.Token.Type.KeywordFloat;
import static translator.Token.Type.KeywordImport;
import static translator.Token.Type.KeywordInt;
import static translator.Token.Type.KeywordPackage;
import static translator.Token.Type.KeywordPrivate;
import static translator.Token.Type.KeywordPublic;
import static translator.Token.Type.KeywordString;
import static translator.Token.Type.KeywordTrue;
import static translator.Token.Type.OpenBrace;
import static translator.Token.Type.OpenParenthesis;
import static translator.Token.Type.Semicolon;
import static translator.Token.Type.Variable;
import static translator.Token.Type.WhiteSpace;

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

    // 'class' Variable '{' ClassAssignment* Methods* '}'
    private boolean handleClass() {
        if (!check(Variable)) return false;
        VariableTable.getInstance().addClass(tokens.get(index - 1).getText());
        if (!check(OpenBrace)) return false;
        handleClassAssignments();
        //handleClassMethods();
        if (!check(CloseBrace)) return false;
        return true;
    }

    // [{ public, private }] [static] Assignment
    private boolean handleClassAssignments() {
        Var.Access access = Var.Access.Private;
        if (check(KeywordPrivate)) access = Var.Access.Private;
        else if (check(KeywordPublic)) access = Var.Access.Public;
        boolean isStatic = check(KeywordStatic);
        return handleAssignment(access, isStatic);
    }

    private boolean handleImport() {
        if (!check(Variable)) return false;
        String name = tokens.get(index - 1).getText();
        while (check(Dot)) {
            if (!check(Variable)) return false;
            name += '.' + tokens.get(index - 1).getText();
        }
        VariableTable.getInstance().importPackage(name);
        return true;
    }

    private boolean handlePackage() {
        if (!check(Variable)) return false;
        String name = tokens.get(index - 1).getText();
        while (check(Dot)) {
            if (!check(Variable)) return false;
            name += '.' + tokens.get(index - 1).getText();
        }
        VariableTable.addPackage(name);
        return true;
    }

    private Var handleInt(Var.Access access, boolean isStatic) {
        if (!check(Variable)) return null;
        return new Var(access, isStatic, tokens.get(index - 1).getText(), 0, Var.VarType.Integer);
    }

    // { 'int', 'float', 'string' } Variable [ '=' { Float, String, Integer } ] ';'
    private boolean handleAssignment(Var.Access access, boolean isStatic) {
        Var var = null;
        if (check(KeywordInt)) {
            if ((var = handleInt(access, isStatic)) == null) return false;
        } else if (check(KeywordFloat)) {
            if ((var = handleInt(access, isStatic)) == null) return false;
        } else if (check(KeywordString)) {
            if ((var = handleInt(access, isStatic)) == null) return false;
        } else {
            return false;
        }

        if (check(Semicolon)) {
            VariableTable.getInstance().addToScope(var);
            return true;
        }
        if (!check(ArithmeticEquals)) return false;
        if (var.varType.equals(Var.VarType.Integer) && check(TypeInteger)) {
            var.value = Integer.valueOf(tokens.get(index - 1).getText());
        } else if (var.getVarType().equals(Var.VarType.Float) && !check(TypeFloat)) {
            return false;
        } else if (var.getVarType().equals(Var.VarType.String)) {
            return false;
        } else {
            return false;
        }
        VariableTable.getInstance().addToScope(var);
        return check(Semicolon);
    }

    // 'if' '(' Expression ')' { Statement , '{' Statements '}' }
    private boolean handleIf() {
        if (!check(OpenParenthesis)) return false;
        if (!handleExpression()) return false;
        if (!check(CloseParenthesis)) return false;
        return true;
    }

    private boolean check(Type expected) {
        while (tokens.get(index).getType().equals(WhiteSpace)) index++;
        if (tokens.get(index).getType().equals(expected)) {
            tree.add(tokens.get(index));
            index++;
            return true;
        }
        return false;
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
        return (check(BooleanAnd) || check(BooleanOr) || check(BooleanEquals) || check(BooleanNotEquals));
    }

    // { '!' Value, '(' Statement ')', Value }
    private boolean handleStatement() {
        if (check(BooleanNot)) return handleValue();
        if (check(OpenParenthesis)) {
            if (!handleStatement()) return false;
            check(CloseParenthesis);
        }
        return handleValue();
    }
    
    // { Eval, Number, 'true', 'false' }
    private boolean handleValue() {
        return (check(KeywordTrue) || check(KeywordFalse) || check(TypeFloat) || check(TypeInteger) || handleVariable() != null);
    }

    // Variable [ '.' Variable ]*
    private Var handleVariable() {
        if (!check(Variable)) return null;
        String name = tokens.get(index - 1).getText();
        while (check(Dot)) {
            if (!check(Variable)) return null;
            name += '.' + tokens.get(index - 1).getText();
        }

        Var var = VariableTable.getInstance().get(name);
        if (var == null) {
            System.err.println(name + " does not exist. ");
            return null;
        } else {
            System.out.println(name + " is currently " + var.getValue());
        }

        return var;
    }


}
