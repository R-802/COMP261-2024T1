package nodes;

import main.Robot;
import nodes.interfaces.ProgramNode;
import util.exepeptions.RobotInterruptedException;

import java.util.ArrayList;
import java.util.List;

public class BlockNode implements ProgramNode {

    private final List<ProgramNode> statements;

    /**
     * Construct a new block with the given list of statements.
     * A block statement is defined as a list of nodes to execute.
     *
     * @param statements the list of statements to execute
     */
    public BlockNode(List<ProgramNode> statements) {
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
    public void execute(Robot robot) throws RobotInterruptedException {
        for (ProgramNode statement : statements) {
            statement.execute(robot);
            if (robot.isDead()) break; // Exit loop if robot is dead
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
        StringBuilder builder = new StringBuilder("{\n");
        for (ProgramNode statement : statements) {
            // Each statement on a new line, indented for clarity
            builder.append("  ").append(statement.toString()).append("\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
