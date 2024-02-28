package nodes;

import main.Robot;
import nodes.interfaces.ActionNode;
import util.exepeptions.RobotInterruptedException;

public class TurnRNode implements ActionNode {

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        robot.turnRight();
    }
}
