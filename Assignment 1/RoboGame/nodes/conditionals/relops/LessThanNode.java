package nodes.conditionals.relops;

import main.Robot;
import nodes.interfaces.BooleanNode;
import nodes.interfaces.ExpressionNode;

/**
 * LessThanNode is a node that represents a less than comparison
 */
public class LessThanNode implements BooleanNode {

    private final ExpressionNode left;
    private final ExpressionNode right;

    public LessThanNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) < right.evaluate(robot);
    }

    @Override
    public String toString() {
        return left.toString() + " is less than " + right.toString();
    }
}
