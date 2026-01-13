package org.firstinspires.ftc.teamcode.mechanisms;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Launcher {
    private final int FEED_TIME_MILLISECONDS = 500; //The feeder servo runs this long when a shot is requested.
    private final double FEED_STOP = 0.0;
    private final double FEED_START = 1.0;

    private DcMotorEx lowerLaunch, upperLaunch, launchFeeder;
    //private Servo launchFeeder;

    private int _launchSpeed = 0; // Commanded launch motor velocity

    /*
     * When we control our launcher motor, we are using encoders. These allow the control system
     * to read the current speed of the motor and apply more or less power to keep it at a constant
     * velocity. Here we are setting the target, and minimum velocity that the launcher should run
     * at. The minimum velocity is a threshold for determining when to fire.
     */
    final double LAUNCHER_TARGET_VELOCITY = 800;
    final double LAUNCHER_MIN_VELOCITY = 700;

    private final double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    //private final double FULL_SPEED = 1.0;

    ElapsedTime feederTimer = new ElapsedTime();

    /*
     * NOTE: we are not currently using the state machine
     * TECH TIP: State Machines
     * We use a "state machine" to control our launcher motor and feeder servos in this program.
     * The first step of a state machine is creating an enum that captures the different "states"
     * that our code can be in.
     * The core advantage of a state machine is that it allows us to continue to loop through all
     * of our code while only running specific code when it's necessary. We can continuously check
     * what "State" our machine is in, run the associated code, and when we are done with that step
     * move on to the next state.
     * This enum is called the "LaunchState". It reflects the current condition of the shooter
     * motor and we move through the enum when the user asks our code to fire a shot.
     * It starts at idle, when the user requests a launch, we enter SPIN_UP where we get the
     * motor up to speed, once it meets a minimum speed then it starts and then ends the launch process.
     * We can use higher level code to cycle through these states. But this allows us to write
     * functions and autonomous routines in a way that avoids loops within loops, and "waits".
     */
    private enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    private LaunchState launchState;

    public void init (HardwareMap hwMap) {
        upperLaunch = hwMap.get(DcMotorEx.class, "left_launch");
        lowerLaunch = hwMap.get(DcMotorEx.class, "right_launch");
        launchFeeder = hwMap.get(DcMotorEx.class, "feeder");
        //launchFeeder = hwMap.get(Servo.class,"launch_feeder");

        // Set launcher motor to RUN_USING_ENCODER and BRAKE to slow down faster than coasting.

        PIDFCoefficients pidf = new PIDFCoefficients(300, 0, 0.001, 10);

        // TODO: add these back in with full battery to test if they work (or at leats do not break anything)
        upperLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lowerLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        upperLaunch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        lowerLaunch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        upperLaunch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lowerLaunch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        upperLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        lowerLaunch.setDirection(DcMotorSimple.Direction.FORWARD);
        launchFeeder.setDirection(DcMotorSimple.Direction.REVERSE);


        // TODO: tets to see if this makes a difference
        /* add these lines when encoders have been attached to the launch motors
        upperLaunch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                300, 0, 0, 10));
        lowerLaunch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                300, 0, 0, 10));
*/

        // Set left feeder servo to reverse so both servos work to feed ball into robot.
        //launchFeeder.setDirection(Servo.Direction.REVERSE);
        resetFeeder(); // default it to "0" degrees

        // Set initial state of launcher to IDLE.
        launchState = LaunchState.IDLE;
        //stopFeeder();
        stopLauncher();
    }

    /// Set launch feeder server back to "0" position
    public void resetFeeder() {
        // Set feeders to a preset value to stop the servos.
        launchFeeder.setPower(STOP_SPEED);
    }

/*    private boolean _triggerActive;
    public boolean getTriggerActive() {
        return _triggerActive;
    }
    public void triggerFeeder() {
        // run this in a separate thread so the sleep doesn't interfere with other controls
        new Thread(new Runnable() {
            @Override
            public void run() {
                // ste this TRUE, so we do not call it again un til done
                _triggerActive = true;
                // move launch feeder to 90 degrees
                launchFeeder.setPosition(FEED_POSITION);

                // hold it there for X amount of time
                try {
                    sleep(FEED_TIME_MILLISECONDS);
                } catch (InterruptedException e) {
                    // nothing should interrupt the sleep
                }
                // move launch feeder back to "0" position
                resetFeeder();
                // set it to FALSE, so we can call it again
                _triggerActive = false;
            }
        }).start();
    }
*/


    // Manual control function to increase launch speed by 100
    // Used for testing and calibration
    public void incrementLaunchSpeed() {
        _launchSpeed = _launchSpeed+100;
    }

    // Manual control function to decrease launch speed by 100
    // Used for testing and calibration
    public void decrementLaunchSpeed() {
        _launchSpeed = _launchSpeed-100;
    }



    // Sets both upper and lower launch motors to the same _launchSpeed
    public void setMotorVelocity() {
        lowerLaunch.setVelocity(_launchSpeed);
        upperLaunch.setVelocity(_launchSpeed);
    }

    public void setMotorVelocityForDistance(double rangeinCm) {
        // TODO: calculate appropriate motor velocity based on range
        _launchSpeed = (int)(10*rangeinCm/4+710);
        setMotorVelocity();
    }

    ///  Returns the commanded _launchSpeed
    public double getTargetLaunchSpeed() {
        return _launchSpeed;
                //_launchSpeed;
    }

    /// Returns the measured upper launch motor velocity
    public double getUpperVelocity() {
        return upperLaunch.getVelocity();
    }

    /// Returns the measured lower launch motor velocity
    public double getLowerVelocity() {
        return lowerLaunch.getVelocity();
    }
    public double getLaunchSpeedError() {
        double AverageVelocity = (getLowerVelocity() + getUpperVelocity())/2.0;
        double TargetError = getTargetLaunchSpeed() - AverageVelocity;
        return Math.abs(TargetError);
    }


    /*
    Launch state machine below not currently used
     */
    public String getState() {
        return launchState.toString();
    }

    /// Used to start the auto launcher state machine - currently unused
    public void startLauncher(){
        if (launchState == LaunchState.IDLE) {
            // transition states
            launchState = LaunchState.SPIN_UP;
        }
    }

    public void stopLauncher () {
        _launchSpeed = 0;
        setMotorVelocity();

        //stopFeeder();
//        upperLaunch.setVelocity(STOP_SPEED);
//        lowerLaunch.setVelocity(STOP_SPEED);
        launchState = LaunchState.IDLE;
    }

    public void updateState () {

       switch (launchState) {
            case IDLE:
                break;
            case SPIN_UP:
                //feederTimer.reset();
                upperLaunch.setVelocity(_launchSpeed);
                lowerLaunch.setVelocity(_launchSpeed);
                if ((upperLaunch.getVelocity() > _launchSpeed-100) &&
                        (lowerLaunch.getVelocity() > _launchSpeed-100)){
                    // transition states
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case LAUNCH:
                launchFeeder.setPower(FEED_START);
                feederTimer.reset();
                // transition state
                launchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING:
                if (feederTimer.milliseconds() > FEED_TIME_MILLISECONDS) {
                    resetFeeder();
                    // transition state
                    launchState = LaunchState.IDLE;
                }
                break;
        }
    }
/*
    public void launchMotorOn () {
        upperLaunch.setPower(0.05);
        lowerLaunch.setPower(0.05);
    }

    public void launchMotorOff () {
        upperLaunch.setPower(0);
        lowerLaunch.setPower(0);
    }

 */
    public void loadBall () {
        launchFeeder.setPower(FEED_START);
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        resetFeeder();*/
    }


/*
    public void startLauncher(){
        if (launchState == LaunchState.IDLE) {
            // transition states
            launchState = LaunchState.SPIN_UP;
        }
    }

    public void stopLauncher () {
        stopFeeder();
        upperLaunch.setVelocity(STOP_SPEED);
        lowerLaunch.setVelocity(STOP_SPEED);
        launchState = LaunchState.IDLE;
    }

    public String getState() {
        return launchState.toString();
    }

    public double getUpperVelocity() {
        return upperLaunch.getVelocity();
    }

    public double getLowerVelocity() {
        return lowerLaunch.getVelocity();
    }*/

}
