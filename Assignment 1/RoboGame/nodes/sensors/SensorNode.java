package nodes.sensors;

import main.Robot;
import nodes.interfaces.ExpressionNode;

public class SensorNode implements ExpressionNode {

    private final SensorType sensorType;
    private final ExpressionNode barrelIndex; // Optional, for barrel-related sensors

    public SensorNode(SensorType sensorType) {
        this(sensorType, null);
    }

    public SensorNode(SensorType sensorType, ExpressionNode barrelIndex) {
        this.sensorType = sensorType;
        this.barrelIndex = barrelIndex;
    }

    @Override
    public int evaluate(Robot robot) {
        switch (sensorType) {
            case FUEL_LEFT:
                return robot.getFuel();
            case OPP_LR:
                return robot.getOpponentLR();
            case OPP_FB:
                return robot.getOpponentFB();
            case NUM_BARRELS:
                return robot.numBarrels();
            case BARREL_LR:
                if (barrelIndex == null) {
                    return robot.getClosestBarrelLR();
                } else {
                    int index = barrelIndex.evaluate(robot);
                    return robot.getBarrelLR(index);
                }
            case BARREL_FB:
                if (barrelIndex == null) {
                    return robot.getClosestBarrelFB();
                } else {
                    int index = barrelIndex.evaluate(robot);
                    return robot.getBarrelFB(index);
                }
            case WALL_DIST:
                return robot.getDistanceToWall();
            default:
                throw new IllegalStateException("Unexpected sensor type: " + sensorType);
        }
    }
}
