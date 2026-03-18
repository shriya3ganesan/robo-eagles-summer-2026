package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.RobotContainer;


/**
 * Subsystem to manage telemetry
 *
 * @author Kw126
 */
public class Telemetry extends SubsystemBase {

    // Local objects and variables here
    public static org.firstinspires.ftc.robotcore.external.Telemetry RCTelemetry;
    public static boolean testingTelemetry = true;

    /** Place code here to initialize subsystem */
    public Telemetry() {
        RCTelemetry = RobotContainer.ActiveOpMode.telemetry;
    }

    /** Method called periodically by the scheduler
     * Place any code here you wish to have run periodically */
    @Override
    public void periodic() {

    }

    /** what is displayed as part of this telemetry?
     *
     * runs when testing telemetry
     */
    public void testingTelemetryEmpty(){
        if (testingTelemetry){
            //telemetry goes here
        }
    }


    /** what is displayed as part of this telemetry?
     *
     * runs when not testing telemetry
     */
    public void operatorTelemetryEmpty(){
        if (!testingTelemetry) {
            //telemetry goes here
        }
    }

    /**
     *Adds an item to the end if the telemetry being built for driver station display. The value shown will be the result of calling toString() on the provided value object. The caption and value are shown on the driver station separated by the #getCaptionValueSeparator() caption value separator. The item is removed if clear() or clearAll() is called.
     * @param caption the caption to use
     * @param value the value to display
     */
    public void addData(String caption, Object value){
        if (testingTelemetry) {
            RCTelemetry.addData(caption, value);
        }
    }

    /**
     *Adds an item to the end if the telemetry being built for driver station display. The value shown will be the result of calling toString() on the provided value object. The caption and value are shown on the driver station separated by the #getCaptionValueSeparator() caption value separator. The item is removed if clear() or clearAll() is called.
     * @param caption the caption to use
     * @param value the value to display
     * @param telemetryForTesting If true the data will be shown with the testing data, if false the data will be shown with the operator data
     */
    public void addData(String caption, Object value, boolean telemetryForTesting){
        if (testingTelemetry == telemetryForTesting) {
            RCTelemetry.addData(caption, value);
        }
    }

    /**
     *Creates and returns a new line in the receiver Telemetry.
     *
     *  @param caption the caption for the line
     */
    public void addLine(String caption){
        if (testingTelemetry) {
            RCTelemetry.addLine(caption);
        }
    }

    /**
     *Creates and returns a new line in the receiver Telemetry.
     *
     *  @param caption the caption for the line
     *  @param telemetryForTesting If true the data will be shown with the testing data, if false the data will be shown with the operator data
     */
    public void addLine(String caption, boolean telemetryForTesting){
        if (testingTelemetry == telemetryForTesting) {
            RCTelemetry.addLine(caption);
        }
    }

    /**
     * displays robot fieldX, fieldY, and Yaw.
     *
     * Runs when testing telemetry.
     */
    public void odometryTelemetry(){
        if (testingTelemetry) {
            Pose2d position = RobotContainer.odometry.getCurrentPos();
            RCTelemetry.addData("fieldX", position.getX());
            RCTelemetry.addData("fieldY", position.getY());
            RCTelemetry.addData("Yaw", position.getRotation().getDegrees());
        }
    }

    /**displays interval time and execute time
     *
     * runs when testing telemetry
     */
    public void timerOdometry(){
        if (testingTelemetry){
            RCTelemetry.addData("interval time(ms)", RobotContainer.intervaltime);
            RCTelemetry.addData("execute time(ms)", RobotContainer.exectimer.milliseconds());
        }
    }


    /**shows climbDistance and roll
     *
     * runs when testing telemetry
     */
    public void climbTelemetry(){
        if (testingTelemetry) {
            //RCTelemetry.addData("Climb distance", RobotContainer.climb.getClimbDistance());
            //RCTelemetry.addData("roll", RobotContainer.gyro.getRollAngle());
        }
    }


    /**
     * Sends the receiver {@link org.firstinspires.ftc.robotcore.external.Telemetry} to the driver station if more than the {@link #getMsTransmissionInterval()
     * transmission interval} has elapsed since the last transmission, or schedules the transmission
     * of the receiver should no subsequent {@link org.firstinspires.ftc.robotcore.external.Telemetry} state be scheduled for transmission before
     * the {@link #getMsTransmissionInterval() transmission interval} expires.
     */
    public void update(){

        RCTelemetry.update();
    }


    /**toggles telemetry mode between testing and operator
     *
     */
    public void telemetryModeToggle(){
        testingTelemetry = !testingTelemetry;
    }
}