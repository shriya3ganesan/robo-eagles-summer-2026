package org.firstinspires.ftc.teamcode;
import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.AllSlots;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_0;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_1;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_2;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.FiringPinIn;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.FiringPinOut;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.None;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.TurnToBall;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinfiringposition;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;
import static org.firstinspires.ftc.teamcode.Util.constants.PART_NAMES.drumslotarray;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
import static org.firstinspires.ftc.teamcode.limelight.LimelightMotifSetting.limelightMotifSet;
import static org.firstinspires.ftc.teamcode.limelight.LimelightPosSetting.limelightposupdate;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots;
import org.firstinspires.ftc.teamcode.Util.Enum.States;
import org.firstinspires.ftc.teamcode.Util.RobotPosition;
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;

import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_0;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_1;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_2;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.None;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots;
import org.firstinspires.ftc.teamcode.Util.Enum.States;
@TeleOp(name="zogning")
public class judgeOp extends LinearOpMode{
    ElapsedTime timer = new ElapsedTime();
    ElapsedTime rapidtime = new ElapsedTime();

    protected boolean isred;

    States currentstate = None;

    @Override
    public void runOpMode() {

        SLOT_0.setLoadedBall(unknown);
        SLOT_1.setLoadedBall(unknown);
        SLOT_2.setLoadedBall(unknown);

        TeamColorRED = isred;

        int motifcyclingautofirearray = 0;

        Balls[] motif = {purple, green, purple};
        double[] firingpositions = {.76, .1, .42};

        DrumSlots targetslotforautolaunch = null;

        boolean autoAimLast = false;

        double[] drumlocations = {.27, .6, .92};
        double targetdrumangle = .27;
        double targetfiringpinangle = firingpinnullposition;
        int targetdrumslot = 0;

        boolean fullunloadflag = false;

        double motortargetspeedradians;
        double currentleftmotorvelocity;

        int firingpositionstarget = 0;

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        Servo drumServo = hardwareMap.get(Servo.class, "DrumServo");
        Servo firingPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftFront.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        leftBack.setDirection(DcMotor.Direction.REVERSE); //inverting later
        rightFront.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        DcMotorEx launcherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        DcMotorEx scooper = hardwareMap.get(DcMotorEx.class, "Scooper");


        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");


        //zeros the encoders and sets the run using encoder mode
        //VariablePowerLauncherAbstract.initializeLauncher(LauncherFL,LauncherFR);
        launcherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.initialize();
        pinpoint.resetPosAndIMU();   // resets encoders and IMU


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Pose2d startPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while (opModeIsActive()) {

            if(gamepad1.a){
                scooper.setVelocity(-999,AngleUnit.RADIANS);
                drumServo.setPosition(SLOT_0.loadPosition);
                sleep(800);
                drumServo.setPosition(SLOT_1.loadPosition);
                sleep(800);
                drumServo.setPosition(SLOT_2.loadPosition);
                sleep(800);
                scooper.setVelocity(0);
            }
            if(gamepad1.b){
                drumServo.setPosition(SLOT_0.shootPosition);
                sleep(600);
                firingPinServo.setPosition(firingpinfiringposition);
                sleep(200);
                firingPinServo.setPosition(firingpinnullposition);
                sleep(200);
                drumServo.setPosition(SLOT_1.shootPosition);
                sleep(600);
                firingPinServo.setPosition(firingpinfiringposition);
                sleep(200);
                firingPinServo.setPosition(firingpinnullposition);
                sleep(200);
                drumServo.setPosition(SLOT_2.shootPosition);
                sleep(600);
                firingPinServo.setPosition(firingpinfiringposition);
                sleep(200);
                firingPinServo.setPosition(firingpinnullposition);
                sleep(200);
            }
        }
    }
}
