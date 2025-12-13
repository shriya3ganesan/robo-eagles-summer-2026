package org.firstinspires.ftc.team417;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp(name = "Auto", group = "Test")
@Config
public class TestAuto extends CompetitionAuto{
    public static double LAUNCH_SPEED = 850;
    public static double DRIVE_SPEED = 1;
    public static double INTAKE_SPEED = 1;
    public static double INTAKE_BACK_TIME = 0.25;

    @Override
    public void runOpMode() {
        ComplexMechGlob.NEAR_FLYWHEEL_VELOCITY = LAUNCH_SPEED;
        CompetitionAuto.ROBOT_SPEED = DRIVE_SPEED;
        CompetitionAuto.INTAKE_SPEED = INTAKE_SPEED;

        //super.runOpMode();
        processAuto(Alliance.RED, SlowBotMovement.NEAR, 0,2);
        // stops auto from stopping so we can see the spew
        while (opModeIsActive()) {
            sleep(1000);
        }
    }
}
