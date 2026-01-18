
package org.firstinspires.ftc.teamcode.field;

import com.pedropathing.geometry.Pose;
public class Blue {

    // Auto
    // Start Pose of our robot.
    public static final Pose START_POSE = new Pose(64, 9, Math.toRadians(90));
    public static final Pose APRILTAG_POSE = new Pose(63,10, Math.toRadians(90));

    // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    public static final Pose SCORE_POSE_AUTO = new Pose(59, 15.8, Math.toRadians(115));
    public static final Pose SCORE_POSE_NEAR = new Pose(59.3, 80.5, Math.toRadians(133));
    public static final Pose SCORE_POSE_FAR   = new Pose(80, 21, Math.toRadians(122.5));//new Pose(92, 13.8, Math.toRadians(122)

    // Highest (First Set) of Artifacts from the Spike Mark.
    public static final Pose READY1_POSE = new Pose(45.5, 36,Math.toRadians(0));
    public static final Pose ALIGN1_POSE  = new Pose(42.5, 36, Math.toRadians(0));//new Pose(50, 84, Math.toRadians(0)); // og = 41.5, 84
    public static final Pose PICKUP1_POSE = new Pose(24.5, 36, Math.toRadians(0)); //new Pose(17, 84, Math.toRadians(0)); //og = 24, 84

    // Middle (Second Set) of Artifacts from the Spike Mark.
    public static final Pose READY2_POSE = new Pose(45.5, 60,Math.toRadians(0));
    public static final Pose ALIGN2_POSE  = new Pose(42.5, 60, Math.toRadians(0));//new Pose(50, 60, Math.toRadians(0)); // og = 41.5, 60
    public static final Pose PICKUP2_POSE = new Pose(24.5, 60, Math.toRadians(0));//new Pose(17, 60, Math.toRadians(0)); // og = 24, 60

    // Lowest (Last Set) of Artifacts from the Spike Mark.
    public static final Pose READY3_POSE = new Pose(50, 84,Math.toRadians(0));
    public static final Pose ALIGN3_POSE  = new Pose(42.5, 84, Math.toRadians(0));//new Pose(50, 36, Math.toRadians(0)); // og = 41.5, 36
    public static final Pose PICKUP3_POSE = new Pose(24.5, 84, Math.toRadians(0));//new Pose(17, 36, Math.toRadians(0)); // og: 24, 36
    // Teleop
    // Gate Start & End
    public static final Pose GATE_START_POSE = new Pose(23,70, Math.toRadians(180));//new Pose(23, 72, Math.toRadians(180));
    public static final Pose GATE_END_POSE = new Pose(17, 70, Math.toRadians(180));//new Pose(17, 72, Math.toRadians(180));

    // Human State Pose
    public static final Pose HUMAN_STATE_POSE = new Pose(132, 13.8, Math.toRadians(90));

    // Endgame
    public static final Pose ENDGAME_POSE = new Pose(105.5, 33, Math.toRadians(90));

    private Blue() {}
}
