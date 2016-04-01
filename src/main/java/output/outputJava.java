package output;

import Syntax.Symbol;
import Syntax.SyntaxBuilder;
import Syntax.SyntaxElement;
import classes.Token;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Josh on 16/03/2016.
 */
public class OutputJava extends OutputCode {
    private String code = "";
    private String filepath = "";
    private String filename = "";

    private OutputJava() {

    }

    public static OutputJava generate(SyntaxElement tree) {
        OutputJava outputJava = new OutputJava();
        outputJava.code = outputJava.handle(tree);
        return outputJava;
    }


    private String handleDefault() {
        return element.getChildren().stream().map(j -> OutputJava.generate(j).getCode()).reduce("", (a,b) -> (a + " " + b).trim());
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getFilepath() {
        return filepath;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String handleLeaf() {
        try {
            java.lang.reflect.Method method = this.getClass().getDeclaredMethod("handle" + element.getGrammar().name());
            return (String) method.invoke(this);
        } catch (Exception e) {
            return element.getValue() + " ";
        }
    }

    @Override
    public String handleInternal() {
        try {
            java.lang.reflect.Method method = this.getClass().getDeclaredMethod("handle" + element.getGrammar().name());
            return (String) method.invoke(this);
        } catch (NoSuchMethodException e) {
            return element.getChildren().stream().map(this::handle).reduce("", (a,b) -> a + b);
        } catch (InvocationTargetException e) {
            try {
                throw e.getCause();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String handlePackage() {
        String packageName = element.getAt(SyntaxBuilder.Grammar.Name);
        filepath = String.join("/", packageName.split("\\."));
        return "package generated." + packageName + ";" + getLanguageImports();
    }

    @Override
    public String handleClassDefinition() {
        filename = element.getAt(SyntaxBuilder.Grammar.Name);
        return handleDefault();
    }

    private String getLanguageImports() {
        return "import lang.objects.SimpleInteger;" +
               "import lang.objects.SimpleFloat;";
    }

    @Override
    public String handlePowerExpression() {
        if (element.getChildren().size() == 1) return generate(element.getChild(0)).getCode();
        return castToResultType(element.getResultType(), "java.lang.Math.pow(" + generate(element.getChild(0)).getCode() + ".asDouble(), " + generate(element.getChild(2)).getCode() + ".asDouble()) ");
    }

    private String castToResultType(Symbol.ResultType type, String codeToCast) {
        if (Symbol.ResultType.Integer == type) {
            return "SimpleInteger.valueOf(" + codeToCast + ")";
        } else if (Symbol.ResultType.Float == type) {
            return "SimpleFloat.valueOf(" + codeToCast + ")";
        } else {
            return codeToCast;
        }
    }

    @Override
    public String handleComparisonExpression() {
        if (element.getChildren().size() == 1) return generate(element.getChild(0)).getCode();
        if (element.getChild(1).getToken().getType().equals(Token.Type.LessThan)) {
            return generate(element.getChild(0)).getCode() + ".lesser(" + generate(element.getChild(2)).getCode() + ") ";
        }
        return generate(element.getChild(0)).getCode() + ".greater(" + generate(element.getChild(2)).getCode() + ") ";
    }

    @Override
    public String handleInteger() {
        return "(new SimpleInteger(" + element.getValue() + "))";
    }

    @Override
    public String handleFloat() {
        return "(new SimpleFloat(" + element.getValue() + "))";
    }

    @Override
    public String handleSumExpression() {
        if (element.getChildren().size() == 1) return generate(element.getChild(0)).getCode();
        return generate(element.getChild(0)).getCode() + ".plus(" + generate(element.getChild(2)).getCode() + ")";
    }

    @Override
    public String handleVariableInstantiation() {
        return handleDefault();
    }

    @Override
    public String handleVariableAssignment() {
        return handleDefault();
    }

    @Override
    public String handleVariableDeclaration() {
        return handleDefault();
    }

    @Override
    public String handleExpression() {
        return handleDefault();
    }
    @Override
    public String handleVariableType() {
        if (element.getToken().getType().equals(Token.Type.KeywordInt)) {
            return "SimpleInteger ";
        }
        return handleDefault();
    }

    @Override
    public String handleConstant() {
        return handleDefault();
    }
}
