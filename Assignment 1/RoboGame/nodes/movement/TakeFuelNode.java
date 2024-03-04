package nodes.movement;

import main.Robot;
import nodes.interfaces.MovementNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that takes fuel from the current location.
 */
public class TakeFuelNode implements MovementNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.takeFuel();
    }
}
