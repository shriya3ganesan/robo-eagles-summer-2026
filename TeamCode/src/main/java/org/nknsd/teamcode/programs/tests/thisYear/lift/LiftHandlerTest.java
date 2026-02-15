package org.nknsd.teamcode.programs.tests.thisYear.lift;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.nknsd.teamcode.components.handlers.LiftHandler;
import org.nknsd.teamcode.components.sensors.IMUSensor;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;
@TeleOp(name="Test the LiftHandler", group="Tests") @Disabled
public class LiftHandlerTest extends NKNProgram {
    LiftHandler liftHandler;
    IMUSensor imuSensor;
    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        liftHandler = new LiftHandler();
        components.add(liftHandler);
        telemetryEnabled.add(liftHandler);

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        IMUSensor imuSensor = new IMUSensor(/*orientationOnRobot*/);

        components.add(imuSensor);
        telemetryEnabled.add(imuSensor);

        liftHandler.link(imuSensor);
    }

    @Override
    public void start() {
        runtime.reset();
        liftHandler.start(runtime, telemetry);
        imuSensor.start(runtime, telemetry);
        liftHandler.startRaise(runtime);
    }
}