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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

import java.util.ArrayList;
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

class MechGlob { //a placeholder class encompassing all code that ISN'T for slowbot.
    MechGlob(){}

    //call DrumGlob.create to create a Glob object for slowbot or fastbot
    static MechGlob create (HardwareMap hardwareMap, Telemetry telemetry){
        if (MecanumDrive.isSlowBot) { //if the robot is slowbot, use ComplexMechGlob.
            return new ComplexMechGlob(hardwareMap, telemetry); //Go to ComplexMechGlob class

        } else { //otherwise, use MechGlob
            return new MechGlob(); //Go to MechGlob class
        }
    }
    //a method that controls the intake based on gamepad2.leftstickx
    //if gamepad2.left_stick_x is > 0, intakeSpeed = 1. If negative, intakeSpeed = -1. If 0, 0.
    void intake (double intakeValue){}

    //a method that determines what color to launch. Options are purple, green, or either.
    void launch (RequestedColor requestedColor) {}

    void update () {}


}

@Config
public class ComplexMechGlob extends MechGlob { //a class encompassing all code that IS for slowbot
    // TODO tune constants via FTC Dashboard:
    static double FEEDER_POWER = 1;
    static double TRANSFER_TIME_UP = 0.3;
    static double TRANSFER_TIME_TOTAL = 0.6; //TRANSFER_TIME_TOTAL must be more than TRANSFER_TIME_UP
    static double UPPER_FLYWHEEL_VELOCITY = 1500;
    static double LOWER_FLYWHEEL_VELOCITY = 1500;
    static double TRANSFER_INACTIVE_POSITION = 0;
    static double TRANSFER_ACTIVE_POSITION = 1;
    static double REVERSE_INTAKE_SPEED = -1;
    static double INTAKE_SPEED = 1;
    static double FLYWHEEL_VELOCITY_TOLERANCE = 25;


    ElapsedTime transferTimer;
    double userIntakeSpeed;
    ArrayList<DrumRequest> drumQueue = new ArrayList<> ();

    ArrayList<PixelColor> slotOccupiedBy = new ArrayList<> (Collections.nCopies(3, PixelColor.NONE));
    enum WaitState {
        DRUM_MOVE, //waiting for the drum to reach desired position
        INTAKE, //waiting for the intake to finish
        TRANSFER, //waiting for the transfer to finish
        SPIN_UP, //waiting for the flywheel(s) to spin up
        IDLE, //waiting for input when the drum is full

    }
    WaitState waitState = WaitState.IDLE;
    // arrays with placeholder values for servo positions and voltages relative to intake and launch
    final double [] INTAKE_POSITIONS = {0, 1, 2};
    final double [] INTAKE_VOLTS = {0, 1, 2};
    final double [] LAUNCH_POSITIONS = {0, 1, 2};
    final double [] LAUNCH_VOLTS = {0, 1, 2};
    double lastQueuedPosition; //where the servo was *queued* to go last. NOT THE SAME AS hwDrumPosition!
    double hwDrumPosition; //where the drum was *told* to go last. NOT THE SAME AS lastQueuedPosition!



    HardwareMap hardwareMap;
    Telemetry telemetry;

    //hardware objects
    Servo servoDrum;
    Servo servoTransfer;
    AnalogInput analogDrum;
    DcMotorEx motLLauncher;
    DcMotorEx motULauncher;
    DcMotorEx motIntake;
    CRServo servoBLaunchFeeder;
    CRServo servoFLaunchFeeder;
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
    ComplexMechGlob (HardwareMap hardwareMap, Telemetry telemetry) {
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
        coolColorDetector = new CoolColorDetector(hardwareMap, telemetry);

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
        motLLauncher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motULauncher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motLLauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));
        motULauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

        motLLauncher.setDirection(DcMotorSimple.Direction.REVERSE);
        servoBLaunchFeeder.setDirection(CRServo.Direction.REVERSE);


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
                    minSlot = i;// if it does, mark the current slot as the nearest slot
                } else if (requestedColor == RequestedColor.GREEN && slotOccupiedBy.get (i) == PixelColor.GREEN){
                    minSlot = i;
                } else if (requestedColor == RequestedColor.EITHER && slotOccupiedBy.get (i) != PixelColor.NONE){
                    minSlot = i;
                } else if (requestedColor == RequestedColor.NONE && slotOccupiedBy.get (i) == PixelColor.NONE){
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
    void launch (RequestedColor requestedColor) {

        int minSlot = findNearestSlot(LAUNCH_POSITIONS, requestedColor);
        if (minSlot == -1){
            telemetry.speak("bad");
        } else {
            addToDrumQueue(LAUNCH_POSITIONS[minSlot], WaitState.SPIN_UP);
            slotOccupiedBy.set (minSlot, PixelColor.NONE); //marking this slot as empty so we don't accidentally try to use it again
        }
    }
    void addToDrumQueue(double position, WaitState waitState){ //this function adds a new drum request to the drum queue.
        drumQueue.add(new DrumRequest(position, waitState));
        lastQueuedPosition = position;
    }

    boolean drumAtPosition() {
        return true;
        // TODO: implement this
    }
    @Override
    void update () {
        double intakePower = 0;
        if (userIntakeSpeed < 0) {
            intakePower = REVERSE_INTAKE_SPEED;
        } else if (userIntakeSpeed > 0) {
            if (waitState == WaitState.INTAKE) {
                intakePower = INTAKE_SPEED;
            } else if (!drumQueue.isEmpty() && drumQueue.get(0).nextState == WaitState.INTAKE) {
                intakePower = INTAKE_SPEED;
            }
        }

        if (waitState == WaitState.IDLE) {
            if (userIntakeSpeed > 0) {
                waitState = WaitState.INTAKE;
                int minSlot = findNearestSlot(INTAKE_POSITIONS, RequestedColor.NONE);
                if (minSlot != -1) {
                    addToDrumQueue(INTAKE_POSITIONS[minSlot], WaitState.INTAKE);
                }
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
            if (Math.abs(motLLauncher.getVelocity() -LOWER_FLYWHEEL_VELOCITY) <= FLYWHEEL_VELOCITY_TOLERANCE &&
                    Math.abs(motULauncher.getVelocity() - UPPER_FLYWHEEL_VELOCITY) <= FLYWHEEL_VELOCITY_TOLERANCE) {
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
            if (transferTimer.seconds() >= TRANSFER_TIME_TOTAL) {
                waitState = WaitState.IDLE;
                transferTimer = null;
            }
        }
        servoDrum.setPosition(hwDrumPosition);
        servoTransfer.setPosition(transferPosition);
        motLLauncher.setVelocity(LOWER_FLYWHEEL_VELOCITY);
        motULauncher.setVelocity(UPPER_FLYWHEEL_VELOCITY);
        motIntake.setPower(intakePower);
        servoBLaunchFeeder.setPower(FEEDER_POWER);
        servoFLaunchFeeder.setPower((FEEDER_POWER));
    }
}

