package Grammar;

import classes.DebugException;
import classes.Token;
import handler.TokenIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Josh on 3/03/2016.
 */
public abstract class GrammarRule<T> {
    private List<GrammarRule<?>> grammars = new ArrayList<>();
    protected TokenIterator tokens;

    public void setTokens(TokenIterator tokens) {
        this.tokens = tokens;
    }

    public void GrammarRule() {
    }

    public abstract T parseGrammar() throws GrammarException;


    <R, T extends GrammarRule<R>> R required(T grammar) throws GrammarException {
        grammar.setTokens(tokens);
        R value = grammar.parseGrammar();
        grammars.add(grammar);
        return value;
    }

    <R, T extends GrammarRule<R>> R optional(T... grammars) throws GrammarException {
        for (T grammar : grammars) {
            try {
                return required(grammar);
            } catch (GrammarException e) {
                if (e.fatal) throw e;
            }
        }
        return null;
    }

    <R, T extends GrammarRule<R>> R required(Class<T>... grammarRules) throws GrammarException {
        GrammarException lastError = null;
        for (Class<T> grammarRule : grammarRules) {
            try {
                GrammarRule<R> grammar = grammarRule.newInstance();
                return required(grammar);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (GrammarException e) {
                // Catch last error to display
                lastError = e;
            }
        }
        except(lastError.getMessage(), lastError.fatal);
        return null;
    }

    <R, T extends GrammarRule<R>> R required(T... grammarRules) throws GrammarException {
        GrammarException lastError = null;
        for (T grammarRule : grammarRules) {
            try {
                return required(grammarRule);
            } catch (GrammarException e) {
                // Catch last error to display
                lastError = e;
            }
        }
        except(lastError.getMessage(), lastError.fatal);
        return null;
    }

    <R, T extends GrammarRule<R>> R optional(Class<T>... grammarRules) throws GrammarException {
        try {
            return required(grammarRules);
        } catch (GrammarException e) {
            if (e.fatal) throw e;
            return null;
        }
    }

    <R, T extends GrammarRule<R>> R test(T... grammars) throws DebugException {
        DebugException exception = new DebugException(null, 0, null);
        for (T grammar : grammars) {
            int repeat = tokens.getIndex();
            try {
                System.out.println("Testing Rule " + grammar.getClass().getSimpleName());
                R value = required(grammar);
                if (notNull(value)) return value;
            } catch (GrammarException e) {
                exception = exception.adjust(e, tokens.getIndex(), tokens.prev());
                System.out.println("Test Rule " + grammar.getClass().getSimpleName() + " failed: " + exception.getError().getMessage());
                reset(repeat);
            }
        }
        throw exception;
    }

    public T repeatable(Class<? extends GrammarRule<T>> grammarRule) throws GrammarException {
        T value = required(grammarRule);
        while ((value = optional(grammarRule)) != null);
        return value;
    }

    public void reset(int to) {
        tokens.setIndex(to);
    }

    private String toString(int level) {
        String prefix = "";
        for (int i = 0; i < level; i++) prefix += "    ";
        String result = prefix + this.getClass().getSimpleName();

        if (grammars.size() == 0) return result + "\n";
        result += " {\n";
        for (GrammarRule<?> grammar : grammars) {
            result += grammar.toString(level + 1);
        }
        return result + prefix + "}\n";
    }

    public String toString() {
        return toString(0);
    }

    protected static void except(String error, boolean fatal) throws GrammarException {
        throw new GrammarException(error, fatal);
    }

    protected static void except(DebugException error) throws GrammarException {
        throw error.getError();
    }

    protected Token required(classes.Token.Type... types) throws GrammarException {
        for (classes.Token.Type type : types) {
            if (tokens.check(type)) {
                return tokens.prev();
            }
        }

        except("Expected one of types " + Arrays.toString(types) + " but was '" + tokens.getType().name() + "'", false);
        return null;
    }

    protected boolean optional(classes.Token.Type type) {
        return tokens.check(type);
    }

    protected void ensure(boolean value) throws GrammarException {
        if (!value) except("Failed boolean test.", false);
    }

    protected <T> T ensure(T value) throws GrammarException {
        if (value == null) except("Failed non-null test.", false);
        return value;
    }

    protected <T> boolean notNull(T obj) {
        return obj != null;
    }
}
