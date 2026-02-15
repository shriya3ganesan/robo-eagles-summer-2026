package org.nknsd.teamcode.programs.tests.thisYear.firing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.launch.FiringSystem;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.handlers.vision.BasketLocator;
import org.nknsd.teamcode.components.handlers.vision.ID;
import org.nknsd.teamcode.components.handlers.launch.LaunchSystem;
import org.nknsd.teamcode.components.handlers.launch.LauncherHandler;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.handlers.vision.TargetingSystem;
import org.nknsd.teamcode.components.handlers.launch.TrajectoryHandler;
import org.nknsd.teamcode.components.handlers.color.BallColorInterpreter;
import org.nknsd.teamcode.components.handlers.color.ColorReader;
import org.nknsd.teamcode.components.motormixers.AbsolutePowerMixer;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.motormixers.MecanumMotorMixer;
import org.nknsd.teamcode.components.motormixers.PowerInputMixer;
import org.nknsd.teamcode.components.sensors.AprilTagSensor;
import org.nknsd.teamcode.components.sensors.FlowSensor;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;
import org.nknsd.teamcode.states.TimerState;

import java.util.List;

@TeleOp(name = "firing system test", group = "Tests")
public class FiringSystemTest extends NKNProgram {

    LaunchSystem launchSystem = new LaunchSystem(RobotVersion.INSTANCE.launchSpeedInterpolater, RobotVersion.INSTANCE.launchAngleInterpolater, 2, 16, 132);

    class FireAllState extends StateMachine.State {

        private final FiringSystem firingSystem;

        FireAllState(FiringSystem firingSystem) {
            this.firingSystem = firingSystem;
        }

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            if (launchSystem.isReady()) {
                firingSystem.fireAll();
                StateMachine.INSTANCE.stopAnonymous(this);
            }
        }

        @Override
        protected void started() {
//            firingSystem.setTargetColor(ID.BLUE);
        }

        @Override
        protected void stopped() {
            StateMachine.INSTANCE.startAnonymous(new TimerState(5000, new String[]{"fire all"}, new String[]{}));
        }
    }


    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {

        components.add(StateMachine.INSTANCE);
        telemetryEnabled.add(StateMachine.INSTANCE);


        TrajectoryHandler trajectoryHandler = new TrajectoryHandler();
        components.add(trajectoryHandler);
        telemetryEnabled.add(trajectoryHandler);

        LauncherHandler launcherHandler = new LauncherHandler(0.95, 1.10);
        components.add(launcherHandler);
        telemetryEnabled.add(launcherHandler);
        launcherHandler.setEnabled(true);


        MicrowaveScoopHandler microwaveScoopHandler = new MicrowaveScoopHandler();
        components.add(microwaveScoopHandler);

        SlotTracker slotTracker = new SlotTracker();
        components.add(slotTracker);
        telemetryEnabled.add(slotTracker);

        ArtifactSystem artifactSystem = new ArtifactSystem();

        ColorReader colorReader = new ColorReader("ColorSensor");
        components.add(colorReader);
        BallColorInterpreter ballColorInterpreter = new BallColorInterpreter(10, 0.01);
        components.add(ballColorInterpreter);


        FlowSensor flowSensor1 = new FlowSensor("RODOS");
        components.add(flowSensor1);
        FlowSensor flowSensor2 = new FlowSensor("LODOS");
        components.add(flowSensor2);
        AbsolutePosition absolutePosition = new AbsolutePosition(flowSensor1, flowSensor2);
        components.add(absolutePosition);
        telemetryEnabled.add(absolutePosition);

        MecanumMotorMixer mecanumMotorMixer = new MecanumMotorMixer();
        components.add(mecanumMotorMixer);
        telemetryEnabled.add(mecanumMotorMixer);

        AbsolutePowerMixer absolutePowerMixer = new AbsolutePowerMixer();
        components.add(absolutePowerMixer);
        absolutePowerMixer.link(mecanumMotorMixer, absolutePosition);

        PowerInputMixer powerInputMixer = new PowerInputMixer();
        components.add(powerInputMixer);

        AutoPositioner autoPositioner = new AutoPositioner();
        components.add(autoPositioner);


        AprilTagSensor aprilTagSensor = new AprilTagSensor();
        components.add(aprilTagSensor);
        telemetryEnabled.add(aprilTagSensor);

        BasketLocator basketLocator = new BasketLocator(RobotVersion.INSTANCE.aprilDistanceInterpolater);
        components.add(basketLocator);
        telemetryEnabled.add(basketLocator);


        TargetingSystem targetingSystem = new TargetingSystem();
        components.add(targetingSystem);
        telemetryEnabled.add(targetingSystem);
        targetingSystem.setTargetingColor(ID.BLUE);


        FiringSystem firingSystem = new FiringSystem();
        components.add(firingSystem);
        telemetryEnabled.add(firingSystem);


        slotTracker.link(microwaveScoopHandler, ballColorInterpreter);
        targetingSystem.link(basketLocator, absolutePosition, autoPositioner);
        basketLocator.link(aprilTagSensor);
        powerInputMixer.link(absolutePowerMixer, mecanumMotorMixer);
        ballColorInterpreter.link(colorReader);
        launchSystem.link(trajectoryHandler, launcherHandler);
        artifactSystem.link(microwaveScoopHandler, slotTracker, launchSystem);
        firingSystem.link(launchSystem, targetingSystem, artifactSystem);
        autoPositioner.link(powerInputMixer, absolutePosition);


        StateMachine.INSTANCE.addState("fire all", new FireAllState(firingSystem));
        StateMachine.INSTANCE.startState("fire all");
    }
}
