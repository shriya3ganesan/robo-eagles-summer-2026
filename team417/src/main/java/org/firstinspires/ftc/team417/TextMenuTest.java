package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import net.valsei.java_text_menu.*;

@Autonomous(name = "Text Menu Test")
public class TextMenuTest extends LinearOpMode {

    enum Alliances {
        RED,
        BLUE,
    }

    enum Positions {
        NEAR,
        FAR,
    }

    enum Movements {
        MINIMAL,
        LAUNCHING,
    }

    double minWaitTime = 0.0;
    double maxWaitTime = 15.0;

    @Override
    public void runOpMode() throws InterruptedException {
        TextMenu menu = new TextMenu();
        MenuInput menuInput = new MenuInput(MenuInput.InputType.CONTROLLER);

        menu.add(new MenuHeader("CHOOSE YOUR PLANTS!"))
                .add("Shortcut?")
                .add() // empty line for spacing
                .add("Pick an alliance:")
                .add("alliance-picker-1", Alliances.class) // enum selector shortcut
                .add()
                .add("Pick a starting position:")
                .add("position-picker-1", Positions.class) // enum selector shortcut
                .add()
                .add("Pick a movement:")
                .add("movement-picker-1", Movements.class) // enum selector shortcut
                .add()
                .add("Wait time:")
                .add("wait-slider-1", new MenuSlider(minWaitTime, maxWaitTime))
                .add()
                .add("finish-button-1", new MenuFinishedButton());

        while (!menu.isCompleted()) {
            // get x,y (stick) and select (A) input from controller
            menuInput.update(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.a);
            menu.updateWithInput(menuInput);
            // display the updated menu
            for (String line : menu.toListOfStrings()) {
                telemetry.addLine(line); // but with appropriate printing method
            }
            telemetry.update();
        }
    }
}
