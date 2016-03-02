package translator;

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
import static translator.Token.Type.Float;
import static translator.Token.Type.Integer;
import static translator.Token.Type.KeywordFalse;
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
    Map<String, Object> currentTable = new HashMap<>();

    public GrammarTree (List<Token> tokens) {
        this.tokens = tokens;
        tree = new ArrayList<>();
        rootTable.put("root", currentTable);
        for (; index < this.tokens.size(); index++) {
            handleToken();
        }
    }

    public List<Token> getTree() {
        return tree;
    }

    private void handleToken() {
        switch (tokens.get(index).getType()) {
            case KeywordIf:
                handleIf();
                break;
            case KeywordClass:
                break;
            case KeywordInt:
                handleInt();
            default:
                break;
        }
    }

    // 'int' Variable {';', Assignment }
    private boolean handleInt() {
        index++;

        if (!check(Variable)) return false;
        String var = tokens.get(index - 1).getText();
        currentTable.put(var, new Var(var, 0, Var.VarType.Integer));
        return check(Semicolon) || handleAssignment((Var) currentTable.get(var));
    }

    // '=' { Float, String, Integer } ';'
    private boolean handleAssignment(Var var) {
        if (check(ArithmeticEquals)) return false;
        if (var.equals(Var.VarType.Integer) && !check(Integer)) {
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
        return true;
    }

    private boolean check(Token.Type expected) {
        while (tokens.get(index).getType().equals(WhiteSpace)) index++;
        if (tokens.get(index).getType().equals(expected)) {
            index++;
            tree.add(tokens.get(index));
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

    // { '!' Statement, Statement, Value }
    private boolean handleStatement() {
        if (check(BooleanNot)) return handleStatement();
        if (check(OpenParenthesis)) {
            if (!handleStatement()) return false;
            check(CloseParenthesis);
        }
        return handleValue();
    }
    
    // { Eval, Number, 'true', 'false' }
    private boolean handleValue() {
        return (check(KeywordTrue) || check(KeywordFalse) || check(Float) || handleEval());
    }

    private boolean handleEval() {
        return false;
    }


}
