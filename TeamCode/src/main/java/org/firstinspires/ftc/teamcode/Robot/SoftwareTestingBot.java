package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.ExampleCode.ExampleAction;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.SWEEPRobot;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Subsystem;
import org.firstinspires.ftc.teamcode.Robot.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;

public class SoftwareTestingBot extends SWEEPRobot {
    // Robot subsystems
    Drivetrain drivetrain;


    public SoftwareTestingBot(HardwareMap hardwareMap, Telemetry telemetry){
        runtime = new ElapsedTime();
        this.drivetrain = new Drivetrain(hardwareMap, telemetry, false);
        subsystems = new Subsystem[]{
                drivetrain,
        };mbots
    }

    @Override
    public void initialize() {
        drivetrain.enableFieldCentricDriving(false);
    }

    @Override
    public void teleopUpdate() {
        updateActions();
    }

    @Override
    public void autonomousUpdate() {
        updateActions();
    }

    // Subsystem getters
    public Drivetrain getDrivetrain() {
        return drivetrain;
    }

    // Robot action definitions
    public ExampleAction exampleAction(Coordinate coordinate, double time, double range){
        return new ExampleAction(this, coordinate, time, range);
    }
    public ExampleAction exampleAction(){
        return new ExampleAction(this, null, getDefaultActionTime(), getDefaultActionRange());
    }

}
