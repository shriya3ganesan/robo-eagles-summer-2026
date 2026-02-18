package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.teamcode.Helperfunctions.Fullfieldshootingvalues;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Spindex;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.otos;

@Config
@TeleOp(name="Far Blue Drive")
public class farBlueTeleop extends OpMode {

    private Fullfieldshootingvalues shootingvalues;

    private Intake intake;
    private Spindex spindex;
    SparkFunOTOS otos;
    private Turret turret;

    private Drivetrain drive;

    private Follower follower;
    public static Pose startingPose = new Pose(56,8,Math.toRadians(90));
    public static Pose resetPose = new Pose(20.34,123.37,Math.toRadians(144));

    private Supplier<PathChain> pathChain;
    private TelemetryManager telemetryM;



    @Override
    public void init(){
        intake = new Intake(hardwareMap);
        spindex = new Spindex (hardwareMap);
        turret = new Turret(hardwareMap, "blue",90);
        drive = new Drivetrain (hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();


        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        shootingvalues = new Fullfieldshootingvalues("blue");




    }
    public void start(){
        follower.startTeleopDrive();
        follower.update();
    }
    public void loop(){

        follower.update();

        double distanceBlue = Math.sqrt(follower.getPose().getX()*follower.getPose().getX() + (144-follower.getPose().getY())*(144-follower.getPose().getY()));


        drive.driveCA(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x, gamepad1.left_trigger, gamepad1.right_trigger);

        turret.aimTurretOriginal(follower.getPose().getX(), follower.getPose().getY(), Math.toDegrees(follower.getPose().getHeading()));

        if (gamepad1.left_trigger>0.25) {
            intake.intakeBalls();
        }
        if (gamepad1.right_trigger>0.25) {
            intake.shootBalls();
        }
        if (gamepad1.left_bumper){
            intake.reverseIntakeDirection();
        }else{
            intake.forwardIntakeDirection();
        }

        if (gamepad2.a) {
            turret.enableTurret();
        }
        if (gamepad2.b){
            turret.disableTurret();
        }
        telemetry.addData("Turret on?", turret.getTurretOn());
        telemetry.addData("Driver 2 controls: square for spindex, left bumper to reset pose, a to toggle turret ", false);

        if (gamepad2.left_trigger>0.25) {
            spindex.setSpindexPower(0.7);
        } else if (gamepad2.right_trigger>0.25){
            spindex.setSpindexPower(-0.7);
        }  else{
            spindex.setSpindexPower(0);
        }

        if (gamepad2.dpad_up){
            turret.setFlyWheelSpeed(-1050);
            turret.setHoodAngle(0.36);
        }else if (gamepad2.dpad_down){
            turret.setFlyWheelSpeed(-1200);
            turret.setHoodAngle(0.92);
        }else{
            turret.setHoodAngle(shootingvalues.hoodanglelut(follower.getPose().getX(), follower.getPose().getY()));
            turret.setFlyWheelSpeed(shootingvalues.flywheelspeedlut(follower.getPose().getX(), follower.getPose().getY()));
        }

        double flyWheelPosition =shootingvalues.flywheelspeedlut(follower.getPose().getX(), follower.getPose().getY());
                telemetry.addData("fly wheel ideal speed", flyWheelPosition);

        double hoodAnglePosition = shootingvalues.hoodanglelut(follower.getPose().getX(), follower.getPose().getY());

        turret.setFlyWheelSpeed(flyWheelPosition);
        turret.setHoodAngle(hoodAnglePosition);
        if (gamepad2.left_bumper) {
            follower.setPose(resetPose);
        }

        turret.updateFlywheelCoefficents();




        telemetry.addData("Fly wheel Speed", turret.getFlyWheelSpeed());





        telemetry.addData("Hood Angle", turret.getHoodAngle());

        telemetry.update();






        // turret.aimTurret(otos.getX(),otos.getY(),otos.getH());




    }
}

