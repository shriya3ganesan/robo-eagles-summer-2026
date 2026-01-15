package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.LEDIndicator;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;


@TeleOp
@Disabled
public class LEDTestOpMode extends OpMode {
    //private Servo servo;
    //Launcher launcher = new Launcher();
    LEDIndicator led = new LEDIndicator();

    //private final int FEED_TIME_MILLISECONDS = 200; //The feeder servo runs this long when a shot is requested.
    //private final double FEED_START_POSITION = 0.0; // nominally 0 degrees, may need to be tuned based on mounting angle of servo
    //private final double FEED_POSITION = 0.5; // nominally 90 degrees, may need to increase it slightly

    //ElapsedTime feederTimer = new ElapsedTime();

    @Override
    public void init() {
        led.init(hardwareMap);

    }

    @Override
    public void loop() {

        if (gamepad2.aWasPressed()) {
            //launcher.loadBall();
            led.incrementLEDValue();
        } else if (gamepad2.bWasPressed()) {
            //launcher.resetFeeder();
            led.decrementLEDValue();
        }

        String ledvaluestr = String.format("%.2f",led.getLEDValue());
        telemetry.addLine("Current LED Value: " + ledvaluestr);

    }
}