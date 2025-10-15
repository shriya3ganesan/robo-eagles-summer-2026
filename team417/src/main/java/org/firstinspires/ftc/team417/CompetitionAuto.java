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
        Pose2d beginPoseNear = new Pose2d(-50, 50, Math.toRadians(41));
        MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, gamepad1, beginPoseNear);

        Pose2d beginPoseFar = new Pose2d(56, 12, Math.toRadians(135));
        MecanumDrive drive1 = new MecanumDrive(hardwareMap, telemetry, gamepad1, beginPoseFar);

        // Build the trajectory *before* the start button is pressed because Road Runner
        // can take multiple seconds for this operation. We wouldn't want to have to wait
        // as soon as the Start button is pressed!

        // Red alliance auto paths
        Action redNear = drive.actionBuilder(beginPoseNear)
                .splineTo(new Vector2d(-20, 51), 0)
                .build();

        Action redFar = drive1.actionBuilder(beginPoseFar)
                .splineTo(new Vector2d(-50, 50), Math.toRadians(41))
                .splineTo(new Vector2d(-20, 51), 0)
                .build();

        Action redFarMinimal = drive1.actionBuilder(beginPoseFar)
                .setTangent(Math.PI/2)
                .splineTo(new Vector2d(56, 35), Math.PI/2)
                .build();

        // Blue alliance auto paths
        Action blueNear = drive.actionBuilder(beginPoseNear)
                .splineTo(new Vector2d(-20, -51), 0)
                .build();

        Action blueFar = drive1.actionBuilder(beginPoseFar)
                .splineTo(new Vector2d(-50, -50), Math.toRadians(41))
                .splineTo(new Vector2d(-20, -51), 0)
                .build();

        Action blueFarMinimal = drive1.actionBuilder(beginPoseFar)
                .setTangent(Math.PI/2)
                .splineTo(new Vector2d(56, -35), Math.PI/2)
                .build();
        
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

        while (!menu.isCompleted() && opModeIsActive()) {
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

        Action trajectoryAction = null;
        switch (chosenAlliance) {
            case RED:
                switch (chosenMovement) {
                    case NEAR:
                        trajectoryAction = redNear;
                        break;
                    case FAR:
                        trajectoryAction = redFar;
                        break;
                    case FAR_MINIMAL:
                        trajectoryAction = redFarMinimal;
                        break;
                }
                break;

            case BLUE:
                switch (chosenMovement) {
                    case NEAR:
                        trajectoryAction = blueNear;
                        break;
                    case FAR:
                        trajectoryAction = blueFar;
                        break;
                    case FAR_MINIMAL:
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
