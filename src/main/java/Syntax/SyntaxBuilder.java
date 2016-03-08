package Syntax;


import classes.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Josh on 7/03/2016.
 */
public class SyntaxBuilder {
    public enum Grammar {
        Name("[a-zA-Z][a-zA-Z0-9.]*", true),
        String("\".*?\"|\'.*?\'", true),
        Float("\\d+[.]\\d+", true),
        Integer("\\d+", true),
        Boolean("true|false", true),
        File("Package Import* ClassDefinition"),
        Package("'package' Name ';'"),
        Import("'import' Name ';'"),
        ClassDefinition("'class' Name '{' [Field,Method]+ '}'"),
        Access("'public' | 'private'"),
        Field("Access? VariableDeclaration Assignment? ';'"),
        Method("Access? ReturnType Name '(' {VariableDeclaration}* ')' '{' Statement* '}'"),
        ReturnType("VariableType | 'void'"),
        VariableType("'int' | 'float' | 'string' | 'map' | 'list' | 'boolean'"),
        Assignment("'=' Value"),
        Expression("'(' Expression ')' | CombinationExpression Combo*"),
        CombinationExpression("SoloOperator? Value Extension*"),
        Extension("SoloOperator | [Comparator,DualOperator] Value | '?' Expression"),
        Combo("BooleanComparator CombinationExpression"),
        Value("Constant | MethodCall | Variable"),
        Constant("String | Float | Integer | Boolean"),
        Variable("Name"),
        VariableDeclaration("VariableType Name"),
        SoloOperator("'++' | '--' | '!'"),
        BooleanComparator("'&&' | '||'"),
        DualOperator("'+' | '-' | '*' | '/' | '**' | '&' | '|' | '^' | '~'"),
        Comparator("'==' | '!=' | '<' | '>'"),
        MethodCall("Name '(' {Expression}* ')'"),
        Statement("WhileStatement | IfStatement | 'return' Expression ';' | Expression ';'"),
        WhileStatement("'while' '(' Expression ')' '{' Statement+ '}'"),
        IfStatement("'if' '(' Expression ')' '{' Statement+ '}'"),
        ListSeparator("','");

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

    private static List<Token> shortestError = null;
    private static List<String> expected = new ArrayList<>();

    private static String[] splitIntoOptions(Grammar grammar) {
        return grammar.grammar.split("(?:[^'])\\|(?:[^'])");
    }

    private static String[] splitIntoParts(String grammar) {
        return grammar.trim().split(" ");
    }

    private static List<Token> match(String grammar, List<Token> tokens, SyntaxElement syntaxElement) {
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
            List<Token> tokensCopy = tokens.subList(0, tokens.size());
            tokensCopy = matchAny(grammar, tokensCopy, syntaxElement);
            if (tokensCopy == null) {
                if (i >= min) {
                    return tokens;
                }
                return null;
            }
            tokens = tokensCopy;
        }
        if (i < min) return null;

        return tokens;
    }

    private static List<Token> matchAny(String grammar, List<Token> tokens, SyntaxElement syntaxElement) {
        if (grammar.matches("^\\[.*,.*\\]$")) {
            grammar = trimCharacter(grammar);
        } else {
            return matchCsv(grammar, tokens, syntaxElement);
        }
        String[] grammars = grammar.split(",");
        for (String g : grammars) {
            List<Token> c = matchCsv(g, tokens, syntaxElement);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    private static List<Token> matchCsv(String grammar, List<Token> tokens, SyntaxElement syntaxElement) {
        if (grammar.matches("^\\{.*\\}$")) {
            grammar = trimCharacter(grammar);
            tokens = matchOne(grammar, tokens, syntaxElement);
            if (tokens == null) return null;
            List<Token> tokensCopy;
            if ((tokensCopy = matchOne(Grammar.ListSeparator.grammar, tokens, syntaxElement)) == null) {
                return tokens;
            }
            return matchNodeGrammar(Grammar.of(grammar), tokensCopy, syntaxElement);
        } else {
            return matchOne(grammar, tokens, syntaxElement);
        }
    }

    private static List<Token> matchOne(String grammar, List<Token> tokens, SyntaxElement syntaxElement) {
        String noQuotes = trimCharacter(grammar);

        if (grammar.matches("^'.*'$") && tokens.get(0).getText().equals(noQuotes)) {
            syntaxElement.addNode(new SyntaxElement(syntaxElement, grammar, noQuotes, true));
            System.out.println("Added Node " + noQuotes + " to parent = " + syntaxElement.getValue());
            return shift(tokens);
        } else if (grammar.matches("^'.*'$")) {
            if (shortestError.size() > tokens.size()) {
                shortestError = tokens;
                expected = new ArrayList<>();
            }
            if (shortestError.size() == tokens.size()) {
                if (!expected.contains(grammar)) expected.add(grammar);
            }
        }

        Grammar g = Grammar.of(grammar);

        if (g == null) return null;

        if (g.isRegex) return matchLeaf(Grammar.of(grammar), tokens, syntaxElement);

        return matchNodeGrammar(g, tokens, syntaxElement);
    }

    private static List<Token> matchLeaf(Grammar grammar, List<Token> tokens, SyntaxElement syntaxElement) {
        if (grammar == null) return null;
        Matcher m = Pattern.compile("^" + grammar.grammar + "$").matcher(tokens.get(0).getText());
        if (m.find()) {
            System.out.println("Added Leaf " + m.group() + " to parent = " + syntaxElement.getValue());
            syntaxElement.addNode(new SyntaxElement(syntaxElement, grammar.name(), m.group(), true));
            return shift(tokens);
        }

        if (shortestError.size() > tokens.size()) {
            shortestError = tokens;
            expected = new ArrayList<>();
        }
        if (shortestError.size() == tokens.size()) {
            if (!expected.contains(grammar.name())) expected.add(grammar.name());
        }

        return null;
    }

    private static List<Token> shift(List<Token> tokens) {
        return tokens.subList(1, tokens.size());
    }

    private static List<Token> matchNodeGrammar(Grammar grammar, List<Token> tokens, SyntaxElement syntaxElement) {
        if (grammar == null) return null;

        SyntaxElement el = new SyntaxElement(syntaxElement, grammar.name(), grammar.name(), false);
        if (grammar.isRegex) {
            List<Token> c = matchLeaf(grammar, tokens, el);
            if (c == null) return null;
            syntaxElement.addChildrenFrom(el);
            return c;
        }
        String[] grammars = splitIntoOptions(grammar);

        for (String g : grammars) {
            String[] parts = splitIntoParts(g);
            System.out.println("Trying '" + g.trim() + "': " + grammar.name());

            boolean matchesOption = true;
            List<Token> tokenCopy = tokens.subList(0, tokens.size());
            for (String part : parts) {
                if ((tokenCopy = match(part, tokenCopy, el)) == null) {
                    matchesOption = false;
                    break;
                }
            }

            if (matchesOption) {
                syntaxElement.addNode(el);
                System.out.println("Matched " + g);
                return tokenCopy;
            }
        }
        System.out.println(Arrays.toString(tokens.toArray()));
        return null;
    }

    private static String trimCharacter(String grammar) {
        if (grammar.length() > 2) return grammar.substring(1, grammar.length() - 1);
        return grammar;
    }

    public static SyntaxElement build(List<Token> tokens) {
        SyntaxElement root = new SyntaxElement(null, "/", "ROOT", false);
        shortestError = tokens;
        if (null == matchNodeGrammar(Grammar.File, tokens, root)) {
            System.out.println(getTokenError().getText());
            System.out.println(getError());
        }
        return root;
    }

    public static Token getTokenError() {
        return shortestError.get(0);
    }

    public static String getError() {
        return "Expected one of " + Arrays.toString(expected.toArray()) + " but was " + getTokenError().getText();
    }
}
