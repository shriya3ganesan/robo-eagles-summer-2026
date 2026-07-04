package org.firstinspires.ftc.teamcode.SWEEP.Classes;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

//TODO Comment and document
public abstract class Robot {

    public Telemetry telemetry;
    public Subsystem[] subsystems;
    public ElapsedTime runtime;
    Alliance alliance = Alliance.RED;
    ArrayList<Action> activeActions = new ArrayList<>();
    /**
     * An Enum for the two different alliances that the robot could be a part of
     */
    enum Alliance {
        RED, BLUE
    }
    public void initialize(){

    }
    public void startMatch(){
        runtime.reset();
    }
    public Alliance getAlliance(){
        return this.alliance;
    }
    public void setAlliance(Alliance alliance){
        this.alliance = alliance;
    }
    public void autonomousUpdate(){
        telemetry.addLine("Teleop In progress, Override the teleopUpdate method in robot to change something!");
        updateActions();
        telemetry.update();
    }
    public void teleopUpdate(){
        telemetry.addLine("Autonomous In progress, Override the teleopUpdate method in robot to change something!");
        updateActions();
        telemetry.update();
    }
    public void emergencyStop(){
        for (Subsystem subsystem : subsystems){
            subsystem.setDisabled(true);
        }
        telemetry.addLine("--ROBOT EMERGENCY STOPPED, RE INIT TO CONTINUE--");
        telemetry.update();
    }
    public double getGameTime(){
        return runtime.seconds();
    }
    public Subsystem[] getAllSubsystems(){
        return subsystems;
    }
    public void updateActions(){
        for (Action action : activeActions){
            action.process();
            if (action.completion()){
                action.end();
                activeActions.remove(action);
            }
        }
    }
    public void runAction(Action action){
        if (action == null) return;
        action.execute();
        activeActions.add(action);
    }
}
