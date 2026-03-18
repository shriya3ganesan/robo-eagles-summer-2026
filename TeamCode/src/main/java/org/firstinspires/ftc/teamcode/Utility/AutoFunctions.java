package org.firstinspires.ftc.teamcode.Utility;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.geometry.Translation2d;

import org.firstinspires.ftc.teamcode.RobotContainer;

/**
 * A class containing functions to mirror our automated movements from the blue side to the red side.
 *
 * @author knutt5
 *
 * */
public class AutoFunctions {

    /**
     * mirrors the provided Pose2d from the blue side to the red side.
     * Requires a public boolean variable in RobotContainer called isRedAlliance that is true when
     * we're on the red alliance and false when on blue alliance.
     *
     * @param pose the Pose2d to be mirrored
     *
     * @return a Pose2d with coordinates for the correct alliance side
     */
    public static Pose2d redVsBlue(Pose2d pose) {
        if (RobotContainer.isRedAlliance)
            return new Pose2d (pose.getX(),-pose.getY(),new Rotation2d(-pose.getHeading()));
        else
            return pose;
    }

    /**
     * Mirror provided Translation2d from the blue side to the red side.
     * Requires a public boolean variable in RobotContainer called isRedAlliance that is true when
     * we're on the red alliance and false when on blue alliance.
     *
     * @param translation the Translation2d to be mirrored
     *
     * @return a Translation2d with coordinates for the correct alliance side
     */
    public static Translation2d redVsBlue(Translation2d translation) {
        if (RobotContainer.isRedAlliance)
            return new Translation2d (translation.getX(),-translation.getY());
        else
            return translation;
    }

    /**
     * Mirror provided Rotation2d from the blue side to the red side.
     * Requires a public boolean variable in RobotContainer called isRedAlliance that is true when
     * we're on the red alliance and false when on blue alliance.
     *
     * @param rotation the Rotation2d to be mirrored
     *
     * @return a Rotation2d with coordinates for the correct alliance side
     */
    public static Rotation2d redVsBlue(Rotation2d rotation) {
        if (RobotContainer.isRedAlliance)
            return new Rotation2d (-rotation.getRadians());
        else
            return rotation;
    }

    /**
     * Mirror provided angle from the blue side to the red side.
     * Requires a public boolean variable in RobotContainer called isRedAlliance that is true when
     * we're on the red alliance and false when on blue alliance.
     *
     * @param angle the angle in degrees to be mirrored
     * @return an angle in degrees for the correct alliance side
     */
    public static double redVsBlue(double angle){
        if (RobotContainer.isRedAlliance)
            return -angle;
        else
            return angle;
    }
}
