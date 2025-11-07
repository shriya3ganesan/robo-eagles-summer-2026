package org.firstinspires.ftc.team417;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


import org.firstinspires.ftc.team417.javatextmenu.MenuFinishedButton;
import org.firstinspires.ftc.team417.javatextmenu.MenuHeader;
import org.firstinspires.ftc.team417.javatextmenu.MenuInput;
import org.firstinspires.ftc.team417.javatextmenu.MenuSlider;
import org.firstinspires.ftc.team417.javatextmenu.TextMenu;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

/**
 * This class exposes the competition version of Autonomous. As a general rule, add code to the
 * BaseOpMode class rather than here so that it can be shared between both TeleOp and Autonomous.
 */
@Autonomous(name = "Auto", group = "Competition", preselectTeleOp = "CompetitionTeleOp")
public class CompetitionAuto extends BaseOpMode {
    enum Alliances {
        RED,
        BLUE,
    }

    enum FastBotMovements {
        NEAR,
        FAR,
        FAR_MINIMAL,
    }

    double minWaitTime = 0.0;
    double maxWaitTime = 15.0;

    @Override
    public void runOpMode() {
        initHardware();
        // different options for start positions - for both SlowBot and FastBot
        Pose2d startPose = new Pose2d(0, 0, 0);

        Pose2d redFBNearStartPose = new Pose2d(-60, 48, Math.toRadians(41));
        Pose2d redFBFarStartPose = new Pose2d(64, 16, Math.toRadians(0));

        Pose2d blueFBNearStartPose = new Pose2d(-50, -50.5, Math.toRadians(139));
        Pose2d blueFBFarStartPose = new Pose2d(64, -16, Math.toRadians(180));
        Pose2d SBNearStartPose = new Pose2d(-60, 48, Math.toRadians(139));
        Pose2d SBFarStartPose = new Pose2d(64, 16, Math.toRadians(180));


        MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, gamepad1, startPose);

        TextMenu menu = new TextMenu();
        MenuInput menuInput = new MenuInput(MenuInput.InputType.CONTROLLER);

        // Text menu for FastBot
        if (MecanumDrive.isFastBot) {
            menu.add(new MenuHeader("AUTO SETUP"))
                    .add() // empty line for spacing
                    .add("Pick an alliance:")
                    .add("alliance-picker-1", Alliances.class) // enum selector shortcut
                    .add()
                    .add("Pick a movement:")
                    .add("movement-picker-1", FastBotMovements.class) // enum selector shortcut
                    .add()
                    .add("Wait time:")
                    .add("wait-slider-1", new MenuSlider(minWaitTime, maxWaitTime))
                    .add()
                    .add("finish-button-1", new MenuFinishedButton());


            // Text menu for SlowBot
        } else if (MecanumDrive.isSlowBot) {
            menu.add(new MenuHeader("AUTO SETUP"))
                    .add() // empty line for spacing
                    .add("Pick an alliance:")
                    .add("alliance-picker-1", Alliances.class) // enum selector shortcut
                    .add()
                    .add("Pick a movement:")
                    .add("movement-picker-1", FastBotMovements.class) // enum selector shortcut
                    .add()
                    .add("Wait time:")
                    .add("wait-slider-1", new MenuSlider(minWaitTime, maxWaitTime))
                    .add()
                    .add("finish-button-1", new MenuFinishedButton());
        }

        while (!menu.isCompleted()) {
            // get x, y (stick) and select (A) input from controller
            // on Wily  Works, this is x, y (wasd) and select (enter) on the keyboard
            menuInput.update(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.a);
            menu.updateWithInput(menuInput);
            // display the updated menu
            for (String line : menu.toListOfStrings()) {
                telemetry.addLine(line); // but with appropriate printing method
            }
            telemetry.update();
        }

        Alliances chosenAlliance = menu.getResult(Alliances.class, "alliance-picker-1");
        FastBotMovements chosenMovement = menu.getResult(FastBotMovements.class, "movement-picker-1");
        double waitTime = menu.getResult(Double.class, "wait-slider-1");

        PathFactory redPathFactory = new PathFactory(drive, false);

        PathFactory bluePathFactory = new PathFactory(drive, true);

        // Red alliance FastBot auto paths
        Action redNearFastBot = drive.actionBuilder(redFBNearStartPose)
                .setTangent(Math.toRadians(-49))
                .stopAndAdd(new SpinUpAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())

                .splineToLinearHeading(new Pose2d(-32, 54, Math.toRadians(0)), Math.toRadians(90))
                .build();

        Action redFarFastBot = drive.actionBuilder(redFBFarStartPose)
                .setTangent(Math.toRadians(135))
                .splineToLinearHeading(new Pose2d(-57, 36, Math.toRadians(0)), Math.toRadians(90))
                .stopAndAdd(new SpinUpAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
                .setTangent(Math.toRadians(-90))
                .splineToLinearHeading(new Pose2d(-56, 12, Math.toRadians(0)), Math.toRadians(-90))
                .build();

        Action redFarMinimalFastBot = drive.actionBuilder(redFBFarStartPose)
                .setTangent(Math.PI / 2)
                .splineTo(new Vector2d(56, 35), Math.PI / 2)
                .build();

        // Blue alliance auto paths
        Action blueNearFastBot = drive.actionBuilder(blueFBNearStartPose)
                .setTangent(Math.toRadians(49))
                .stopAndAdd(new SpinUpAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
//                .splineTo(new Vector2d(-44, -44), Math.toRadians(49))
//                .setTangent(Math.toRadians(139))
                .splineToLinearHeading(new Pose2d(-32, -54, Math.toRadians(180)), Math.toRadians(-90))
                .build();

        Action blueFarFastBot = drive.actionBuilder(blueFBFarStartPose)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-57, -36, Math.toRadians(180)), Math.toRadians(-90))
                .stopAndAdd(new SpinUpAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
                .stopAndAdd(new LaunchAction())
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(-56, -12, Math.toRadians(180)), Math.toRadians(90))
                .build();

        Action blueFarMinimalFastBot = drive.actionBuilder(blueFBFarStartPose)
                .setTangent(Math.PI / 2)
                .splineTo(new Vector2d(56, -35), Math.PI / 2)
                .build();

        PathFactory pathFactory;

        switch (chosenAlliance) {
            case RED:
                pathFactory = redPathFactory;
                break;
            case BLUE:
                pathFactory = bluePathFactory;
                break;
            default:
                throw new IllegalArgumentException("Alliance must be red or blue");
        }

        Action farMinimalSlowBot = pathFactory.actionBuilder(SBFarStartPose)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(48,32,Math.toRadians(180)), Math.toRadians(180))
                .build();

        // the first parameter is the type to return as
        if (MecanumDrive.isFastBot) {
            Action trajectoryAction = null;
            switch (chosenAlliance) {
                case RED:
                    switch (chosenMovement) {
                        case NEAR:
                            drive.setPose(redFBNearStartPose);
                            trajectoryAction = redNearFastBot;
                            break;
                        case FAR:
                            drive.setPose(redFBFarStartPose);
                            trajectoryAction = redFarFastBot;
                            break;
                        case FAR_MINIMAL:
                            drive.setPose(redFBFarStartPose);
                            trajectoryAction = redFarMinimalFastBot;
                            break;
                    }
                    break;

                case BLUE:
                    switch (chosenMovement) {
                        case NEAR:
                            drive.setPose(blueFBNearStartPose);
                            trajectoryAction = blueNearFastBot;
                            break;
                        case FAR:
                            drive.setPose(blueFBFarStartPose);
                            trajectoryAction = blueFarFastBot;
                            break;
                        case FAR_MINIMAL:
                            drive.setPose(blueFBFarStartPose);
                            trajectoryAction = blueFarMinimalFastBot;
                            break;
                    }
                    break;
            }

            // Get a preview of the trajectory's path:
            Canvas previewCanvas = new Canvas();
            trajectoryAction.preview(previewCanvas);

            // Show the preview on FTC Dashboard now.
            TelemetryPacket packet = MecanumDrive.getTelemetryPacket();
            packet.fieldOverlay().getOperations().addAll(previewCanvas.getOperations());
            MecanumDrive.sendTelemetryPacket(packet);

            // Wait for Start to be pressed on the Driver Hub!
            waitForStart();

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

        } else if (MecanumDrive.isSlowBot) {
            Action trajectoryAction = null;

            switch (chosenMovement) {
                case NEAR:

                    drive.setPose(SBNearStartPose);
                    trajectoryAction = redNearFastBot;
                    break;
                case FAR:
                    drive.setPose(SBFarStartPose);
                    trajectoryAction = redFarFastBot;
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

            // Wait for Start to be pressed on the Driver Hub!
            waitForStart();

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
            builder.setTangent(-tangent);
        } else {
            builder.setTangent(tangent);
        }
        return this;
    }

    public PathFactory splineToLinearHeading(Pose2d pose, double tangent) {

        if (mirror) {
            builder.splineToLinearHeading(mirrorPose(pose), -tangent);
        } else {
            builder.splineToLinearHeading(pose, tangent);
        }
        return this;
    }


    public Action build() {
        return builder.build();
    }

    public PathFactory stopAndAdd(Action a) {
        builder.stopAndAdd(a);
        return this;
    }
}
