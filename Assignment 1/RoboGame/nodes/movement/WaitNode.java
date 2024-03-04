package nodes.movement;

import main.Robot;
import nodes.interfaces.MovementNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that makes the robot wait.
 */
public class WaitNode implements MovementNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.idleWait();
    }
}
