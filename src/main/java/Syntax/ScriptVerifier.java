package Syntax;

import Syntax.Symbol.ResultType;
import Syntax.SyntaxBuilder.Grammar;
import classes.Token;

import java.util.List;

import static Syntax.SyntaxBuilder.Grammar.*;
import static Syntax.SyntaxBuilder.Grammar.Package;

/**
 * Created by Josh on 8/03/2016.
 */
public class ScriptVerifier {
    private final SyntaxElement syntax;
    private boolean passed = true;

    public ScriptVerifier(SyntaxElement tree) {
        this.syntax = tree;
    }

    public boolean verifyTree() {
        handleGeneric();
        return passed;
    }

    private boolean syntaxAssert(Boolean result, String errorString) {
        if (!result) {
            passed = false;
            Token token = syntax.getToken();
            System.err.println("Syntax Error [" + token.getLineNumber() + ":" + token.getColumnNumber() + "]: " + errorString);
        }
        return result;
    }

    protected void handleVariableAssignment() {
        String name = syntax.getAt(VariableDeclaration, Name);
        Symbol variableSymbol = SymbolTable.find(name);
        handleChildrenOf(Expression);
        String error = "Cannot instantiate variable " + name + " of type " + variableSymbol.result.name() + " to type " + syntax.getSyntaxAt(Expression).getResultType();
        syntaxAssert(syntax.getSyntaxAt(Expression).getResultType().equals(variableSymbol.result), error);
    }

    protected void handleVariableInstantiation() {
        handleChildrenOf(VariableDeclaration);
        String name = syntax.getAt(VariableDeclaration, Name);
        Symbol variableSymbol = SymbolTable.find(name);
        handleChildrenOf(Expression);
        String error = "Cannot instantiate variable " + name + " of type " + variableSymbol.result.name() + " to type " + syntax.getSyntaxAt(Expression).getResultType();
        syntaxAssert(syntax.getSyntaxAt(Expression).getResultType().equals(variableSymbol.result), error);
    }

    protected void handleVariableDeclaration() {
        Symbol symbol = new Symbol(syntax.getAt(Name));
        symbol.result = ResultType.fromDeclaredType(syntax.getAt(VariableType));
        SymbolTable.addToScope(symbol);
    }

    protected void handleField() {
        handleChildrenOf(VariableInstantiation);
        Symbol symbol = SymbolTable.find(syntax.getAt(VariableInstantiation, VariableDeclaration, Name));
        symbol.access = Symbol.AccessType.fromDeclaredType(syntax.getAt(Access));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Private;
        SymbolTable.updateSymbol(symbol);
    }

    protected void handleMethod() {
        String name = syntax.getAt(Name);
        Symbol symbol = new Symbol(name);
        symbol.symbol = Symbol.SymbolType.Method;
        symbol.result = ResultType.fromDeclaredType(syntax.getAt(ReturnType));
        symbol.access = Symbol.AccessType.fromDeclaredType(syntax.getAt(Access));
        if (symbol.access == null) symbol.access = Symbol.AccessType.Public;

        syntax.setResultType(symbol.result);
        SymbolTable.addToScope(symbol);
        SymbolTable.newScope(name);
        handleChildrenOf(VariableDeclaration);
        Symbol methodScope = Symbol.newScopeSymbol("Method-" + name);
        SymbolTable.newScope(methodScope.name);
        handleChildrenOf(Statement);
        SymbolTable.exitScope();
    }

    protected void handleReturnStatement() {
        Symbol symbol = SymbolTable.find(syntax.ancestor(Method).getAt(Name));
        ResultType resultType = ResultType.Void;
        if (syntax.contains(Expression)) {
            handleChildrenOf(Expression);
            resultType = syntax.getSyntaxAt(Expression).getResultType();
        }
        syntaxAssert(symbol.result.equals(resultType), "Return statement of type " + resultType + " but expected " + symbol.result);
    }

    protected void handleConstant() {
        ResultType resultType = ResultType.fromGrammarName(syntax.getChild(0).getGrammar().name());
        syntax.assignUpRecursive(resultType);
    }

    protected void handleVariable() {
        ResultType resultType = SymbolTable.find(syntax.getAt(Name)).result;
        syntax.assignUpRecursive(resultType);
    }

    protected void handleMethodCall() {
        String name = syntax.getAt(Name);
        Symbol method = SymbolTable.find(name);
        SymbolTable table = SymbolTable.findScope(name);
        syntaxAssert(table != null && method != null, "Method " + name + " does not exist");
        if (table == null || method == null) return;
        syntax.assignUpRecursive(method.result);
        handleChildrenOf(Expression);

        List<SyntaxElement> arguments = syntax.childrenFilter(Expression);
        String error = "Expected " + table.getSymbols().size() + " arguments but got " + arguments.size();
        if (!syntaxAssert(arguments.size() == table.getSymbols().size(), error)) {
            return;
        }

        int i = 0;
        for (Symbol s : table.getSymbols().values()) {
            ResultType resultType = arguments.get(i++).getResultType();
            error = "Query parameter " + s.name + " is type " + s.result.name() + " but argument is type " + resultType.name();
            syntaxAssert(s.result.equals(resultType), error);
        }
    }

    protected void handleFile() {
        SymbolTable.newScope(syntax.getAt(Package, Name));
        handleChildrenOf(Import);
        handleChildrenOf(ClassDefinition);
        SymbolTable.exitScope();
    }

    protected void handleClassDefinition() {
        Symbol symbol = new Symbol(syntax.getAt(Name));
        symbol.symbol = Symbol.SymbolType.Class;
        symbol.result = ResultType.None;
        symbol.access = Symbol.AccessType.Public;

        SymbolTable.addToScope(symbol);
        SymbolTable.newScope(syntax.getAt(Name));
        handleChildrenOf(Field, Method);
        SymbolTable.exitScope();
    }

    protected void handlePowerExpression() {
        handleChildrenOf(IncrementExpression);
        if (syntax.children.size() == 1) return;
        for (SyntaxElement expression : syntax.childrenFilter(IncrementExpression)) {
            ResultType type = expression.getResultType();
            if (syntax.getResultType() == null) syntax.setResultType(type);
            syntaxAssert(type.equals(ResultType.Integer) || type.equals(ResultType.Float), "Can only raise Integers and Floats to a power");
        }
    }

    private void handleChildrenOf(Grammar... grammars) {
        for (Grammar grammar : grammars) {
            List<SyntaxElement> elements = syntax.childrenFilter(grammar);
            elements.forEach(el -> grammar.getVerifier().verify(new ScriptVerifier(el)));
        }
    }


    protected void handleGeneric() {
        if (!syntax.isLeaf()) {
            syntax.children.forEach(el -> {
                if (el != null && el.getGrammar() != null) {
                    el.getGrammar().getVerifier().verify(new ScriptVerifier(el));
                }
            });
        }

        if (syntax.getParent() != null && syntax.getParent().getResultType() == null) {
            syntax.getParent().setResultType(syntax.getResultType());
        }
    }
}
