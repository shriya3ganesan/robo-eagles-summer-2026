package org.firstinspires.ftc.team417;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp(name = "Auto", group = "Test")
public class TestAuto extends CompetitionAuto{
    @Override
    public void runOpMode() {
        super.runOpMode();
        // stops auto from stopping so we can see the spew
        while (opModeIsActive()) {
            sleep(1000);
        }
    }
}
