package org.firstinspires.ftc.teamcode.CommandGroups;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;


// command template
public class TemplateDynamicSequenceCommand extends CommandBase {

    // the sequential command that we are creating and running
    SequentialCommandGroup cmd;


    // constructor
    public TemplateDynamicSequenceCommand() {

        // add subsystem requirements (if any) - for example:
        //addRequirements(RobotContainer.drivesystem);
    }

    // This method is called once when command is started
    @Override
    public void initialize() {

        // TODO - use this space to dynamically create command sequence
        cmd = new SequentialCommandGroup();

        // add first sequence
        // cmd.addCommands(new InitialCommands);

        // conditionally add next in sequence
        // if (this)
        // cmd.addCommands(new DoThis());
        // else
        // cmd.addCommands(new DoThat());


        // initialize the sequence command
        cmd.initialize();
    }

    // This method is called periodically while command is active
    @Override
    public void execute() {
        cmd.execute();
    }

    // This method to return true only when command is to finish. Otherwise return false
    @Override
    public boolean isFinished() {
        // we are finished only when the sequence is finished
        return cmd.isFinished();
    }

    // This method is called once when command is finished.
    @Override
    public void end(boolean interrupted) {
        // we are ending, end the sequence
        cmd.end(interrupted);
    }

}