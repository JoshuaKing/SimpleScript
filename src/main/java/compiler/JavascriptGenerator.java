package compiler;

import Grammar.GrammarRule;
import classes.Token;

import java.util.List;

/**
 * Created by Josh on 7/03/2016.
 */
public class JavascriptGenerator {
    public static String parseGrammar(GrammarRule grammarTree) {
        String javascript = "";
        List<Object> syntaxTree = grammarTree.getSyntaxTree();
        for (Object syntax : syntaxTree) {
            if (syntax.getClass().isAssignableFrom(Token.class)) {
                javascript += ((Token) syntax).getText() + " ";
            } else {
                javascript += syntax.getClass().getSimpleName() + "{{ " + JavascriptGenerator.parseGrammar((GrammarRule) syntax) + " }}";
            }
        }
        return javascript;
    }
}
