package org.firstinspires.ftc.teamcode.SWEEP.Classes;

/**
 * A class used to safely manage a robot's mechanisms with season specific code to the
 * generalized SWEEP Library, where the actions can be called autonomously
 *
 * //TODO: explain how to implement this system and write example code for using it
 */
public interface SWEEPAction {
    /**
     * Start the action - any initial logic
     */
    void execute();

    /**
     * Update loop for the action, runs every master iteration
     */
    void process();

    /**
     * If another action is queued that conflicts with this action, stop this action safely
     * Returns true if the action is complete, will return false if there is a reason to block the change
     *
     */
    default boolean interrupt(){
        end();
        return true; // Action can be interrupted safely
    }

    /**
     * Used by the action manager to check if the action is naturally complete by specific conditions
     * @return True if action is complete, False if it is still running
     */
    boolean completion();

    /**
     * The last bit of code that will run once the action is completed naturally
     */
    void end();

}
