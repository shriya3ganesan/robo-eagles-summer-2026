package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

import java.util.ArrayList;
import java.util.Collections;

enum LaunchColor { //an enum for different color cases for launching
    PURPLE,
    GREEN,
    EITHER
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
    void launch (LaunchColor launchColor) {}

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


    class DrumRequest {
        double position;
        WaitState nextState;
    }
    ComplexMechGlob (HardwareMap hardwareMap, Telemetry telemetry) {}

    int findNearestSlot (LaunchColor launchColor) {
        double minDistance = Double.MAX_VALUE;
        int minSlot = -1;

        for (int i = 0; i <= 2; i++){
            double distance;
            if (launchColor == LaunchColor.PURPLE && slotOccupiedBy.get (i) == PixelColor.PURPLE){
                return i;
            } else if (launchColor == LaunchColor.GREEN && slotOccupiedBy.get (i) == PixelColor.GREEN){
                return i;
            } else if (launchColor == LaunchColor.EITHER &&slotOccupiedBy.get (i) != PixelColor.NONE){
                return i;
            }
        }
        return -1;
    }
    @Override
    void intake (double intakeSpeed) {

        userIntakeSpeed = intakeSpeed;
    }

    @Override
        //a class that controls the launcher and transfer
    void launch (LaunchColor launchColor) {



//        drumQueue.add (new DrumRequest (position, WaitState.TRANSFER));
    }

    @Override
    void update () {

    }
}

