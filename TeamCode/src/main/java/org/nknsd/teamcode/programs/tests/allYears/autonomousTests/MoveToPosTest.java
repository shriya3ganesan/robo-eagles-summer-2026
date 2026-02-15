package org.nknsd.teamcode.programs.tests.allYears.autonomousTests;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.autoStates.AutoMoveToPosState;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.motormixers.AbsolutePowerMixer;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.motormixers.MecanumMotorMixer;
import org.nknsd.teamcode.components.motormixers.PowerInputMixer;
import org.nknsd.teamcode.components.sensors.FlowSensor;
import org.nknsd.teamcode.components.utility.PositionTransform;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;


import java.util.List;

@TeleOp(name = "Move To Position", group = "Tests") @Disabled

public class MoveToPosTest extends NKNProgram {


    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
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

        absolutePowerMixer.link(mecanumMotorMixer, absolutePosition);
        powerInputMixer.link(absolutePowerMixer, mecanumMotorMixer);

        AutoPositioner autoPositioner = new AutoPositioner();

        components.add(autoPositioner);
        autoPositioner.link(powerInputMixer, absolutePosition);

        components.add(StateMachine.INSTANCE);
        PositionTransform transform = new PositionTransform(0, 0, 0, 1, 1, 1);
        StateMachine.INSTANCE.addState("start", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(0, 0, 0), 0.1, 0.1, 0.1, 0.05, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"next"}));
        StateMachine.INSTANCE.addState("next", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(20, 0, Math.PI), 0.1, 0.1, 0.1, 0.05, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"then"}));
        StateMachine.INSTANCE.addState("then", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(20, 20, 0), 0.1, 0.1, 0.1, 0.05, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"final"}));
        StateMachine.INSTANCE.addState("final", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(0, 20, -Math.PI), 0.1, 0.1, 0.1, 0.05, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"start"}));

        StateMachine.INSTANCE.startState("start");
    }
}
