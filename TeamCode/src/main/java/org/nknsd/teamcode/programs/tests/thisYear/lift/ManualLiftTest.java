package org.nknsd.teamcode.programs.tests.thisYear.lift;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.BalancedLiftHandler;
import org.nknsd.teamcode.components.sensors.IMUSensor;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;

@TeleOp(name = "manual lift")
public class ManualLiftTest extends NKNProgram {

    private IMUSensor imuSensor;
    BalancedLiftHandler balancedLiftHandler;

    public class LiftTheServos extends StateMachine.State{

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
//            if (runtime.milliseconds()>startTimeMS+4000) {
//                balancedLiftHandler.startLift();
//            } else if (runtime.milliseconds()>startTimeMS+1000){
//                balancedLiftHandler.stopLift();
//            }

        }

        @Override
        protected void started() {
           balancedLiftHandler.startLift();
        }

        @Override
        protected void stopped() {

        }
    }

    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        components.add(StateMachine.INSTANCE);
        StateMachine.INSTANCE.startAnonymous(new LiftTheServos());

        imuSensor = new IMUSensor(/*new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)*/);
        components.add(imuSensor);
        telemetryEnabled.add(imuSensor);

        balancedLiftHandler = new BalancedLiftHandler();
        balancedLiftHandler.link(imuSensor);
        components.add(balancedLiftHandler);
        telemetryEnabled.add(balancedLiftHandler);


    }
}
