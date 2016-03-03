package Grammar;

import handler.TokenIterator;

import java.util.ArrayList;
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


    <T> T create(Class<? extends GrammarRule<T>> grammarRule) throws GrammarException {
        try {
            GrammarRule<T> grammar = grammarRule.newInstance();
            grammar.setTokens(tokens);
            T value = grammar.parseGrammar();
            grammars.add(grammar);
            return value;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
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

    protected static void except(String error) throws GrammarException {
        throw new GrammarException(error);
    }

    protected boolean check(classes.Token.Type type) throws GrammarException {
        if (!tokens.check(type)) except("Expected type '" + type.name() + "' but was '" + tokens.getType().name() + "'");
        return true;
    }

    protected boolean next(classes.Token.Type type) {
        return tokens.check(type);
    }
}
