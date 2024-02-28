package nodes;

import main.Robot;
import nodes.interfaces.ActionNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that turns the robot right.
 */
public class TurnRNode implements ActionNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.turnRight();
    }
}
