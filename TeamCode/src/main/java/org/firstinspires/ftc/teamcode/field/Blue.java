
package org.firstinspires.ftc.teamcode.field;

import com.pedropathing.geometry.Pose;
public class Blue {
    // Auto
    // Start Pose of our robot.
    public static final Pose START_POSE_FAR = new Pose(64, 9, Math.toRadians(90));
    public static final Pose START_POSE_NEAR = new Pose(33, 135, Math.toRadians(90));
    public static final Pose APRILTAG_POSE_FAR = new Pose(63,10, Math.toRadians(90));
    public static final Pose APRILTAG_POSE_NEAR_REACH = new Pose(62,78.5, Math.toRadians(80));
    public static final Pose APRILTAG_POSE_NEAR_READ = new Pose(62,80.5, Math.toRadians(80));

    // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    public static final Pose SCORE_POSE_AUTO = new Pose(60, 15.8, Math.toRadians(112));

    public static final Pose SCORE_POSE_NEAR = new Pose(59.3, 80.5, Math.toRadians(133));
    public static final Pose SCORE_POSE_FAR   = new Pose(80, 21, Math.toRadians(122.5));

    // Highest (First Set) of Artifacts from the Spike Mark.
    public static final Pose READY_FAR_POSE = new Pose(45.5, 36,Math.toRadians(0));
    public static final Pose ALIGN_FAR_POSE = new Pose(42.5, 36, Math.toRadians(0));
    public static final Pose PICKUP_FAR_POSE = new Pose(24.5, 36, Math.toRadians(0));

    // Middle (Second Set) of Artifacts from the Spike Mark.
    public static final Pose READY_MID_POSE = new Pose(45.5, 60,Math.toRadians(0));
    public static final Pose ALIGN_MID_POSE  = new Pose(42.5, 60, Math.toRadians(0));
    public static final Pose PICKUP_MID_POSE = new Pose(24.5, 60, Math.toRadians(0));

    // Lowest (Last Set) of Artifacts from the Spike Mark.
    public static final Pose READY_NEAR_POSE = new Pose(45.5, 84,Math.toRadians(0));
    public static final Pose ALIGN_NEAR_POSE  = new Pose(42.5, 84, Math.toRadians(0));
    public static final Pose PICKUP_NEAR_POSE = new Pose(24.5, 84, Math.toRadians(0));

    // Teleop
    public static final Pose TELEOP_START_FAR = new Pose(36, 12,Math.toRadians(90));
    public static final Pose TELEOP_START_MID = PICKUP_MID_POSE;
    public static final Pose TELEOP_START_NEAR = new Pose(18, 102,Math.toRadians(90));

    // Gate Start & End
    public static final Pose GATE_START_POSE = new Pose(23,70, Math.toRadians(180));
    public static final Pose GATE_END_POSE = new Pose(18, 70, Math.toRadians(180));

    // Human State Pose
    public static final Pose HUMAN_STATE_POSE = new Pose(125, 13.8, Math.toRadians(180));

    // Endgame
    public static final Pose ENDGAME_POSE = new Pose(105.5, 33, Math.toRadians(90));

    private Blue() {}
}