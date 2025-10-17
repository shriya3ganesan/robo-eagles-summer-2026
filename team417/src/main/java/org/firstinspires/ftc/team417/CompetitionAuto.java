package org.firstinspires.ftc.team417;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
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
@Autonomous(name="Auto", group="Competition", preselectTeleOp="CompetitionTeleOp")
public class CompetitionAuto extends BaseOpMode {
    enum Alliances {
        RED,
        BLUE,
    }

    enum Movements {
        NEAR,
        FAR,
        FAR_MINIMAL,
    }

    double minWaitTime = 0.0;
    double maxWaitTime = 15.0;
    
    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(0, 0, 0);

        Pose2d redNearStartPose = new Pose2d(-48, 48, Math.toRadians(41));
        Pose2d redFarStartPose = new Pose2d(56, 12, Math.toRadians(180));

        Pose2d blueNearStartPose = new Pose2d(-48, -48, Math.toRadians(139));
        Pose2d blueFarStartPose = new Pose2d(56, -12, Math.toRadians(180));

        MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, gamepad1, startPose);

        TextMenu menu = new TextMenu();
        MenuInput menuInput = new MenuInput(MenuInput.InputType.CONTROLLER);

        menu.add(new MenuHeader("AUTO SETUP"))
                .add() // empty line for spacing
                .add("Pick an alliance:")
                .add("alliance-picker-1", Alliances.class) // enum selector shortcut
                .add()
                .add("Pick a movement:")
                .add("movement-picker-1", Movements.class) // enum selector shortcut
                .add()
                .add("Wait time:")
                .add("wait-slider-1", new MenuSlider(minWaitTime, maxWaitTime))
                .add()
                .add("finish-button-1", new MenuFinishedButton());

        while (!menu.isCompleted()) {
            // get x, y (stick) and select (A) input from controller
            // on wilyworks, this is x, y (wasd) and select (enter) on the keyboard
            menuInput.update(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.a);
            menu.updateWithInput(menuInput);
            // display the updated menu
            for (String line : menu.toListOfStrings()) {
                telemetry.addLine(line); // but with appropriate printing method
            }
            telemetry.update();
        }

        // the first parameter is the type to return as
        Alliances chosenAlliance = menu.getResult(Alliances.class, "alliance-picker-1");
        Movements chosenMovement = menu.getResult(Movements.class, "movement-picker-1");
        double waitTime = menu.getResult(Double.class, "wait-slider-1");

        // Red alliance auto paths
        Action redNear = drive.actionBuilder(redNearStartPose)
                .splineTo(new Vector2d(-20, 51), Math.toRadians(0))
                .build();

        Action redFar = drive.actionBuilder(redFarStartPose)
                .splineTo(new Vector2d(-50, 50), Math.toRadians(41))
                .splineTo(new Vector2d(-20, 51), 0)
                .build();

        Action redFarMinimal = drive.actionBuilder(redFarStartPose)
                .setTangent(Math.PI/2)
                .splineTo(new Vector2d(-56, 35), Math.PI/2)
                .build();

        // Blue alliance auto paths
        Action blueNear = drive.actionBuilder(blueNearStartPose)
                .splineTo(new Vector2d(-20, -51), Math.toRadians(135))
                .build();

        Action blueFar = drive.actionBuilder(blueFarStartPose)
                .splineTo(new Vector2d(-50, -50), Math.toRadians(41))
                .build();

        Action blueFarMinimal = drive.actionBuilder(blueFarStartPose)
                .setTangent(Math.PI/2)
                .splineTo(new Vector2d(-56, 35), Math.PI/2)
                .build();


        Action trajectoryAction = null;
        switch (chosenAlliance) {
            case RED:
                switch (chosenMovement) {
                    case NEAR:
                        drive.setPose(redNearStartPose);
                        trajectoryAction = redNear;
                        break;
                    case FAR:
                        drive.setPose(redFarStartPose);
                        trajectoryAction = redFar;
                        break;
                    case FAR_MINIMAL:
                        drive.setPose(redFarStartPose);
                        trajectoryAction = redFarMinimal;
                        break;
                }
                break;

            case BLUE:
                switch (chosenMovement) {
                    case NEAR:
                        drive.setPose(blueNearStartPose);
                        trajectoryAction = blueNear;
                        break;
                    case FAR:
                        drive.setPose(blueFarStartPose);
                        trajectoryAction = blueFar;
                        break;
                    case FAR_MINIMAL:
                        drive.setPose(blueFarStartPose);
                        trajectoryAction = blueFarMinimal;
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


        // Build the trajectory *before* the start button is pressed because Road Runner
        // can take multiple seconds for this operation. We wouldn't want to have to wait
        // as soon as the Start button is pressed!


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
