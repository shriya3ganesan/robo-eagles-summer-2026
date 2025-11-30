package org.firstinspires.ftc.team417;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PoseMap;
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
    TextMenu menu = new TextMenu();
    MenuInput menuInput = new MenuInput(MenuInput.InputType.CONTROLLER);
    Pattern pattern;
    Alliance chosenAlliance;
    SlowBotMovement chosenMovement;
    double intakeCycles;
    public Action getPath(SlowBotMovement chosenMovement, Alliance chosenAlliance, double intakeCycles, MecanumDrive drive) {
        Pose2d startPose = new Pose2d(0, 0, 0);


        Pose2d SBNearStartPose = new Pose2d(-60, 48, Math.toRadians(139));
        Pose2d SBFarStartPose = new Pose2d(60, 12, Math.toRadians(157.5));




        PoseMap poseMap = pose -> new Pose2dDual<>(
                pose.position.x,
                pose.position.y,
                pose.heading);
        if (chosenAlliance == Alliance.BLUE) {
            poseMap = pose -> new Pose2dDual<>(
                    pose.position.x,
                    pose.position.y.unaryMinus(),
                    pose.heading.inverse());
        }
        TrajectoryActionBuilder trajectoryAction = null; 
        switch (chosenMovement) {
            case NEAR:
                trajectoryAction = drive.actionBuilder(SBNearStartPose, poseMap);
                trajectoryAction = trajectoryAction.setTangent(Math.toRadians(-51))
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
                    trajectoryAction = trajectoryAction.setTangent(Math.toRadians(0))
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
                        trajectoryAction = trajectoryAction.setTangent(Math.toRadians(0))
                                .splineToSplineHeading(new Pose2d(36, 32, Math.toRadians(90)), Math.toRadians(90)) //go to intake  farthest from goal
                                .setTangent(Math.toRadians(90))
                                .splineToConstantHeading(new Vector2d(36, 60), Math.toRadians(90))
                                .setTangent(Math.toRadians(-90))
                                .splineToSplineHeading(new Pose2d(-36, 36, Math.toRadians(139)), Math.toRadians(180)); //go to launch position

                    }
                }
                break;
                
            case FAR:
                trajectoryAction = drive.actionBuilder(SBFarStartPose, poseMap);
                if (intakeCycles == 0) {
                    trajectoryAction = trajectoryAction.setTangent(Math.toRadians(180));
                    // 3 launch actions
                    //then after disp intake action
                }
                trajectoryAction = trajectoryAction.splineToSplineHeading(new Pose2d(36,32, Math.toRadians(90)), Math.toRadians(90)) //go to intake farthest from goal
                        .setTangent(Math.toRadians(90))
                        .splineToConstantHeading(new Vector2d(36,60), Math.toRadians(90))
                        .setTangent(Math.toRadians(-90))
                        .splineToSplineHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90));  //go to launch position
                
                if (intakeCycles > 1) {
                    // 3 launch actions
                    //after disp intake action
                    trajectoryAction = trajectoryAction.setTangent(Math.toRadians(180))
                            .splineToSplineHeading(new Pose2d(12, 32, Math.toRadians(90)), Math.toRadians(90)) //go to intake middle from goal
                            .setTangent(Math.toRadians(90))
                            .splineToConstantHeading(new Vector2d(12, 60), Math.toRadians(90))
                            .setTangent(Math.toRadians(-90))
                            .splineToSplineHeading(new Pose2d(54, 12, Math.toRadians(157.5)), Math.toRadians(-90)); //go to launch position
                    // 3 launch actions
                    //after disp intake action
                    
                    if (intakeCycles > 2) {
                        trajectoryAction = trajectoryAction.setTangent(Math.toRadians(180))
                                .splineToSplineHeading(new Pose2d(-12,32, Math.toRadians(90)), Math.toRadians(90)) //go to intake closest to goal
                                .setTangent(Math.toRadians(90))
                                .splineToConstantHeading(new Vector2d(-12,55), Math.toRadians(90))
                                .setTangent(Math.toRadians(-90))
                                .splineToSplineHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90)); //go to launch position
                    }
                }
                break;
                
            case FAR_OUT_OF_WAY:
                // 3 launch actions
                // after disp intake action
                trajectoryAction = drive.actionBuilder(SBFarStartPose, poseMap);
                trajectoryAction = trajectoryAction.setTangent(Math.toRadians(180))
                    .splineToLinearHeading(new Pose2d(60,61, Math.toRadians(0)), Math.toRadians(0))
                    .setTangent(Math.toRadians(-90))
                    .splineToLinearHeading(new Pose2d(54,12, Math.toRadians(157.5)), Math.toRadians(-90))
                    // 3 launch actions
                    .setTangent(Math.toRadians(90))
                    .splineToLinearHeading(new Pose2d(50,32,Math.toRadians(180)), Math.toRadians(180));
                break;
            case FAR_MINIMAL:
                trajectoryAction = drive.actionBuilder(SBFarStartPose, poseMap);
                trajectoryAction = trajectoryAction.setTangent(Math.toRadians(90))
                    .splineToLinearHeading(new Pose2d(48,32,Math.toRadians(180)), Math.toRadians(180));

                break;
        }
        return trajectoryAction.build();





    }
    @Override
    public void runOpMode() {




        Pose2d startPose = new Pose2d(0, 0, 0);


        Pose2d SBNearStartPose = new Pose2d(-60, 48, Math.toRadians(139));
        Pose2d SBFarStartPose = new Pose2d(60, 12, Math.toRadians(157.5));
        MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, gamepad1, startPose);
        MechGlob mechGlob = ComplexMechGlob.create(hardwareMap, telemetry, false);


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




        // the first parameter is the type to return as


            Action trajectoryAction;

            switch (chosenMovement) {
                case NEAR:

                    drive.setPose(SBNearStartPose);

                    break;
                case FAR:
                    drive.setPose(SBFarStartPose);
                    break;
                case FAR_OUT_OF_WAY:
                    drive.setPose(SBFarStartPose);
                    break;
                case FAR_MINIMAL:
                    drive.setPose(SBFarStartPose);
                    break;
            }
            trajectoryAction = getPath(chosenMovement, chosenAlliance, intakeCycles, drive);

            // Get a preview of the trajectory's path:
            Canvas previewCanvas = new Canvas();
            trajectoryAction.preview(previewCanvas);

            // Show the preview on FTC Dashboard now.
            TelemetryPacket packet = MecanumDrive.getTelemetryPacket();
            packet.fieldOverlay().getOperations().addAll(previewCanvas.getOperations());
            MecanumDrive.sendTelemetryPacket(packet);


            // Assume unknown pattern unless detected otherwise.
            pattern = Pattern.UNKNOWN;

            // Detect the pattern with the AprilTags from the camera!
            // Wait for Start to be pressed on the Driver Hub!
            // (This try-with-resources statement automatically calls detector.close() when it exits
            //  the try-block.)
            try (AprilTagDetector detector = new AprilTagDetector(hardwareMap)) {

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
                mechGlob.update();
                // Only send the packet if there's more to do in order to keep the very last
                // drawing up on the field once the robot is done:
                if (more)
                    MecanumDrive.sendTelemetryPacket(packet);
                telemetry.update();
            }
        }
    }


