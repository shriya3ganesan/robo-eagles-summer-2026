package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

enum LaunchColor {
    PURPLE,
    GREEN,
    EITHER
}
class DrumGlob {
    DrumGlob(){}
    void intake (double intakeValue){}

    void launch (LaunchColor launchColor) {}

    static DrumGlob create (HardwareMap hardwareMap, Telemetry telemetry){
        if (MecanumDrive.isSlowBot) {
            return new ComplexDrumGlob(hardwareMap, telemetry);

        } else {
            return new DrumGlob();
        }
    }
}

public class ComplexDrumGlob extends DrumGlob{

    ComplexDrumGlob(HardwareMap hardwareMap, Telemetry telemetry){}
    @Override
    void intake (double intakeValue){}

    void launch (LaunchColor launchColor) {}

}

