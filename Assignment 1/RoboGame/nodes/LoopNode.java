package nodes;

import main.Robot;
import nodes.interfaces.StatementNode;
import util.exepeptions.RobotInterruptedException;

public class LoopNode implements StatementNode {

    private final BlockNode body;

    public LoopNode(BlockNode body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "loop: " + body;
    }

    @Override
    public void execute(Robot robot) throws RobotInterruptedException {
        while (!robot.isDead()) {
            body.execute(robot);
        }
    }
}
