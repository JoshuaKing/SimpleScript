package Syntax;

import Syntax.SyntaxBuilder.Grammar;
import classes.Token;

import java.util.List;

import static Syntax.SyntaxBuilder.Grammar.*;
import static Syntax.SyntaxBuilder.Grammar.Package;

/**
 * Created by Josh on 8/03/2016.
 */
public class Verify {
    private static boolean passed = true;

    public static boolean verify(SyntaxElement tree, String filename) {
        tree.verify();
        return passed;
    }

    private static boolean isConstantType(Symbol.ResultType type, SyntaxElement syntax) {
        return syntax.getAt(Grammar.of(type.name())) != null;
    }

    public static boolean syntaxAssert(Boolean result, SyntaxElement errorElement, String errorString) {
        if (!result) {
            passed = false;
            Token token = errorElement.getToken();
            System.err.println("Syntax Error [" + token.getLineNumber() + ":" + token.getColumnNumber() + "]:" + errorString);
        }
        return result;
    }

    protected static void handleVariableInstantiation(SyntaxElement syntax) {
        handleAll(syntax, VariableDeclaration);
        String name = syntax.getAt(VariableDeclaration, Name);
        Symbol symbol = SymbolTable.find(name);

        if (syntax.getAt(Value, Constant) != null) {
            String error = "Error instantiating " + symbol.result + " " + symbol.name + " to " + syntax.getSyntaxAt(Value, Constant);
            syntaxAssert(isConstantType(symbol.result, syntax.getSyntaxAt(Value, Constant)), syntax, error);
        } else if (syntax.getAt(Value, Variable) != null) {
            String variableName = syntax.getAt(Value, Variable, Name);
            Symbol variable = SymbolTable.find(variableName);
            syntaxAssert(variable != null, syntax, "Error assigning variable " + symbol.name + " to non-existent variable " + variableName);
            if (variable == null) return;
            syntaxAssert(symbol != variable, syntax, "Invalid self-reference (" + variableName + ") in variable instantiation.");
            String error = "Error instantiating " + symbol.result + " " + symbol.name + " to " + variable.name + " of type " + variable.result;
            syntaxAssert(symbol.result.equals(variable.result), syntax, error);
        } else if (syntax.getAt(Value, MethodCall) != null) {
            Symbol method = SymbolTable.find(syntax.getAt(Value, MethodCall, Name));
            String error = "Error instantiating " + symbol.result + " " + symbol.name + " to method " + method.name + " return type " + method.result;
            syntaxAssert(symbol.result.equals(method.result), syntax, error);
        }

        handleAll(syntax, Value);
    }

    protected static void handleVariableDeclaration(SyntaxElement syntax) {
        Symbol symbol = new Symbol(syntax.getAt(Name));
        symbol.result = Symbol.ResultType.fromDeclaredType(syntax.getAt(VariableType));
        SymbolTable.addToScope(symbol);
    }

    protected static void handleField(SyntaxElement syntax) {
        handleAll(syntax, VariableInstantiation);
        Symbol symbol = SymbolTable.find(syntax.getAt(VariableInstantiation, VariableDeclaration, Name));
        symbol.access = Symbol.AccessType.fromDeclaredType(syntax.getAt(Access));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Private;
        SymbolTable.updateSymbol(symbol);
    }

    protected static void handleMethod(SyntaxElement syntax) {
        String name = syntax.getAt(Name);
        Symbol symbol = new Symbol(name);
        symbol.symbol = Symbol.SymbolType.Method;
        symbol.result = Symbol.ResultType.fromDeclaredType(syntax.getAt(ReturnType));
        symbol.access = Symbol.AccessType.fromDeclaredType(syntax.getAt(Access));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Public;

        syntax.setResultType(symbol.result);
        SymbolTable.addToScope(symbol);
        SymbolTable.newScope(name);
        handleAll(syntax, VariableDeclaration);
        Symbol methodScope = Symbol.newScopeSymbol("Method-" + name);
        SymbolTable.newScope(methodScope.name);
        handleAll(syntax, Statement);
        SymbolTable.exitScope();
    }

    protected static void handleReturnStatement(SyntaxElement syntax) {
        Symbol symbol = SymbolTable.find(syntax.ancestor(Method).getAt(Name));
        Symbol.ResultType resultType = Symbol.ResultType.Void;
        if (syntax.contains(Expression)) {
            handleAll(syntax, Expression);
            resultType = syntax.getSyntaxAt(Expression).getResultType();
        }
        syntaxAssert(symbol.result.equals(resultType), syntax, "Return statement of type " + resultType + " but expected " + symbol.result);
    }

    protected static void handleConstant(SyntaxElement syntax) {
        Symbol.ResultType resultType = Symbol.ResultType.fromGrammarName(syntax.getChild(0).getGrammar().name());
        syntax.assignRecursive(resultType);
    }

    protected static void handleVariable(SyntaxElement syntax) {
        Symbol.ResultType resultType = SymbolTable.find(syntax.getAt(Name)).result;
        syntax.assignRecursive(resultType);
    }

    protected static void handleMethodCall(SyntaxElement syntax) {
        String name = syntax.getAt(Name);
        Symbol method = SymbolTable.find(name);
        SymbolTable table = SymbolTable.findScope(name);
        syntaxAssert(table != null && method != null, syntax, "Method " + name + " does not exist");
        if (table == null || method == null) return;
        syntax.assignRecursive(method.result);
        handleAll(syntax, Expression);

        List<SyntaxElement> arguments = syntax.childrenFilter(Expression);
        String error = "Expected " + table.getSymbols().size() + " arguments but got " + arguments.size();
        if (!syntaxAssert(arguments.size() == table.getSymbols().size(), syntax, error)) {
            return;
        }

        int i = 0;
        for (Symbol s : table.getSymbols().values()) {
            Symbol.ResultType resultType = arguments.get(i++).getResultType();
            error = "Query parameter " + s.name + " is type " + s.result.name() + " but argument is type " + resultType.name();
            syntaxAssert(s.result.equals(resultType), syntax, error);
        }
    }

    protected static void handleFile(SyntaxElement syntax) {
        SymbolTable.newScope(syntax.getAt(Package, Name));
        handleAll(syntax, Import);
        handleAll(syntax, ClassDefinition);
        SymbolTable.exitScope();
    }

    protected static void handleClassDefinition(SyntaxElement syntax) {
        Symbol symbol = new Symbol(syntax.getAt(Name));
        symbol.symbol = Symbol.SymbolType.Class;
        symbol.result = Symbol.ResultType.None;
        symbol.access = Symbol.AccessType.Public;

        SymbolTable.addToScope(symbol);
        SymbolTable.newScope(syntax.getAt(Name));
        handleAll(syntax, Field, Method);
        SymbolTable.exitScope();
    }

    private static void handleAll(SyntaxElement syntax, Grammar... grammars) {
        for (Grammar grammar : grammars) {
            List<SyntaxElement> elements = syntax.childrenFilter(grammar);
            for (SyntaxElement element : elements) {
                element.verify();
            }
        }
    }
}
