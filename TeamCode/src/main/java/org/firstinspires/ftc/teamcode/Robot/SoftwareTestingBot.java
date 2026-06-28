package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Robot.Subsystems.Drivetrain;

public class SoftwareTestingBot extends Robot{

    public SoftwareTestingBot(HardwareMap hardwareMap, Telemetry telemetry){
        runtime = new ElapsedTime();
        subsystems = new Subsystem[]{
                new Drivetrain(hardwareMap,telemetry, false),
        };
    }

    @Override
    public void initialize() {

    }

    @Override
    public void teleopUpdate() {

    }

    @Override
    public void autonomousUpdate() {

    }
}
