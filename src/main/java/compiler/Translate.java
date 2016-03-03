package compiler;

import Grammar.GrammarFile;
import Grammar.GrammarRule;
import classes.Token;
import handler.TokenIterator;
import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by josking on 3/2/16.
 */
public class Translate {
    public static void main(String[] args) throws Exception {
        StringWriter sw = new StringWriter(10);
        IOUtils.copy(new FileReader("files/test.ss"), sw);
        List<Token> tokens = LexicalAnalyser.tokenize(sw.toString());
        for (Token t : tokens) {
            System.out.println(t.getType().name() + " : " + t.getText());
        }
        /*Parser tree = new Parser(tokens);
        for (Token t : tree.getTree()) {
            System.out.println(t.getType().name() + " = " + t.getText());
        }*/

        GrammarRule file = new GrammarFile();
        file.setTokens(new TokenIterator(tokens));
        file.parseGrammar();
        System.out.println("Grammar =\n" + file.toString());
    }
}
