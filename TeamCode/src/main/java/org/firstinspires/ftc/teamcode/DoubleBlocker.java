package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DoubleBlocker {
    Servo blockerServo = null;
    boolean leftBumperPressed;
    boolean rightBumperPressed;
    double leftBlockerClosePosition = 0.65;
    double leftBlockerOpenPosition = 0.78;
    double rightBlockerClosePosition = 0.415;
    double rightBlockerOpenPosition = 0.5;
    String direction;

    // Fix twitches
    boolean previousLeftState = false;
    boolean previousRightState = false;
    boolean isOpen = false;


    public DoubleBlocker(HardwareMap hardwareMap, Telemetry telemetry, String direction) {
        blockerServo = hardwareMap.get(Servo.class,direction);
        this.leftBumperPressed = false;
        this.rightBumperPressed = false;
        this.direction = direction;
    }

    public void powerServo(Gamepad gamepad, Telemetry telemetry) {
        controlServo(gamepad, telemetry);
    }

    public void controlServo(Gamepad gamepad, Telemetry telemetry) {
        if (this.direction.equals("left")) {
            if (gamepad.left_bumper && !previousLeftState) {
                isOpen = !isOpen;

                if (isOpen) {
                    open();
                } else {
                    close();
                }
            }
            previousLeftState = gamepad.left_bumper;
        }

        if (this.direction.equals("right")) {
            if (gamepad.right_bumper && !previousRightState) {
                isOpen = !isOpen;

                if (isOpen) {
                    open();
                } else {
                    close();
                }
            }
            previousRightState = gamepad.right_bumper;
        }
    }

    public double getServoPosition() {
        return blockerServo.getPosition();
    }

    public void open() {
        if (direction.equals("left")){
            blockerServo.setPosition(leftBlockerOpenPosition);
        } else {
            blockerServo.setPosition(rightBlockerOpenPosition);
        }

    }

    public void close() {
        if (direction.equals("left")){
            blockerServo.setPosition(leftBlockerClosePosition);
        } else {
            blockerServo.setPosition(rightBlockerClosePosition);
        }
    }
}
