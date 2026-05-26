package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants();
    public static PinpointConstants pinpoint = new PinpointConstants();
    public static MecanumConstants driveTrain = new MecanumConstants();
    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        driveTrain = driveTrain.
                leftFrontMotorName("leftFront").
                leftRearMotorName("leftBack").
                rightFrontMotorName("rightFront").
                rightRearMotorName("rightBack");
        pinpoint = pinpoint.hardwareMapName("odo");
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveTrain)
                .pinpointLocalizer(pinpoint)
                .pathConstraints(pathConstraints)
                .build();
    }
}