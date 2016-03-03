package compiler;

import Grammar.GrammarException;
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
        TokenIterator tokenIterator = new TokenIterator(tokens);
        file.setTokens(tokenIterator);
        try {
            file.parseGrammar();
        } catch (GrammarException e) {
            display(sw.toString(), tokenIterator.tok().getLineNumber(), tokenIterator.tok().getColumnNumber());
            System.out.println("Grammar =\n" + file.toString());
            throw e;
        }
        System.out.println("Grammar =\n" + file.toString());
    }

    private static void display(String file, int lineNumber, int columnNumber) {
        System.out.println("Error on line " + lineNumber + ":" + columnNumber);
        String[] lines = file.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            System.out.println(lines[i]);
            if (i == lineNumber) {
                String prefix = "";
                for (int c = 0; c < columnNumber; c++) prefix += " ";
                System.out.println(prefix + "^");
            }
        }
    }
}
