package nodes;

import main.Robot;
import nodes.interfaces.ActionNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that turns the robot left.
 */
public class TurnLNode implements ActionNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.turnLeft();
    }
}
