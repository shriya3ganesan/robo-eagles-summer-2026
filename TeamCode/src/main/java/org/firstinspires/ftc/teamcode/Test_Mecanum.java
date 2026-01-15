package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;

@TeleOp
@Disabled
public class Test_Mecanum extends OpMode {

    MecanumDrive drive = new MecanumDrive();

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {

        // from either controller, drive each motor forward individually to verify direction is correct
        if (gamepad2.x || gamepad1.x) { // front_left
            drive.testWheelDirection(true, true, false, false);
            telemetry.addLine("Front Left Forward");
        } else if (gamepad2.a || gamepad1.a) { // front right
            telemetry.addLine("Front Right Forward");
        } else if(gamepad2.y || gamepad1.y) { // back left
            telemetry.addLine("Back Left Forward");
        } else if(gamepad2.b || gamepad1.b) { // back right
            telemetry.addLine("Back Right Forward");
        }
    }
}


