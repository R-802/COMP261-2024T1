package nodes;

import main.Robot;
import nodes.interfaces.ActionNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that takes fuel from the current location.
 */
public class TakeFuelNode implements ActionNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.takeFuel();
    }
}
