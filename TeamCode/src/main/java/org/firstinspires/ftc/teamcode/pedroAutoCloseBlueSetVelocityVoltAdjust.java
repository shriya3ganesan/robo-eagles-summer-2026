package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name="PedroAutoCloseBlueVoltageAdjust", group="Autonomous")
public class PedroAutoCloseBlueVoltageAdjust extends OpMode{
    private DcMotor launchMotor = null;
    private DcMotor transferMotor = null;
    private Servo trigger = null;
    private DcMotor intakeMotor = null;
    private Follower follower;
    private Timer pathTimer, opModeTimer, shootTimer;
    private int Target_RPM = 4000; // TUNING NEEDED
    private double launchTargetTicks = (Target_RPM * 383.6) / 60;

    // Servo positions (servos use 0.0 to 1.0)
    private double triggerStartPos = 0.158;
    private double triggerShootPos = 0.208;  // Adjust this value based on your mechanism

    private int shotsFired = 0;
    private boolean isShooting = false;

    public enum PathState{
        DRIVE_STARTPOS_SHOOTPOS,
        SHOOT_PRELOAD,
        DRIVE_SHOOTPOS_INTAKEONE,
        DRIVE_INTAKEONE_SHOOTPOS,
        SHOOT_SAMPLES_1,
        DRIVE_SHOOTPOS_INTAKETWO,
        DRIVE_INTAKETWO_SHOOTPOS,
        SHOOT_SAMPLES_2,
        DRIVE_SHOOTPOS_INTAKETHREE,
        DRIVE_INTAKETHREE_SHOOTPOS,
        SHOOT_SAMPLES_3,
        DONE
    }

    PathState pathState;
    private final Pose startPose = new Pose(20.77377049180328, 122.99016393442623, Math.toRadians(145));
    private final Pose shootPose = new Pose(58.780327868852446, 84.27540983606556, Math.toRadians(138));
    private final Pose intakeOne = new Pose(21, 84.4295081967213, Math.toRadians(185));
    private final Pose intakeTwo = new Pose(16, 59, Math.toRadians(185));
    private final Pose intakeThree = new Pose(16, 35, Math.toRadians(185));

    private PathChain driveStartShootClose, driveShootIntakeOne, driveIntakeOneShoot;
    private PathChain driveShootIntakeTwo, driveIntakeTwoShoot;
    private PathChain driveShootIntakeThree, driveIntakeThreeShoot;

    public void buildPaths(){
        // Initial paths
        driveStartShootClose = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();

        // Intake 1 paths
        driveShootIntakeOne = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, intakeOne))
                .setConstantHeadingInterpolation(intakeOne.getHeading())
                .setBrakingStart(0.6)
                .setBrakingStrength(0.4)
                .build();
        driveIntakeOneShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeOne, shootPose))
                .setLinearHeadingInterpolation(intakeOne.getHeading(), shootPose.getHeading(), 0.6)
                .build();

        // Intake 2 paths with control point
        Pose controlPointShootToIntake2 = new Pose(55.511475409836066, 54.80983606557377, Math.toRadians(185));
        driveShootIntakeTwo = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointShootToIntake2, intakeTwo))
                .setConstantHeadingInterpolation(intakeTwo.getHeading())
                .setBrakingStart(0.6)
                .setBrakingStrength(0.4)
                .build();
        driveIntakeTwoShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeTwo, shootPose))
                .setLinearHeadingInterpolation(intakeTwo.getHeading(), shootPose.getHeading(), 0.6)
                .build();

        // Intake 3 paths
        Pose controlPointShootToIntake3 = new Pose(60.94098360655738, 31.43934426229508, Math.toRadians(185));
        driveShootIntakeThree = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointShootToIntake3,intakeThree))
                .setConstantHeadingInterpolation(intakeThree.getHeading())
                .setBrakingStart(0.6)
                .setBrakingStrength(0.4)
                .build();
        driveIntakeThreeShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeThree, shootPose))
                .setLinearHeadingInterpolation(intakeThree.getHeading(), shootPose.getHeading(), 0.6)
                .build();
    }

    public void statePathUpdate(){
        switch(pathState){
            case DRIVE_STARTPOS_SHOOTPOS:
                if (!isShooting) {
                    follower.followPath(driveStartShootClose, true);
                    launchMotor.setVelocity(launchTargetTicks);  // Start flywheel early
                    isShooting = true;
                    trigger.setPosition(triggerStartPos);
                }
                if (!follower.isBusy()) {
                    setPathState(PathState.SHOOT_PRELOAD);
                    shotsFired = 0;
                }
                break;

            case SHOOT_PRELOAD:
                // Wait a moment for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < 2.5) {
                    launchMotor.setVelocity(launchTargetTicks);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 3) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 1
                    follower.followPath(driveShootIntakeOne, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    launchMotor.setPower(0);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKEONE);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKEONE:
                // Keep intake running while driving
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    transferMotor.setPower(-1);
                    follower.followPath(driveIntakeOneShoot, true);
                    setPathState(PathState.DRIVE_INTAKEONE_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKEONE_SHOOTPOS:
                if (!follower.isBusy()) {
                    launchMotor.setVelocity(launchTargetTicks);  // Spin up flywheel

                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_1);
                    shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES_1:
                // Wait for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < 2) {
                    intakeMotor.setPower(0);
                    launchMotor.setVelocity(launchTargetTicks);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 3) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 2
                    follower.followPath(driveShootIntakeTwo, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    launchMotor.setPower(0);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKETWO);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKETWO:
                // Keep intake running while driving
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    transferMotor.setPower(-1);
                    follower.followPath(driveIntakeTwoShoot, true);
                    setPathState(PathState.DRIVE_INTAKETWO_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKETWO_SHOOTPOS:
                if (!follower.isBusy()) {
                    launchMotor.setVelocity(launchTargetTicks);  // Spin up flywheel
                    intakeMotor.setPower(0);
                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_2);
                    shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES_2:
                // Wait for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < 2) {
                    intakeMotor.setPower(0);
                    launchMotor.setVelocity(launchTargetTicks);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 3) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 3
                    follower.followPath(driveShootIntakeThree, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    launchMotor.setPower(0);
                    telemetry.addLine("The line right after this is changing the cadse to shootpose to intake 3");
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKETHREE);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKETHREE:
                telemetry.addLine("I MADE IT LOOK AT ME WHY DID THE CODE STOP HERE");
                // Keep intake running while driving
                if (!follower.isBusy()) {

                    transferMotor.setPower(-1);
                    follower.followPath(driveIntakeThreeShoot, true);
                    setPathState(PathState.DRIVE_INTAKETHREE_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKETHREE_SHOOTPOS:
                if (!follower.isBusy()) {
                    launchMotor.setVelocity(launchTargetTicks);  // Spin up flywheel
                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_3);
                    shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES_3:
                // Wait for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < 0.5) {
                    intakeMotor.setPower(0);
                    launchMotor.setVelocity(launchTargetTicks);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 3) {
                    shootOneShot();
                } else {
                    // All samples collected and shot
                    launchMotor.setPower(0);
                    intakeMotor.setPower(0);
                    transferMotor.setPower(0);
                    setPathState(PathState.DONE);
                }
                break;

            case DONE:
                telemetry.addLine("Autonomous Complete!");
                break;

            default:
                telemetry.addLine("No State Set");
                break;
        }
    }

    private void shootOneShot() {
        double elapsed = pathTimer.getElapsedTimeSeconds();

        launchMotor.setVelocity(launchTargetTicks);
        // Each shot cycle takes ~2.3 seconds
        double cycleTime = elapsed % 2.3;
        if (cycleTime <= 1.3) {
            telemetry.addLine("Waiting");
        }
        else if (1.7 < cycleTime && cycleTime < 1.8 && shotsFired < 1) {
            transferMotor.setPower(1);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Firing");
        }
        else if (1.3 < cycleTime && cycleTime < 1.8) {
            // Move trigger to shoot position and run transfer

            transferMotor.setPower(1);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Firing");
        }
        else if (cycleTime < 2) {
            trigger.setPosition(triggerShootPos);
            transferMotor.setPower(0);
        }
        else if (cycleTime < 2.15) {
            trigger.setPosition(triggerStartPos);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Resetting");
        }
        else {
            // Wait before next shot
            if (elapsed > (shotsFired + 1) * 2.3) {
                shotsFired++;
            }
        }
    }

    public void setPathState(PathState newState){
        pathState = newState;
        pathTimer.resetTimer();
        isShooting = false;
    }

    @Override
    public void init(){
        pathState = PathState.DRIVE_STARTPOS_SHOOTPOS;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        shootTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);

        launchMotor = hardwareMap.get(DcMotor.class, "launch_motor");
        transferMotor = hardwareMap.get(DcMotor.class, "transfer");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        trigger = hardwareMap.get(Servo.class, "Trigger");

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        trigger.setDirection(Servo.Direction.FORWARD);
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        launchMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        launchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        trigger.setPosition(triggerStartPos);

        buildPaths();
        follower.setPose(startPose);
    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    @Override
    public void loop(){
        follower.update();
        statePathUpdate();
        telemetry.addData("path state", pathState.toString());
        telemetry.addData("shots fired", shotsFired);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("path time", pathTimer.getElapsedTimeSeconds());
        telemetry.update();
    }
}
