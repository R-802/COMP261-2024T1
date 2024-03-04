package nodes;

import main.Robot;
import nodes.interfaces.BooleanNode;
import nodes.interfaces.ProgramNode;

/**
 * Represents a while loop in the robot program.
 * The while loop has a condition and a block of code to execute while the condition is true.
 */
public class WhileNode implements ProgramNode {

    private final BooleanNode conditional;
    private final BlockNode block;

    public WhileNode(BooleanNode conditional, BlockNode block) {
        this.conditional = conditional;
        this.block = block;
    }

    /**
     * Executes the block node while the conditional is true
     *
     * @param robot the robot to control
     */
    @Override
    public void execute(Robot robot) {
        while (conditional.evaluate(robot)) {
            block.execute(robot);
        }
    }
}
