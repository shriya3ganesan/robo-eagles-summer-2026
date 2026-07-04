package org.firstinspires.ftc.teamcode.ExampleCode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Robot.SoftwareTestingBot;
import org.firstinspires.ftc.teamcode.SWEEP.Builder.Path;
import org.firstinspires.ftc.teamcode.SWEEP.Builder.PathBuilder;
public class PathBuilderExampleUsage extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SoftwareTestingBot robot = new SoftwareTestingBot(hardwareMap, telemetry);
        Path path = new PathBuilder()
                .start(0,0,0)
                .splineTo(10,10,0.5)
                .splineToAngle(20,20,90,0.5)
                .addBreak()
                .linearTo(30,30,0.5)
                .linearToAngle(40,40,180,0.5)
                .addAction(robot.exampleAction())
                .waitAt(2)
                .end(60,60,270,0.5)
                .build();
        robot.setPath(path);
        waitForStart();
        while (opModeIsActive()) {
            robot.autonomousUpdate();
        }
    }
}
