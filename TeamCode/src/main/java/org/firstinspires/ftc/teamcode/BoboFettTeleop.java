package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp
public class BoboFettTeleop extends OpMode {

    BoboFettFieldCentric fieldCentric;
    BoboFettControls controls;

    @Override
    public void init() {
        fieldCentric = new BoboFettFieldCentric(hardwareMap,this,384.5,1,4);
        controls = new BoboFettControls(hardwareMap,this);
    }
    @Override
    public void loop() {
        fieldCentric.UpdateDriveTrain();
        controls.teleOpControls();
    }


    @Override
    public void stop() {
    }

}
