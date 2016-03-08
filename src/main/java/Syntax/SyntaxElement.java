package Syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 7/03/2016.
 */
public class SyntaxElement {
    boolean isLeaf = false;
    String value;
    String grammar;
    SyntaxElement parent;
    List<SyntaxElement> children = new ArrayList<>();

    public SyntaxElement(SyntaxElement parent, String grammar, String value, boolean leaf) {
        this.parent = parent;
        this.value = value;
        this.grammar = grammar;
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
        return true;
    }

    public SyntaxElement addNode(SyntaxElement element) {
        if (children.size() > 0 && children.get(children.size() - 1).equals(element)) return element;
        children.add(element);
        return element;
    }

    public String traverse() {
        String traversal = grammar + "<" + this.value + ":" + (parent != null ? parent.getValue() : "") + "> {\n";
        for (SyntaxElement el : children) {
            if (el.isLeaf) traversal += indent(el.grammar + "<" + el.value + "::" + el.parent.getValue() + ">");
            else traversal += indent(el.traverse());
        }
        return traversal + "}\n";
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

    public String getGrammar() {
        return grammar;
    }

    public String getValue() {
        return value;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public List<SyntaxElement> getChildren() {
        return children;
    }

    public void addFrom(SyntaxElement el) {
        for (SyntaxElement e : el.children) {
            addNode(e);
        }
    }
}
