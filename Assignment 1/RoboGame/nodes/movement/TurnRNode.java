package nodes.movement;

import main.Robot;
import nodes.interfaces.MovementNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that turns the robot right.
 */
public class TurnRNode implements MovementNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.turnRight();
    }
}
