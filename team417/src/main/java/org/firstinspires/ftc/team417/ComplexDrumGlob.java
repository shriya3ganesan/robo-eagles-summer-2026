package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

enum LaunchColor { //an enum for different color cases for launching
    PURPLE,
    GREEN,
    EITHER
}
class DrumGlob { //a placeholder class encompassing all code that ISN'T for slowbot.
    DrumGlob(){}
    void intake (double intakeValue){}

    void launch (LaunchColor launchColor) {}


    static DrumGlob create (HardwareMap hardwareMap, Telemetry telemetry){
        if (MecanumDrive.isSlowBot) { //if the robot is slowbot, use ComplexDrumGlob.
            return new ComplexDrumGlob(hardwareMap, telemetry); //Go to ComplexDrumGlob class

        } else { //otherwise, use DrumGlob
            return new DrumGlob(); //Go to DrumGlob class
        }
    }
}

public class ComplexDrumGlob extends DrumGlob{ //a class encompassing all code that IS for slowbot

    ComplexDrumGlob(HardwareMap hardwareMap, Telemetry telemetry){}
    @Override
    void intake (double intakeValue){} //a class that controls the intake based on intakeValue

    void launch (LaunchColor launchColor) {} //a class that controls the launcher and transfer

}

