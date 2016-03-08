package Syntax;

import Grammar.GrammarException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).equals(el.children.get(i))) return false;
        }
        return true;
    }

    public SyntaxElement addNode(SyntaxElement element) {
        if (children.size() > 0 && children.get(children.size() - 1).equals(element)) return element;
        children.add(element);
        return element;
    }

    @Override
    public String toString() {
        String traversal = grammar + "<" + this.value + ":" + (parent != null ? parent.getValue() : "") + "> {\n";
        for (SyntaxElement el : children) {
            if (el.isLeaf) traversal += indent(el.grammar + "<" + el.value + "::" + el.parent.getValue() + ">");
            else traversal += indent(el.toString());
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

    public SyntaxElement getChild(int i) {
        return children.get(i);
    }

    public SyntaxElement getSyntax(String... path) {
        SyntaxElement value = this;
        for (String element : path) {
            boolean found = false;
            for (SyntaxElement child : value.children) {
                if (child.grammar.equals(element)) {
                    value = child;
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        return value;
    }

    public List<SyntaxElement> getAllSyntax(String... grammars) {
        List<SyntaxElement> syntaxList = new ArrayList<>();
        for (String grammar : grammars) {
            syntaxList.addAll(children.stream().filter(c -> c.grammar.equals(grammar)).collect(Collectors.toList()));
        }
        return syntaxList;
    }

    public String get(String... path) {
        SyntaxElement value = getSyntax(path);
        if (value == null) return null;
        if (value.children != null && value.children.size() > 0) return value.getChild(0).value;
        return value.value;
    }

    public void addChildrenFrom(SyntaxElement el) {
        for (SyntaxElement e : el.children) {
            addNode(e);
        }
    }

    public boolean verify() throws GrammarException {
        try {
            if (isLeaf) {
                System.out.println("verified leaf " + value);
                return true;
            }
            java.lang.reflect.Method method = Verify.class.getDeclaredMethod("handle" + value, SyntaxElement.class);
            if (!(boolean) method.invoke(null, this)) return false;
        } catch (NoSuchMethodException e) {
            for (SyntaxElement el : children) {
                if (!el.verify()) return false;
            }
        } catch (InvocationTargetException e) {
            try {
                throw e.getCause();
            } catch (GrammarException err) {
                throw err;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return false;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
