package nodes.interfaces;

import main.Robot;
import util.exepeptions.RobotInterruptedException;

/**
 Defines the contract for node classes representing actions within a robot program.
 */
public interface ActionNode extends StatementNode{

    /**
     * Performs the action associated with the node.
     *
     * @param robot The robot that will execute the action.
     * @throws RobotInterruptedException If the action is interrupted.
     */
    void execute(Robot robot) throws RobotInterruptedException;
}
