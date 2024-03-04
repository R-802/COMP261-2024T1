package nodes.movement;

import main.Robot;
import nodes.interfaces.ProgramNode;

public class TurnAroundNode implements ProgramNode {

    @Override
    public void execute(Robot robot) {
        robot.turnAround();
    }
}
