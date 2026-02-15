package org.nknsd.teamcode.components.handlers.artifact;

import org.nknsd.teamcode.components.utility.RobotVersion;

public enum MicrowavePositions {



    LOAD0(0.22, 0.95, 0.855),
    LOAD1(0.99, 3.10, 3.05),
    LOAD2(0.61, 2.01, 1.97),
    FIRE0(0.8, 2.58, 2.509),
    FIRE1(0.42, 1.51, 1.424),
    FIRE2(0.03, 0.43, 0.312);



    public final double ROBOT_OFFSET = RobotVersion.INSTANCE.microwaveOffset;
    public final boolean OLD_VOLTAGE_POSITIONS = RobotVersion.INSTANCE.oldVoltagePositions;


    public final double microPosition;
    public final double powerPosition;



    MicrowavePositions(double microPositions, double powerPosition, double oldPowerPosition) {
        this.microPosition = microPositions + ROBOT_OFFSET;
        if(!OLD_VOLTAGE_POSITIONS){
        this.powerPosition = powerPosition;}
        else{
            this.powerPosition = oldPowerPosition;
        }
    }
}
