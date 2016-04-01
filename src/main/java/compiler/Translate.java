package compiler;

import Syntax.SyntaxBuilder;
import Syntax.SyntaxElement;
import Syntax.ScriptVerifier;
import classes.Token;
import org.apache.commons.io.IOUtils;
import output.OutputJava;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        boolean passed = new ScriptVerifier(syntaxTree).verifyTree();
        if (!passed) {
            System.out.println("File did not verify, continuing for debugging");
        }
        System.out.println(Syntax.SymbolTable.dump());

        OutputJava javaCode = OutputJava.generate(syntaxTree);
        File output = new File("src/main/java/generated/" + javaCode.getFilepath() + "/" + javaCode.getFilename() + ".java");
        output.getParentFile().mkdirs();
        FileWriter fileWriter = new FileWriter(output);
        fileWriter.write(javaCode.getCode());
        fileWriter.close();

        System.out.println(javaCode.getCode());
    }
}
