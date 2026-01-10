package org.firstinspires.ftc.teamcode.field;

import com.pedropathing.geometry.Pose;

public class Red_NearFar {
    // Auto
    // Start Pose of our robot.
    public static final Pose START_POSE = new Pose(121, 125, Math.toRadians(37));
    public static final Pose APRILTAG_POSE = new Pose(84.7,80.5, Math.toRadians(90));

    // Scoring Pose of our robot. It is facing the goal at a 45 degree angle.
    public static final Pose SCORE_POSE_AUTO = new Pose(85, 15.8, Math.toRadians(70));
    public static final Pose SCORE_POSE_NEAR = new Pose(84.7, 80.5, Math.toRadians(47));

    // Highest (First Set) of Artifacts from the Spike Mark.
    public static final Pose READY1_POSE = new Pose(98.5,36, Math.toRadians(0));
    public static final Pose ALIGN1_POSE = new Pose(101.5, 36, Math.toRadians(0));
    public static final Pose PICKUP1_POSE = new Pose(119.5, 36, Math.toRadians(0));

    // Middle (Second Set) of Artifacts from the Spike Mark.
    public static final Pose READY2_POSE = new Pose(98.5,60, Math.toRadians(0));
    public static final Pose ALIGN2_POSE = new Pose(101.5, 60, Math.toRadians(0));
    public static final Pose PICKUP2_POSE = new Pose(119.5, 60, Math.toRadians(0));

    // Lowest (Last Set) of Artifacts from the Spike Mark.
    public static final Pose READY3_POSE = new Pose(94,84, Math.toRadians(0));
    public static final Pose ALIGN3_POSE = new Pose(101.5, 84, Math.toRadians(0));
    public static final Pose PICKUP3_POSE = new Pose(119.5, 84, Math.toRadians(0));

    // Teleop
    // Gate Start & End
    public static final Pose GATE_START_POSE = new Pose(126, 72, Math.toRadians(180));
    public static final Pose GATE_END_POSE = new Pose(129, 72, Math.toRadians(180));

    // Endgame
    public static final Pose ENDGAME_POSE = new Pose(38, 33, Math.toRadians(90));

    private Red_NearFar() {} // Prevent instantiation
}
