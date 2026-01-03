/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
/*
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 */

@TeleOp(name="AprilTagDT2limelightOneController", group="Linear OpMode")

public class AprilTagDTOneController extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime launchTimer = new ElapsedTime();
    private ElapsedTime intakeJitterTimer = new ElapsedTime();
    private static final boolean USE_WEBCAM = true;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor intakeMotor = null;
    private DcMotor launchMotor = null;
    private Servo Trigger = null;
    private Servo push = null;
    private DcMotor transferMotor = null;
    GoBildaPinpointDriver pinpoint;
    private Limelight3A limelight;

    // Launch sequence states
    private enum LaunchState {

        IDLE,
        SPINNING_UP,
        FIRING,
        RESETTING
    }
    private LaunchState launchState = LaunchState.IDLE;

    // Intake jitter states
    private enum IntakeJitterState {
        IDLE,
        REVERSE,
        FORWARD
    }
    private IntakeJitterState intakeJitterState = IntakeJitterState.IDLE;

    // Servo positions - adjust these based on your robot
    private final double TRIGGER_START_POS = 0;
    private final double TRIGGER_FIRE_POS = 300;
    private final double PUSH_START_POS = 0;
    private final double PUSH_FIRE_POS = 300;


    // Timing constants (in seconds)
    private final double SPINUP_TIME_SHORT = 1.1;  // Left trigger - quick shot
    private final double SPINUP_TIME_LONG = 1.9;   // Right trigger - power shot
    private final double FIRE_TIME = 0.3;          // Time trigger is in fire position
    private final double RESET_TIME = 0.3;         // Time for trigger to reset
    private double currentSpinupTime = SPINUP_TIME_SHORT; // Track which shot type we're using

    // Distance-based spinup time constants
    private final double MIN_DISTANCE = 55.0;  // inches - minimum shooting distance
    private final double MAX_DISTANCE = 140.0; // inches - maximum shooting distance
    private final double MIN_SPINUP = 0.71;     // seconds - spinup time at MIN_DISTANCE
    private final double MAX_SPINUP = 2.18;     // seconds - spinup time at MAX_DISTANCE

    // Intake jitter timing
    private final double JITTER_REVERSE_TIME = 0.05;
    private final double JITTER_FORWARD_TIME = 0.10;

    // AprilTag lock-on constants
    private final double TAG_LOCK_KP = 0.05;        // Proportional gain for rotation
    private final double TAG_LOCK_TOLERANCE = 0.7;  // Degrees - how close is "locked on"
    private final double TAG_LOCK_MIN_POWER = 0.00; // Minimum rotation power
    private final double TAG_LOCK_MAX_POWER = 0.4;  // Maximum rotation power

    private double tagX = 0.0;
    private double tagY = 0.0;
    private double tagZ = 0.0;

    private double tagPitch = 0.0;
    private double tagRoll = 0.0;
    private double tagYaw = 0.0;

    private double tagRange = 0.0;
    private double tagBearing = 0.0;
    private double tagElevation = 0.0;

    private int tagId = -1;        // ID of the tag being tracked
    private boolean tagVisible = false; // true if a tag was seen this frame
    private boolean isLockedOn = false; // true if aligned within tolerance
    double distance;

    @Override
    public void runOpMode() {
        //configurePinpoint();

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        launchMotor = hardwareMap.get(DcMotor.class, "launch_motor");
        Trigger = hardwareMap.get(Servo.class, "Trigger");
        push = hardwareMap.get(Servo.class, "push");
        transferMotor = hardwareMap.get(DcMotor.class, "transfer");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        launchMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        Trigger.setDirection(Servo.Direction.FORWARD);
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        // Wait for the game to start (driver presses START)
        telemetry.addData("Status: ", "Initialized");
        telemetry.update();

        // Set initial positions
        Trigger.setPosition(TRIGGER_START_POS);
        push.setPosition(PUSH_START_POS);

        waitForStart();
        runtime.reset();

        limelight.start();
        limelight.pipelineSwitch(0);
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //updates the telemetry for the camera
            LLResult result = limelight.getLatestResult();
            if (result != null) {
                if (result.isValid()) {
                    Pose3D botpose = result.getBotpose();
                    telemetry.addData("Lock On: ", "AVAILABLE");
                    telemetry.addData("tX: ", result.getTx());
                    telemetry.addData("tY:", result.getTy());
                    telemetry.addData("Orientation: ", botpose.getOrientation());
                    distance = (29.5 - 12.25)/(Math.tan(result.getTy() * (3.14159 / 180)));
                    telemetry.addData("distance: ", distance);
                }
                else{
                    telemetry.addData("no limelight read", ":(");
                }
            }

            double max;

            // ===== GAMEPAD 1 (DRIVER) CONTROLS =====
            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            // ===== APRILTAG LOCK-ON FEATURE =====
            // Left bumper activates AprilTag lock-on mode
            if (gamepad1.left_trigger > 0.02 && result != null && result.isValid()) {
                // Get horizontal offset from Limelight (tx)
                double tx = result.getTx();

                // Check if we're within tolerance
                isLockedOn = Math.abs(tx) < TAG_LOCK_TOLERANCE;

                // Calculate proportional correction
                double rotationCorrection = tx * TAG_LOCK_KP;

                // Clamp the correction to min/max power
                rotationCorrection = Range.clip(rotationCorrection, -TAG_LOCK_MAX_POWER, TAG_LOCK_MAX_POWER);

                // Apply minimum power if not locked on
                if (!isLockedOn && Math.abs(rotationCorrection) < TAG_LOCK_MIN_POWER) {
                    rotationCorrection = TAG_LOCK_MIN_POWER * Math.signum(rotationCorrection);
                }
// Check if we're within tolerance
                isLockedOn = Math.abs(tx) < TAG_LOCK_TOLERANCE;

// Calculate proportional correction

// Add deadband - don't correct if we're close enough
                if (isLockedOn) {
                    rotationCorrection = 0.0;  // Stop correcting when locked on
                }
                else {
                    // Clamp the correction to min/max power
                    rotationCorrection = Range.clip(rotationCorrection, -TAG_LOCK_MAX_POWER, TAG_LOCK_MAX_POWER);

                    // Apply minimum power if not locked on
                    if (Math.abs(rotationCorrection) < TAG_LOCK_MIN_POWER) {
                        rotationCorrection = TAG_LOCK_MIN_POWER * Math.signum(rotationCorrection);
                    }
                }
                // Override yaw with our correction
                yaw = -rotationCorrection;

                tagVisible = true;
                telemetry.addData("TAG LOCK", "ACTIVE");
                telemetry.addData("Lock Status", isLockedOn ? "🟢 LOCKED ON" : "🟡 ALIGNING...");
                telemetry.addData("TX Offset", "%.2f", tx);
                telemetry.addData("Yaw Correction", "%.2f", yaw);
            }
            else {
                // Normal manual control
                yaw = gamepad1.right_stick_x;
                tagVisible = false;
                isLockedOn = false;

                if (gamepad1.left_trigger > 0.02) {
                    telemetry.addData("Tag Lock", "No AprilTag visible");
                }
            }

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            double frontLeftPower  = (axial + lateral + yaw) * 1.25;
            double frontRightPower = (axial - lateral - yaw) * 0.75;
            double backLeftPower   = (axial - lateral + yaw) * 1.25;
            double backRightPower  = (axial + lateral - yaw) * 0.75;

            // Normalize the values so no wheel power exceeds 100%
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (gamepad1.dpad_up) {
                push.setPosition(PUSH_FIRE_POS);
            }
            else {
                push.setPosition(PUSH_START_POS);
            }

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            // Send calculated power to wheels - ALWAYS UPDATE, NEVER BLOCK
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);


            // ===== GAMEPAD 2 (OPERATOR) CONTROLS =====

            // ===== INTAKE CONTROL =====
            // Handle intake jitter state machine first (X button)
            switch (intakeJitterState) {
                case IDLE:
                    if (gamepad1.x) {
                        intakeJitterState = IntakeJitterState.REVERSE;
                        intakeJitterTimer.reset();
                    }
                    break;

                case REVERSE:
                    if (intakeJitterTimer.seconds() >= JITTER_REVERSE_TIME) {
                        intakeJitterState = IntakeJitterState.FORWARD;
                        intakeJitterTimer.reset();
                    }
                    break;

                case FORWARD:
                    if (intakeJitterTimer.seconds() >= JITTER_FORWARD_TIME) {
                        intakeJitterState = IntakeJitterState.IDLE;
                    }
                    break;
            }

            // Now handle all intake controls based on button priority
            if (intakeJitterState == IntakeJitterState.REVERSE) {
                // Jitter mode - reverse phase
                intakeMotor.setPower(-1.0);
            }
            else if (intakeJitterState == IntakeJitterState.FORWARD) {
                // Jitter mode - forward phase
                intakeMotor.setPower(1.0);
            }
            else if (gamepad1.a) {
                intakeMotor.setPower(1.0);
                transferMotor.setPower(1);
            }
            else if (gamepad1.y) {
                intakeMotor.setPower(-1.0);
                transferMotor.setPower(1);
            }
            else if (gamepad1.b) {
                // B button: Slow intake
                intakeMotor.setPower(0.5);
            }
            else {
                // No buttons pressed: Stop everything
                intakeMotor.setPower(0.0);
                transferMotor.setPower(0);

            }

            // ===== LAUNCH SEQUENCE CONTROL =====
            // State machine for launch sequence
            switch (launchState) {
                case IDLE:
                    if (gamepad1.right_trigger > 0.02) {
                        LLResult llResult = limelight.getLatestResult();

                        if (llResult != null && llResult.isValid()) {
                            // Get the botpose which includes distance information
                            Pose3D botpose = llResult.getBotpose();

                            // Calculate distance to target (using X-Z plane distance)
                            // The Pose3D gives us the robot's position relative to the AprilTag
                            double distanceX = botpose.getPosition().x;
                            double distanceZ = botpose.getPosition().z;
                            telemetry.addData("DistanceX", distanceX);
                            telemetry.addData("DistanceY", distanceZ);
                            double distanceInches = distance;
                            telemetry.addData("Distance in inches", distanceInches);

                            // Calculate required spinup time
                            currentSpinupTime = calculateSpinupTime(distanceInches);

                            launchState = LaunchState.SPINNING_UP;
                            launchTimer.reset();
                            launchMotor.setPower(1.0);

                            telemetry.addData("Launch", "Auto Shot - Distance: %.1f in", distanceInches);
                            telemetry.addData("Spinup Time", "%.2f seconds", currentSpinupTime);
                        } else {
                            // No AprilTag visible - use default long spinup
                            telemetry.addData("Launch ERROR", "No AprilTag visible!");
                        }
                    }
                    break;

                case SPINNING_UP:
                    // Wait for motor to spin up (uses currentSpinupTime)
                    if (launchTimer.seconds() >= currentSpinupTime) {
                        launchState = LaunchState.FIRING;
                        launchTimer.reset();
                        Trigger.setPosition(TRIGGER_FIRE_POS);
                        telemetry.addData(" Launch", "FIRING!");
                    }
                    else {
                        telemetry.addData(" Launch", "Spinning... %.1fs", launchTimer.seconds());
                    }
                    break;

                case FIRING:
                    // Wait for ball to launch
                    if (launchTimer.seconds() >= FIRE_TIME) {
                        launchState = LaunchState.RESETTING;
                        launchTimer.reset();
                        Trigger.setPosition(TRIGGER_START_POS);
                        launchMotor.setPower(0.0);
                        telemetry.addData(" Launch", "Resetting...");
                    }
                    break;

                case RESETTING:
                    // Wait for trigger to reset, then return to idle
                    if (launchTimer.seconds() >= RESET_TIME) {
                        launchState = LaunchState.IDLE;
                        telemetry.addData("Launch", "Ready");
                    }
                    break;
            }

            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("Launch State", launchState);
            telemetry.addData("distance: ", distance);
            telemetry.addData("Spinup Time", "%.2f seconds", currentSpinupTime);
            telemetry.update();

        }
    }

    /**
     * Calculates the required spinup time based on distance to target
     * @param distanceInches Distance to AprilTag in inches
     * @return Spinup time in seconds
     */
    private double calculateSpinupTime(double distanceInches) {
        // Clamp distance to valid range
        double clampedDistance = Range.clip(distanceInches, MIN_DISTANCE, MAX_DISTANCE);

        // Linear interpolation between min and max spinup times
        double normalizedDistance = (clampedDistance - MIN_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE);
        double spinupTime = MIN_SPINUP + (normalizedDistance * (MAX_SPINUP - MIN_SPINUP));

        return spinupTime;
    }

    public void configurePinpoint(){
        pinpoint.setOffsets(-84.0, -168.0, DistanceUnit.MM);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU();
    }

    // Helper to convert degrees (0–300 typical) to normalized 0.0–1.0 servo position
    private static double degToPos(double degrees) {
        // goBILDA servos typically rotate ~300 degrees over full 0–1 range
        return Range.clip(degrees / 300.0, 0.0, 1.0);
    }

    private void driveForward(double power, long ms) {
        frontLeftDrive.setPower(power);
        frontRightDrive.setPower(power);
        backLeftDrive.setPower(power);
        backRightDrive.setPower(power);
        sleep(ms);
    }

    private void stopDrive() {
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }

}
