/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.robot.Launcher.TRIGGER_START_POS;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Launcher;
import org.firstinspires.ftc.teamcode.robot.RobotHardware;
import org.firstinspires.ftc.teamcode.robot.Drivetrain;
import org.firstinspires.ftc.teamcode.robot.Vision;

@TeleOp(name="CompTeleop", group="Linear OpMode")
@Configurable
public class CompTeleop extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private RobotHardware robot = new RobotHardware();
    private Intake intake = null;
    private Launcher launcher = null;
    private Drivetrain drivetrain = null;
    private Vision vision = null;

    public static double PUSH_START_POS = 0;

    @Override
    public void runOpMode() {
        //configurePinpoint();
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        robot.init(hardwareMap);

        intake = new Intake(robot);

        launcher = new Launcher(robot);
        launcher.init();

        drivetrain = new Drivetrain(robot);



        // Wait for the game to start (driver presses START)
        telemetry.addData("Status: ", "Initialized");
        telemetry.update();

        // Set initial positions
        robot.Trigger.setPosition(TRIGGER_START_POS);
        robot.push.setPosition(PUSH_START_POS);

        waitForStart();
        runtime.reset();

        vision = new Vision(robot);
        vision.init();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            LLResult result = vision.update();
            double distance = vision.getDistance();
            double voltage = robot.myControlHubVoltageSensor.getVoltage();
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw = vision.getYaw(gamepad1.right_stick_x, gamepad1.left_bumper, result);
            if (!result.isValid()) {
                telemetry.addLine("Not Locked On");
            } else {
                telemetry.addLine("Locked On");
            }

            telemetry.addData("Voltage: ", voltage);

            drivetrain.drive(axial, lateral, yaw);

            launcher.update(gamepad2.right_trigger, gamepad2.y, vision.hasTarget(), distance, voltage);            intake.update(gamepad2.a, gamepad2.b, gamepad1.y, gamepad2.x, launcher.isTripleShotActive());
            intake.setTransfer(gamepad2.right_bumper, gamepad2.left_bumper);

            telemetry.addData("Front left/Right", "%4.2f, %4.2f", robot.frontLeftDrive.getPower(), robot.frontRightDrive.getPower());
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", robot.backLeftDrive.getPower(), robot.backRightDrive.getPower());
            telemetry.addData("Launch State", launcher.getLaunchState());
            telemetry.addData("Current Launch Power", "%.2f", launcher.getCurrentLaunchPower());
            telemetry.addData("distance: ", distance);
            telemetry.update();
        }
    }
}
