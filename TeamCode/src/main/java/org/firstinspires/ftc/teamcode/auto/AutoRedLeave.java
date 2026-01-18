package org.firstinspires.ftc.teamcode.auto;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.field.Red;
import org.firstinspires.ftc.teamcode.field.Red_Leave;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.robot.MechController;
import org.firstinspires.ftc.teamcode.robot.MechState;
import org.firstinspires.ftc.teamcode.robot.RobotHardware;
import org.firstinspires.ftc.teamcode.robot.VisionController;
import org.firstinspires.ftc.vision.VisionPortal;

@Autonomous(name = "AutoRedLeave", group = "Auto")
public class AutoRedLeave extends OpMode {
        RobotHardware robot;
        MechController mechController;
        VisionController visionController;
        private VisionPortal visionPortal;

        private Follower follower;
        private Timer pathTimer, actionTimer, opmodeTimer;
        private int pathState;

        private final Pose startPose = Red_Leave.START_POSE;
        private final Pose aprilTagPose = Red_Leave.APRILTAG_POSE;
        private final Pose leavePose = Red_Leave.LEAVE_POSE;


        private Path aprilTagRead;
        private PathChain leaveStart;

        public void buildPaths() {
            aprilTagRead = new Path(new BezierLine(startPose, aprilTagPose));
            aprilTagRead.setLinearHeadingInterpolation(startPose.getHeading(), aprilTagPose.getHeading());

            leaveStart = follower.pathBuilder()
                    .addPath(new BezierLine(aprilTagPose, leavePose))
                    .setLinearHeadingInterpolation(aprilTagPose.getHeading(), leavePose.getHeading())
                    .build();


        }
        public void autonomousPathUpdate() {
            switch (pathState) {
                case 0:
                    follower.followPath(aprilTagRead);
                    mechController.setState(MechState.APRIL_TAG);
                    setPathState(1);
                    break;
                case 1:
                    if (!follower.isBusy()) {
                        follower.followPath(leaveStart, true);
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
            mechController.update();
            follower.update();
            autonomousPathUpdate();

            MechState state = mechController.getCurrentState();
            if (state == MechState.SHOOT_STATE || state == MechState.APRIL_TAG) {
                follower.setMaxPower(0.0);
            } else if (state == MechState.INTAKE_STATE) {
                follower.setMaxPower(MechController.INTAKE_DRIVE_POWER);
            } else {
                follower.setMaxPower(MechController.FULL_DRIVE_POWER);
            }

            telemetry.addData("path state", pathState);
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            mechController.allTelemetry();
        }

        @Override
        public void init() {
            robot = new RobotHardware(hardwareMap, telemetry);

            visionController = new VisionController(robot);
            visionController.initAprilTag();
            visionPortal = visionController.getVisionPortal();

            mechController = new MechController(robot, visionController);
            mechController.handleMechState(MechState.START);

            telemetry.addData("Status", "Initialized. Detecting April Tag....");
            telemetry.update();

            pathTimer = new Timer();
            actionTimer = new Timer();
            opmodeTimer = new Timer();
            opmodeTimer.resetTimer();


            follower = Constants.createFollower(hardwareMap);
            buildPaths();
            follower.setStartingPose(startPose);
        }

        @Override
        public void init_loop() {
            mechController.update();
            mechController.allTelemetry();
        }

        @Override
        public void start() {
            opmodeTimer.resetTimer();
            setPathState(0);
        }

        @Override
        public void stop() {
            visionPortal.stopStreaming();
            mechController.setLifter(0);
            mechController.setIndexer(MechController.INTAKE[0]);
        }
    }



