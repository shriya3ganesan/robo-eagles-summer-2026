package org.firstinspires.ftc.teamcode.TeleOP;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "Test 2026 PID Heading", group = "Test")
public class Test2026 extends LinearOpMode {
    
    // Core components
    private final ElapsedTime runtime = new ElapsedTime();
    private MecanumDrive drive;
    private GamepadEx driveGamepad1;
    private IMU imu;

    private PIDController headingPID;
    private double targetHeading = 0;

    private static final double Kp = 0.015;
    private static final double Ki = 0.0;
    private static final double Kd = 0.001;

    public void initialize() {
        // Initialize motors using MotorEx for FTCLib compatibility
        MotorEx frontLeft = new MotorEx(hardwareMap, "frontleft");
        MotorEx frontRight = new MotorEx(hardwareMap, "frontright");
        MotorEx backLeft = new MotorEx(hardwareMap, "backleft");
        MotorEx backRight = new MotorEx(hardwareMap, "backright");
        
        // Initialize the drive base
        drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);
        
        // Initialize Gamepad wrapper
        driveGamepad1 = new GamepadEx(gamepad1);
        
        // Initialize IMU with robot orientation
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        );
        imu.initialize(new IMU.Parameters(orientation));
        
        // Initialize PID Controller
        headingPID = new PIDController(Kp, Ki, Kd);
        headingPID.setTolerance(1.5); // Degree tolerance
        
        telemetry.addData("Status", "Hardware Initialized");
    }
    private double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }

    @Override
    public void runOpMode() {
        initialize();
        telemetry.update();

        waitForStart();
        runtime.reset();
        
        // Start with the current heading as the target
        targetHeading = getHeading();

        while (opModeIsActive()) {
            // Safety: Stop robot if match time expires (standard 2 mins)
            if (runtime.milliseconds() > 119000) {
                drive.stop();
                break;
            }

            double currentHeading = getHeading();

            // Press A to reset the target heading to your current orientation
            if (gamepad1.a) {
                targetHeading = currentHeading;
                headingPID.reset();
            }

            // Calculate shortest-path heading error (Angle Wrapping)
            double error = targetHeading - currentHeading;
            while (error > 180)  error -= 360;
            while (error <= -180) error += 360;

            // PID calculates turn correction based on wrapped error
            // We pass 0 as current because we already calculated the wrapped error
            double turnCorrection = headingPID.calculate(-error, 0);
            turnCorrection = Range.clip(turnCorrection, -0.5, 0.5);

            // Drive control
            double driveSpeed = 0.8;
            drive.driveRobotCentric(
                    -driveGamepad1.getLeftX() * driveSpeed,
                    -driveGamepad1.getLeftY() * driveSpeed,
                    turnCorrection // PID handles the rotation
            );

            // Telemetry output for debugging and tuning
            telemetry.addData("Heading", "Target: %.1f, Current: %.1f", targetHeading, currentHeading);
            telemetry.addData("Correction", "Error: %.1f, Power: %.2f", error, turnCorrection);
            telemetry.addData("Runtime", "%.1f seconds", runtime.seconds());
            telemetry.update();
        }
    }
}
