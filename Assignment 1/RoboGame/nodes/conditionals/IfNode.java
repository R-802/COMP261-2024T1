package nodes.conditionals;

import main.Robot;
import nodes.BlockNode;
import nodes.interfaces.BooleanNode;
import nodes.interfaces.ProgramNode;

/**
 * Represents an if statement in the robot program.
 * The if statement has a condition and a block of code to execute if the condition is true.
 * Optionally, it may also have an else block of code to execute if the condition is false.
 */
public class IfNode implements ProgramNode {

    private final BooleanNode condition;
    private final BlockNode block;
    private final BlockNode elseBlock; // May remain null if no else block is provided

    public IfNode(BooleanNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
        this.elseBlock = null;
    }

    public IfNode(BooleanNode condition, BlockNode block, BlockNode elseBlock) {
        this.condition = condition;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    @Override
    public void execute(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        } else if (elseBlock != null) {
            elseBlock.execute(robot);
        }
    }

    @Override
    public String toString() {
        return "IF : " + condition.toString();
    }
}
