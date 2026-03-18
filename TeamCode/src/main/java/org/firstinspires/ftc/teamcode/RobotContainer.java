package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Commands.ManualDrive;
import org.firstinspires.ftc.teamcode.Subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.Subsystems.Gyro;
import org.firstinspires.ftc.teamcode.Subsystems.Odometry;
import org.firstinspires.ftc.teamcode.Subsystems.PinpointOdometry;
import org.firstinspires.ftc.teamcode.Subsystems.Telemetry;
import org.firstinspires.ftc.teamcode.Utility.AutoFunctions;
import java.util.List;


public class RobotContainer {

    // active OpMode - used so any subsystem and command and access it and its members
    public static CommandOpMode ActiveOpMode;

    // team alliance color = false if robot on blue alliance, true for red
    public static boolean isRedAlliance;

    // FTC dashboard and telemetries
    //public static Panels Panels;
    public static Telemetry telemetrySubsystem;

    // timer used to determine how often to run scheduler periodic
    private static ElapsedTime timer;
    public static ElapsedTime exectimer;

    // create robot GamePads
    public static GamepadEx driverOp;
    public static GamepadEx toolOp;

    // create pointers to robot subsystems
    public static Gyro gyro;
    public static PinpointOdometry odometryPod;
    public static Odometry odometry;
    public static DriveTrain drivesystem;

    // Angle of the robot at the start of auto
    public static double RedStartAngle = 90;
    public static double BlueStartAngle = -90;

    // List of robot control and expansion hubs - used for caching of I/O
    static List<LynxModule> allHubs;

    // Robot Modes
    public enum Modes { Off, AutoInit, Auto, TeleOp, TeleOpInit}
    private static Modes CurrentRobotMode;

    public static double intervaltime;


    /**Robot initialization - common to both auto and teleop
     * @param mode A value from the Modes enum representing the current opmode being run, valid Modes as of 2/9/2026: Off, AutoInit, Auto, TeleOp
     * @param RedAlliance True if red alliance, false if blue alliance
     */
    public static void Init(CommandOpMode mode, boolean RedAlliance) {

        // save pointer to active OpMode
        ActiveOpMode = mode;

        // set alliance colour
        isRedAlliance = RedAlliance;

        // set robot mode - robot is off until we have initialized
        CurrentRobotMode = Modes.Off;

        // create list of robot control and expansion hubs
        // set each for manual caching - cache updated in periodic()
        allHubs = ActiveOpMode.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        // create and reset timer
        timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timer.reset();
        exectimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timer.reset();

        // set up dashboard and various telemetries
        // Panels = new Panels();
        telemetrySubsystem = new Telemetry();

        // cancel any commands previously running by scheduler
        CommandScheduler.getInstance().cancelAll();

        // create gamepads
        driverOp = new GamepadEx(ActiveOpMode.gamepad1);
        toolOp = new GamepadEx(ActiveOpMode.gamepad2);

        // create systems
        gyro = new Gyro();
        odometryPod = new PinpointOdometry();
        odometry = new Odometry();
        drivesystem = new DriveTrain();

    }

    /**Robot initialization for teleop - This runs once at initialization of teleop*/
    public static void Init_TeleOp() {
    }

    /**Robot starting code for teleop - This runs once at start of teleop*/
    public static void Start_TeleOp() {

        // robot is in teleop mode
        CurrentRobotMode = Modes.TeleOp;

        // set drivetrain default command to manual driving mode
        drivesystem.setDefaultCommand(new ManualDrive());


        //      -------------------------- Driver Controls --------------------------
        // Reset odometry
        driverOp.getGamepadButton(GamepadKeys.Button.BACK).whenPressed(new InstantCommand(()-> odometry.setCurrentPos
                (AutoFunctions.redVsBlue(new Pose2d(0.0, 0.0, new Rotation2d(Math.toRadians(-90.0)))))));



        //      -------------------------- Examples --------------------------
        // bind commands to buttons
        // bind gyro reset to back button.
        // Note: since reset is very simple command, we can just use 'InstandCommand'
        // instead of creating a full command, just to run one line of java code.

        // example turn to command
        // driverOp.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenHeld((new TurnTo(AutoFunctions.redVsBlue(0.0), true, 3.0)));

        // example sequential command
        // driverOp.getGamepadButton(GamepadKeys.Button.Y).whileHeld(new ExampleCommandGroup1());


        // example of binding more complex command to a button. This would be in a separate command file
        // driverOp.getGamepadButton(GamepadKeys.Button.BACK).whenPressed(new ExampleCommand());

        // add other button commands here
        // Note: can trigger commands on
        // whenPressed - once when button is pressed
        // whenHeld - runs command while button held, but does not restart if command ends
        // whileHeld - runs command while button held, but will restart command if it ends
        // whenReleased - runs once when button is released
        // togglewhenPressed - turns command on and off at each button press

    }

    /**Robot initialization for auto - This runs once at initialization of auto*/
    public static void Init_Auto() {

        // robot is in auto init mode
        CurrentRobotMode = Modes.AutoInit;

    }

    /**Robot starting code for auto - This runs once at start of auto*/
    public static void Start_Auto() {

        // robot is in auto mode
        CurrentRobotMode = Modes.Auto;

        // perform any autonomous-specific start functions here
    }


    /**call this function periodically to operate scheduler*/
    public static void Periodic() {

        // clear I/O cache for robot control and expansion hubs
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        // actual interval time
        intervaltime = timer.milliseconds();

        // execute robot periodic function 50 times per second (=50Hz)
        if (intervaltime>=20.0) {

            // reset timer
            timer.reset();

            // start execution timer
            exectimer.reset();

            // run scheduler
            CommandScheduler.getInstance().run();

            // report robot odometry on robot controller
            telemetrySubsystem.odometryTelemetry();

            // report time interval on robot controller
            telemetrySubsystem.timerOdometry();

            telemetrySubsystem.update();
        }
    }


    /**Gets the current alliance colour
     * @return True if red alliance, false if blue alliance
     */
    public static boolean isRedAlliance() {
        return isRedAlliance;
    }

    /**Returns the current robot mode
     * @return a value from the Modes enum, valid Modes as of 2/9/2026: Off, AutoInit, Auto, TeleOp*/
    public static Modes GetCurrentMode() { return CurrentRobotMode; }

    /**Gets our most commonly used starting angles for auto when we're on blue alliance
     * @return The angle of the robot when it's facing the red alliance drive team*/
    public static double getBlueStartAngle() {
        return BlueStartAngle;
    }

    /**Gets our most commonly used starting angles for auto when we're on red alliance
     * @return The angle of the robot when it's facing the blue alliance drive team*/
    public static double getRedStartAngle() {
        return RedStartAngle;
    }

}
