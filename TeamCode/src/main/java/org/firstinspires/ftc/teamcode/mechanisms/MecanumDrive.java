package org.firstinspires.ftc.teamcode.mechanisms;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
//Control HUB
//Motor 0 = left_front
//Motor 1 = right_front
//Motor 2 = left_back
//Motor 3 = right_back

//Servo 1 = launch_feeder
//Servo 2 = turret_servo

//Expansion HUB
//Motor 0 = intake
//Motor 1 = lower_launch
//Motor 2 = upper_launch
//Motor 3 =

public class MecanumDrive {

    // Declare OpMode members for each of the 4 motors.
    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;

    // TODO: likely change this back to 1
    private final double SPIN_DAMPING = 2.0;   // higher means slower turning

    public void init(HardwareMap hwMap) {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the Driver Hub.
        frontLeftDrive = hwMap.get(DcMotor.class, "left_front");
        backLeftDrive = hwMap.get(DcMotor.class, "left_back");
        frontRightDrive = hwMap.get(DcMotor.class, "right_front");
        backRightDrive = hwMap.get(DcMotor.class, "right_back");

        // Set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // This uses RUN_WITHOUT_ENCODER because we are using the dead wheels.
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // This sets the motor stop behavior
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses START)
        //telemetry.addData("Status", "Mecanum Drive Initialized");
        //telemetry.update();
    }

    public void drive(double axial, double lateral, double yaw) {

        // apply damping to spin
        yaw /= SPIN_DAMPING;

        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double frontLeftPower = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower = axial - lateral + yaw;
        double backRightPower = axial + lateral - yaw;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        double max = Math.max(Math.abs(frontLeftPower),Math.abs(frontRightPower));
        max = Math.max(max,Math.abs(backLeftPower));
        max = Math.max(max,Math.abs(backRightPower));

        if(max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send calculated power to wheels
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);

        // Show the elapsed game time and wheel power.
        //telemetry.addData("Status","Run Time: "+runtime.toString());
        //telemetry.addData("Front left/Right","%4.2f, %4.2f",frontLeftPower,frontRightPower);
        //telemetry.addData("Back  left/Right","%4.2f, %4.2f",backLeftPower,backRightPower);
        //telemetry.update();
    }

    public void testWheelDirection(boolean front_left, boolean front_right, boolean back_left, boolean back_right) {

        // This is test code:
        //
        // Each button should make the corresponding motor run FORWARD.
        //   1) First get all the motors to take to correct positions on the robot
        //      by adjusting your Robot Configuration if necessary.
        //   2) Then make sure they run in the correct direction by modifying the
        //      the setDirection() calls above.


        double frontLeftPower  = front_left ? 1.0 : 0.0; // X gamepad
        double frontRightPower = front_right ? 1.0 : 0.0; // A gamepad
        double backLeftPower   = back_left ? 1.0 : 0.0; // Y gamepad
        double backRightPower  = back_right ? 1.0 : 0.0; // B gamepad


        // Send calculated power to wheels
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }
}
