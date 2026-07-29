
package org.firstinspires.ftc.teamcode.Exercises.Keerthy;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="Basic: Iterative OpMode", group="Iterative OpMode")
@Disabled
public class TeleOp_Template extends OpMode
{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor fl = null;
    private DcMotor fr = null;
    private DcMotor bl = null;
    private DcMotor br = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        fl = hardwareMap.get (DcMotor.class, "fl");
        fr = hardwareMap.get (DcMotor.class, "fr");
        bl = hardwareMap.get (DcMotor.class, "bl");
        br = hardwareMap.get (DcMotor.class, "br");

        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);


        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {

/*
    //left joystick is forward and backward
    if (Math.abs(gamepad1.left_stick_y) > .5) {
        fl.setPower(gamepad1.left_stick_y);
        fr.setPower(gamepad1.left_stick_y);
        bl.setPower(gamepad1.left_stick_y);
        br.setPower(gamepad1.left_stick_y);
    } else {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }


    // right joystick turns

        if (Math.abs(gamepad1.right_stick_x) > .5) {
            fl.setPower(gamepad1.right_stick_x);
            fr.setPower(-gamepad1.right_stick_x);
            bl.setPower(gamepad1.right_stick_x);
            br.setPower(-gamepad1.right_stick_x);
        } else {
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);
        }
*/

        double left_power;
        double right_power;


        left_power = gamepad1.left_stick_y + gamepad1.right_stick_x;
        right_power = gamepad1.left_stick_y - gamepad1.right_stick_x;


        if (Math.abs(left_power) < .05) {
            left_power = 0;
        }
        if (Math.abs(right_power) < .05) {
            right_power = 0;


        fl.setPower(left_power);
        fr.setPower(right_power);
        bl.setPower(left_power);
        br.setPower(right_power);

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
