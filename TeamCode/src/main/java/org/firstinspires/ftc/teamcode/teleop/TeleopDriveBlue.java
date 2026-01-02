package org.firstinspires.ftc.teamcode.teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.field.Blue;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.robot.MechController;
import org.firstinspires.ftc.teamcode.robot.MechState;
import org.firstinspires.ftc.teamcode.robot.RobotHardware;
import org.firstinspires.ftc.teamcode.robot.VisionController;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.function.Supplier;

@Configurable
@TeleOp(name = "TeleopBlue", group = "Teleop")
public class TeleopDriveBlue extends OpMode {
    private Follower follower;
    private final Pose startingPose = Blue.SCORE_POSE_NEAR;
    private final Pose scorePoseNear = Blue.SCORE_POSE_NEAR;
    private final Pose scorePoseFar = Blue.SCORE_POSE_FAR;
    private final Pose endgamePose = Blue.ENDGAME_POSE;
    private final Pose gateStartPose = Blue.GATE_START_POSE;
    private final Pose gateEndPose = Blue.GATE_END_POSE;
    private final Pose humanPose = Blue.HUMAN_STATE_POSE;
    private boolean automatedDrive;
    private Supplier<PathChain> pathChain;
    private TelemetryManager telemetryM;
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    RobotHardware robot;
    MechController mechController;
    VisionController visionController;
    private VisionPortal visionPortal;
    boolean buttonPressed = false;
    boolean bypass = false;
    boolean bypass_vision = false;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        robot = new RobotHardware(hardwareMap, telemetry);

        visionController = new VisionController(robot);
        visionController.initAprilTag();
        visionPortal = visionController.getVisionPortal();

        mechController = new MechController(robot, visionController);
        mechController.handleMechState(MechState.START);

        telemetry.addData("Status", "Initialized...");
        telemetry.addData("Distance Sensor", visionController.distanceSensor());
        telemetry.update();
    }

    @Override
    public void init_loop() {
        mechController.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
        visionPortal.resumeStreaming();
    }

    @Override
    public void loop() {
        follower.update();
        telemetryM.update();
        mechController.allTelemetry();
        mechController.update(); // Keeps running states till IDLE

        bypass = gamepad1.right_bumper;
        bypass_vision = visionController.distanceSensor() && !bypass;

        MechState state = mechController.getCurrentState();
        if (state == MechState.SHOOT_STATE || state == MechState.SHOOT_PURPLE || state == MechState.SHOOT_GREEN || bypass_vision) {
            follower.setMaxPower(0.0);
        } else if (state == MechState.INTAKE_STATE) {
            follower.setMaxPower(MechController.INTAKE_DRIVE_TELEOP);
        } else {
            follower.setMaxPower(MechController.FULL_DRIVE_POWER);
        }


        if ((gamepad1.right_trigger > 0.2) && !buttonPressed) {
            buttonPressed = true;
            mechController.setState(MechState.INTAKE_STATE);
        } else if ((gamepad2.right_trigger > 0.2) && !buttonPressed) {
            buttonPressed = true;
            mechController.setState(MechState.SHOOT_STATE);
        } else if ((gamepad2.left_trigger > 0.2) && !buttonPressed) {
            buttonPressed = true;
            mechController.setState(MechState.HUMAN_STATE);
        }else if ((gamepad2.left_bumper) && !buttonPressed) {
            buttonPressed = true;
            mechController.setState(MechState.SHOOT_GREEN);
        } else if ((gamepad2.right_bumper) && !buttonPressed) {
            buttonPressed = true;
            mechController.setState(MechState.SHOOT_PURPLE);
        } else if ((gamepad2.b) && !buttonPressed) {
            buttonPressed = true;
            mechController.setState(MechState.IDLE);
        } else if ((gamepad2.dpad_up) && !buttonPressed) {
            buttonPressed = true;
            mechController.tagPattern = new int[]{21, 2, 1, 1}; // ID 21: GPP
        } else if ((gamepad2.dpad_right) && !buttonPressed) {
            buttonPressed = true;
            mechController.tagPattern = new int[]{22, 1, 2, 1}; // ID 22: PGP
        } else if ((gamepad2.dpad_down) && !buttonPressed) {
            buttonPressed = true;
            mechController.tagPattern = new int[]{23, 1, 1, 2}; // ID 23: PPG
        } else if ((gamepad1.dpad_up) && !buttonPressed) {
            buttonPressed = true;
            MechController.SHOOTING_WHEEL_SPEED_FAR += 10; //slowModeMultiplier += 0.25;
        } else if ((gamepad1.dpad_down) && !buttonPressed) {
            buttonPressed = true;
            MechController.SHOOTING_WHEEL_SPEED_FAR -= 10; //slowModeMultiplier -= 0.25;
        }

        if (!automatedDrive) {
            if (!slowMode) follower.setTeleOpDrive(
                    gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    false // true = Robot Centric | false = Field Centric
            );

            else follower.setTeleOpDrive(
                    gamepad1.left_stick_y * slowModeMultiplier,
                    gamepad1.left_stick_x * slowModeMultiplier,
                    -gamepad1.right_stick_x * slowModeMultiplier,
                    false // true = Robot Centric | false = Field Centric
            );
        }
        //Shooting Pose Near
        if (gamepad2.aWasPressed()) {
            PathChain shootingPath = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(follower::getPose, scorePoseNear)))
                    .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, scorePoseNear.getHeading(), 0.8))
                    .build();
            follower.followPath(shootingPath);
            automatedDrive = true;
        }

        //Shooting Pose Far
        if (gamepad2.yWasPressed()) {
            PathChain shootingPath = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(follower::getPose, scorePoseFar)))
                    .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, scorePoseFar.getHeading(), 0.8))
                    .build();
            follower.followPath(shootingPath);
            automatedDrive = true;
        }

        //Gate Pose
        if (gamepad1.left_trigger > 0.1 && !automatedDrive) {
            PathChain gatePath = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(follower::getPose, gateStartPose)))
                    .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, gateStartPose.getHeading(), 0.8))
                    .addPath(new Path(new BezierLine(follower::getPose, gateEndPose)))
                    .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, gateEndPose.getHeading(), 0.8))
                    .build();
            follower.followPath(gatePath);
            automatedDrive = true;
        }

        // QR Pose
        if (gamepad1.yWasPressed()) {
            Pose updatedPose = visionController.getRobotPoseFromCamera();
            if (updatedPose != null) {
                follower.setPose(updatedPose);
            }
            //automatedDrive = true;
        }

        //Human State Pose
        if (gamepad2.xWasPressed()) {
            PathChain endgamePath = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(follower::getPose, humanPose)))
                    .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, humanPose.getHeading(), 0.8))
                    .build();
            follower.followPath(endgamePath);
            automatedDrive = true;
        }

        //Endgame Pose
        if (gamepad1.xWasPressed()) {
            PathChain endgamePath = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(follower::getPose, endgamePose)))
                    .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, endgamePose.getHeading(), 0.8))
                    .build();
            follower.followPath(endgamePath);
            automatedDrive = true;
        }

        boolean noButtons =
                gamepad1.right_trigger <= 0.2 &&
                gamepad2.left_trigger <= 0.2 &&
                gamepad2.right_trigger <= 0.2 &&
                !gamepad2.left_bumper &&
                !gamepad2.right_bumper &&
                !gamepad2.a &&
                !gamepad2.b &&
                !gamepad2.dpad_up &&
                !gamepad2.dpad_right &&
                !gamepad2.dpad_down &&
                !gamepad1.dpad_up &&
                !gamepad1.dpad_down;

        if (noButtons) {
            buttonPressed = false;
        }

        //Automated PathFollowing
//        if (gamepad1.aWasPressed()) {
//            follower.followPath(pathChain.get());
//            automatedDrive = true;
//        }

        //Stop automated following if the follower is done
        if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
            follower.startTeleopDrive();
            automatedDrive = false;
        }

        //Slow Mode
        if (gamepad1.leftBumperWasPressed()) {
            slowMode = !slowMode;
        }

        telemetryM.debug("position", follower.getPose());
        telemetryM.debug("velocity", follower.getVelocity());
        telemetryM.debug("automatedDrive", automatedDrive);
    }

    @Override
    public void stop() {
        visionPortal.stopStreaming();
        mechController.setLifter(0);
        mechController.setIndexer(MechController.INTAKE[0]);
    }
}
