package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Muchen extends OpMode {

    @Override
    public void init() {
        int i = 1;
        int fah = 2;

        int sdsd = i + fah;

        telemetry.addData(sdsd);
    }

    @Override
    public void loop() {

        int wierdthing = 60845;
        int anotherthingy = 409095;

        int bothwierdthings = wierdthing + anotherthingy;

        telemetry.addData("hi this is a thing", bothwierdthings);

    }

}
