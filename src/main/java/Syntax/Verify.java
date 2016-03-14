package Syntax;

import Syntax.SyntaxBuilder.Grammar;

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

    public static void syntaxAssert(Boolean result, String error) {
        if (!result) {
            passed = false;
            System.err.println("Syntax Error: " + error);
        }
    }

    protected static void handleVariableInstantiation(SyntaxElement syntax) {
        handleAll(syntax, VariableDeclaration);
        String name = syntax.getAt(VariableDeclaration, Name);
        Symbol symbol = SymbolTable.find(name);

        if (syntax.getAt(Value, Constant) != null) {
            String error = "Error instantiating " + symbol.result + " " + symbol.name + " to " + syntax.getSyntaxAt(Value, Constant);
            syntaxAssert(isConstantType(symbol.result, syntax.getSyntaxAt(Value, Constant)), error);
        } else if (syntax.getAt(Value, Variable) != null) {
            String variableName = syntax.getAt(Value, Variable, Name);
            Symbol variable = SymbolTable.find(variableName);
            syntaxAssert(variable != null, "Error assigning variable " + symbol.name + " to non-existent variable " + variableName);
            if (variable == null) return;
            syntaxAssert(symbol != variable, "Invalid self-reference (" + variableName + ") in variable instantiation.");
            String error = "Error instantiating " + symbol.result + " " + symbol.name + " to " + variable.name + " of type " + variable.result;
            syntaxAssert(symbol.result.equals(variable.result), error);
        } else if (syntax.getAt(Value, MethodCall) != null) {
            Symbol method = SymbolTable.find(syntax.getAt(Value, MethodCall, Name));
            String error = "Error instantiating " + symbol.result + " " + symbol.name + " to method " + method.name + " return type " + method.result;
            syntaxAssert(symbol.result.equals(method.result), error);
        }
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
        handleAll(syntax, CumulativeExpression);
        Symbol.ResultType resultType = syntax.getSyntaxAt(CumulativeExpression).getResultType();
        syntaxAssert(symbol.result.equals(resultType), "Return statement of type " + resultType + " but expected " + symbol.result);
    }

    protected static boolean handleExtension(SyntaxElement syntax) {
        Symbol.ResultType parentType = getValueType(syntax.ancestor(CumulativeExpression).getSyntaxAt(Expression, Value));
        Symbol.ResultType type = getValueType(syntax.getSyntaxAt(Expression, Value));
        if (parentType.equals(Symbol.ResultType.None) || parentType.equals(Symbol.ResultType.Void)) return false;
        return parentType.equals(type);
    }

    private static Symbol.ResultType getValueType(SyntaxElement value) {
        if (value.contains(MethodCall)) {
            return SymbolTable.find(value.getAt(MethodCall, Name)).result;
        } else if (value.contains(Constant)) {
            return Symbol.ResultType.fromGrammarName(value.getSyntaxAt(Constant).getChild(0).getGrammar().name());
        } else if (value.contains(Variable)) {
            return SymbolTable.find(value.getAt(Variable, Name)).result;
        }
        return Symbol.ResultType.None;
    }

    protected static void handleMethodCall(SyntaxElement syntax) {
        String name = syntax.getAt(Name);
        Symbol method = SymbolTable.find(name);
        syntaxAssert(method != null, "Method " + name + " does not exist");
        if (method == null) return;

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
