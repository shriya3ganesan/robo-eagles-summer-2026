package org.firstinspires.ftc.teamcode.java;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Flywheel;
import org.firstinspires.ftc.teamcode.Intake;
import org.firstinspires.ftc.teamcode.TankDrivetrain;
@TeleOp
public class OpMODE extends OpMode {
    TankDrivetrain tankDrivetrain = new TankDrivetrain();
    Flywheel flywheel = new Flywheel();
    Intake intake = new Intake();

    double throttle, spin;
    public double goalRPM = 5000;
    @Override
    public void init() {
        tankDrivetrain.init(hardwareMap);
        flywheel.init(hardwareMap);
        intake.init(hardwareMap);

    }

    @Override
    public void loop() {
        throttle = -gamepad1.left_stick_y;
        spin = gamepad1.right_stick_x;
        tankDrivetrain.drive(throttle,spin);

        //uttakesub//
        if(gamepad1.left_bumper) {
            flywheel.setMotorRPM(goalRPM);
        }
        else {
            flywheel.stopMotorPOwer();
        }

        //intake sub//
        if(gamepad1.right_bumper) {
            intake.intake();
        }
        else if(gamepad1.right_trigger > 0.1) {
            intake.setPower(-gamepad1.right_trigger);
        }
        else {
            intake.stop();
        }
        if(gamepad1.left_trigger > 0.3) {
            intake.openServo();
        }
        else {
            intake.closeServo();
        }
        if(gamepad1.a) {
            intake.outtake();
        }



    }
}
