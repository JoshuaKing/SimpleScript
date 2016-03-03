package handler;

import translator.Token;

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

    public boolean isType(Token.Type type) {
        return getType().equals(type);
    }

    public void increment() {
        index++;
    }
}
