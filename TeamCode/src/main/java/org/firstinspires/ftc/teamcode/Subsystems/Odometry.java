package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.hardware.limelightvision.LLResult;
import org.firstinspires.ftc.teamcode.RobotContainer;


/** Subsystem */
// EXAMPLE - use @Configurable to add public variables to dashboard for realtime updating
public class Odometry extends SubsystemBase {

    // stored robot position (static) used to keep robot pose between opmodes
    // value persists even if new odometry system is created.
    private static Pose2d StoredRobotPose = new Pose2d(0,0, new Rotation2d(0));

    // current robot position (in m)
    private Pose2d CurrentPose;

    /** Place code here to initialize subsystem */
    public Odometry() {

        // initialize field position from stored value (i.e. previous op-mode)
        setCurrentPos(StoredRobotPose);
    }

    /** Method called periodically by the scheduler
     * Place any code here you wish to have run periodically */
    @Override
    public void periodic() {

        // get/save current robot position
        CurrentPose = RobotContainer.odometryPod.GetPose();

        // ---------- Limelight Odometry Integration ----------
        // NOTE: This section to be worked on.  Commented out for now.

        // get limelight MT2 odometry
        //LLResult result = RobotContainer.limeLight.limeLight.getLatestResult();

        // new addition: only use apriltag when in teleop mode
        // if we have valid result and it is not stale (>100ms)
        // and we have at least one apriltag detection

        //if (RobotContainer.GetCurrentMode()== RobotContainer.Modes.TeleOp &&
        //        result!=null && result.isValid() && result.getStaleness() < 100 &&
        //    result.getFiducialResults()!=null && !result.getFiducialResults().isEmpty())
        //{
        //    // we have a valid LL result and at least one tag detection
        //    double LLpose_x = result.getBotpose_MT2().getPosition().x;
        //    double LLpose_y = result.getBotpose_MT2().getPosition().y;
        //
        //    //RobotContainer.RCTelemetry.addData("ken_x", LLpose_x);
        //     //RobotContainer.RCTelemetry.addData("ken_y", LLpose_y);

        //  MecanumDriveWheelSpeeds speed = RobotContainer.drivesystem.GetWheelSpeeds();

        //    // does LL result make sense and is robot currently at low speed (<0.1m/s)?
        //    // 0.1m/s used. assume LL result off by 50ms, this would result in
        //    // 0.1m/s*0.05s = 0.005m(=1/2cm) error which is acceptable
        //    if (LLpose_x > -1.8 && LLpose_x <1.8 &&
        //         LLpose_y > -1.8 && LLpose_y <1.8 &&
        //        speed.rearRightMetersPerSecond > -0.1 &&
        //        speed.rearRightMetersPerSecond < 0.1 &&
        //        speed.rearLeftMetersPerSecond > -0.1 &&
        //        speed.rearLeftMetersPerSecond < 0.1 &&
        //        speed.frontRightMetersPerSecond > -0.1 &&
        //        speed.frontRightMetersPerSecond < 0.1 &&
        //        speed.frontLeftMetersPerSecond > -0.1 &&
        //        speed.frontLeftMetersPerSecond < 0.1)
        //    {
        //        Rotation2d gyro = new Rotation2d(Math.toRadians(RobotContainer.gyro.getYawAngle()));
        //
        //        // determine new pose - apply low pass filter for blending
        //         double newpose_x = 0.95*CurrentPose.getX() + 0.05*LLpose_x;
        //        double newpose_y = 0.95*CurrentPose.getY() + 0.05*LLpose_y;
        //
        //        // construct new pose2d
        //        CurrentPose = new Pose2d(newpose_x, newpose_y, gyro);
        //        //CurrentPose = new Pose2d(newpose_x, newpose_y, CurrentPose.getRotation());
        //
        //        // adjust robot's odometry for new value
        //        RobotContainer.odometry.setCurrentPos(CurrentPose);
        //    }
        //}


        // save position to data store, in case op mode ends
        // check again if op mode not about to shut down - otherwise don't save it
        if (!RobotContainer.ActiveOpMode.isStopRequested())
            StoredRobotPose = new Pose2d (CurrentPose.getX(), CurrentPose.getY(), CurrentPose.getRotation());
    }


    /**
     * returns the current robot pose
     * @return Pose2d of robot position (in m)
     */
    public Pose2d getCurrentPos() {
       return new Pose2d (CurrentPose.getX(), CurrentPose.getY(), CurrentPose.getRotation());
    }

    /**
     * sets robot position to provided Pose2d
     * @param pos - Pose2d of robot position (in m)
     */
    public void setCurrentPos(Pose2d pos){
        // record new robot position
        CurrentPose = pos;

        // set position of odometry pods
        RobotContainer.odometryPod.SetPose(pos);

        // set angle of gyro
        RobotContainer.gyro.setYawAngle(Math.toDegrees(pos.getHeading()));
    }

    /**
     * resets current pose2d to default (0,0,0deg)
     */
    public void resetCurrentPos(){
        setCurrentPos(new Pose2d(0,0,new Rotation2d(0)));
    }

}