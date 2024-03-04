package nodes;

import main.Robot;
import nodes.conditionals.IfNode;
import nodes.interfaces.ProgramNode;

/**
 * Represents a statement in the parsed robot program.
 * Statements control the robot's actions.
 */
public class StatementNode implements ProgramNode {

    ProgramNode statement;

    public StatementNode(ProgramNode actionNode) {
        this.statement = actionNode;
    }

    public StatementNode(LoopNode loopNode) {
        this.statement = loopNode;
    }

    public StatementNode(IfNode ifNode) {
        this.statement = ifNode;
    }

    public StatementNode(WhileNode whileNode) {
        this.statement = whileNode;
    }

    @Override
    public void execute(Robot robot) {
        this.statement.execute(robot);
    }

    @Override
    public String toString() {
        return statement.toString();
    }
}
