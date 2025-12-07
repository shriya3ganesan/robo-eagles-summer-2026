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

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Simple Autonomous:
 * 1. Drive backward for 2.5 seconds at 0.5 speed
 * 2. Shoot 3 balls with automatic loading
 */
@Autonomous(name="Drive Back and Shoot 3", group="Autonomous")
public class MoveAndShootAuto extends LinearOpMode {

    // Drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;

    // Launcher
    private DcMotor launchMotor = null;

    // Servos
    private Servo Trigger = null;
    private Servo push = null;

    // Servo positions
    private final double TRIGGER_START_POS = 0;
    private final double TRIGGER_FIRE_POS = 300;
    private final double PUSH_START_POS = 0;
    private final double PUSH_LOAD_POS = 100;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        // Initialize hardware
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        launchMotor = hardwareMap.get(DcMotor.class, "launch_motor");
        Trigger = hardwareMap.get(Servo.class, "Trigger");
        push = hardwareMap.get(Servo.class, "push");

        // Set motor directions
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        launchMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        Trigger.setDirection(Servo.Direction.FORWARD);
        push.setDirection(Servo.Direction.FORWARD);

        // Set initial servo positions
        Trigger.setPosition(TRIGGER_START_POS);
        push.setPosition(PUSH_START_POS);

        // Display initialization status
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Sequence", "1. Drive back 2.5s");
        telemetry.addData("", "2. Shoot 3 balls");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // ==========================================
        // STEP 1: DRIVE BACKWARD FOR 2.5 SECONDS
        // ==========================================
        telemetry.addData("Status", "Step 1: Driving backward...");
        telemetry.update();

        frontLeftDrive.setPower(-0.4);
        backLeftDrive.setPower(-0.4);
        frontRightDrive.setPower(-0.4);
        backRightDrive.setPower(-0.4);

        sleep(500);  // .5 seconds

        // Stop driving
        stopAllMotors();

        telemetry.addData("Status", "Drive complete");
        telemetry.update();
        launchMotor.setPower(0.67);
        sleep(300);  // Brief pause to settle

        // ==========================================
        // STEP 2: SHOOT 3 BALLS
        // ==========================================

        for (int ballNumber = 1; ballNumber <= 3; ballNumber++) {
            telemetry.addData("Status", "Shooting ball %d of 3", ballNumber);
            telemetry.update();

            // Spin up launch motor
            telemetry.addData("Ball %d", "Spinning up...", ballNumber);
            telemetry.update();
            launchMotor.setPower(0.67);

            // Fire the trigger
            telemetry.addData("Ball %d", "FIRING!", ballNumber);
            telemetry.update();
            Trigger.setPosition(TRIGGER_FIRE_POS);
            sleep(300);  // Wait for ball to launch

            // Reset trigger
            Trigger.setPosition(TRIGGER_START_POS);
            sleep(200);  // Wait for trigger to reset

            // If not the last ball, load the next one
            if (ballNumber < 3) {
                telemetry.addData("Status", "Loading next ball...");
                telemetry.update();

                // Push next ball into chamber
                push.setPosition(PUSH_LOAD_POS);
                sleep(400);  // Wait for ball to load

                // Reset push servo
                push.setPosition(PUSH_START_POS);
                sleep(400);  // Wait for servo to reset
            }
        }

        // ==========================================
        // COMPLETE
        // ==========================================
        telemetry.addData("Status", "Autonomous Complete!");
        telemetry.addData("Balls Shot", "3");
        telemetry.addData("Total Runtime", "%.1f seconds", runtime.seconds());
        telemetry.update();

        // Ensure everything is stopped
        stopAllMotors();
        launchMotor.setPower(0.0);
    }

    /**
     * Stop all drive motors
     */
    private void stopAllMotors() {
        frontLeftDrive.setPower(0);
        backLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backRightDrive.setPower(0);
    }
}
