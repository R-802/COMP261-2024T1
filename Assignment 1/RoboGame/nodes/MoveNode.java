package nodes;

import main.Robot;
import nodes.interfaces.ActionNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that moves the robot forward.
 */
public class MoveNode implements ActionNode {
    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.move();
    }
}
