package org.firstinspires.ftc.teamcode.OpModes.Auto;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.RobotContainer;

/*
 * This file contains an example of an "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 */
@Autonomous(name="Blue Auto", group="OpMode", preselectTeleOp="Blue TeleOp")
//@Disabled
public class BlueAutoOp extends CommandOpMode {

    // Initialize all objects, set up subsystems, etc...
    @Override
    public void initialize() {

        // initialize robot
        // set team alliance color to blue (isRedAlliance=false)
        RobotContainer.Init(this, false);

        // perform any auto initialization
        RobotContainer.Init_Auto();

        // do not proceed until start button is pressed or stop is requested
        // note: this code replaces the previous "waitForStart()"
        while (!isStarted() && !isStopRequested())
            RobotContainer.Periodic();

        // if start button has not been pressed
        if (!opModeIsActive())
            // we were told to stop so interrupt this thread
            Thread.currentThread().interrupt();
        else
        {
            // start was pressed
            // perform any functions to be run at start of auto
            RobotContainer.Start_Auto();

            // ---------- autonomous command ----------

            // add autonomous command to scheduler and run it
            //new Front9Auto().schedule();
        }

    }

    // Run Op Mode. Is called after user presses play button
    // called continuously
    @Override
    public void run() {
        // execute robot periodic function
        RobotContainer.Periodic();
    }
}