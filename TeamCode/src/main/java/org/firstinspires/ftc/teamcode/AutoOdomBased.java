package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.Util.Enum.PossibleStates;
import org.firstinspires.ftc.teamcode.Util.Motion;
import org.firstinspires.ftc.teamcode.Util.RPMEstimator;
import org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions;

import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.*;

/** TODO: implement a way to tell where to hit the gate and where to do so
 *        implement a way to park near end of auto
 *        test current op mode and check for bugs or errors
 *        implement a way to check pattern status in gate
 *        check all odom locations
 *        check all motor powers
 *        check all distances
 *        check how to implement motor built in PID using set velocity
 *        check how to use PID to make this better
 *        make it get more balls after shooting the three it has
 *        make it shoot the balls in the desired pattern instead of just in order*/

@Autonomous(name="AutoOdomBased", group="Auto")
public class AutoOdomBased extends LinearOpMode {

    // hardware
    private Limelight3A limelight;
    private GoBildaPinpointDriver odomhub;
    private Motion motion;
    private DcMotorEx launcherMotor;
    private DcMotor scooperMotor;
    private Servo drumServo;
    private Servo firingPinServo;
    private NormalizedColorSensor colorSensor1;
    private NormalizedColorSensor colorSensor2;
    private DcMotor BR, BL, FR, FL;

    // states - now using the enum from PossibleStates.java
    private PossibleStates state = PossibleStates.INITIALIZE;
    private final ElapsedTime stateTimer = new ElapsedTime();

    // constants
    private static final int PIPELINE_SCORING = 0;
    private static final int PIPELINE_PATTERN = 1;
    private static final int PIPELINE_BALL = 4;
    private static final double LIMELIGHT_MIN_RANGE_METERS = 0.9144; // 3 feet
    private static final double MAX_SHOOTING_RANGE = 3.7; // meters

    private static final double[] DRUM_FIRE_POSITIONS = {0.1, 0.42, 0.76};
    private static final double[] DRUM_LOAD_POSITIONS = {0.27, 0.6, 0.92};
    private static final double FIRING_PIN_NULL = 0.98;
    private static final double FIRING_PIN_FIRE = FIRING_PIN_NULL - 0.32;

    // I'm storing it
    private double targetDistanceMeters = 0;
    private int targetBallSlot = 0;
    private final Balls targetBallColor = Balls.green;

    // TUNING CONSTANTS - Check these values during testing
    private static final double DRIVE_POWER = 0.45; // {check} motor power for driving
    private static final double TURN_POWER = 0.3;   // {check} motor power for turning
    private static final double SPINUP_TIMEOUT = 3000; // {check} max time to spin up launcher

    @Override
    public void runOpMode() {
        initializeHardware();

        // ALLIANCE SELECTION
        telemetry.addLine("SELECT ALLIANCE");
        telemetry.addLine("Press X for BLUE, B for RED");
        telemetry.addLine("Current: RED (default)");
        telemetry.update();

        boolean isBlueSelected = false;
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.x) {
                isBlueSelected = true;
                telemetry.addLine("BLUE alliance selected");
                telemetry.update();
                sleep(200);
            }
            if (gamepad1.b) {
                isBlueSelected = false;
                telemetry.addLine("RED alliance selected");
                telemetry.update();
                sleep(200);
            }
        }

        final boolean isRedAlliance = !isBlueSelected;

        telemetry.clearAll();
        telemetry.addData("Alliance", isRedAlliance ? "RED" : "BLUE");
        telemetry.addData("Target Ball", targetBallColor);
        telemetry.addData("Status", "Auto Ready");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            executeStateMachine(isRedAlliance);
        }
    }

    private void initializeHardware() {
        // Odometry
        odomhub = hardwareMap.get(GoBildaPinpointDriver.class, "odomhub");
        odomhub.initialize();
        odomhub.resetPosAndIMU();

        // Drive motors
        BR = hardwareMap.get(DcMotor.class, "BR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        FL = hardwareMap.get(DcMotor.class, "FL");
        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);

        // Motion utility - now using the Motion.java class
        motion = new Motion(this, odomhub, FL, FR, BL, BR);

        // Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();

        // Launcher
        launcherMotor = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        launcherMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Scooper
        scooperMotor = hardwareMap.get(DcMotorEx.class, "Scooper");

        // Servos
        drumServo = hardwareMap.get(Servo.class, "DrumServo");
        firingPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        // Color sensors
        colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");

        // Initial positions
        drumServo.setPosition(DRUM_LOAD_POSITIONS[0]);
        firingPinServo.setPosition(FIRING_PIN_NULL);
    }

    private void executeStateMachine(boolean isRedAlliance) {
        switch (state) {
            case INITIALIZE:
                telemetry.addData("State", "INITIALIZE");
                telemetry.update();
                sleep(500);
                state = PossibleStates.DRIVE_TO_SHOOT_POSITION;
                break;

            case DRIVE_TO_SHOOT_POSITION:
                telemetry.addData("State", "DRIVE TO SHOOT POSITION");
                // {check} if this is right
                motion.driveTo(2.0, 1.5, 0.05, 6.0);
                motion.turnTo(Math.toRadians(45), Math.toRadians(3), 3.0);
                state = PossibleStates.DETECT_SCORING_TAG;
                break;

            case DETECT_SCORING_TAG:
                telemetry.addData("State", "DETECT SCORING TAG");
                limelight.pipelineSwitch(PIPELINE_SCORING);
                sleep(500);

                // Simplified: try to find tag directly, fallback to search if needed
                LLResult result = limelight.getLatestResult();
                if (result != null && result.isValid() && result.getFiducialResults() != null) {
                    for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                        int tagId = tag.getFiducialId();

                        // {check} Update these tag IDs for your field - these are likely correct for Centerstage
                        boolean isTargetTag = (isRedAlliance && tagId == 20) ||
                                (!isRedAlliance && tagId == 24);

                        if (isTargetTag) {
                            Pose3D botpose = tag.getRobotPoseFieldSpace();
                            targetDistanceMeters = calculateDistanceToTarget(botpose);
                            telemetry.addData("Target Tag", tagId);
                            telemetry.addData("Distance", "%.2f m", targetDistanceMeters);

                            if (targetDistanceMeters > LIMELIGHT_MIN_RANGE_METERS &&
                                    targetDistanceMeters <= MAX_SHOOTING_RANGE) {
                                state = PossibleStates.CALCULATE_AND_SPIN_UP;
                                return; // Exit early on success
                            }
                        }
                    }
                }

                // If we get here, target tag wasn't found - try searching
                telemetry.addData("WARN", "Target tag not found - searching...");
                if (searchForScoringTag(isRedAlliance, 2.0)) {
                    state = PossibleStates.CALCULATE_AND_SPIN_UP;
                } else {
                    telemetry.addData("ERROR", "Tag not found after search");
                    // TODO: Add recovery - maybe drive to default shooting position
                    targetDistanceMeters = 2.2; // {check} if this is correct at all
                    state = PossibleStates.CALCULATE_AND_SPIN_UP;
                }
                telemetry.update();
                break;

            case CALCULATE_AND_SPIN_UP:
                telemetry.addData("State", "CALCULATE AND SPIN UP");
                try {
                    double requiredRPM = RPMEstimator.calculateRequiredRPM(targetDistanceMeters);
                    double ticksPerSec = RPMEstimator.wheelRPMToEncoderVelocity(requiredRPM);
                    double commandRadPerSec = (ticksPerSec / ENCODER_TICKS_PER_OUTPUT_REV) * 2 * Math.PI;

                    telemetry.addData("Required RPM", "%.0f", requiredRPM);
                    telemetry.addData("Command (rad/s)", "%.2f", commandRadPerSec);

                    // Motor built-in PID: RUN_USING_ENCODER + setVelocity() uses hardware PID automatically
                    // No custom PID needed for velocity control!
                    launcherMotor.setVelocity(commandRadPerSec, AngleUnit.RADIANS);

                    boolean atSpeed = false;
                    stateTimer.reset();

                    while (opModeIsActive() && stateTimer.milliseconds() < SPINUP_TIMEOUT) {
                        if (isAtTargetRPM(requiredRPM)) {
                            atSpeed = true;
                            break;
                        }
                        telemetry.addData("RPM", getCurrentRPM());
                        telemetry.update();
                    }

                    if (!atSpeed) {
                        telemetry.addLine("WARN: Shooter didn't get to speed firing anyway");
                    }

                    state = PossibleStates.FIND_CORRECT_BALL;
                } catch (Exception e) {
                    telemetry.addData("ERROR", e.getMessage());
                    telemetry.update();
                    sleep(2000);
                    // TODO: Add recovery - maybe move to different position
                }
                break;

            case FIND_CORRECT_BALL:
                telemetry.addData("State", "FIND CORRECT BALL");
                limelight.pipelineSwitch(PIPELINE_BALL);
                sleep(500);

                boolean found = false;

                // Retry logic - 2 attempts to find ball
                for (int attempt = 0; attempt < 2 && !found; attempt++) {
                    for (int slot = 0; slot < 3; slot++) {
                        drumServo.setPosition(DRUM_LOAD_POSITIONS[slot]);
                        sleep(250); // {check} reduced from 300ms

                        Balls ball = ColorSensingFunctions.colorDetection(colorSensor1, colorSensor2);
                        telemetry.addData("Attempt " + attempt + " Slot " + slot, ball);

                        if (ball == targetBallColor) {
                            targetBallSlot = slot;
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    telemetry.addLine("Fallback: firing slot 0");//the color sensors misses sometimes
                    targetBallSlot = 0; // Default to first slot
                }

                state = PossibleStates.AIM_AND_FIRE;
                telemetry.update();
                break;

            case AIM_AND_FIRE:
                // Fire all 3 balls in sequence
                for (int i = 0; i < 3; i++) {
                    fireBall((targetBallSlot + i) % 3);
                }
                state = PossibleStates.PARK;
                break;

            case PARK:
                telemetry.addData("State", "PARK");
                // {check} Tune this final parking position
                motion.driveTo(0.5, 0.5, 0.05, 4.0);
                launcherMotor.setVelocity(0);
                telemetry.addData("Autonomous", "COMPLETE");
                telemetry.update();
                state = PossibleStates.COMPLETE;
                break;

            case COMPLETE:
                requestOpModeStop();
                break;
        }
        telemetry.update();
    }

    // Searches for scoring tag by rotating - returns true if found
    private boolean searchForScoringTag(boolean isRedAlliance, double timeoutSec) {
        stateTimer.reset();
        while (opModeIsActive() && stateTimer.seconds() < timeoutSec) {
            // Rotate 10 degrees at a time
            motion.turnTo(odomhub.getHeading(AngleUnit.RADIANS) + Math.toRadians(10),
                    Math.toRadians(3), 0.5);

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                    int id = tag.getFiducialId();
                    if ((isRedAlliance && id == 20) || (!isRedAlliance && id == 24)) {
                        Pose3D pose = tag.getRobotPoseFieldSpace();
                        targetDistanceMeters = calculateDistanceToTarget(pose);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Helper method to fire a ball from a specific slot
    private void fireBall(int slot) {
        drumServo.setPosition(DRUM_FIRE_POSITIONS[slot]);
        sleep(300);
        firingPinServo.setPosition(FIRING_PIN_FIRE);
        sleep(150);
        firingPinServo.setPosition(FIRING_PIN_NULL);
        sleep(250);
    }

    // {check} This helper is defined but not used - kept for future reliability improvements
    private LLResult getLimelightResult() {   //if limelight doesn't have enough time for processing use this
        for (int i = 0; i < 50 && opModeIsActive(); i++) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) return result;
            sleep(100);
        }
        return null;
    }

    private double calculateDistanceToTarget(Pose3D botpose) {
        if (botpose == null) return 999; // Return large number if pose invalid
        double x = botpose.getPosition().x;
        double y = botpose.getPosition().y;
        return Math.sqrt(x * x + y * y);
    }

    private double getCurrentRPM() {
        double ticksPerSecond = launcherMotor.getVelocity();
        return (ticksPerSecond / ENCODER_TICKS_PER_OUTPUT_REV) * 60.0;
    }

    private boolean isAtTargetRPM(double targetRPM) {
        return Math.abs(getCurrentRPM() - targetRPM) <= 5.0; // {check} Tolerance might need adjustment
    }