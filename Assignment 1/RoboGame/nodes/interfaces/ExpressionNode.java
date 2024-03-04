package nodes.interfaces;

import main.Robot;

/**
 * ExpressionNode is a node that represents an expression
 */
public interface ExpressionNode {

    int evaluate(Robot robot);

    String toString();
}
