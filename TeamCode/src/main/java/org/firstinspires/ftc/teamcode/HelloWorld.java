package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class HelloWorld extends OpMode{

    @Override
    public void init() {
        telemetry.addData("Hello World!!! First FTC code of the season!!!", "1");
    }

    @Override
    public void loop() {
    }
}
