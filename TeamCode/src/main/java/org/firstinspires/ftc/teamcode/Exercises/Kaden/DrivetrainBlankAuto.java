package org.firstinspires.ftc.teamcode.Exercises.Kaden;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(
        name = "Drivetrain Blank Exercise",
        group = "Exercises"
)
public class DrivetrainBlankAuto extends LinearOpMode {

    @Override
    public void runOpMode() {

        DrivetrainBlank dt = new DrivetrainBlank(this);

        /*
         * Create a DrivetrainBlank object named drivetrain.
         *
         * Pass "this" into the constructor so the drivetrain class
         * can access this OpMode's hardwareMap, telemetry, and timer.
         */


        telemetry.addLine("Robot is ready.");
        telemetry.addLine("Press Play to begin.");
        telemetry.update();


        /*
         * Wait here until the driver presses Play.
         */
        waitForStart();


        /*
         * Only begin the movement sequence if the OpMode
         * has not been stopped.
         */
        if (opModeIsActive()) {

            /*
             * STEP 1:
             * Drive forward at 40% power for 1,000 milliseconds.
             *
             * TODO: Call driveForTime().
             */
            dt.driveForTime(.4, 1000);



            /*
             * STEP 2:
             * Stop for 500 milliseconds before the next movement.
             *
             * sleep() belongs to LinearOpMode, so it does not need
             * to be called through the drivetrain object.
             */
            sleep(500);


            /*
             * STEP 3:
             * Turn right at 30% power for 750 milliseconds.
             *
             * TODO: Call turnForTime().
             */
            dt.turnForTime(.3, 750);



            /*
             * STEP 4:
             * Pause for 500 milliseconds.
             */
            sleep(500);


            /*
             * STEP 5:
             * Drive forward 24 inches at 50% power.
             *
             * Give the movement a 5,000-millisecond timeout.
             *
             * TODO: Call driveInches().
             */
            dt.driveInches(24, .5, 5000);



            /*
             * STEP 6:
             * Pause for 500 milliseconds.
             */
            sleep(500);


            /*
             * STEP 7:
             * Drive backward at 40% power for 1,000 milliseconds.
             * TODO: Call driveForTime().
             */
            dt.driveForTime(-.4, 1000);



            /*
             * STEP 8:
             * Make sure all drivetrain motors are stopped.
             *
             * TODO: Call stop().
             */
            stop();

        }
    }
}