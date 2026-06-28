package org.firstinspires.ftc.teamcode.Robot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
//TODO Comment and document
public interface Robot {
    /**
     * An Enum for the two different alliances that the robot could be a part of
     */
    enum Alliance {
        RED, BLUE
    }
    public void initialize();
    public void startMatch();
    public Alliance getAlliance();
    public void setAlliance(Alliance alliance);
    public void teleopUpdate();
    public void autonomousUpdate();
    public void emergencyStop();
    public double getGameTime();
    public Subsystem[] getAllSubsystems();
}
