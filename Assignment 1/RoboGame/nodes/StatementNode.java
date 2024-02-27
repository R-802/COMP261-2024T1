package nodes;

import ProgramNode;

public abstract class StatementNode implements ProgramNode {

    @Override
    public abstract String toString();

    @Override
    public abstract void execute(Robot robot);
}
