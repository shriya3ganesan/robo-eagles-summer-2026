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
import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@Autonomous(name="PedroAutoCloseRed", group="Autonomous")
public class pedroAutoCloseRed extends OpMode{
    private DcMotor launchMotor = null;
    private DcMotor transferMotor = null;
    private Servo trigger = null;
    private DcMotor intakeMotor = null;
    private Follower follower;
    private Timer pathTimer, opModeTimer, shootTimer;
    public static double launchPower = 0.75;

    // Servo positions (servos use 0.0 to 1.0)
    public static double triggerStartPos = 0.11;
    public static double triggerShootPos = 0.4;  // Adjust this value based on your mechanism

    private int shotsFired = 1;
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
    private final Pose startPose = new Pose(20.77, 122.99, Math.toRadians(145)).mirror();
    private final Pose shootPose = new Pose(58.78, 84.27, Math.toRadians(132)).mirror();
    private final Pose intakeOne = new Pose(21, 84.43, Math.toRadians(185)).mirror();
    private final Pose intakeTwo = new Pose(16.8, 59, Math.toRadians(185)).mirror();
    private final Pose intakeThree = new Pose(16.3, 35, Math.toRadians(185)).mirror();

    private PathChain driveStartShootClose, driveShootIntakeOne, driveIntakeOneShoot;
    private PathChain driveShootIntakeTwo, driveIntakeTwoShoot;
    private PathChain driveShootIntakeThree, driveIntakeThreeShoot;
    public static double spinUpTime = 0.2;

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
                .build();
        driveIntakeOneShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeOne, shootPose))
                .setLinearHeadingInterpolation(intakeOne.getHeading(), shootPose.getHeading(), 0.6)
                .build();

        // Intake 2 paths with control point
        Pose controlPointShootToIntake2 = new Pose(55.51, 54.81, Math.toRadians(185)).mirror();
        driveShootIntakeTwo = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointShootToIntake2, intakeTwo))
                .setConstantHeadingInterpolation(intakeTwo.getHeading())
                .build();
        driveIntakeTwoShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeTwo, shootPose))
                .setLinearHeadingInterpolation(intakeTwo.getHeading(), shootPose.getHeading(), 0.6)
                .build();

        // Intake 3 paths
        Pose controlPointShootToIntake3 = new Pose(90, 31.2, Math.toRadians(185)).mirror();
        driveShootIntakeThree = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointShootToIntake3,intakeThree))
                .setConstantHeadingInterpolation(intakeThree.getHeading())
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
                    launchMotor.setPower(launchPower);  // Start flywheel early
                    isShooting = true;
                    trigger.setPosition(triggerStartPos);
                }
                if (!follower.isBusy()) {
                    setPathState(PathState.SHOOT_PRELOAD);
                    shotsFired = 1;
                }
                break;

            case SHOOT_PRELOAD:
                // Wait a moment for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    launchMotor.setPower(launchPower);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 2) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 1
                    follower.followPath(driveShootIntakeOne, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    //launchMotor.setPower(0);
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
                    launchMotor.setPower(launchPower);  // Spin up flywheel

                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_1);
                    shotsFired = 1;
                }
                break;

            case SHOOT_SAMPLES_1:
                // Wait for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    intakeMotor.setPower(0);
                    launchMotor.setPower(launchPower);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 2) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 2
                    follower.followPath(driveShootIntakeTwo, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    //launchMotor.setPower(0);
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
                    launchMotor.setPower(launchPower);  // Spin up flywheel
                    intakeMotor.setPower(0);
                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_2);
                    shotsFired = 1;
                }
                break;

            case SHOOT_SAMPLES_2:
                // Wait for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    intakeMotor.setPower(0);
                    launchMotor.setPower(launchPower);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 2) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 3
                    follower.followPath(driveShootIntakeThree, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    //launchMotor.setPower(0);
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
                    launchMotor.setPower(launchPower);  // Spin up flywheel
                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_3);
                    shotsFired = 1;
                }
                break;

            case SHOOT_SAMPLES_3:
                // Wait for flywheel to spin up
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    intakeMotor.setPower(0);
                    launchMotor.setPower(launchPower);
                    break;
                }

                // Fire 3 shots
                if (shotsFired < 2) {
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
        // Check if we're done before starting any shot logic
        if (shotsFired >= 3) {
            return;
        }

        double elapsed = pathTimer.getElapsedTimeSeconds();
        launchMotor.setPower(launchPower);

        double cycleTime = elapsed % 1.3;
        if (cycleTime <= 0.4) {
            telemetry.addLine("Waiting");
        }
        else if (0.4 < cycleTime && cycleTime < 0.9) {
            transferMotor.setPower(1);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Firing");
        }
        else if (cycleTime < 1.1) {
            trigger.setPosition(triggerShootPos);
            transferMotor.setPower(0);
        }
        else if (cycleTime < 1.25) {
            trigger.setPosition(triggerStartPos);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Resetting");
        }
        else {
            if (elapsed > (shotsFired + 1) * 1.3) {
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
