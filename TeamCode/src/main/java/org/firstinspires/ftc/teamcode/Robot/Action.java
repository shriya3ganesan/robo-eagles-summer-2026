package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class Action {
    Robot robot;
    double holdTime = 0; // Default hold time of 0 seconds, can be changed in the constructor
    private final ElapsedTime timer = new ElapsedTime();
    public Action(Robot robot){
        this.robot = robot;
    }
    public void setHoldTime(double holdTime){
        this.holdTime = holdTime;
    }
    /**
     * Start the action - any initial logic
     */
    public abstract void execute();

    /**
     * Update loop for the action, runs every master iteration
     */
    public abstract void process();
    /**
     * If another action is queued that conflicts with this action, stop this action safely
     * Returns true if the action is complete, will return false if there is a reason to block the change
     *
     */
    public boolean interrupt(){
        end();
        return true; // Action can be interrupted safely
    }

    /**
     * Used by the action manager to check if the action is naturally complete by specific conditions
     * @return True if action is complete, False if it is still running
     */
    public boolean completion(){
        return timer.seconds() >= holdTime;
    }

    /**
     * The last bit of code that will run once the action is completed naturally
     */
    abstract public void end();

}
