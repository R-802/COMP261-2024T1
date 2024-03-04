package nodes;

import main.Robot;
import nodes.interfaces.ProgramNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a loop statement in the program.
 * <p>
 * A loop contains a block of statements to be executed repeatedly until the robot is dead.
 * It acts as a single {@code StatementNode} within the parsed program structure.
 */
public class LoopNode implements ProgramNode {

    private final BlockNode body;

    public LoopNode(BlockNode body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "loop: " + body;
    }

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        while (!robot.isDead()) {
            body.execute(robot);
        }
    }
}
