package nodes.movement;

import main.Robot;
import nodes.interfaces.MovementNode;
import util.exepeptions.RobotInterruptedException;

/**
 * Represents a node that moves the robot forward.
 */
public class MoveNode implements MovementNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.move();
    }
}
