package handler;

import classes.Token;

import java.util.List;

/**
 * Created by josking on 3/3/16.
 */
public class TokenIterator {
    private final List<Token> tokens;
    private int index = 0;

    public TokenIterator(List<Token> tokens) {
        this.tokens = tokens;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Token tok() {
        return tokens.get(index);
    }

    public Token prev() {
        return tokens.get(index - 1);
    }

    public String prevText() {
        return prev().getText();
    }

    public Token.Type getType() {
        return tok().getType();
    }

    public boolean check(Token.Type type) {
        if (getType().equals(type)) {
            increment();
            return true;
        }
        return false;
    }

    public boolean checkOr(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) return true;
        }
        return false;
    }

    public Token checkAny(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) return prev();
        }
        return null;
    }

    public void increment() {
        index++;
    }
}
