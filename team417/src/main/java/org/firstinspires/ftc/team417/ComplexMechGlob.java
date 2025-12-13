package org.firstinspires.ftc.team417;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.wilyworks.common.WilyWorks;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team417.apriltags.LimelightDetector;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

enum RequestedColor { //an enum for different color cases for launching
    PURPLE,
    GREEN,
    EITHER,
    NONE
}
enum PixelColor {
    PURPLE,
    GREEN,
    NONE
}
enum LaunchDistance {
    FAR,
    NEAR,
    OFF //turns the flywheel off
}

class MechGlob { //a placeholder class encompassing all code that ISN'T for slowbot.
    Telemetry telemetry;

    MechGlob() {
    }

    //call DrumGlob.create to create a Glob object for slowbot or fastbot
    static MechGlob create (HardwareMap hardwareMap, Telemetry telemetry, PixelColor[] preloads){

        if (MecanumDrive.isSlowBot) { //if the robot is slowbot, use ComplexMechGlob.
            return new ComplexMechGlob(hardwareMap, telemetry, preloads); //Go to ComplexMechGlob class

        } else { //otherwise, use MechGlob
            return new MechGlob(); //Go to MechGlob class
        }
    }
    //a method that controls the intake based on gamepad2.leftstickx
    //if gamepad2.left_stick_x is > 0, intakeSpeed = 1. If negative, intakeSpeed = -1. If 0, 0.
    void intake (double intakeValue){}

    //a method that determines what color to launch. Options are purple, green, or either.
    boolean launch (RequestedColor requestedColor, LimelightDetector detector) {
        detector.tryResetRobotPose(telemetry); // Resets the robot pose only if the robot is not moving
        return true;
    }

    void update () {}

    boolean isDoneLaunching () {
        return true;
    }

    boolean preLaunch (RequestedColor requestedColor) {
        return true;
    }
    void setLaunchVelocity (LaunchDistance launchDistance) {}

    public PixelColor getSlotColor(int slotIndex) {
        return PixelColor.NONE;
    }
    void controlDrumManually () {}




}

@Config
public class ComplexMechGlob extends MechGlob { //a class encompassing all code that IS for slowbot
    // TODO tune constants via FTC Dashboard:
    public static double FEEDER_POWER = 1;
    public static double TRANSFER_TIME_UP = 0.6;
    public static double TRANSFER_TIME_DOWN = 0.25;

    // how long we wait before continuing after the color detector
    // detects. this is 0 because it will likely become obsolete
    public static double INTAKE_TIMER = 0;
    public static double FAR_FLYWHEEL_VELOCITY = 1080; //was 1500
    public static double NEAR_FLYWHEEL_VELOCITY = 850; //was 1500
    public static double FLYWHEEL_BACK_SPIN = 300; //was 300
    public static double TRANSFER_INACTIVE_POSITION = 0.45;
    public static double TRANSFER_ACTIVE_POSITION = 0.7;
    public static double REVERSE_INTAKE_SPEED = -1;
    public static double INTAKE_SPEED = 1;
    public static double FLYWHEEL_VELOCITY_TOLERANCE = 25; //this is an epsiiiiiiiiilon
    public static double VOLTAGE_TOLERANCE = 0.01; //THIS IS AN EPSILON AS WELLLLLL
    public static double DRUM_GATE_OPEN_POSITION = .8;
    public static double DRUM_GATE_CLOSED_POSITION = 0.59;
    public static double MOTOR_D_VALUE = 1;


    ElapsedTime transferTimer;
    ElapsedTime intakeTimer;
    double userIntakeSpeed;
    ArrayList<DrumRequest> drumQueue = new ArrayList<> ();

    ArrayList<PixelColor> slotOccupiedBy = new ArrayList<> (Collections.nCopies(3, PixelColor.NONE));
    enum WaitState {
        DRUM_MOVE_WAIT, //waiting for the ball to fully enter the slot before moving the drum
        DRUM_MOVE, //waiting for the drum to reach desired position
        INTAKE, //waiting for the intake to finish
        TRANSFER, //waiting for the transfer to finish
        SPIN_UP, //waiting for the flywheel(s) to spin up
        IDLE, //waiting for input when the drum is full

    }
    WaitState waitState = WaitState.IDLE;
    // arrays with placeholder values for servo positions and voltages relative to intake and launch
    double [] INTAKE_POSITIONS = {0.067, 0.44, 0.803};
    double [] INTAKE_VOLTS = {2.94, 1.83, 0.74};
    double [] LAUNCH_POSITIONS = {0.627, 1, 0.258};
    double [] LAUNCH_VOLTS = {1.27, 0.155, 2.37};
    double lastQueuedPosition; //where the servo was *queued* to go last. NOT THE SAME AS hwDrumPosition!
    double hwDrumPosition; //where the drum was *told* to go last. NOT THE SAME AS lastQueuedPosition!
    double upperLaunchVelocity;
    double lowerLaunchVelocity;
    double feederPower;
    LaunchDistance launchDistance = LaunchDistance.OFF;


    HardwareMap hardwareMap;

    //hardware objects
    Servo servoDrum;
    Servo servoTransfer;
    AnalogInput analogDrum;
    DcMotorEx motLLauncher;
    DcMotorEx motULauncher;
    DcMotorEx motIntake;
    CRServo servoBLaunchFeeder;
    CRServo servoFLaunchFeeder;
    Servo servoDrumGate;
    NormalizedColorSensor sensorColor1;
    NormalizedColorSensor sensorColor2;

    CoolColorDetector coolColorDetector;

    class DrumRequest {
        double position;
        WaitState nextState;

        public DrumRequest(double position, WaitState nextState) {
            this.nextState = nextState;
            this.position = position;
        }
    }
    ComplexMechGlob (HardwareMap hardwareMap, Telemetry telemetry, PixelColor[] preloads) {

        //this changes some lists if we are using WilyWorks
        if (WilyWorks.isSimulating) {
            INTAKE_POSITIONS = new double[]{0 / 6.0, 2 / 6.0, 4 / 6.0};
            LAUNCH_POSITIONS = new double[]{3 / 6.0, 5 / 6.0, 1.0 / 6};
            INTAKE_VOLTS = new double[]{3.5 * 0 / 6.0, 3.5 * 2 / 6.0, 3.5 * 4 / 6.0};
            LAUNCH_VOLTS = new double[]{3.5 * 3 / 6.0, 3.5 * 5 / 6.0, 3.5 * 1.0 / 6};
        }

        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        servoDrum = hardwareMap.get(Servo.class, "servoDrum");
        servoTransfer = hardwareMap.get(Servo.class, "servoTransfer");
        analogDrum = hardwareMap.get(AnalogInput.class, "analogDrum");
        motLLauncher = hardwareMap.get(DcMotorEx.class, "motLLauncher");
        motULauncher = hardwareMap.get(DcMotorEx.class, "motULauncher");
        motIntake = hardwareMap.get(DcMotorEx.class, "motIntake");
        servoBLaunchFeeder = hardwareMap.get(CRServo.class, "servoBLaunchFeeder");
        servoFLaunchFeeder = hardwareMap.get(CRServo.class, "servoFLaunchFeeder");
        servoDrumGate = hardwareMap.get(Servo.class, "servoDrumGate");
        coolColorDetector = new CoolColorDetector(hardwareMap, telemetry);
        slotOccupiedBy = new ArrayList<>(Arrays.asList(preloads));


        /*
         * Here we set our flywheels to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        motLLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motULauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // set the motors to a braking behavior so it slows down faster when left trigger is pressed
        motLLauncher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motULauncher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motLLauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, MOTOR_D_VALUE, 10));
        motULauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, MOTOR_D_VALUE, 10));

        motULauncher.setDirection(DcMotorSimple.Direction.REVERSE);
        motLLauncher.setDirection(DcMotorSimple.Direction.REVERSE);
        servoBLaunchFeeder.setDirection(DcMotorSimple.Direction.REVERSE);



    }

    //the position argument denotes whether we are using intake or launch positions
    //position takes INTAKE_POSITIONS or LAUNCH_POSITIONS.
    int findNearestSlot (double [] position, RequestedColor requestedColor) {

        double minDistance = Double.MAX_VALUE; //the minimum distance to a slot that has what we want
        int minSlot = -1; // this will only ever be 0, 1, or 2. -1 represents a invalid value

        // a for loop that will determine what slot has the requested color.
        for (int i = 0; i <= 2; i++){ //here, the integer i represents the slot we are currently checking
            double distance = Math.abs(position[i] - lastQueuedPosition);
            //each conditional checks if what we requested and what we have in a specific slot matches.
            if (distance < minDistance){
                if (requestedColor == RequestedColor.PURPLE && slotOccupiedBy.get (i) == PixelColor.PURPLE){
                    minDistance = distance;
                    minSlot = i;// if it does, mark the current slot as the nearest slot
                } else if (requestedColor == RequestedColor.GREEN && slotOccupiedBy.get (i) == PixelColor.GREEN){
                    minDistance = distance;
                    minSlot = i;
                } else if (requestedColor == RequestedColor.EITHER && slotOccupiedBy.get (i) != PixelColor.NONE){
                    minDistance = distance;
                    minSlot = i;
                } else if (requestedColor == RequestedColor.NONE && slotOccupiedBy.get (i) == PixelColor.NONE){
                    minDistance = distance;
                    minSlot = i;
                }
            }

        }
        return minSlot;
    }

    @Override
    void intake (double intakeSpeed) {
        userIntakeSpeed = intakeSpeed;

    }

    @Override
        //a class that controls the launcher and transfer
    boolean launch (RequestedColor requestedColor, LimelightDetector detector) {
        detector.tryResetRobotPose(telemetry); // Resets the robot pose only if the robot is not moving

        if (launchDistance == LaunchDistance.OFF) {
            launchDistance = LaunchDistance.NEAR;
        }
        int minSlot = findNearestSlot(LAUNCH_POSITIONS, requestedColor);
        if (minSlot != -1){
            addToDrumQueue(LAUNCH_POSITIONS[minSlot], WaitState.SPIN_UP);
            slotOccupiedBy.set (minSlot, PixelColor.NONE); //marking this slot as empty so we don't accidentally try to use it again
            return true;
        }
        return false;
    }
    //this function adds a new drum request to the drum queue. nextState is the state do use after the drum is finished moving
    void addToDrumQueue(double position, WaitState nextState){
        drumQueue.add(new DrumRequest(position, nextState));
        lastQueuedPosition = position;
    }

    boolean drumAtPosition() {

        int intakeSlot = findSlotFromPosition(hwDrumPosition, INTAKE_POSITIONS);
        int launchSlot = findSlotFromPosition(hwDrumPosition, LAUNCH_POSITIONS);
        double expectedVolts;

        if (intakeSlot != -1) {
            expectedVolts = INTAKE_VOLTS[intakeSlot];
        } else {
            expectedVolts = LAUNCH_VOLTS[launchSlot];
        }
        return Math.abs(analogDrum.getVoltage() - expectedVolts) <= VOLTAGE_TOLERANCE;
    }
    @Override
    boolean isDoneLaunching () {
        return drumQueue.isEmpty() && (waitState == WaitState.IDLE || waitState == WaitState.INTAKE);
    }
    @Override
    //this function is just for auto. it rotates to the requested color but does not launch (to save time)
    boolean preLaunch (RequestedColor requestedColor) {
        int minSlot = findNearestSlot(LAUNCH_POSITIONS, requestedColor);
        if (minSlot == -1){
            return false;
        } else {
            addToDrumQueue(LAUNCH_POSITIONS[minSlot], WaitState.IDLE);
            return true;
        }

    }
    @Override
    void setLaunchVelocity (LaunchDistance launchDistance) {
        this.launchDistance = launchDistance;
    }
    void calculateLaunchVelocity () {
        if (launchDistance == LaunchDistance.NEAR) {
            upperLaunchVelocity = NEAR_FLYWHEEL_VELOCITY - (0.5 * FLYWHEEL_BACK_SPIN);
            lowerLaunchVelocity = NEAR_FLYWHEEL_VELOCITY + (0.5 * FLYWHEEL_BACK_SPIN);
            feederPower = FEEDER_POWER;
        } else if (launchDistance == LaunchDistance.FAR){
            upperLaunchVelocity = FAR_FLYWHEEL_VELOCITY - (0.5 * FLYWHEEL_BACK_SPIN);
            lowerLaunchVelocity = FAR_FLYWHEEL_VELOCITY + (0.5 * FLYWHEEL_BACK_SPIN);
            feederPower = FEEDER_POWER;
        } else {
            upperLaunchVelocity = 0;
            lowerLaunchVelocity = 0;
            servoBLaunchFeeder.setPower(0);
            servoFLaunchFeeder.setPower(0);
            feederPower = 0;
        }
    }
    int findSlotFromPosition (double position, double [] positions) {
        for (int i = 0; i < positions.length; i++) {
            if (positions [i] == position){
               return i;
            }
        }
        return -1;
    }

    void controlDrumManually () {
        int currentSlot = findSlotFromPosition(hwDrumPosition, INTAKE_POSITIONS);
        if (currentSlot != -1) {
            slotOccupiedBy.set(currentSlot, PixelColor.PURPLE);
        }
        int minSlot = findNearestSlot(INTAKE_POSITIONS, RequestedColor.NONE);
        if (minSlot != -1) {
            addToDrumQueue(INTAKE_POSITIONS[minSlot], WaitState.INTAKE);
        }

    }
    @Override
    public PixelColor getSlotColor(int slotIndex) {
        PixelColor artifactColor = slotOccupiedBy.get(slotIndex);
        return artifactColor;
    }

    @Override
    void update () {
        double intakePower = 0;

        calculateLaunchVelocity();
        if (waitState == WaitState.DRUM_MOVE_WAIT) {
            // always run the intake, even while we're waiting for the ball to enter the drum
            intakePower = INTAKE_SPEED;
        } else if (userIntakeSpeed < 0 ) {
            // allow the intake to run if the driver wants it to
            intakePower = REVERSE_INTAKE_SPEED;
        } else if (userIntakeSpeed > 0) {
            // if we are in the intake waitState, allow the intake to run
            if (waitState == WaitState.INTAKE) {
                intakePower = INTAKE_SPEED;
            } else if (!drumQueue.isEmpty() && drumQueue.get(0).nextState == WaitState.INTAKE) {
                intakePower = INTAKE_SPEED;
            }
        }
        if (intakePower > 0 && waitState != WaitState.DRUM_MOVE) {
            servoDrumGate.setPosition(DRUM_GATE_OPEN_POSITION);
        } else {
            servoDrumGate.setPosition(DRUM_GATE_CLOSED_POSITION);
        }

        if (waitState == WaitState.IDLE) {
            if (userIntakeSpeed > 0) {
                int minSlot = findNearestSlot(INTAKE_POSITIONS, RequestedColor.NONE);
                if (minSlot != -1) {
                    addToDrumQueue(INTAKE_POSITIONS[minSlot], WaitState.INTAKE);
                    waitState = WaitState.INTAKE;
                }
            }
            // this makes it so that after we are done launching the drum goes to intake position
            if (drumQueue.isEmpty() && slotOccupiedBy.stream().allMatch(e -> e == PixelColor.NONE)) {
                addToDrumQueue(INTAKE_POSITIONS[0], WaitState.INTAKE);
            }
        }

        // let a firing request interrupt an intake
        if (waitState == WaitState.IDLE || waitState == WaitState.INTAKE) {
            if (!drumQueue.isEmpty()) {
                hwDrumPosition = drumQueue.get(0).position;
                waitState = WaitState.DRUM_MOVE;
            }
        }
        if (waitState == WaitState.DRUM_MOVE) {
            if (drumAtPosition()) {
                waitState = drumQueue.get(0).nextState;
                drumQueue.remove(0);
            }
        }
        if (waitState == WaitState.SPIN_UP) {
            if (Math.abs(motLLauncher.getVelocity() - lowerLaunchVelocity) <= FLYWHEEL_VELOCITY_TOLERANCE &&
                    Math.abs(motULauncher.getVelocity() - upperLaunchVelocity) <= FLYWHEEL_VELOCITY_TOLERANCE) {
                waitState = WaitState.TRANSFER;
            }
        }
        double transferPosition = TRANSFER_INACTIVE_POSITION;
        if (waitState == WaitState.TRANSFER) {
            if (transferTimer == null) {
                transferTimer = new ElapsedTime();
            }
            if (transferTimer.seconds() <= TRANSFER_TIME_UP) {
                transferPosition = TRANSFER_ACTIVE_POSITION;
            }
            if (transferTimer.seconds() >= TRANSFER_TIME_UP + TRANSFER_TIME_DOWN) {
                waitState = WaitState.IDLE;
                transferTimer = null;
            }
        }
        if (waitState == WaitState.INTAKE) {
            PixelColor slotColor = coolColorDetector.detectArtifactColor();
            if (slotColor != PixelColor.NONE) {
                int slot = findSlotFromPosition(hwDrumPosition, INTAKE_POSITIONS);
                slotOccupiedBy.set(slot, slotColor);
                waitState = WaitState.DRUM_MOVE_WAIT;
                intakeTimer = new ElapsedTime();
            }
        }
        if (waitState == WaitState.DRUM_MOVE_WAIT) {
            if (intakeTimer.seconds() >= INTAKE_TIMER) {
                waitState = WaitState.IDLE;
            }
        }

        servoDrum.setPosition(hwDrumPosition);
        //servoTransfer.setPosition(transferPosition);

            // Enable on real hardware once transfer parameters are tuned
        servoTransfer.setPosition(transferPosition);


        motLLauncher.setVelocity(lowerLaunchVelocity);
        motULauncher.setVelocity(upperLaunchVelocity);
        motIntake.setPower(intakePower);
        servoBLaunchFeeder.setPower(feederPower);
        servoFLaunchFeeder.setPower(feederPower);

        telemetry.addData("hwDrumPos", hwDrumPosition);
        telemetry.addData("currVoltage ", "%.2f", analogDrum.getVoltage());
    }
}

