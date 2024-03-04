package nodes.conditionals.relops;

import main.Robot;
import nodes.interfaces.BooleanNode;
import nodes.interfaces.ExpressionNode;

/**
 * GreaterThanNode is a node that represents a greater than comparison
 */
public class GreaterThanNode implements BooleanNode {

    private final ExpressionNode left;
    private final ExpressionNode right;

    public GreaterThanNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) > right.evaluate(robot);
    }

    @Override
    public String toString() {
        return left.toString() + " is greater than " + right.toString();
    }
}
