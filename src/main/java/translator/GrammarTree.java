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
import static translator.Token.Type.CloseParenthesis;
import static translator.Token.Type.Dot;
import static translator.Token.Type.Float;
import static translator.Token.Type.Integer;
import static translator.Token.Type.KeywordFalse;
import static translator.Token.Type.KeywordIf;
import static translator.Token.Type.KeywordImport;
import static translator.Token.Type.KeywordInt;
import static translator.Token.Type.KeywordPackage;
import static translator.Token.Type.KeywordTrue;
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
        for (; index < this.tokens.size(); index++) {
            if (handleToken()) index--;
        }
    }

    public List<Token> getTree() {
        return tree;
    }

    private boolean handleToken() {
        Type t = tokens.get(index).getType();
        if (check(KeywordPackage)) return handlePackage();
        if (check(KeywordImport)) return handleImport();
        if (check(KeywordIf)) return handleIf();
        if (check(KeywordInt)) return handleInt();
        return false;
    }

    private boolean handleImport() {
        if (!check(Variable)) return false;
        String name = tokens.get(index - 1).getText();
        while (check(Dot)) {
            if (!check(Variable)) return false;
            name += '.' + tokens.get(index - 1).getText();
        }
        VariableTable.importPackage(name);
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

    // 'int' Variable {';', Assignment }
    private boolean handleInt() {
        index++;

        if (!check(Variable)) return false;
        String var = tokens.get(index - 1).getText();
        VariableTable.addToScope(new Var(var, 0, Var.VarType.Integer));
        return check(Semicolon) || handleAssignment(VariableTable.get(var));
    }

    // '=' { Float, String, Integer } ';'
    private boolean handleAssignment(Var var) {
        if (check(ArithmeticEquals)) return false;
        if (var.equals(Var.VarType.Integer) && !check(Integer)) {
            var.value = Integer.valueOf(tokens.get(index - 1).getText());
            return false;
        } else if (var.equals(Var.VarType.Float) && !check(Float)) {
            return false;
        } else if (var.equals(Var.VarType.String)) {
            // return handleString();
        } else {
            return false;
        }
        return check(Semicolon);
    }

    // 'if' '(' Expression ')' { Statement , '{' Statements '}' }
    private boolean handleIf() {
        index++;
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
        return (check(KeywordTrue) || check(KeywordFalse) || check(Float) || check(Integer) || handleVariable() != null);
    }

    // Variable [ '.' Variable ]*
    private Var handleVariable() {
        if (!check(Variable)) return null;
        String name = tokens.get(index - 1).getText();
        while (check(Dot)) {
            if (!check(Variable)) return null;
            name += '.' + tokens.get(index - 1).getText();
        }

        Var var = VariableTable.get(name);
        if (var == null) {
            System.err.println(name + " does not exist. ");
            return null;
        }

        return var;
    }


}
