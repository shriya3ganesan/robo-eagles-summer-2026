package org.firstinspires.ftc.team28420;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.team28420.config.CameraConf;
import org.firstinspires.ftc.team28420.config.GamepadConf;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.module.Actions;
import org.firstinspires.ftc.team28420.module.shooter.Shooter;
import org.firstinspires.ftc.team28420.types.AprilTag;

@TeleOp(name = "Shooter Speed test", group = "New Actions")
public class ShooterSpeedTest extends LinearOpMode {
    private boolean dpadPressed = false;
    Shooter shooter;

    public void initialize() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        shooter = new Shooter(hardwareMap, telemetry);
        shooter.setup();
    }


    private void handleRevolverInput() {
        if (!dpadPressed) {
            if (gamepad1.dpad_left) shooter.rotateRevolver(-120);
            if (gamepad1.dpad_right) shooter.rotateRevolver(120);
            if (gamepad1.dpad_up)  shooter.pushBall(true);
            if (gamepad1.dpad_down)  shooter.pushBall(false);
        }
        dpadPressed = (gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up || gamepad1.dpad_down);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        ShooterConf.TARGET_MOTIF = null;
        shooter.toggleManualControl(true);

        while (opModeIsActive()) {
            if(gamepad1.right_trigger > 0.2) {
                shooter.pushBall(true);
                shooter.setVelocityCoefficient(gamepad1.right_trigger);
            } else {
                shooter.pushBall(false);
                shooter.setVelocityCoefficient(0);
            }
            if(gamepad1.circle) shooter.setPids();
            if (gamepad1.cross) shooter.rotateRevolver(-1);
            if (gamepad1.triangle) shooter.rotateRevolver(1);

            handleRevolverInput();
            shooter.log(telemetry);
            telemetry.update();
        }
    }

}
