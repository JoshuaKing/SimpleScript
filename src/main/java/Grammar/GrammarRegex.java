package Grammar;

import Syntax.SyntaxElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Josh on 7/03/2016.
 */
public class GrammarRegex {
    public enum Grammar {
        Name("[a-zA-Z][a-zA-Z0-9.]*", true),
        String("\".*?\"|\'.*?\'", true),
        Float("\\d+[.]\\d+", true),
        Integer("\\d+", true),
        Boolean("true|false", true),
        Anything(".*", true),
        Anything2("[^}]*", true),
        Anything3("[^)]*", true),
        File("Package Import* ClassDefinition"),
        Package("'package' Name ';'"),
        Import("'import' Name ';'"),
        ClassDefinition("'class' Name '{' [Field,Method]+ '}'"),
        Field("['public','private'] VariableDeclaration Assignment? ';'"),
        Method("ReturnType Name '(' Arguments ')' '{' Statement* '}'"),
        Arguments("VariableDeclaration {VariableDeclaration}*"),
        CommaArgument("',' Arguments"),
        ReturnType("VariableType | 'void'"),
        VariableType("'int' | 'float' | 'string' | 'map' | 'list' | 'boolean'"),
        Assignment("'=' Constant"),
        Expression("'(' Expression ')' | '!'? Value ExtensionExpression?"),
        ExtensionExpression("SoloOperator | [DualOperator,Comparator] Expression"),
        Value("Constant | MethodCall | Variable"),
        Constant("String | Float | Integer | Boolean"),
        Variable("Name"),
        VariableDeclaration("VariableType Name"),
        SoloOperator("'++' | '--'"),
        DualOperator("'+' | '-' | '*' | '/' | '&' | '^' | '~'"),
        Comparator("'==' | '&&' | '||' | '!=' | '<' | '>'"),
        MethodCall("Name '(' Parameters? ')'"),
        Parameters("Expression {Expression}*"),
        Statement("GeneralStatement | Expression ';' | 'return' Expression ';'"),
        GeneralStatement("['if','for','while'] '(' Expression ')' '{' Statement+ '}'");

        private final boolean isRegex;
        String grammar;

        Grammar(String grammar) {
            this(grammar, false);
        }

        Grammar(String grammar, boolean isRegex) {
            this.isRegex = isRegex;
            this.grammar = grammar;
        }

        static Grammar of(String grammar) {
            for (Grammar g : Grammar.values()) {
                if (g.name().equals(grammar)) return g;
            }
            return null;
        }
    }

    private static String[] splitIntoOptions(Grammar grammar) {
        return grammar.grammar.split("\\|");
    }

    private static String[] splitIntoParts(String grammar) {
        return grammar.trim().split(" ");
    }

    private static String match(String grammar, String code, SyntaxElement syntaxElement) {
        int min = 1;
        int max = 1;

        if (grammar.endsWith("*")) {
            min = 0;
            max = Integer.MAX_VALUE;
            grammar = grammar.substring(0, grammar.length() - 1);
        } else if (grammar.endsWith("?")) {
            min = 0;
            max = 1;
            grammar = grammar.substring(0, grammar.length() - 1);
        } else if (grammar.endsWith("+")) {
            min = 1;
            max = Integer.MAX_VALUE;
            grammar = grammar.substring(0, grammar.length() - 1);
        }

        int i = 0;
        for (; i < max; i++) {
            String c = code;
            c = matchAny(grammar, c, syntaxElement);
            if (c == null) {
                if (i >= min) {
                    return code;
                }
                return null;
            }
            code = c;
        }
        if (i < min) return null;

        return code;
    }

    private static String matchAny(String grammar, String code, SyntaxElement syntaxElement) {
        if (grammar.matches("^\\[.*,.*\\]$")) {
            grammar = trimCharacter(grammar);
        } else {
            return matchCsv(grammar, code, syntaxElement);
        }
        String[] grammars = grammar.split(",");
        for (String g : grammars) {
            String c = matchCsv(g, code, syntaxElement);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    private static String matchCsv(String grammar, String code, SyntaxElement syntaxElement) {
        if (grammar.matches("^\\{.*\\}$")) {
            grammar = trimCharacter(grammar);
            if ((code = matchOne("','", code, syntaxElement)) == null) return null;
            return matchNodeGrammar(Grammar.of(grammar), code, syntaxElement);
        } else {
            return matchOne(grammar, code, syntaxElement);
        }
    }

    private static String matchOne(String grammar, String code, SyntaxElement syntaxElement) {
        String noQuotes = trimCharacter(grammar);

        if (grammar.matches("^'.*'$") && code.startsWith(noQuotes)) {
            syntaxElement.addNode(new SyntaxElement(syntaxElement, grammar, noQuotes, true));
            System.out.println("Added Node " + noQuotes + " to parent = " + syntaxElement.getValue());
            return shift(code, noQuotes.length());
        }

        Grammar g = Grammar.of(grammar);

        if (g == null) return null;

        if (g.isRegex) return matchLeaf(Grammar.of(grammar), code, syntaxElement);

        return matchNodeGrammar(g, code, syntaxElement);
    }

    private static String matchLeaf(Grammar grammar, String code, SyntaxElement syntaxElement) {
        if (grammar == null) return null;
        Matcher m = Pattern.compile("^" + grammar.grammar).matcher(code);
        if (m.find()) {
            System.out.println("Added Leaf " + m.group() + " to parent = " + syntaxElement.getValue());
            syntaxElement.addNode(new SyntaxElement(syntaxElement, grammar.name(), m.group(), true));
            return shift(code, m.group().length());
        }
        return null;
    }

    private static String shift(String code, int length) {
        return code.substring(length).replaceFirst("^\\W", "");
    }

    private static String matchNodeGrammar(Grammar grammar, String code, SyntaxElement syntaxElement) {
        if (grammar == null) return null;

        SyntaxElement el = new SyntaxElement(syntaxElement, grammar.name(), grammar.name(), false);
        if (grammar.isRegex) {
            String c = matchLeaf(grammar, code, el);
            if (c == null) return null;
            syntaxElement.addFrom(el);
            return c;
        }
        String[] grammars = splitIntoOptions(grammar);

        for (String g : grammars) {
            String[] parts = splitIntoParts(g);
            System.out.println("Trying '" + g.trim() + "': " + grammar.name());

            boolean matchesOption = true;
            String c = code;
            for (String part : parts) {
                if ((c = match(part, c, el)) == null) {
                    matchesOption = false;
                    break;
                }
            }

            if (matchesOption) {
                syntaxElement.addNode(el);
                System.out.println("Matched " + g);
                return c;
            }
        }
        return null;
    }

    private static String trimCharacter(String grammar) {
        if (grammar.length() > 2) return grammar.substring(1, grammar.length() - 1);
        return grammar;
    }

    public static SyntaxElement parse(String reformated) throws GrammarException {
        SyntaxElement root = new SyntaxElement(null, "/", "ROOT", false);
        matchNodeGrammar(Grammar.File, reformated, root);
        return root;
    }
}
