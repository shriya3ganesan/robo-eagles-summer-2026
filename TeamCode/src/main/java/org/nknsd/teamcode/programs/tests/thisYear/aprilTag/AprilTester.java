package org.nknsd.teamcode.programs.tests.thisYear.aprilTag;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.nknsd.teamcode.components.handlers.vision.BasketLocator;
import org.nknsd.teamcode.components.sensors.AprilTagSensor;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;
@TeleOp(name = "AprilTagTester", group="Tests")  @Disabled
public class AprilTester extends NKNProgram {
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        AprilTagSensor aprilTagSensor = new AprilTagSensor();
        components.add(aprilTagSensor);
        telemetryEnabled.add(aprilTagSensor);

        BasketLocator basketLocator = new BasketLocator(RobotVersion.INSTANCE.aprilDistanceInterpolater);
        components.add(basketLocator);
        telemetryEnabled.add(basketLocator);
        basketLocator.link(aprilTagSensor);
    }
}
