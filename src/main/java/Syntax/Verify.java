package Syntax;

import classes.GrammarException;

import java.util.List;

/**
 * Created by Josh on 8/03/2016.
 */
public class Verify {
    public static boolean verify(SyntaxElement tree, String filename) throws GrammarException {
        return tree.verify();
    }

    private static boolean verifyType(Symbol.ResultType type, SyntaxElement syntax, String... location) {
        return syntax.getSyntax(location).get(type.name()) != null;
    }

    protected static boolean handleVariableInstantiation(SyntaxElement syntax) throws GrammarException {
        Symbol symbol = new Symbol(syntax.get("VariableDeclaration", "Name"));
        symbol.result = Symbol.ResultType.of(syntax.get("VariableDeclaration", "VariableType"));
        if (syntax.get("Value", "Constant") != null) {
            if (verifyType(symbol.result, syntax, "Value", "Constant")) {
                System.out.println("Instantiated " + symbol.name + " to " + syntax.get("Value", "Constant"));
            } else {
                System.err.println("Error instantiating " + symbol.result.name() + " " + symbol.name + " to " + syntax.getSyntax("Value", "Constant"));
            }
        } else if (syntax.get("Value", "Variable") != null) {
            Symbol var = SymbolTable.find(syntax.get("Value", "Variable", "Name"));
            if (var == null) {
                System.err.println("Error assigning variable to non-existant variable.");
            } else if (!symbol.result.equals(var.result)) {
                System.err.println("Error instantiating " + symbol.result.name() + " " + symbol.name + " to " + var.name + " of type " + var.result.name());
            } else {
                System.out.println("Instantiated " + symbol.result.name() + " " + symbol.name + " to " + var.result.name() + " " + var.name);
            }
        }
        if (syntax.get("Value", "MethodCall") != null) return true;
        return SymbolTable.addToScope(symbol);
    }

    protected static boolean handleField(SyntaxElement syntax) throws GrammarException {
        if (!handleAll(syntax, "VariableInstantiation")) return false;
        Symbol symbol = SymbolTable.find(syntax.get("VariableInstantiation", "VariableDeclaration", "Name"));
        symbol.access = Symbol.AccessType.of(syntax.get("Access"));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Private;
        if (!SymbolTable.updateSymbol(symbol)) return false;
        return true;
    }

    protected static boolean handleAssignment(SyntaxElement syntax) throws GrammarException {
        if (!handleAll(syntax, "VariableDeclaration")) return false;
        Symbol symbol = SymbolTable.find(syntax.get("VariableDeclaration", "Name"));
        symbol.access = Symbol.AccessType.of(syntax.get("Access"));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Private;
        if (!SymbolTable.updateSymbol(symbol)) return false;
        return handleAll(syntax, "Assignment");
    }

    protected static boolean handleMethod(SyntaxElement syntax) throws GrammarException {
        Symbol symbol = new Symbol(syntax.get("Name"));
        symbol.symbol = Symbol.SymbolType.Method;
        symbol.result = Symbol.ResultType.of(syntax.get("ReturnType"));
        symbol.access = Symbol.AccessType.of(syntax.get("Access"));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Public;
        if (!SymbolTable.addToScope(symbol)) return false;
        if (SymbolTable.newScope(syntax.get("Name")) == null) return false;
        return handleAll(syntax, "VariableDeclaration", "Statement");
    }

    protected static boolean handleStatement(SyntaxElement syntax) throws GrammarException {
        switch (syntax.index) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                Symbol symbol = SymbolTable.find(ancestor(syntax, "Method").get("Name"));
                if (symbol == null) {
                    System.err.println("Method " + ancestor(syntax, "Method").get("Name") + " not found.");
                    return false;
                }
                if (!handleAll(syntax, "CumulativeExpression")) return false;
                System.out.println("Return Statement: " +
                        symbol.result.equals(syntax.getSyntax("CumulativeExpression").getResultType()));
                break;
            default:
                break;
        }

        for (SyntaxElement el : syntax.getChildren()) {
            if (!el.verify()) return false;
        }
        return true;
    }

    protected static boolean handleCumulativeExpression(SyntaxElement syntax) throws GrammarException {
        if (!contains(syntax, "Extension")) {
            Symbol.ResultType method = SymbolTable.find(ancestor(syntax, "Method").get("Name")).result;
            return getValueType(syntax.getSyntax("Value")).equals(method);
        }
        //SyntaxElement value = recurseCumulativeExpression();

        for (SyntaxElement el : syntax.getChildren()) {
            if (!el.verify()) return false;
        }
        return true;
    }

    private static Symbol.ResultType getValueType(SyntaxElement value) throws GrammarException {
        if (contains(value, "MethodCall")) {
            return SymbolTable.find(value.get("MethodCall", "Name")).result;
        } else if (contains(value, "Constant")) {
            return value.getSyntax("Constant").getChild(0).getResultType();
        } else if (contains(value, "Variable")) {
            return SymbolTable.find(value.get("Variable", "Name")).result;
        }
        return Symbol.ResultType.None;
    }

    protected static boolean handlePackage(SyntaxElement syntax) throws GrammarException {
        return SymbolTable.newScope(syntax.get("Name")) != null;
    }

    protected static boolean handleClassDefinition(SyntaxElement syntax) throws GrammarException {
        Symbol symbol = new Symbol(syntax.get("Name"));
        symbol.symbol = Symbol.SymbolType.Class;
        symbol.result = Symbol.ResultType.None;
        symbol.access = Symbol.AccessType.Public;
        if (!SymbolTable.addToScope(symbol)) return false;
        if (SymbolTable.newScope(syntax.get("Name")) == null) return false;
        return handleAll(syntax, "Field", "Method");
    }

    private static boolean handleAll(SyntaxElement syntax, String... grammars) throws GrammarException {
        for (String grammar : grammars) {
            List<SyntaxElement> elements = syntax.getAllSyntax(grammar);
            for (SyntaxElement element : elements) {
                if (!element.verify()) return false;
            }
        }
        return true;
    }

    private static boolean contains(SyntaxElement syntax, String grammar) throws GrammarException {
        List<SyntaxElement> elements = syntax.getAllSyntax(grammar);
        for (SyntaxElement element : elements) {
            if (element.getGrammar().equals(grammar)) return true;
        }
        return false;
    }

    private static SyntaxElement ancestor(SyntaxElement syntax, String grammarName) {
        while (syntax != null) {
            if (syntax.grammar.equals(grammarName)) return syntax;
            syntax = syntax.getParent();
        }
        return null;
    }
}
