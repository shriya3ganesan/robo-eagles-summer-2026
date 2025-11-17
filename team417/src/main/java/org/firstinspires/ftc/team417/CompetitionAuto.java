package org.firstinspires.ftc.team417;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team417.apriltags.AprilTagDetector;
import org.firstinspires.ftc.team417.apriltags.Pattern;
import org.firstinspires.ftc.team417.javatextmenu.MenuFinishedButton;
import org.firstinspires.ftc.team417.javatextmenu.MenuHeader;
import org.firstinspires.ftc.team417.javatextmenu.MenuInput;
import org.firstinspires.ftc.team417.javatextmenu.MenuSlider;
import org.firstinspires.ftc.team417.javatextmenu.TextMenu;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;
import org.firstinspires.ftc.team417.roadrunner.RobotAction;

/**
 * This class exposes the competition version of Autonomous. As a general rule, add code to the
 * BaseOpMode class rather than here so that it can be shared between both TeleOp and Autonomous.
 */
@Autonomous(name = "Auto", group = "Competition", preselectTeleOp = "CompetitionTeleOp")
public class CompetitionAuto extends BaseOpMode {
    public enum Alliance {
        RED,
        BLUE,
    }

    enum SlowBotMovement {
        NEAR,
        FAR,
        FAR_OUT_OF_WAY,
        FAR_MINIMAL,
    }

    double minWaitTime = 0.0;
    double maxWaitTime = 30.0;

    double minIntakes = 0.0;
    double maxIntakes = 3.0;

    @Override
    public void runOpMode() {
        initHardware();

        Pose2d startPose = new Pose2d(0, 0, 0);


        Pose2d SBNearStartPose = new Pose2d(-60, 48, Math.toRadians(139));
        Pose2d SBFarStartPose = new Pose2d(60, 12, Math.toRadians(157.5));


        MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, gamepad1, startPose);

        // Test to make sure the camera is there, and then immediately close the detector object
        try (AprilTagDetector detector = new AprilTagDetector()) {
            detector.initAprilTag(hardwareMap);
        }

        TextMenu menu = new TextMenu();
        MenuInput menuInput = new MenuInput(MenuInput.InputType.CONTROLLER);

        // Text menu for FastBot


            // Text menu for SlowBot
            menu.add(new MenuHeader("AUTO SETUP"))
                    .add() // empty line for spacing
                    .add("Pick an alliance:")
                    .add("alliance-picker-1", Alliance.class) // enum selector shortcut
                    .add()
                    .add("Pick a movement:")
                    .add("movement-picker-1", SlowBotMovement.class) // enum selector shortcut
                    .add()
                    .add("Intake Cycles:")
                    .add("intake-slider", new MenuSlider(minIntakes, maxIntakes))
                    .add()
                    .add("Wait time:")
                    .add("wait-slider-1", new MenuSlider(minWaitTime, maxWaitTime))
                    .add()
                    .add("finish-button-1", new MenuFinishedButton());


        while (!menu.isCompleted()) {
            // get x, y (stick) and select (A) input from controller
            // on Wily Works, this is x, y (wasd) and select (enter) on the keyboard
            menuInput.update(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.a);
            menu.updateWithInput(menuInput);
            // display the updated menu
            for (String line : menu.toListOfStrings()) {
                telemetry.addLine(line); // but with appropriate printing method
            }
            telemetry.update();
        }

        Alliance chosenAlliance = menu.getResult(Alliance.class, "alliance-picker-1");
        SlowBotMovement chosenMovement = menu.getResult(SlowBotMovement.class, "movement-picker-1");
        double waitTime = menu.getResult(Double.class, "wait-slider-1");
        double intakeCycles = menu.getResult(Double.class, "intake-slider");

        PathFactory pathFactory;

        switch (chosenAlliance) {
            case RED:
                pathFactory = new PathFactory(drive, false);
                break;
            case BLUE:
                pathFactory = new PathFactory(drive, true);
                break;
            default:
                throw new IllegalArgumentException("Alliance must be red or blue");
        }

        Action farMinimalSlowBot = pathFactory.actionBuilder(SBFarStartPose)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(48,32,Math.toRadians(180)), Math.toRadians(180))
                .build();
        Action farOutOfWay = pathFactory.actionBuilder(SBFarStartPose)
                // 3 launch actions
                // after disp intake action
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(new Pose2d(60,61, Math.toRadians(0)), Math.toRadians(0))
                .setTangent(Math.toRadians(-90))
                .splineToLinearHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90))
                // 3 launch actions
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(50,32,Math.toRadians(180)), Math.toRadians(180))
                .build();


        PathFactory farSlowBotIntake1 = pathFactory.actionBuilder(SBFarStartPose);
        if (intakeCycles == 0) {
            farSlowBotIntake1.setTangent(Math.toRadians(180));
            // 3 launch actions
            //then after disp intake action
        }


                farSlowBotIntake1.splineToSplineHeading(new Pose2d(36,32, Math.toRadians(90)), Math.toRadians(90)) //go to intake farthest from goal
                .setTangent(Math.toRadians(90))
                .splineToConstantHeading(new Vector2d(36,60), Math.toRadians(90))
                .setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90));  //go to launch position
        if (intakeCycles > 1) {


            // 3 launch actions
            //after disp intake action
                farSlowBotIntake1 = farSlowBotIntake1.setTangent(Math.toRadians(180))
                    .splineToSplineHeading(new Pose2d(12, 32, Math.toRadians(90)), Math.toRadians(90)) //go to intake middle from goal
                    .setTangent(Math.toRadians(90))
                    .splineToConstantHeading(new Vector2d(12, 60), Math.toRadians(90))
                    .setTangent(Math.toRadians(-90))
                    .splineToSplineHeading(new Pose2d(54, 12, Math.toRadians(157.5)), Math.toRadians(-90)); //go to launch position
            // 3 launch actions
            //after disp intake action
            if (intakeCycles > 2) {
                 farSlowBotIntake1 = farSlowBotIntake1.setTangent(Math.toRadians(180))
                        .splineToSplineHeading(new Pose2d(-12,32, Math.toRadians(90)), Math.toRadians(90)) //go to intake closest to goal
                        .setTangent(Math.toRadians(90))
                        .splineToConstantHeading(new Vector2d(-12,55), Math.toRadians(90))
                        .setTangent(Math.toRadians(-90))
                        .splineToSplineHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90)); //go to launch position

            }
        }
        farSlowBotIntake1 = farSlowBotIntake1.setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(48,32,Math.toRadians(180)), Math.toRadians(180));
        Action farSlowBot = farSlowBotIntake1.build();




        PathFactory nearSlowBotPath = pathFactory.actionBuilder(SBNearStartPose)
                .setTangent(Math.toRadians(-51))
                .splineToConstantHeading(new Vector2d(-36,36), Math.toRadians(-51))
                //3 launches
                //after disp intake
                .setTangent(Math.toRadians(0))
                .splineToSplineHeading(new Pose2d(-12,32, Math.toRadians(90)), Math.toRadians(90)) //go to intake closest from goal
                .setTangent(Math.toRadians(90))
                .splineToConstantHeading(new Vector2d(-12,55), Math.toRadians(90))
                .setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(-36,36, Math.toRadians(139)), Math.toRadians(180)); //go to launch position
        if (intakeCycles > 1) {
            nearSlowBotPath = nearSlowBotPath.setTangent(Math.toRadians(0))


                    //3 launches
                    //after disp intake

                    .splineToSplineHeading(new Pose2d(12, 32, Math.toRadians(90)), Math.toRadians(90)) //go to intake middle from goal
                    .setTangent(Math.toRadians(90))
                    .splineToConstantHeading(new Vector2d(12, 60), Math.toRadians(90))
                    .setTangent(Math.toRadians(-90))
                    .splineToSplineHeading(new Pose2d(-36, 36, Math.toRadians(139)), Math.toRadians(180)); //go to launch position
            //3 launches
            //after disp intake
            if (intakeCycles > 2) {
                nearSlowBotPath = nearSlowBotPath.setTangent(Math.toRadians(0))
                        .splineToSplineHeading(new Pose2d(36, 32, Math.toRadians(90)), Math.toRadians(90)) //go to intake  farthest from goal
                        .setTangent(Math.toRadians(90))
                        .splineToConstantHeading(new Vector2d(36, 60), Math.toRadians(90))
                        .setTangent(Math.toRadians(-90))
                        .splineToSplineHeading(new Pose2d(-36, 36, Math.toRadians(139)), Math.toRadians(180)); //go to launch position

            }
        }
        nearSlowBotPath = nearSlowBotPath.setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(-48, 12, Math.toRadians(180)), Math.toRadians(180));
        Action nearSlowBot = nearSlowBotPath.build();
        // the first parameter is the type to return as


            Action trajectoryAction = null;

            switch (chosenMovement) {
                case NEAR:

                    drive.setPose(SBNearStartPose);
                    trajectoryAction = nearSlowBot;
                    break;
                case FAR:
                    drive.setPose(SBFarStartPose);
                    trajectoryAction = farSlowBot;
                    break;
                case FAR_OUT_OF_WAY:
                    drive.setPose(SBFarStartPose);
                    trajectoryAction = farOutOfWay;
                    break;
                case FAR_MINIMAL:
                    drive.setPose(SBFarStartPose);
                    trajectoryAction = farMinimalSlowBot;
                    break;
            }


            // Get a preview of the trajectory's path:
            Canvas previewCanvas = new Canvas();
            trajectoryAction.preview(previewCanvas);

            // Show the preview on FTC Dashboard now.
            TelemetryPacket packet = MecanumDrive.getTelemetryPacket();
            packet.fieldOverlay().getOperations().addAll(previewCanvas.getOperations());
            MecanumDrive.sendTelemetryPacket(packet);


            // Assume unknown pattern unless detected otherwise.
            Pattern pattern = Pattern.UNKNOWN;

            // Detect the pattern with the AprilTags from the camera!
            // Wait for Start to be pressed on the Driver Hub!
            try (AprilTagDetector detector = new AprilTagDetector()) {
                detector.initAprilTag(hardwareMap);

                while (!isStarted() && !isStopRequested()) {
                    Pattern last = detector.detectPattern(chosenAlliance);
                    if (last != Pattern.UNKNOWN) {
                        pattern = last;
                    }

                    telemetry.addData("Chosen alliance: ", chosenAlliance);
                    telemetry.addData("Chosen movement: ", chosenMovement);
                    telemetry.addData("Chosen wait time: ", waitTime);
                    telemetry.addData("Last valid pattern: ", pattern);

                    telemetry.update();
                }
            }

            sleep((long)waitTime*1000);
            boolean more = true;
            while (opModeIsActive() && more) {
                telemetry.addLine("Running Auto!");

                // 'packet' is the object used to send data to FTC Dashboard:
                packet = MecanumDrive.getTelemetryPacket();

                // Draw the preview and then run the next step of the trajectory on top:
                packet.fieldOverlay().getOperations().addAll(previewCanvas.getOperations());
                more = trajectoryAction.run(packet);

                // Only send the packet if there's more to do in order to keep the very last
                // drawing up on the field once the robot is done:
                if (more)
                    MecanumDrive.sendTelemetryPacket(packet);
                telemetry.update();
            }
        }
    }

class PathFactory {
    MecanumDrive drive;
    TrajectoryActionBuilder builder;
    boolean mirror;

    public PathFactory(MecanumDrive drive, boolean mirror) {
        this.drive = drive;
        this.mirror = mirror;
    }

    Pose2d mirrorPose(Pose2d pose) {
        return new Pose2d(pose.position.x, -pose.position.y, -pose.heading.log());
    }
    Vector2d mirrorVector(Vector2d vector) {
        return new Vector2d(vector.x,-vector.y);
    }

    public PathFactory actionBuilder(Pose2d pose) {
        if (mirror) {
            builder = drive.actionBuilder(mirrorPose(pose));
        } else {
            builder = drive.actionBuilder(pose);
        }
        return this;
    }

    public PathFactory setTangent(double tangent) {
        if (mirror) {
            builder = builder.setTangent(-tangent);
        } else {
            builder = builder.setTangent(tangent);
        }
        return this;
    }

    public PathFactory splineToLinearHeading(Pose2d pose, double tangent) {
        if (mirror) {
            builder = builder.splineToLinearHeading(mirrorPose(pose), -tangent);
        } else {
            builder = builder.splineToLinearHeading(pose, tangent);
        }
        return this;
    }
    public PathFactory splineToSplineHeading(Pose2d pose, double tangent) {
        if (mirror) {
            builder = builder.splineToSplineHeading(mirrorPose(pose), -tangent);
        } else {
            builder = builder.splineToSplineHeading(pose, tangent);
        }
        return this;
    }
    public PathFactory splineToConstantHeading(Vector2d vector, double tangent) {
        if(mirror) {
            builder = builder.splineToConstantHeading(mirrorVector(vector), -tangent);
        } else {
            builder = builder.splineToConstantHeading(vector, tangent);

        }
        return this;
    }


    public PathFactory stopAndAdd(Action a) {
        builder = builder.stopAndAdd(a);
        return this;
    }

    public Action build() {
        return builder.build();
    }



}
class LaunchAction extends RobotAction {
    @Override
    public boolean run(double elapsedTime) {
        return false;
    }
}
