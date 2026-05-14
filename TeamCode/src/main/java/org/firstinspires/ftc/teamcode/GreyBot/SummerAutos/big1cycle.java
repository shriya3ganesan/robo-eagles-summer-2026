package org.firstinspires.ftc.teamcode.GreyBot.SummerAutos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.GreyBot.pedroPathing.Constants;

@Config
@Autonomous(name="big1cycle", group = "summer")
public class big1cycle extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    private ElapsedTime intakeTimer;
    private ElapsedTime lancherTimer;
    private DcMotor intake = null;
    private DcMotor passThrough = null;
    private DcMotorEx lancher = null;
    double targetRPM = 5700; //shooter
    double ticksPerRev = 28;


    private final Pose startPose = new Pose(22, 124, Math.toRadians(142));
    private final Pose pose2 = new Pose(36, 112);
    private final Pose pose3 = new Pose(56, 112);
    private final Pose collect1 = new Pose(25, 95, Math.toDegrees(90));
    private final Pose collect2 = new Pose(25, 71, Math.toDegrees(90));

    private PathChain path1, path2, goshot2, gocollect2, goShot3;

    public void buildPaths() {
        path1 = follower.pathBuilder()
                .addPath(new BezierLine(startPose, pose2))
                .setLinearHeadingInterpolation(Math.toRadians(142),Math.toRadians(142))
                .build();
        path2 = follower.pathBuilder()
                .addPath(new BezierLine(pose2, pose3))
                .setLinearHeadingInterpolation(Math.toRadians(142),Math.toRadians(142))
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(path1,.7, true);
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()){
                    follower.followPath(path2);
                    follower.pausePathFollowing();
                    lancherTimer.reset();
                    lancher.setVelocity((targetRPM * ticksPerRev) / 60);
                    while (lancherTimer.seconds() < 3.5) {
                        if (lancherTimer.seconds() > 2.0) {
                            passThrough.setPower(.4);
                            intake.setPower(.75);
                        }
                    }
                    follower.resumePathFollowing();
                    lancher.setVelocity(0);
                    intake.setPower(0);
                    passThrough.setPower(0);
                    setPathState(-1);
                }
                break;
        }
    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();

    }
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        intakeTimer = new ElapsedTime();
        lancherTimer = new ElapsedTime();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.REVERSE);
        passThrough = hardwareMap.get(DcMotor.class, "passThrough");
        passThrough.setDirection(DcMotorSimple.Direction.FORWARD);
        lancher = hardwareMap.get(DcMotorEx.class, "lancher");
        lancher.setDirection(DcMotorEx.Direction.FORWARD);
        lancher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients a = new PIDFCoefficients(38.3, 0, 0, 12.227);

        lancher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, a);
    }
    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }
}