package nodes.interfaces;

import main.Robot;

/**
 * Interface for all conditional and boolean operations
 */
public interface BooleanNode {

    /**
     * Evaluates the conditional represented by this node on the given robot.
     * The functionality of this method depends on the type of the implementing class.
     *
     * @param robot the robot to control
     * @return the result of the evaluation
     */
    boolean evaluate(Robot robot);

    String toString();
}
