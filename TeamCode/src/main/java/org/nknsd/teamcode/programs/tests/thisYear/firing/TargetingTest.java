package org.nknsd.teamcode.programs.tests.thisYear.firing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.handlers.vision.BasketLocator;
import org.nknsd.teamcode.components.handlers.vision.ID;
import org.nknsd.teamcode.components.handlers.vision.TargetingSystem;
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

import java.util.List;

@TeleOp(name = "Targeting Test", group = "Tests")
public class TargetingTest extends NKNProgram {

    class Targeting extends StateMachine.State{

        private final TargetingSystem targetingSystem;

        Targeting(TargetingSystem targetingSystem) {
            this.targetingSystem = targetingSystem;
        }

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            targetingSystem.enableAutoTargeting(true);
        }

        @Override
        protected void started() {

        }

        @Override
        protected void stopped() {

        }
    }


    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        RobotVersion.setRobotAlliance(ID.BLUE);
        RobotVersion.setIsAutonomous(true);

        components.add(StateMachine.INSTANCE);
        telemetryEnabled.add(StateMachine.INSTANCE);

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
        telemetryEnabled.add(autoPositioner);


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


        absolutePowerMixer.link(mecanumMotorMixer, absolutePosition);
        powerInputMixer.link(absolutePowerMixer, mecanumMotorMixer);
        autoPositioner.link(powerInputMixer, absolutePosition);
        basketLocator.link(aprilTagSensor);
        targetingSystem.link(basketLocator, absolutePosition, autoPositioner);

        StateMachine.INSTANCE.startAnonymous( new Targeting(targetingSystem));
    }
}
