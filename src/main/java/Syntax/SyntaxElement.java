package Syntax;

import Syntax.SyntaxBuilder.Grammar;
import classes.Token;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Josh on 7/03/2016.
 */
public class SyntaxElement {
    private final Token token;
    boolean isLeaf = false;
    int index = 0;
    String value;
    Grammar grammar;
    SyntaxElement parent;
    List<SyntaxElement> children = new ArrayList<>();
    private Symbol.ResultType resultType;

    public SyntaxElement(SyntaxElement parent, Grammar grammar, String value, Token token, boolean leaf) {
        this.parent = parent;
        this.value = value;
        this.grammar = grammar;
        this.token = token;
        isLeaf = leaf;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!o.getClass().isAssignableFrom(SyntaxElement.class)) return false;
        SyntaxElement el = (SyntaxElement) o;
        if (el.isLeaf != isLeaf) return false;
        if (!el.value.equals(value)) return false;
        if (parent != el.parent) return false;
        if (children.size() != el.children.size()) return false;
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).equals(el.children.get(i))) return false;
        }
        return true;
    }

    public void incrementIndex() {
        index++;
    }

    public SyntaxElement addNode(SyntaxElement element) {
        if (children.size() > 0 && children.get(children.size() - 1).equals(element)) return element;
        children.add(element);
        return element;
    }

    @Override
    public String toString() {
        String compressedRepresentation = (token == null ? "/" : token.getText() + " [" + this.value + "]");

        if (children.size() == 1) {
            compressedRepresentation += " " + children.get(0).toString();
        } else if (children.size() > 1) {
            compressedRepresentation += ":\n";
            for (SyntaxElement el : children) {
                compressedRepresentation += indent(el.toString());
            }
        }
        return compressedRepresentation;
    }

    private String indent(String text) {
        String[] lines = text.split("\\n");
        StringBuilder content = new StringBuilder();
        String tab = "    ";
        for (String line : lines) {
            content.append(tab).append(line).append('\n');
        }
        return content.toString();
    }

    public SyntaxElement getParent() {
        return parent;
    }

    public boolean remove(SyntaxElement element) {
        return children.remove(element);
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public String getValue() {
        return value;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public SyntaxElement getChild(int i) {
        return children.get(i);
    }

    public List<SyntaxElement> getChildren() {
        return children;
    }

    public SyntaxElement getSyntaxAt(Grammar... path) {
        SyntaxElement value = this;
        for (Grammar grammar : path) {
            boolean found = false;
            for (SyntaxElement child : value.children) {
                if (grammar.equals(child.grammar)) {
                    value = child;
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        return value;
    }

    public List<SyntaxElement> childrenFilter(Grammar... grammars) {
        List<SyntaxElement> syntaxList = new ArrayList<>();
        for (Grammar grammar : grammars) {
            syntaxList.addAll(children.stream().filter(c -> grammar.equals(c.grammar)).collect(Collectors.toList()));
        }
        return syntaxList;
    }

    public String getAt(Grammar... path) {
        SyntaxElement value = getSyntaxAt(path);
        if (value == null) return null;
        if (value.children != null && value.children.size() > 0) return value.getChild(0).value;
        return value.value;
    }

    public void addChildrenFrom(SyntaxElement el) {
        for (SyntaxElement e : el.children) {
            addNode(e);
        }
    }

    public SyntaxElement ancestor(Grammar grammar) {
        if (grammar.equals(getGrammar())) return this;
        if (parent == null) return null;
        return parent.ancestor(grammar);
    }

    public boolean contains(Grammar grammar) {
        for (SyntaxElement child : children) {
            if (grammar.equals(child.getGrammar())) return true;
        }
        return false;
    }

    public void assignRecursive(Symbol.ResultType type) {
        if (resultType != null) return;
        resultType = type;
        if (parent == null) return;
        parent.assignRecursive(type);
    }

    public Symbol.ResultType getResultType() {
        return resultType;
    }

    public void setResultType(Symbol.ResultType resultType) {
        this.resultType = resultType;
    }

    public Token getToken() {
        return token;
    }
}
