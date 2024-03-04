package nodes.conditionals.relops;

import main.Robot;
import nodes.interfaces.BooleanNode;
import nodes.interfaces.ExpressionNode;

/**
 * EqualNode is a node that represents an equal comparison
 */
public class EqualNode implements BooleanNode {

    private final ExpressionNode left;
    private final ExpressionNode right;

    public EqualNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Robot robot) {
        return left.evaluate(robot) == right.evaluate(robot);
    }

    @Override
    public String toString() {
        return left.toString() + " is equal to " + right.toString();
    }
}
