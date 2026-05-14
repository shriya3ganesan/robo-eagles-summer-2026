package org.firstinspires.ftc.teamcode.GreyBot.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.OTOSConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants{
    public static FollowerConstants followerConstants = new FollowerConstants()
            .forwardZeroPowerAcceleration(-23.859)
            .lateralZeroPowerAcceleration(-78.173)
            .centripetalScaling(0.005)
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.005,0.01,0.00001,0.6,0.01))
            .translationalPIDFCoefficients(new PIDFCoefficients(0.2, 0, 0.02, 0.03))
            .headingPIDFCoefficients(new PIDFCoefficients(1, 0, 0.03,0.03))
            .mass(8.61826);
    //109.2 128.2lb
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)

            .rightFrontMotorName("frontRightDrive")
            .rightRearMotorName("backRightDrive")
            .leftFrontMotorName("frontLeftDrive")
            .leftRearMotorName("backLeftDrive")

            .xVelocity(69.783)
            .yVelocity(27.722)

            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE);
    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static OTOSConstants localizerConstants = new OTOSConstants()
            .hardwareMapName("otos")
            .offset(new SparkFunOTOS.Pose2D(-8,0,Math.PI /-2))
            .linearUnit(DistanceUnit.INCH)
            .linearScalar(0.991)
            .angularScalar(.991)
            .angleUnit(AngleUnit.RADIANS);
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .OTOSLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}