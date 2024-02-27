package nodes.interfaces;

import main.Robot;

/**
 * Represents a statement in the parsed robot program.
 * Statements control the robot's actions.
 */
public interface StatementNode extends ProgramNode {

    /**
     * Returns the text representation of the statement node.
     * Useful for debugging and displaying parsed program structure.
     *
     * @return a string representing the statement
     */
    String toString();

    /**
     * Executes the statement on the given robot.
     * Each concrete implementation defines its specific execution logic.
     *
     * @param robot the robot to control
     */
    @Override
    void execute(Robot robot);
}
