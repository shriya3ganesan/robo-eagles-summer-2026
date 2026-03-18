package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.RobotContainer;

/**
 * The Pinpoint Odometry class is a subsystem that manages the odometry pods using the Pinpoint Odometry Computer hardware.
 * It extends the SubsystemBase class from FTCLib.
 */
public class PinpointOdometry extends SubsystemBase {

    // Create pinpoint driver object
    GoBildaPinpointDriver pinpointDriver;

    // Encoder values from each odometry pod
    double OdometryLeftEncoder;
    double OdometryRightEncoder;
    double OdometryFrontEncoder;

    Pose2D myPose2D;

    /**
     * Constructor for the OctQuad class.
     * Initializes the OctoQuad hardware and resets encoders.
     */
    public PinpointOdometry() {
        // Set up pinpoint driver object
        pinpointDriver = RobotContainer.ActiveOpMode.hardwareMap.get(GoBildaPinpointDriver.class, "PinpointDriver");

        // Set encoder directions
        pinpointDriver.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);

        // Set encoder resolution
        pinpointDriver.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        // set x and y offset from centre
        pinpointDriver.setOffsets(-152.0,-70.0, DistanceUnit.MM);

        // Reset pinpoint driver
        pinpointDriver.resetPosAndIMU();

        // wait for pinpoint to finish resetting before continuing with initialization
        RobotContainer.ActiveOpMode.sleep(300);

    }

    /**
     * The periodic method is called periodically by the scheduler.
     * Reads the encoder values from the odometry pods.
     */
    @Override
    public void periodic() {
        pinpointDriver.update(); // the fast one
    }

    /**
     * Resets the odometry position and IMU heading to zero.
     */
    public void reset(){
        pinpointDriver.resetPosAndIMU();
    }

    /**
     * Sets the robot's pose to the specified Pose2d.
     *
     * @param pose the desired Pose2d to set the robot's position and heading
     */
    public void SetPose(Pose2d pose){
        Pose2D newPose = new Pose2D(DistanceUnit.METER, pose.getX(), pose.getY(), AngleUnit.RADIANS, pose.getHeading());

        pinpointDriver.setHeading(pose.getHeading(), AngleUnit.RADIANS);
        pinpointDriver.setPosition(newPose);

    }


    /**
     * gets the current pose of the robot
     *
     * @return A pose2d with the X & Y position in meters and the heading in radians
     */
    public Pose2d GetPose(){
        Pose2d pose = new Pose2d(pinpointDriver.getPosX(DistanceUnit.METER),
                pinpointDriver.getPosY(DistanceUnit.METER),
                new Rotation2d(pinpointDriver.getHeading(AngleUnit.RADIANS)));
        return pose;
    }
}