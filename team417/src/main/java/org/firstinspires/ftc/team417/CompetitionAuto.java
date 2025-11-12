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

import java.nio.file.Path;

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
    double minIntakes = 0.0;
    double maxIntakes = 3.0;

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
                    .add("Intake Cycles:")
                    .add("intake-slider", new MenuSlider(minIntakes, maxIntakes))
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
        double intakeCycles = menu.getResult(Double.class, "intake-slider");

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



        PathFactory farSlowBotIntake1 = pathFactory.actionBuilder(SBFarStartPose)
                .setTangent(Math.toRadians(180))
                // 3 launch actions
                //then after disp intake action
                .splineToSplineHeading(new Pose2d(36,32, Math.toRadians(90)), Math.toRadians(90))
                .setTangent(Math.toRadians(90))
                .splineToConstantHeading(new Vector2d(36,60), Math.toRadians(90))
                .setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90));
        if (intakeCycles > 1) {


            // 3 launch actions
            //after disp intake action
                farSlowBotIntake1 = farSlowBotIntake1.setTangent(Math.toRadians(180))
                    .splineToSplineHeading(new Pose2d(12, 32, Math.toRadians(90)), Math.toRadians(90))
                    .setTangent(Math.toRadians(90))
                    .splineToConstantHeading(new Vector2d(12, 60), Math.toRadians(90))
                    .setTangent(Math.toRadians(-90))
                    .splineToSplineHeading(new Pose2d(54, 12, Math.toRadians(157.5)), Math.toRadians(-90));
            // 3 launch actions
            //after disp intake action
            if (intakeCycles > 2) {
                 farSlowBotIntake1 = farSlowBotIntake1.setTangent(Math.toRadians(180))
                        .splineToSplineHeading(new Pose2d(-12,32, Math.toRadians(90)), Math.toRadians(90))
                        .setTangent(Math.toRadians(90))
                        .splineToConstantHeading(new Vector2d(-12,55), Math.toRadians(90))
                        .setTangent(Math.toRadians(-90))
                        .splineToSplineHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90));

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
                .splineToSplineHeading(new Pose2d(-12,32, Math.toRadians(90)), Math.toRadians(90))
                .setTangent(Math.toRadians(90))
                .splineToConstantHeading(new Vector2d(-12,55), Math.toRadians(90))
                .setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(-36,36, Math.toRadians(139)), Math.toRadians(180));
        if (intakeCycles > 1) {
            nearSlowBotPath = nearSlowBotPath.setTangent(Math.toRadians(0))


                    //3 launches
                    //after disp intake

                    .splineToSplineHeading(new Pose2d(12, 32, Math.toRadians(90)), Math.toRadians(90))
                    .setTangent(Math.toRadians(90))
                    .splineToConstantHeading(new Vector2d(12, 60), Math.toRadians(90))
                    .setTangent(Math.toRadians(-90))
                    .splineToSplineHeading(new Pose2d(-36, 36, Math.toRadians(139)), Math.toRadians(180));
            //3 launches
            //after disp intake
            if (intakeCycles > 2) {
                nearSlowBotPath = nearSlowBotPath.setTangent(Math.toRadians(0))
                        .splineToSplineHeading(new Pose2d(36, 32, Math.toRadians(90)), Math.toRadians(90))
                        .setTangent(Math.toRadians(90))
                        .splineToConstantHeading(new Vector2d(36, 60), Math.toRadians(90))
                        .setTangent(Math.toRadians(-90))
                        .splineToSplineHeading(new Pose2d(-36, 36, Math.toRadians(139)), Math.toRadians(180));

            }
        }
        nearSlowBotPath = nearSlowBotPath.setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(-48, 12, Math.toRadians(180)), Math.toRadians(180));
        Action nearSlowBot = nearSlowBotPath.build();
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
                    trajectoryAction = nearSlowBot;
                    break;
                case FAR:
                    drive.setPose(SBFarStartPose);
                    trajectoryAction = farSlowBot;
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
