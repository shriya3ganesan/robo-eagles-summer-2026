package org.firstinspires.ftc.teamcode.field;

import com.pedropathing.geometry.Pose;

public class Red {
    // Auto
    // Start Pose of our robot.
    public static final Pose START_POSE_FAR = new Pose(80, 9, Math.toRadians(90));
    public static final Pose START_POSE_NEAR = new Pose(111, 135, Math.toRadians(90));
    public static final Pose APRILTAG_POSE_FAR = new Pose(81,10, Math.toRadians(90));
    public static final Pose APRILTAG_POSE_NEAR_REACH = new Pose(82,78.5, Math.toRadians(100));
    public static final Pose APRILTAG_POSE_NEAR_READ = new Pose(82,80.5, Math.toRadians(100));

    // Scoring Pose of our robot. It is facing the goal at a 45 degree angle.
    public static final Pose SCORE_POSE_AUTO = new Pose(85, 15.8, Math.toRadians(65));

    public static final Pose SCORE_POSE_NEAR = new Pose(84.7, 80.5, Math.toRadians(47));
    public static final Pose SCORE_POSE_FAR = new Pose(64, 21, Math.toRadians(57.5));

    // Highest (First Set) of Artifacts from the Spike Mark.
    public static final Pose READY_FAR_POSE = new Pose(98.5,36, Math.toRadians(180));
    public static final Pose ALIGN_FAR_POSE = new Pose(101.5, 36, Math.toRadians(180));
    public static final Pose PICKUP_FAR_POSE = new Pose(119.5, 36, Math.toRadians(180));

    // Middle (Second Set) of Artifacts from the Spike Mark.
    public static final Pose READY_MID_POSE = new Pose(98.5,60, Math.toRadians(180));
    public static final Pose ALIGN_MID_POSE = new Pose(101.5, 60, Math.toRadians(180));
    public static final Pose PICKUP_MID_POSE = new Pose(119.5, 60, Math.toRadians(180));

    // Lowest (Last Set) of Artifacts from the Spike Mark.
    public static final Pose READY_NEAR_POSE = new Pose(98.5,84, Math.toRadians(180));
    public static final Pose ALIGN_NEAR_POSE = new Pose(101.5, 84, Math.toRadians(180));
    public static final Pose PICKUP_NEAR_POSE = new Pose(119.5, 84, Math.toRadians(180));

    // Teleop
    public static final Pose TELEOP_START_FAR = new Pose(108, 12,Math.toRadians(90));
    public static final Pose TELEOP_START_MID = PICKUP_MID_POSE;
    public static final Pose TELEOP_START_NEAR = new Pose(126, 102,Math.toRadians(90));

    // Gate Start & End
    public static final Pose GATE_START_POSE = new Pose(121, 70, Math.toRadians(0));
    public static final Pose GATE_END_POSE = new Pose(127, 70, Math.toRadians(0));

    // Human State Pose
    public static final Pose HUMAN_STATE_POSE = new Pose(16, 13.8, Math.toRadians(0));

    // Endgame
    public static final Pose ENDGAME_POSE = new Pose(38.5, 33, Math.toRadians(90));

    private Red() {} // Prevent instantiation
}