package nodes;

import main.Robot;
import nodes.interfaces.StatementNode;
import util.exepeptions.RobotInterruptedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a block/list of statements to be executed sequentially.
 * <p>
 * A block can contain any number of statements, including other blocks,
 * actions, and loops. It acts as a single {@code StatementNode} within
 * the parsed program structure.
 */
public class BlockNode implements StatementNode {

    /**
     * The list of statements within this block.
     */
    private final List<StatementNode> statements;

    /**
     * Constructor
     */
    public BlockNode(List<StatementNode> statements) {
        this.statements = new ArrayList<>(statements);
    }

    /**
     * Executes all statements within this block in sequence.
     * <p>
     * If any statement results in the robot's death, execution is stopped immediately.
     *
     * @param robot the robot on which to execute the block
     * @throws RobotInterruptedException if the robot is interrupted during execution
     */
    @Override
    public void execute(Robot robot) throws  RobotInterruptedException {
        for (StatementNode statement : statements) {
            statement.execute(robot);
            if (robot.isDead()) { // Terminate on robot death
                return;
            }
        }
    }

    /**
     * Returns a string representation of this block.
     * <p>
     * The string representation is a concatenation of the string representations
     * of all statements within the block, enclosed in curly braces.
     *
     * @return a string representation of this block
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ ");
        for (StatementNode statement : statements) {
            builder.append(statement.toString()).append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
}
