package org.nknsd.teamcode.programs.tests.thisYear.firing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.vision.BasketLocator;
import org.nknsd.teamcode.components.handlers.launch.LaunchSystem;
import org.nknsd.teamcode.components.handlers.launch.LauncherHandler;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.MicrowavePositions;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.handlers.launch.TrajectoryHandler;
import org.nknsd.teamcode.components.handlers.color.BallColor;
import org.nknsd.teamcode.components.handlers.color.BallColorInterpreter;
import org.nknsd.teamcode.components.handlers.color.ColorReader;
import org.nknsd.teamcode.components.sensors.AprilTagSensor;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;

@TeleOp(name = "chute adjust test", group = "Tests")
public class ChuteAdjustTest extends NKNProgram {

    private double distance = 36;

    class IntakeState extends StateMachine.State {

        private final MicrowavePositions slot;
        private final MicrowaveScoopHandler microwaveScoopHandler;
        private final SlotTracker slotTracker;
        private final LaunchSystem launchSystem;
        private final int slotNum;

        IntakeState(int slotNum, MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, LaunchSystem launchSystem) {
            this.launchSystem = launchSystem;
            switch (slotNum) {
                case 1:
                    slot = MicrowavePositions.LOAD1;
                    break;
                case 2:
                    slot = MicrowavePositions.LOAD2;
                    break;
                default:
                    slot = MicrowavePositions.LOAD0;
            }
            this.microwaveScoopHandler = microwaveScoopHandler;
            this.slotTracker = slotTracker;
            this.slotNum = slotNum;
        }


        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            if (microwaveScoopHandler.isDone()) {
                if (slotTracker.getSlotColor(slotNum) == BallColor.GREEN || slotTracker.getSlotColor(slotNum) == BallColor.PURPLE) {
                    StateMachine.INSTANCE.stopAnonymous(this);
                }
            }
        }

        @Override
        protected void started() {
            microwaveScoopHandler.toggleIntake(true);
            microwaveScoopHandler.setMicrowavePosition(slot);
            launchSystem.setDistance(distance);
        }

        @Override
        protected void stopped() {
            if (slotNum != 2) {
                StateMachine.INSTANCE.startAnonymous(new IntakeState(slotNum + 1, microwaveScoopHandler, slotTracker, launchSystem));
            } else {
                StateMachine.INSTANCE.startAnonymous(new LaunchState(0, microwaveScoopHandler, launchSystem, slotTracker));
            }

        }
    }

    class LaunchState extends StateMachine.State {

        private boolean hasLaunched = false;

        private final MicrowavePositions slot;
        private final MicrowaveScoopHandler microwaveScoopHandler;
        private final LaunchSystem launchSystem;
        private final SlotTracker slotTracker;
        private final int slotNum;

        LaunchState(int slotNum, MicrowaveScoopHandler microwaveScoopHandler, LaunchSystem launchSystem, SlotTracker slotTracker) {
            this.launchSystem = launchSystem;
            this.slotTracker = slotTracker;
            switch (slotNum) {
                case 1:
                    slot = MicrowavePositions.FIRE1;
                    break;
                case 2:
                    slot = MicrowavePositions.FIRE2;
                    break;
                default:
                    slot = MicrowavePositions.FIRE0;
            }
            this.microwaveScoopHandler = microwaveScoopHandler;
            this.slotNum = slotNum;
        }

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            if (!hasLaunched && microwaveScoopHandler.isDone() && launchSystem.isReady()) {
                microwaveScoopHandler.doScoopLaunch();
                hasLaunched = true;
            }
            if (hasLaunched && microwaveScoopHandler.isDone()) {
                slotTracker.clearSlot(slotNum);
                StateMachine.INSTANCE.stopAnonymous(this);
            }
        }

        @Override
        protected void started() {
            microwaveScoopHandler.setMicrowavePosition(slot);
            microwaveScoopHandler.toggleIntake(false);
        }

        @Override
        protected void stopped() {
            if (slotNum != 2) {
                StateMachine.INSTANCE.startAnonymous(new LaunchState(slotNum + 1, microwaveScoopHandler, launchSystem, slotTracker));
            } else {
                StateMachine.INSTANCE.startAnonymous(new IntakeState(0, microwaveScoopHandler, slotTracker, launchSystem));
            }
        }
    }


    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        TrajectoryHandler trajectoryHandler = new TrajectoryHandler();
        components.add(trajectoryHandler);
        telemetryEnabled.add(trajectoryHandler);


        components.add(StateMachine.INSTANCE);
        telemetryEnabled.add(StateMachine.INSTANCE);

        MicrowaveScoopHandler microwaveScoopHandler = new MicrowaveScoopHandler();
        components.add(microwaveScoopHandler);
//        telemetryEnabled.add(microwaveScoopHandler);
 

        LauncherHandler launcherHandler = new LauncherHandler(0.95, 1.10);
        components.add(launcherHandler);
        telemetryEnabled.add(launcherHandler);
        launcherHandler.setEnabled(true);

        ColorReader colorReader = new ColorReader("ColorSensor");
        components.add(colorReader);
//        telemetryEnabled.add(colorReader);
        BallColorInterpreter ballColorInterpreter = new BallColorInterpreter(10, 0.01);
        components.add(ballColorInterpreter);
//        telemetryEnabled.add(ballColorInterpreter);
        ballColorInterpreter.link(colorReader);

        LaunchSystem launchSystem = new LaunchSystem(RobotVersion.INSTANCE.launchSpeedInterpolater, RobotVersion.INSTANCE.launchAngleInterpolater, 4, 16, 132);
        launchSystem.link(trajectoryHandler, launcherHandler);

        SlotTracker slotTracker = new SlotTracker();
        components.add(slotTracker);
        telemetryEnabled.add(slotTracker);
        slotTracker.link(microwaveScoopHandler, ballColorInterpreter);

        AprilTagSensor aprilTagSensor = new AprilTagSensor();
        components.add(aprilTagSensor);
        telemetryEnabled.add(aprilTagSensor);

        BasketLocator basketLocator = new BasketLocator(RobotVersion.INSTANCE.aprilDistanceInterpolater);
        components.add(basketLocator);
        telemetryEnabled.add(basketLocator);
        basketLocator.link(aprilTagSensor);

        StateMachine.INSTANCE.startAnonymous(new IntakeState(0, microwaveScoopHandler, slotTracker, launchSystem));
    }
}
