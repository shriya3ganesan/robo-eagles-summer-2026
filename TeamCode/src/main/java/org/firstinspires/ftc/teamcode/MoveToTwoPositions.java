package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RRSplineToLaunchPos.splineLaunchPos;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinfiringposition;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;
import static org.firstinspires.ftc.teamcode.limelight.LimelightMotifSetting.limelightMotifSet;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;

@Autonomous(name="MoveToTwoPositiongs")
@Config

public class MoveToTwoPositions extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    private DcMotorEx Scooper;
    private Servo DrumServo;
    private Servo FiringPinServo;
    private DcMotorEx LauncherFL;

    public static double xdistance = 12;
    public static double ydistance =12;

    @Override
    public void runOpMode() {
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        Scooper = hardwareMap.get(DcMotorEx.class, "Scooper");

        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        LauncherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");

        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");


        double[] firingpositions = {.1,.42,.76};
        double motortargetspeedradians = 0;
        Balls[] drumBallColors = {purple,green,purple};
        Balls[] targetballcolors = {green,purple,purple};

        double[] drumlocations = {.27,.6,.92};

        double targetdrumangle = .27;
        double targetfiringpinangle = 1;
        int targetdrumslot = 0;


        double firingpositionstarget;


        // y = x
        // x = -y
        Pose2d startPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(  hardwareMap,  startPose);

        waitForStart();////////////////////////////////////////////////////

        while(opModeIsActive()) {
            Action movetoloadingone = drive.actionBuilder(startPose)
                    .splineToConstantHeading(new Vector2d(xdistance, ydistance), Math.toRadians(0))
                    .build();
            Actions.runBlocking(movetoloadingone);
            startPose = drive.localizer.getPose();

            //splineLaunchPos(drive, startPose, 0);
            Action movetoloadingtwo = drive.actionBuilder(startPose)
                    .splineToConstantHeading(new Vector2d(0, 0), Math.toRadians(0))
                    .build();
            Actions.runBlocking(movetoloadingtwo);
            startPose = drive.localizer.getPose();
        }
    }
}
