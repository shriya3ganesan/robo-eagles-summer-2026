package org.firstinspires.ftc.team417;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp
public class TestColorDetect extends LinearOpMode {
    CoolColorDetector detector;
    @Override
    public void runOpMode() {
        detector = new CoolColorDetector(hardwareMap);
        waitForStart();
        while (opModeIsActive()) {
            detector.showTelemetry(telemetry);
        }
    }
}

