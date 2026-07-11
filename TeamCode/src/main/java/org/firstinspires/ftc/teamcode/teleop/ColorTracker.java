package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="ColorTracker", group="Linear OpMode")
public class ColorTracker extends LinearOpMode {

    // ── Hardware ──────────────────────────────────────────────────────────────
    private DcMotor frontLeftDrive, backLeftDrive, frontRightDrive, backRightDrive;
    private DcMotor launchMotor, transferMotor, intakeMotor;
    private Servo trigger;
    private Limelight3A limelightColor;     // color detection  (pipelines 3 & 4)
    private Limelight3A limelightAprilTag;  // AprilTag detection (pipeline 0)

    // ── Pattern ───────────────────────────────────────────────────────────────
    private char[] pattern = {'P', 'P', 'P'};
    private static final int PATTERN_LENGTH = 3;
    private int currentColorIndex = 0;

    // ── State machine ─────────────────────────────────────────────────────────
    private enum TrackerState { FOLLOWING, SHOOTING, DONE }
    private TrackerState state = TrackerState.FOLLOWING;

    // ── Following constants ───────────────────────────────────────────────────
    private static final double FOLLOW_DRIVE_SPEED  = 0.5;
    private static final double STEER_KP            = 0.03;
    private static final double STEER_DEADBAND      = 1.5;
    private static final double STOP_AREA_THRESHOLD = 3.5;
    private static final double SEARCH_SPIN_POWER   = 0.25;

    // ── Pipeline mappings ─────────────────────────────────────────────────────
    private static final int PURPLE_PIPELINE   = 3;
    private static final int GREEN_PIPELINE    = 4;
    private static final int APRILTAG_PIPELINE = 0;   // limelight_apriltag pipeline

    // ── Shooter constants ─────────────────────────────────────────────────────
    public static double LAUNCH_POWER      = 0.71;
    public static double TRIGGER_START_POS = 0.11;
    public static double TRIGGER_SHOOT_POS = 0.4;
    public static double SPIN_UP_TIME      = 0.5;
    public static int    SHOTS_PER_COLOR   = 1;

    // ── Shooting state ────────────────────────────────────────────────────────
    private int         shotsFired = 0;
    private ElapsedTime shootTimer = new ElapsedTime();

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void runOpMode() {

        // ── Hardware init ─────────────────────────────────────────────────────
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive   = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive  = hardwareMap.get(DcMotor.class, "back_right_drive");
        launchMotor     = hardwareMap.get(DcMotor.class, "launch_motor");
        transferMotor   = hardwareMap.get(DcMotor.class, "transfer");
        intakeMotor     = hardwareMap.get(DcMotor.class, "intake_motor");
        trigger         = hardwareMap.get(Servo.class,   "Trigger");

        limelightColor    = hardwareMap.get(Limelight3A.class, "limelight_color");
        limelightAprilTag = hardwareMap.get(Limelight3A.class, "limelight_apriltag");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        launchMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        trigger.setDirection(Servo.Direction.FORWARD);


        //try FLOAT instead of BRAKE - might be fun
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        trigger.setPosition(TRIGGER_START_POS);

        // ── Pattern selection (runs before Start is pressed) ──────────────────
        int selectedSlot = 0;
        boolean prevDpadR = false, prevDpadL = false, prevA = false, prevB = false;

        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.dpad_right && !prevDpadR)
                selectedSlot = Math.min(selectedSlot + 1, PATTERN_LENGTH - 1);
            if (gamepad1.dpad_left && !prevDpadL)
                selectedSlot = Math.max(selectedSlot - 1, 0);
            if (gamepad1.a && !prevA) pattern[selectedSlot] = 'P';
            if (gamepad1.b && !prevB) pattern[selectedSlot] = 'G';

            prevDpadR = gamepad1.dpad_right;
            prevDpadL = gamepad1.dpad_left;
            prevA     = gamepad1.a;
            prevB     = gamepad1.b;

            telemetry.addLine("──── SET PATTERN ────");
            telemetry.addLine("DPad L/R : select slot");
            telemetry.addLine("A : Purple   B : Green");
            telemetry.addLine("");
            for (int i = 0; i < PATTERN_LENGTH; i++) {
                String arrow = (i == selectedSlot) ? "  ◄" : "";
                telemetry.addData("  Slot " + (i + 1), colorName(pattern[i]) + arrow);
            }
            telemetry.addLine("");
            telemetry.addLine("Press START when ready");
            telemetry.update();
        }

        // ── Start both Limelights ─────────────────────────────────────────────
        limelightColor.start();
        limelightAprilTag.start();
        limelightAprilTag.pipelineSwitch(APRILTAG_PIPELINE);   // fixed — always pipeline 0
        switchPipelineToCurrentColor();                         // sets limelightColor pipeline

        // ── Main loop ─────────────────────────────────────────────────────────
        while (opModeIsActive()) {

            // Poll both cameras every loop
            LLResult colorResult    = limelightColor.getLatestResult();
            LLResult aprilTagResult = limelightAprilTag.getLatestResult();

            boolean colorVisible    = colorResult    != null && colorResult.isValid();
            boolean aprilTagVisible = aprilTagResult != null && aprilTagResult.isValid();

            switch (state) {

                // ── FOLLOWING ─────────────────────────────────────────────────
                case FOLLOWING:
                    followColor(colorResult, colorVisible);

                    // Arrival condition: color area large enough OR an AprilTag
                    // is detected close enough (ta >= threshold on the AT camera)
                    boolean arrivedByColor    = colorVisible
                            && colorResult.getTa() >= STOP_AREA_THRESHOLD;
                    boolean arrivedByAprilTag = aprilTagVisible
                            && aprilTagResult.getTa() >= STOP_AREA_THRESHOLD;

                    if (arrivedByColor || arrivedByAprilTag) {
                        stopDrive();
                        shotsFired = 0;
                        shootTimer.reset();
                        launchMotor.setPower(LAUNCH_POWER);
                        state = TrackerState.SHOOTING;
                        telemetry.addData("Status", "Arrived ("
                                + (arrivedByAprilTag ? "AprilTag" : "color area")
                                + ") — starting shoot sequence");
                    }
                    break;

                // ── SHOOTING ──────────────────────────────────────────────────
                case SHOOTING:
                    launchMotor.setPower(LAUNCH_POWER);

                    if (shotsFired < SHOTS_PER_COLOR) {
                        shootOneShot();
                    } else {
                        launchMotor.setPower(0);
                        transferMotor.setPower(0);
                        trigger.setPosition(TRIGGER_START_POS);

                        currentColorIndex++;

                        if (currentColorIndex >= PATTERN_LENGTH) {
                            state = TrackerState.DONE;
                        } else {
                            switchPipelineToCurrentColor();
                            shootTimer.reset();
                            state = TrackerState.FOLLOWING;
                        }
                    }
                    break;

                // ── DONE ──────────────────────────────────────────────────────
                case DONE:
                    stopDrive();
                    launchMotor.setPower(0);
                    transferMotor.setPower(0);
                    telemetry.addLine("══ PATTERN COMPLETE ══");
                    break;
            }

            // ── Telemetry ─────────────────────────────────────────────────────
            telemetry.addData("State",    state);
            telemetry.addData("Color",    currentColorIndex < PATTERN_LENGTH
                    ? colorName(pattern[currentColorIndex]) : "Done");
            telemetry.addData("Progress", currentColorIndex + " / " + PATTERN_LENGTH);
            telemetry.addData("Shots",    shotsFired + " / " + SHOTS_PER_COLOR);

            // Color camera
            if (colorVisible) {
                telemetry.addData("[Color cam] TX",   "%.2f°",  colorResult.getTx());
                telemetry.addData("[Color cam] Area",  "%.2f%%", colorResult.getTa());
            } else {
                telemetry.addData("[Color cam]", "No target");
            }

            // AprilTag camera
            if (aprilTagVisible) {
                telemetry.addData("[AprilTag cam] TX",  "%.2f°",  aprilTagResult.getTx());
                telemetry.addData("[AprilTag cam] Area", "%.2f%%", aprilTagResult.getTa());
                // If the result carries a fiducial ID, display it
                if (!aprilTagResult.getFiducialResults().isEmpty()) {
                    telemetry.addData("[AprilTag cam] ID",
                            aprilTagResult.getFiducialResults().get(0).getFiducialId());
                }
            } else {
                telemetry.addData("[AprilTag cam]", "No tag");
            }

            telemetry.addData("Controls", "LB = manual override");
            telemetry.update();
        }

        // Safe stop
        stopDrive();
        launchMotor.setPower(0);
        limelightColor.stop();
        limelightAprilTag.stop();
    }

    // ── Follow the current color target ───────────────────────────────────────
    private void followColor(LLResult result, boolean targetVisible) {

        double axial = 0.0;
        double yaw   = 0.0;

        if (targetVisible) {
            double tx = result.getTx();
            double ta = result.getTa();

            if (Math.abs(tx) > STEER_DEADBAND)
                yaw = Range.clip(tx * STEER_KP, -0.4, 0.4);

            if (ta < STOP_AREA_THRESHOLD) {
                double approachSpeed = FOLLOW_DRIVE_SPEED * (1.0 - (ta / STOP_AREA_THRESHOLD));
                axial = Math.max(approachSpeed, 0.15);
            }

            telemetry.addData("Status", "Following " + colorName(pattern[currentColorIndex]));
            telemetry.addData("TX",   "%.2f°",  tx);
            telemetry.addData("Area", "%.2f%%", ta);

        } else {
            yaw = SEARCH_SPIN_POWER;
            telemetry.addData("Status", "Searching for " + colorName(pattern[currentColorIndex]));
        }

        // Manual mecanum override
        if (gamepad1.left_bumper) {
            double a = -gamepad1.left_stick_y;
            double l =  gamepad1.left_stick_x;
            double y =  gamepad1.right_stick_x;
            double fl = a + l + y, fr = a - l - y, bl = a - l + y, br = a + l - y;
            double m  = Math.max(1.0, Math.max(Math.max(Math.abs(fl), Math.abs(fr)),
                    Math.max(Math.abs(bl), Math.abs(br))));
            setDrivePower(fl/m, fr/m, bl/m, br/m);
            telemetry.addData("Status", "MANUAL OVERRIDE");
            return;
        }

        double fl = axial + yaw, fr = axial - yaw;
        double bl = axial + yaw, br = axial - yaw;
        double max = Math.max(1.0, Math.max(Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))));
        setDrivePower(fl/max, fr/max, bl/max, br/max);
    }

    // ── Fire one ball ─────────────────────────────────────────────────────────
    private void shootOneShot() {
        double elapsed     = shootTimer.seconds();

        if (elapsed < SPIN_UP_TIME) {
            telemetry.addData("Shoot", "Spinning up (%.2fs)", elapsed);
            return;
        }

        double cycleElapsed = elapsed - SPIN_UP_TIME;
        double cycleTime    = cycleElapsed % 1.3;

        if (cycleTime <= 0.4) {
            telemetry.addData("Shoot", "Waiting — shot " + (shotsFired + 1));
        } else if (cycleTime < 0.9) {
            transferMotor.setPower(1);
            telemetry.addData("Shoot", "Feeding — shot " + (shotsFired + 1));
        } else if (cycleTime < 1.1) {
            trigger.setPosition(TRIGGER_SHOOT_POS);
            transferMotor.setPower(0);
            telemetry.addData("Shoot", "Firing — shot " + (shotsFired + 1));
        } else if (cycleTime < 1.25) {
            trigger.setPosition(TRIGGER_START_POS);
            telemetry.addData("Shoot", "Resetting — shot " + (shotsFired + 1));
        } else {
            if (cycleElapsed >= (shotsFired + 1) * 1.3) {
                shotsFired++;
                telemetry.addData("Shoot", "Shot " + shotsFired + " confirmed");
            }
        }
    }

    // ── Switch limelightColor to the pipeline for the current pattern slot ────
    private void switchPipelineToCurrentColor() {
        if (currentColorIndex >= PATTERN_LENGTH) return;
        int pipeline = (pattern[currentColorIndex] == 'P') ? PURPLE_PIPELINE : GREEN_PIPELINE;
        limelightColor.pipelineSwitch(pipeline);
    }

    private String colorName(char c) { return c == 'P' ? "Purple" : "Green"; }
    private void stopDrive()         { setDrivePower(0, 0, 0, 0); }
    private void setDrivePower(double fl, double fr, double bl, double br) {
        frontLeftDrive.setPower(fl);
        frontRightDrive.setPower(fr);
        backLeftDrive.setPower(bl);
        backRightDrive.setPower(br);
    }
}
