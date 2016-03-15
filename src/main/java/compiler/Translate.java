package compiler;

import Syntax.*;
import classes.Token;
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
        StringBuilder reformatted = new StringBuilder();
        for (Token t : tokens) {
            reformatted.append(t.getText()).append(' ');
        }
        System.out.println(reformatted.toString());

        SyntaxElement syntaxTree = SyntaxBuilder.generate(tokens);
        System.out.println(syntaxTree.toString());
        Verify.verify(syntaxTree, "test.ss");
        System.out.println(Syntax.SymbolTable.dump());
    }
}
