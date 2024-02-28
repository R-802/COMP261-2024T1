package nodes;

import main.Robot;
import nodes.interfaces.ActionNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that makes the robot wait.
 */
public class WaitNode implements ActionNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.idleWait();
    }
}
