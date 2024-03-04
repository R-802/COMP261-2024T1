package nodes.movement;

import main.Robot;
import nodes.interfaces.ProgramNode;

public class ShieldOffNode implements ProgramNode {
    @Override
    public void execute(Robot robot) {
        robot.setShield(false);
    }
}
