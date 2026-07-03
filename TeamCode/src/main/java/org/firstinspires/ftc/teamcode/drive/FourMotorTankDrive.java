package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "4M Tank Drive")
public class FourMotorTankDrive extends OpMode {
    /*
     * These four variables are motor objects.
     * They start out empty, then init() connects them to the real motors
     * listed in the Driver Station robot configuration.
     */
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void init() {
        /*
         * 1. Get each motor from the hardware map.
         *
         * The names in quotes must match the robot configuration exactly:
         * - "frontLeft"
         * - "frontRight"
         * - "backLeft"
         * - "backRight"
         *
         * If one name is spelled differently in the Driver Station config,
         * the OpMode will stop with a hardware error.
         */
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        /*
         * 2. Reverse the right side motors.
         *
         * Motors on opposite sides of a drivetrain face opposite directions.
         * Without reversing one side, giving both sides positive power would
         * make one side drive forward and the other side drive backward.
         */
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
         * 3. Tell the motors to brake when power is 0.
         *
         * BRAKE makes the robot resist rolling after the joysticks are released.
         * The other common option is FLOAT, which lets the robot coast.
         */
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*
         * 4. Run the drive motors without encoders.
         *
         * This is simple driver control: joystick power goes straight to the motors.
         * Encoders are not being used here to move an exact distance.
         */
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Show a message on the Driver Station after INIT is pressed.
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        /*
         * 5. Read the joysticks.
         *
         * FTC gamepad stick Y values are negative when pushed forward, so the
         * minus sign changes forward stick movement into positive motor power.
         *
         * Range.clip keeps the value between -1.0 and 1.0, which is the safe
         * power range for DcMotor.setPower().
         */
        double leftPower = Range.clip(-gamepad1.left_stick_y, -1.0, 1.0);
        double rightPower = Range.clip(-gamepad1.right_stick_y, -1.0, 1.0);

        /*
         * 6. Send the left joystick power to both left motors.
         * Send the right joystick power to both right motors.
         *
         * This is called tank drive because each joystick controls one side
         * of the drivetrain, like the treads on a tank.
         */
        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);

        /*
         * 7. Print useful values on the Driver Station.
         * This helps you confirm that the joystick inputs are reaching the code.
         */
        telemetry.addData("Status", "Running");
        telemetry.addData("Left Power", leftPower);
        telemetry.addData("Right Power", rightPower);
    }
}
