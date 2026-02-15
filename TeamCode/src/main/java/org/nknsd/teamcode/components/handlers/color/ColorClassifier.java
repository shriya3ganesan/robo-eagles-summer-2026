package org.nknsd.teamcode.components.handlers.color;

import com.qualcomm.robotcore.util.RobotLog;

import org.nknsd.teamcode.components.utility.RobotVersion;

public class ColorClassifier {


    final double maxDist = RobotVersion.INSTANCE.distSensorThreshold;
    final private ColorReader colorReader;

    public ColorClassifier(ColorReader colorReader) {
        this.colorReader = colorReader;
    }

    public BallColor classifyColor() {
        double[] colors = colorReader.getReading();
//        RobotLog.v("Color Reader Dist: " + colors[3]);
        if (colors[3] >= maxDist) {
            return BallColor.NOTHING;
        }
        if (colors[1] > colors[0] && colors[1] > colors[2]) {
            return BallColor.GREEN;
        }
        if (colors[2] > colors[0]) {
            return BallColor.PURPLE;
        }
        return BallColor.UNSURE;
    }
}
