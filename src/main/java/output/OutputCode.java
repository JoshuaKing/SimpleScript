package output;

import Syntax.SyntaxElement;

/**
 * Created by Josh on 16/03/2016.
 */
public abstract class OutputCode implements CodeOutputter {
    protected SyntaxElement element;

    public String handle(SyntaxElement syntax) {
        element = syntax;
        if (syntax.isLeaf()) {
            return handleLeaf();
        } else if (syntax.getGrammar() != null) {
            return handleInternal();
        } else {
            return syntax.getChildren().stream().map(this::handle).reduce("", (a, b) -> a + b);
        }
    }
}
