package classes;

import Grammar.GrammarException;

/**
 * Created by josking on 3/4/16.
 */
public class DebugException extends Throwable {
    private final GrammarException error;
    private final int index;
    private Token token;

    public DebugException(GrammarException error, int index, Token token) {
        this.error = error;
        this.index = index;
        this.token = token;
        if (error != null) addSuppressed(error);
    }

    public DebugException adjust(GrammarException e, int loc, Token t) {
        if (index > loc) return this;
        return new DebugException(e, loc, t);
    }

    public DebugException adjust(DebugException e) {
        if (e == null) return this;
        if (index > e.index) return this;
        return e;
    }

    public GrammarException getError() {
        return error;
    }
}
