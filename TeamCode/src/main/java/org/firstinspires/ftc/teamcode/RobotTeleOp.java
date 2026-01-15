package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;

@TeleOp
@Disabled
public class RobotTeleOp extends OpMode {

    MecanumDrive drive = new MecanumDrive();
    Launcher launcher = new Launcher();
    Intake intake = new Intake();

    @Override
    public void init() {
        drive.init(hardwareMap);
        launcher.init(hardwareMap);
        intake.init(hardwareMap);
    }

    @Override
    public void loop() {

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        // Note: pushing left stick forward gives negative value
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        // The user has control of the speed of launcher motor without automatically
        // queuing a shot.
        if (gamepad2.leftStickButtonWasPressed()) {
            launcher.startLauncher();
        }
        else if (gamepad2.rightStickButtonWasPressed()) {
            launcher.stopLauncher();
        }

         if (gamepad2.aWasPressed()) {
             launcher.incrementLaunchSpeed();
         } else if (gamepad2.bWasPressed()) {
             launcher.decrementLaunchSpeed();
         }


        //For Intake (test if same buttons works)
        if (gamepad1.right_trigger !=0 ) {
            intake.startIntake();
        } else if (gamepad1.left_trigger !=0) {
            intake.reverseIntake();
        } else {
            intake.stopIntake();
        }

        // update launcher state machine
        launcher.updateState();

        telemetry.addData("State", launcher.getState());
        telemetry.addData("Upper Launch Velocity", launcher.getUpperVelocity());
        telemetry.addData("Lower Launch Velocity", launcher.getLowerVelocity());
        telemetry.addData("Target Velocity", launcher.getTargetLaunchSpeed());
    }
}


