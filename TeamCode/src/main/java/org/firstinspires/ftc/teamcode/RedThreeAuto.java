package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;

@Autonomous
@Disabled
public class RedThreeAuto extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    Launcher launcher = new Launcher();

    enum State {
        MOVE_FORWARD,
        FINISHED
    }
    State state = State.MOVE_FORWARD;
    ElapsedTime driveTimer = new ElapsedTime();

    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);

        state = State.MOVE_FORWARD;
    }

    @Override
    public void loop() {
        telemetry.addData("Current state", state);

        switch (state) {
            case MOVE_FORWARD:
                driveTimer.reset();
                while (driveTimer.seconds() < 5) {
                    drive.drive(0.3, 0.3, 0.1);
                }
                drive.drive(0,0,0);
                state = State.FINISHED;
                break;
            case FINISHED:
                break;
            default:

        }

    }
}