package nodes;

import main.Robot;
import nodes.interfaces.ExpressionNode;

public class NumberNode implements ExpressionNode {

    private final int value;

    public NumberNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate(Robot robot) {
        return value;
    }
}
