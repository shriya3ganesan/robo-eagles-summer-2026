package org.firstinspires.ftc.teamcode.robot;

public class Drivetrain {

    private RobotHardware robot;

    public Drivetrain(RobotHardware robot) {
        this.robot = robot;
    }

    /** Call every loop with joystick inputs (and yaw already overridden if lock-on is active) */
    public void drive(double axial, double lateral, double yaw) {
        double frontLeftPower  = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower   = axial - lateral + yaw;
        double backRightPower  = axial + lateral - yaw;

        // Normalize so no wheel exceeds 100%
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower  /= max;
            frontRightPower /= max;
            backLeftPower   /= max;
            backRightPower  /= max;
        }

        robot.frontLeftDrive.setPower(frontLeftPower);
        robot.frontRightDrive.setPower(frontRightPower);
        robot.backLeftDrive.setPower(backLeftPower);
        robot.backRightDrive.setPower(backRightPower);
    }

    public void stop() {
        drive(0, 0, 0);
    }

    public void driveForward(double power, long ms) throws InterruptedException {
        drive(power, 0, 0);
        Thread.sleep(ms);
        stop();
    }
}