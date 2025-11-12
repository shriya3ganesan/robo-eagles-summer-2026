package org.firstinspires.ftc.team417;


import com.qualcomm.robotcore.hardware.HardwareMap;
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

//
public class ComplexMechGlob extends MechGlob { //a class encompassing all code that IS for slowbot
    double userIntakeSpeed;
    double drumServoPosition; //the last position the servo went to
    ArrayList<DrumRequest> drumQueue = new ArrayList<> ();

    ArrayList<PixelColor> slotOccupiedBy = new ArrayList<> (Collections.nCopies(3, PixelColor.NONE));
    enum WaitState {
        DRUM_MOVE, //waiting for the drum to reach desired position
        INTAKE, //waiting for the intake to finish
        TRANSFER, //waiting for the transfer to finish
        SPIN_UP, //waiting for the flywheel(s) to spin up
        IDLE, //waiting for input when the drum is full

    }
    // arrays with placeholder values for servo positions and voltages relative to intake and launch
    final double [] INTAKE_POSITIONS = {0, 1, 2};
    final double [] INTAKE_VOLTS = {0, 1, 2};
    final double [] LAUNCH_POSITIONS = {0, 1, 2};
    final double [] LAUNCH_VOLTS = {0, 1, 2};
    double lastQueuedPosition; //variable remembering where the servo was told to go last
    HardwareMap hardwareMap;
    Telemetry telemetry;


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
    }

    //the position argument denotes whether we are using intake or launch positions
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
            queueDrum(LAUNCH_POSITIONS[minSlot], WaitState.SPIN_UP);
            slotOccupiedBy.set (minSlot, PixelColor.NONE); //marking this slot as empty so we don't accidentally try to use it again
        }
    }
    void queueDrum (double position, WaitState waitState){
        drumQueue.add(new DrumRequest(position, waitState));
        lastQueuedPosition = position;
    }

    @Override
    void update () {

    }
}

