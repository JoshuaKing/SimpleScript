package Syntax;

import Grammar.GrammarException;

import java.util.List;

/**
 * Created by Josh on 8/03/2016.
 */
public class Verify {
    public static boolean verify(SyntaxElement tree, String filename) {
        try {
            return tree.verify();
        } catch (GrammarException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    protected static boolean handleVariableDeclaration(SyntaxElement syntax) throws GrammarException {
        Symbol symbol = new Symbol(syntax.get("Name"));
        symbol.result = Symbol.ResultType.of(syntax.get("VariableType"));
        return SymbolTable.addToScope(symbol);
    }

    protected static boolean handleField(SyntaxElement syntax) throws GrammarException {
        if (!handleAll(syntax, "VariableDeclaration")) return false;
        Symbol symbol = SymbolTable.find(syntax.get("VariableDeclaration", "Name"));
        symbol.access = Symbol.AccessType.of(syntax.get("Access"));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Private;
        if (!SymbolTable.updateSymbol(symbol)) return false;
        return handleAll(syntax, "Assignment");
    }

    protected static boolean handleAssignment(SyntaxElement syntax) throws GrammarException {
        if (!handleAll(syntax, "VariableDeclaration")) return false;
//        Symbol symbol = SymbolTable.find(syntax.get("VariableDeclaration", "Name"));
//        symbol.access = Symbol.AccessType.of(syntax.get("Access"));
//        if (symbol.access == null) symbol.access = Symbol.AccessType.Private;
//        if (!SymbolTable.updateSymbol(symbol)) return false;
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
}
