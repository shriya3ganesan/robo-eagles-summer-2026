package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RRSplineToLaunchPos.splineLaunchPos;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.FIELD_HALF;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.mtoin;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinfiringposition;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;

import java.util.Arrays;

@Autonomous(name="FullRRtestautoTurning")
@Config

public class AutoWithRoadRunnerStrafe extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    private DcMotorEx Scooper;
    private Servo DrumServo;
    private Servo FiringPinServo;
    private DcMotorEx LauncherFL;
    public double firingangle = 142;

    public double preloadingy = 18;
    public double ballpickupy = 53.5;
    public double loadingprepxoffset = 6;
    public double loadonex = -18;
    public double loadtwox = 12;
    public double loadthreex = 30;

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
        Pose2d startPose = new Pose2d(-62, 37.5, 0);
        MecanumDrive drive = new MecanumDrive(  hardwareMap,  startPose);

        waitForStart();////////////////////////////////////////////////////

        FiringPinServo.setPosition(firingpinnullposition);
        motortargetspeedradians = 3.5;//autoLaunch();
        LauncherFL.setVelocity(-motortargetspeedradians, AngleUnit.RADIANS);
        DrumServo.setPosition(0.1);



        telemetry.addLine("moving to launch zone");
        telemetry.update();

        splineLaunchPos(drive,startPose,-170,1);
        startPose = drive.localizer.getPose();


        telemetry.addLine("moving to launch zone");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        //limelight get pattern

        targetballcolors = limelightMotifSet(limelight);
        // TODO add error handling
        telemetry.addData("motif",targetballcolors);

        splineLaunchPos(drive,startPose,firingangle,1);
        startPose = drive.localizer.getPose();

        telemetry.addLine("moving to first load");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();



        for(int i = 0; i <= 2; i++){
            for(int j = 0; j<= 2; j++) {
                if (drumBallColors[j] == targetballcolors[i]){
                    drumBallColors[j] = unknown;
                    targetdrumangle = firingpositions[j];
                    DrumServo.setPosition(targetdrumangle);
                    sleep(600);
                    FiringPinServo.setPosition(firingpinfiringposition);
                    sleep(400);
                    FiringPinServo.setPosition(firingpinnullposition);
                    sleep(400);
                }
            }

        }



        //launch with pattern

        Action movetoloadingone = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadonex + loadingprepxoffset,ballpickupy),Math.toRadians(0))
                .build();
        Actions.runBlocking(movetoloadingone);
        startPose = drive.localizer.getPose();


        telemetry.addLine("doing first load");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        //intake

        Action pickUpLoadOne = new ParallelAction(
                drive.actionBuilder(startPose)
                        .splineTo(new Vector2d(loadonex, ballpickupy), Math.toRadians(0))
                        .build(),
                new Action() {
                    @Override
                    public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                        int targetdrumslotload = 0;
                        double targetdrumangleload = 0.27;

                        Balls loadedcolor = colorDetection(colorSensor1, colorSensor2);;

                        Scooper.setVelocity(-999, AngleUnit.RADIANS);
                        if (loadedcolor != unknown && targetdrumslot < 3 && timer.milliseconds() > 600){
                            timer.reset();
                            drumBallColors[targetdrumslot] = loadedcolor;
                            telemetry.addLine("ball Detected");
                            targetdrumslotload++;


                        }
                        targetdrumslotload = Math.min(targetdrumslotload,2);
                        targetdrumangleload = drumlocations[targetdrumslotload];
                        return false;
                    }
                }
        );
        Actions.runBlocking(pickUpLoadOne);
        startPose = drive.localizer.getPose();

        telemetry.addLine("moving to launch zone");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        splineLaunchPos(drive,startPose,firingangle,1);
        startPose = drive.localizer.getPose();
        //luanch

        telemetry.addLine("moving to second load ");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        Action moveToLoadingTwo = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadtwox-loadingprepxoffset,preloadingy),Math.toRadians(0))
                .build();
        Actions.runBlocking(moveToLoadingTwo);
        startPose = drive.localizer.getPose();

        telemetry.addLine("second load");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();
        //intake

        Action pickUpLoadTwo = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadtwox,ballpickupy),Math.toRadians(0))
                .build();
        Actions.runBlocking(pickUpLoadTwo);
        startPose = drive.localizer.getPose();

        telemetry.addLine("moving to launch zone");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        splineLaunchPos(drive,startPose,firingangle,1);
        startPose = drive.localizer.getPose();

        telemetry.addLine("moving to load three ");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        //launch

        Action moveToLoadingThree = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadthreex-loadingprepxoffset,preloadingy),Math.toRadians(0))
                .build();
        Actions.runBlocking(moveToLoadingThree);
        startPose = drive.localizer.getPose();

        telemetry.addLine("doing third load");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();

        //intake

        Action pickUpLoadThree = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadthreex,ballpickupy),Math.toRadians(0))
                .build();
        Actions.runBlocking(pickUpLoadThree);
        startPose = drive.localizer.getPose();

        telemetry.addLine("moving to launch zone");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.addLine("end");
        telemetry.update();

        splineLaunchPos(drive,startPose,firingangle,1);
    }
}
