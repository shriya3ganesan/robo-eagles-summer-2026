package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.teamcode.Util.Enum.Balls;


@Autonomous(name = "DualNormalizedColorSensorTest")
@Disabled
public class ColorSensorTestOpMode extends LinearOpMode {


    int color = 0;
    private NormalizedColorSensor colorSensor1;
    private NormalizedColorSensor colorSensor2;

    @Override
    public void runOpMode() {

        // Initialize the sensors
        colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");
        colorSensor1.setGain(5);  // Try values 1–10
        colorSensor2.setGain(5);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();


        while (opModeIsActive()) {
            Balls detectedColor;

            detectedColor = colorDetection(colorSensor1, colorSensor2);



            telemetry.addLine("COMBINED RESULT");
            telemetry.addData("Combined Color", detectedColor.name());

            telemetry.update();
        }
    }


}
