package output;

import Syntax.SyntaxElement;

/**
 * Created by Josh on 16/03/2016.
 */
public interface CodeOutputter {
    String getCode();
    String getFilepath();
    String getFilename();
    String handle(SyntaxElement syntax);
    String handleLeaf();
    String handleInternal();
    String handlePackage();
    String handleClassDefinition();
    String handleComparisonExpression();
    String handlePowerExpression();
    String handleInteger();
    String handleVariableAssignment();
    String handleVariableInstantiation();
    String handleExpression();
    String handleSumExpression();
    String handleConstant();
    String handleVariableType();
    String handleVariableDeclaration();
}
