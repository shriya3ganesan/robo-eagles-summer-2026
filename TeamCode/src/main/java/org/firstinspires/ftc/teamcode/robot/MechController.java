package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class MechController {

    private final RobotHardware robot;
    private final Telemetry telemetry;
    private final VisionController visionController;
    private MechState currentState;

    private MechState previousState = MechState.IDLE;


    // Hardware constants
    private static final double SERVO_OFFSET = 0;
    public static final double[] INTAKE = {0 + SERVO_OFFSET, 135 + SERVO_OFFSET, 271 + SERVO_OFFSET}; // Indexer 0, 1, 2 @ Intake Post degrees 0, 120, 240
    public static final double[] SHOOT = {195 + SERVO_OFFSET, 334 + SERVO_OFFSET, 467 + SERVO_OFFSET}; // Indexer 0, 1, 2 @ Shooting Post degrees 180, 300, 420
    private static final double MAX_LIFTER_ROTATION = 300.0; // Degrees
    private static final double MAX_INDEXER_ROTATION = 1800.0; // Degrees
    private static final double INTAKE_TICKS_PER_FULL_ROTATION = 537.7; //Encoder Resolution PPR for RPM 312
    private static final long INTAKE_CUTOFF_MS = 4000; // 4 seconds wait time while searching for artifact
    private static final long POST_ROTATE_WAIT_MS = 90; // After every intake state rotation
    private static final long POST_HUMAN_WAIT_MS = 800; // After every human state rotation
    private static final long MOTOR_WAIT_MS = 1300; // Shooting motor to reach full speed
    private static final long POST_INDEXER_WAIT_MS = 900; // Post Indexer rotation shooting
    private static final long LIFT_WAIT_MS = 800; // Lifter in Up position for shooting
    private static final long DROP_WAIT_MS = 500; // Post Lifter in Down position
    private static final long APRIL_TAG_WAIT_MS = 3000; // 3 seconds waiting to detect AprilTag
    public static final double FULL_DRIVE_POWER = 1.0; // Normal Drive speed
    public static final double INTAKE_DRIVE_POWER = 0.25; // Drive speed during Intake
    public static final double INTAKE_DRIVE_TELEOP = 0.5; // Drive speed during Intake
    static final double SHOOTER_CPR = 28.0; // REV HD Hex encoder counts/rev
    static final double MOTOR_PULLEY_T = 66.0; // Tooth count on motor
    static final double WHEEL_PULLEY_T = 54.0; // Tooth count on flywheel
    public static double SHOOTING_WHEEL_SPEED_NEAR = 4300; // Flywheel RPM | Max flywheel RPM: 7333 | Flywheel RPM ≈ 6000 (Motor RPM) * 66/54 = 7333 RPM | Motor RPM ≈ 6000 (Flywheel RPM) * 54/66 = 4909 RPM
    public static double SHOOTING_WHEEL_SPEED_FAR = 6600; // Flywheel RPM | Max flywheel RPM: 7333 | Flywheel RPM ≈ 6000 (Motor RPM) * 66/54 = 7333 RPM | Motor RPM ≈ 6000 (Flywheel RPM) * 54/66 = 4909 RPM
    private static final double INDEXER_DEG_PER_SEC_INTAKE = 200.0;
    private static final double INDEXER_SLOW_END_DEG = 40.0;

    // Limit constants
    private static final int lifterDown = 13; // Lifter down angle degrees
    private static final int lifterUp = 125; // Lifter up angle degrees

    // Variables
    public int[] tagPattern = {0, 0, 0, 0}; // Tag ID & Pattern
    public int[] indexer = {2, 1, 1}; // GPP - Color of artifact in Indexer 0, 1, 2
    private int artifactCount = 3;
    private double lastIndexer = 1;
    private int lastLifter = 0;
    private int intakeTargetIndex = -1;
    private int intakeStage = 0;
    private long intakeStageStart = 0;
    private boolean shootingMotorRunning = false;
    private boolean motorInitialWaitDone = false;
    private int shootPatternIndex = 1;
    private int slotToShoot = -1;
    private long shootStageStart = 0;
    private long shootElapsed = 0;
    private int shootStage = 0;
    private long aprilTagStageStart = 0;
    private boolean aprilTagRunning = false;
    private long aprilTagElapsed = 0;
    private boolean humanIntakeRunning = false;
    private int humanIndex = -1;
    private long humanStateStart = 0;
    private int targetPos;
    private long indexerLastUpdateMs = 0;
    private double intakeIndexerTargetDeg = -1;
    private boolean lastIntake = false;
    private double intakeIndexerStartDeg = -1;
    private boolean artifactCounted = false;

    // Constructor
    public MechController(RobotHardware RoboRoar, VisionController visionController) {
        this.robot = RoboRoar;
        this.telemetry = RoboRoar.telemetry;
        this.visionController = visionController;
        this.currentState = MechState.IDLE;
    }

    // State machine handler
    public void handleMechState(MechState state) {
        switch (state) {
            case START:
                currentState = MechState.START;
                setLifter(0);
                setIndexer(INTAKE[0]);
                setState(MechState.APRIL_TAG);
                break;

            case IDLE:
                currentState = MechState.IDLE;
                break;

            case SHOOT_STATE:
                currentState = MechState.SHOOT_STATE;
                if (!shootingMotorRunning) { // Start shooting Motor
                    runShootingMot(1);
                    shootingMotorRunning = true;
                    if (!motorInitialWaitDone) {
                        shootStageStart = System.currentTimeMillis();
                        shootStage = -1;
                    }
                }

                if (shootPatternIndex >= tagPattern.length || artifactCount <= 0) {
                    runShootingMot(0); // Stop shooting stage
                    shootingMotorRunning = false;
                    motorInitialWaitDone = false;
                    shootStage = 0;
                    shootPatternIndex = 1;
                    slotToShoot = -1;
                    shootElapsed = 0;
                    setState(MechState.IDLE);
                    break;
                }

                // Shooting stage machine
                switch (shootStage) {
                    case -1:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= MOTOR_WAIT_MS) { // Waiting for shooting motor to reach full speed
                            motorInitialWaitDone = true;
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 0;
                        }
                        break;

                    case 0:
                        if (slotToShoot == -1) {
                            int targetColor = tagPattern[shootPatternIndex]; // Checking motif pattern color to shoot
                            for (int i = 0; i < indexer.length; i++) {
                                if (indexer[i] == targetColor) { // Finding the color to shoot in index
                                    slotToShoot = i;
                                    break;
                                }
                            }
                            if (slotToShoot != -1) {
                                setIndexer(SHOOT[slotToShoot]); // Setting indexer to shooting position
                                shootStageStart = System.currentTimeMillis();
                                shootStage = 3;
                            } else {
                                shootPatternIndex++;
                            }
                        }
                        break;

                    case 3:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= POST_INDEXER_WAIT_MS) { // Waiting post indexer rotation
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 1;
                        }
                        break;

                    case 1:
                        setLifter(1); // Lifter up
                        shootStageStart = System.currentTimeMillis();
                        shootStage = 2;
                        break;

                    case 2:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= LIFT_WAIT_MS) {
                            setLifter(0); // Lifter down
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 4;
                        }
                        break;

                    case 4:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= DROP_WAIT_MS) {
                            int targetColor = tagPattern[shootPatternIndex];
                            for (int i = 0; i < indexer.length; i++) {
                                if (indexer[i] == targetColor) {
                                    indexer[i] = 0; // Setting 0 in indexer for the artifact shot
                                    artifactCount--;
                                    break;
                                }
                            }
                            slotToShoot = -1;
                            shootPatternIndex++;
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 0;
                        }
                        break;
                }
                break;
/*
            case INTAKE_STATE:
                currentState = MechState.INTAKE_STATE;

                switch (intakeStage) {

                    case 0:
                        intakeTargetIndex = getEmptyIndex();
                        if (intakeTargetIndex == -1) { // Stop intake stage
                            if (!lastIntake) {
                                setState(MechState.IDLE);
                                break;
                            } else { //Slow indexer start
                                if (intakeIndexerTargetDeg < 0) {
                                    intakeIndexerTargetDeg = (statusIndexer() + 60);
                                    indexerLastUpdateMs = 0;
                                }
                                if (setIndexerIntake(intakeIndexerTargetDeg)) {
                                    intakeStage = 1;
                                    intakeIndexerTargetDeg = -1;
                                    intakeStageStart = System.currentTimeMillis();
                                    lastIntake = false;
                                    setState(MechState.IDLE);
                                }
                                break;
                            }
                        }

                        if (artifactCount < 1) {
                            setIndexer(INTAKE[intakeTargetIndex]);
                        } else {
                            if (intakeIndexerTargetDeg < 0) {
                                intakeIndexerTargetDeg = INTAKE[intakeTargetIndex];
                                indexerLastUpdateMs = 0;
                            }
                            if (setIndexerIntake(intakeIndexerTargetDeg)) {
                                intakeStage = 1;
                                intakeIndexerTargetDeg = -1;
                                intakeStageStart = System.currentTimeMillis();
                            }
                            break;
                        }
                        //Slow indexer stop
                        //setIndexer(INTAKE[intakeTargetIndex]); // Original code
                        intakeStageStart = System.currentTimeMillis();
                        intakeStage = 1;
                        break;

                    case 1:
                        if (System.currentTimeMillis() - intakeStageStart < POST_ROTATE_WAIT_MS) // Wait for indexer rotate
                            break;

                        runIntakeMot(1);
                        intakeStageStart = System.currentTimeMillis();   // Start timer
                        intakeStage = 2;
                        break;

                    case 2:
                        int color = visionController.artifactColor();
                        boolean detected = color != 0;

                        if (detected) { // Artifact detection
                            runIntakeMot(0);
                            indexer[intakeTargetIndex] = color;
                            artifactCount++;
                            if (artifactCount == 3) { //Checks for last intake
                                lastIntake = true;
                            }
                            intakeStageStart = System.currentTimeMillis(); // Resets timer
                            intakeStage = 3;
                            break;
                        }

                        if (System.currentTimeMillis() - intakeStageStart >= INTAKE_CUTOFF_MS) { // Timer cut-off
                            runIntakeMot(0);
                            setState(MechState.IDLE);
                            intakeStage = 0;
                            break;
                        }

                        break;

                    case 3:
                        if (System.currentTimeMillis() - intakeStageStart >= POST_ROTATE_WAIT_MS) { // Wait time after detected artifact
                            intakeStage = 0;
                        }
                        break;
                }
                break;
*/

            case INTAKE_STATE:
                currentState = MechState.INTAKE_STATE;

                switch (intakeStage) {

                    case 0:
                        intakeTargetIndex = getEmptyIndex();
                        if (intakeTargetIndex == -1) { // Stop intake stage
                            if (lastIntake) {
                                setIndexer(statusIndexer() + 60);
                                lastIntake = false;
                                artifactCounted = false;
                            }
                            if (robot.intakeMot.getPower() == 1) {
                                runIntakeMot(0);
                            }
                            setState(MechState.IDLE);
                            break;
                        } else {
                            setIndexer(INTAKE[intakeTargetIndex]);
                            intakeStageStart = System.currentTimeMillis();
                            if (robot.intakeMot.getPower() == 0) {
                                runIntakeMot(1);
                            }
                            intakeStage = 1;
                            break;
                        }
                    case 1:
                        if (System.currentTimeMillis() - intakeStageStart >= POST_ROTATE_WAIT_MS) { // Wait time before detecting artifact
                            intakeStage = 2;
                        }
                        break;

                    case 2:
                        int color = visionController.artifactColor();
                        boolean detected = color != 0;

                        if (detected && !artifactCounted) { // Artifact detection
                            artifactCounted = true;
                            indexer[intakeTargetIndex] = color;
                            artifactCount++;
                            if (artifactCount == 3) { //Checks for last intake
                                lastIntake = true;
                            }
                            intakeStage = 0;
                            break;
                        }
                        if (!detected && artifactCounted) {
                            artifactCounted = false;
                        }

                        if (System.currentTimeMillis() - intakeStageStart >= INTAKE_CUTOFF_MS) { // Timer cut-off
                            runIntakeMot(0);
                            setState(MechState.IDLE);
                            intakeStage = 0;
                            break;
                        }
                }
                break;

            case SHOOT_PURPLE:
                currentState = MechState.SHOOT_PURPLE;
                if (!shootingMotorRunning) {
                    int purpleIndex = getPurpleIndex();
                    if (purpleIndex != -1) {
                        runShootingMot(1); // Start shooter
                        shootingMotorRunning = true;
                        setIndexer(SHOOT[purpleIndex]); // Setting indexer to shooting position
                        shootStageStart = System.currentTimeMillis();
                        shootStage = -1;
                    } else {
                        setState(MechState.IDLE);
                        break;
                    }
                }

                switch (shootStage) {
                    case -1:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= MOTOR_WAIT_MS) {
                            motorInitialWaitDone = true;
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 1;
                        }
                        break;

                    case 1:
                        setLifter(1); // Lifter up
                        shootStageStart = System.currentTimeMillis();
                        shootStage = 2;
                        break;

                    case 2:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= LIFT_WAIT_MS) {
                            setLifter(0); // Lifter down
                            runShootingMot(0);
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 3;
                        }
                        break;

                    case 3:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= DROP_WAIT_MS) {
                            int purpleIndex = getPurpleIndex();
                            if (purpleIndex != -1) {
                                indexer[purpleIndex] = 0;
                            }
                            shootingMotorRunning = false;
                            motorInitialWaitDone = false;
                            artifactCount--;
                            shootStage = 0;
                            shootElapsed = 0;
                            setState(MechState.IDLE);
                        }
                        break;
                }
                break;

            case SHOOT_GREEN:
                currentState = MechState.SHOOT_GREEN;
                if (!shootingMotorRunning) {
                    int greenIndex = getGreenIndex();
                    if (greenIndex != -1) {
                        runShootingMot(1); // Start shooter
                        shootingMotorRunning = true;
                        setIndexer(SHOOT[greenIndex]); // Setting indexer to shooting position
                        shootStageStart = System.currentTimeMillis();
                        shootStage = -1;
                    } else {
                        setState(MechState.IDLE);
                        break;
                    }
                }

                switch (shootStage) {
                    case -1:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= MOTOR_WAIT_MS) {
                            motorInitialWaitDone = true;
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 1;
                        }
                        break;

                    case 1:
                        setLifter(1); // Lifter up
                        shootStageStart = System.currentTimeMillis();
                        shootStage = 2;
                        break;

                    case 2:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= LIFT_WAIT_MS) {
                            setLifter(0); // Lifter down
                            runShootingMot(0);
                            shootStageStart = System.currentTimeMillis();
                            shootStage = 3;
                        }
                        break;

                    case 3:
                        shootElapsed = System.currentTimeMillis() - shootStageStart;
                        if (shootElapsed >= DROP_WAIT_MS) {
                            int greenIndex = getGreenIndex();
                            if (greenIndex != -1) {
                                indexer[greenIndex] = 0;
                            }
                            shootingMotorRunning = false;
                            motorInitialWaitDone = false;
                            artifactCount--;
                            shootStage = 0;
                            shootElapsed = 0;
                            setState(MechState.IDLE);
                        }
                        break;
                }
                break;

            case APRIL_TAG:
                currentState = MechState.APRIL_TAG;
                if (!aprilTagRunning) {
                    aprilTagRunning = true;
                    aprilTagStageStart = System.currentTimeMillis();
                }
                aprilTagElapsed = System.currentTimeMillis() - aprilTagStageStart;
                tagPattern = visionController.findTagPattern(visionController.getAprilTag());
                if (tagPattern[0] != 0) { // If tag detected
                    aprilTagRunning = false;
                    aprilTagStageStart = 0;
                    aprilTagElapsed = 0;
                    setState(MechState.IDLE); // Stop april tage state
                    break;
                }
                if (aprilTagElapsed >= APRIL_TAG_WAIT_MS) { // If timed out
                    tagPattern = new int[]{21, 2, 1, 1};  // ID 21: GPP
                    aprilTagRunning = false;
                    aprilTagStageStart = 0;
                    aprilTagElapsed = 0;
                    setState(MechState.IDLE); // Stop april tage state
                    break;
                }
                break;

            case HUMAN_STATE:
                currentState = MechState.HUMAN_STATE;
                if (!humanIntakeRunning) {
                    humanIndex = getEmptyIndex(); // Finding slot 0 for intake
                    if (humanIndex != -1) { // Checking if all slots are full
                        setIndexer(INTAKE[humanIndex]); // Setting indexer to intake position
                        humanStateStart = System.currentTimeMillis();
                        humanIntakeRunning = true;

                    } else {
                        setState(MechState.IDLE); // Stop human stage
                        break;
                    }

                } else {
                    if (System.currentTimeMillis() - humanStateStart < POST_HUMAN_WAIT_MS) {
                        break;
                    }
                    int color = visionController.artifactColor(); // Reading sensor color
                    if (color != 0) { // Checking if Purple or Green artifact
                            indexer[humanIndex] = color; // Storing the artifact color based on indexer slot filled
                            artifactCount++;
                            humanIntakeRunning = false;
                            humanIndex = -1;
                            humanStateStart = 0;   
                        }
                    }
                break;
        }
    }

    // Cleanup State

    private void onStateExit(MechState from, MechState to) {
        switch (from) {
            case INTAKE_STATE:
                // Stop intake motor and reset intake state machine
                robot.intakeMot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.intakeMot.setPower(0);
                targetPos = robot.intakeMot.getCurrentPosition();
                intakeStage = 0;
                intakeTargetIndex = -1;
                intakeIndexerTargetDeg = -1;
                indexerLastUpdateMs = 0;
                lastIntake = false;
                intakeIndexerStartDeg = -1;
                break;

            case SHOOT_STATE:
            case SHOOT_PURPLE:
            case SHOOT_GREEN:
                // Ensure shooter is off and shooting state is reset
                runShootingMot(0);
                shootingMotorRunning = false;
                motorInitialWaitDone = false;
                shootStage = 0;
                shootPatternIndex = 1;
                slotToShoot = -1;
                shootElapsed = 0;
                shootStageStart = 0;
                break;

            case APRIL_TAG:
                aprilTagRunning = false;
                aprilTagStageStart = 0;
                aprilTagElapsed = 0;
                break;

            case HUMAN_STATE:
                humanIntakeRunning = false;
                humanIndex = -1;
                humanStateStart = 0;
                break;

            default:
                break;
        }

        // When we enter IDLE, make sure all actuators are safe
        if (to == MechState.IDLE) {
            // Stop intake
            robot.intakeMot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.intakeMot.setPower(0);

            // Stop shooter
            runShootingMot(0);
            shootingMotorRunning = false;
            motorInitialWaitDone = false;

            // Ensure lifter is down
            setLifter(0);
        }
    }

    // State machine methods

    public MechState getCurrentState() {
        return currentState;
    }

    public void setState(MechState newState) {
        if (newState == null) return;

        if (this.currentState != newState) {
            onStateExit(this.currentState, newState);
            this.previousState = this.currentState;
            this.currentState = newState;
        }
    }

    public void update() {
        handleMechState(this.currentState);
    }

    public void setIndexer(double targetDegrees) {
        if (lastIndexer != targetDegrees) {
            double pos = targetDegrees / MAX_INDEXER_ROTATION;
            pos = Math.max(0, Math.min(1, pos));
            robot.indexer.setPosition(pos);
            lastIndexer = targetDegrees;
        }
    }

    public boolean setIndexerIntake(double targetDegrees) {
        targetDegrees = Math.max(0, Math.min(MAX_INDEXER_ROTATION, targetDegrees));

        double currentDeg = statusIndexer();
        if (intakeIndexerStartDeg < 0) {
            intakeIndexerStartDeg = currentDeg;
            indexerLastUpdateMs = 0;
        }
        double traveled = Math.abs(currentDeg - intakeIndexerStartDeg);
        double error = targetDegrees - currentDeg;

        if (Math.abs(error) <= 2.0) {
            setIndexer(targetDegrees);
            intakeIndexerStartDeg = -1;
            return true;
        }

        if (traveled < INDEXER_SLOW_END_DEG) {
            long now = System.currentTimeMillis();
            double dt;

            if (indexerLastUpdateMs == 0) {
                dt = 0.02;
            } else {
                dt = (now - indexerLastUpdateMs) / 1000.0;
                dt = Math.max(0.0, Math.min(0.05, dt));
            }
            indexerLastUpdateMs = now;

            double maxStep = INDEXER_DEG_PER_SEC_INTAKE * dt;
            double step = Math.max(-maxStep, Math.min(maxStep, error));
            setIndexer(currentDeg + step);
            return false;
        } else {
            setIndexer(targetDegrees);
            intakeIndexerStartDeg = -1;
            return true;
        }
    }

    public void runIntakeMot(double power) {
        if (Math.abs(power) > 0.01) {
            robot.intakeMot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.intakeMot.setPower(power);
            double currentPos = robot.intakeMot.getCurrentPosition();
            targetPos = (int)(((Math.ceil(currentPos / INTAKE_TICKS_PER_FULL_ROTATION)) + 1) * INTAKE_TICKS_PER_FULL_ROTATION);

        } else if (robot.intakeMot.getCurrentPosition() < targetPos) {
            robot.intakeMot.setTargetPosition(targetPos);
            robot.intakeMot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.intakeMot.setPower(0.5);

        } else {
            robot.intakeMot.setPower(0);
            robot.intakeMot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            targetPos = robot.intakeMot.getCurrentPosition();
        }
    }

    static double flywheelRpmToMotorTicksPerSec(double flywheelRpm) {
        double motorRpm = flywheelRpm * (WHEEL_PULLEY_T / MOTOR_PULLEY_T);
        return motorRpm * SHOOTER_CPR / 60.0;
    }

    public void runShootingMot(double power) {
        if (Math.abs(power) > 0.01) {
            if (robot.pinpoint.getPosY(DistanceUnit.INCH) > 48.0) {
                robot.shootingMot.setVelocity(flywheelRpmToMotorTicksPerSec(SHOOTING_WHEEL_SPEED_NEAR));
            } else {
                robot.shootingMot.setVelocity(flywheelRpmToMotorTicksPerSec(SHOOTING_WHEEL_SPEED_FAR));
            }
        } else {
            robot.shootingMot.setVelocity(0);
            robot.shootingMot.setPower(0);
        }
    }
    public void setLifter(int down0up1) {
        if (lastLifter != down0up1) {
            if (down0up1 == 1) {
                robot.lifter.setPosition(lifterUp / MAX_LIFTER_ROTATION);
                lastLifter = 1;
            } else {
                robot.lifter.setPosition(lifterDown / MAX_LIFTER_ROTATION);
                lastLifter = 0;
            }
        }
    }
    public int getEmptyIndex() {
        for (int i = 0; i < indexer.length; i++) {
            if (indexer[i] == 0) {
                return i;
            }
        }
        return -1;
    }
    public int getPurpleIndex() {
        for (int i = 0; i < indexer.length; i++) {
            if (indexer[i] == 1) {
                return i;
            }
        }
        return -1;
    }
    public int getGreenIndex() {
        for (int i = 0; i < indexer.length; i++) {
            if (indexer[i] == 2) {
                return i;
            }
        }
        return -1;
    }

    // Status
    public double statusIndexer(){
        return robot.indexer.getPosition() * MAX_INDEXER_ROTATION;
    }
    public double statusLifter(){
        return robot.lifter.getPosition() * MAX_LIFTER_ROTATION;
    }

    public String convertColor(int artifactNumber){
        if (artifactNumber == 1) {
            return "P";
        } else if (artifactNumber == 2) {
            return "G";
        } else {
            return "N";
        }
    }

    // Telemetry output
    public void allTelemetry() {
        telemetry.addData("State", currentState);

        telemetry.addData("Tag Pattern",
                "%d --> %s | %s | %s",
                tagPattern[0], convertColor(tagPattern[1]), convertColor(tagPattern[2]), convertColor(tagPattern[3]));

        telemetry.addData("Artifact Count | Indexer",
                "%d --> %s | %s | %s",
                artifactCount, convertColor(indexer[0]), convertColor(indexer[1]), convertColor(indexer[2]));

        if (robot.pinpoint != null) {
            robot.pinpoint.update();
            telemetry.addData("Pinpoint",
                    "X: %.1f in | Y: %.1f in | Heading: %.1f°",
                    robot.pinpoint.getPosX(DistanceUnit.INCH),
                    robot.pinpoint.getPosY(DistanceUnit.INCH),
                    robot.pinpoint.getHeading(AngleUnit.DEGREES)
            );
        }

        telemetry.addData("Indexer | Lifter",
                "%.1f° | %.1f°", statusIndexer(), statusLifter());

        telemetry.addData("Shooting Mot FAR RPM", SHOOTING_WHEEL_SPEED_FAR);
        telemetry.addData("Shooting Mot NEAR RPM", SHOOTING_WHEEL_SPEED_NEAR);


        //visionController.sensorTelemetry();
        //visionController.aprilTagTelemetry();

        telemetry.update();
    }

}