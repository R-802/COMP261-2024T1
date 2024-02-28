package nodes.interfaces;

import main.Robot;

/**
 * Interface for all nodes in the parsed robot program representing components that can be executed.
 * This includes the top-level program node and individual statements that control the robot's behavior.
 */
public interface ProgramNode {

    /**
     * Executes the program component represented by this node on the given robot.
     * The functionality of this method depends on the type of the implementing class.
     *
     * @param robot the robot to control
     */
    void execute(Robot robot);
}
