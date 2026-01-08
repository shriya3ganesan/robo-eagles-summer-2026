package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.modifyRobotCoordinates;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.shoottargetx;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.shoottargetyblue;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.shoottargetyred;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
import static org.firstinspires.ftc.teamcode.limelight.LimelightPosSetting.limelightposupdate;
import static java.lang.Math.atan2;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.Util.RobotPosition;
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;


public class BaseOpModeAutoAimCrosby extends LinearOpMode {

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime rapidtime = new ElapsedTime();
    private Servo DrumServo;
    private Servo FiringPinServo;
    private GoBildaPinpointDriver pinpoint;
    private DcMotorEx Scooper;
    private DcMotor rightBack;
    private DcMotor leftBack;
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotorEx LauncherFL;

    protected boolean isred;

    @Override
    public void runOpMode() {
        TeamColorRED = isred;

        double[] firingpositions = {.1,.42,.76};

        double odomoffset;

        boolean autoAimLast = false;

        double[] drumlocations = {.27,.6,.92};
        Balls[] drumBallColors = {unknown, unknown, unknown};
        double targetdrumangle = .27;
        double targetfiringpinangle = 1;
        int targetdrumslot = 0;

        boolean fullunloadflag = false;
        double lasttime = timer.milliseconds();

        double[] drumBallColorsarray = {0, 0, 0};
        boolean firing = false;

        double motortargetspeedradians = 0;
        double currentleftmotorvelocity = 0;
        double currentrightmotorvelocity = 0;

        double firingpinnullposition = .98;

        double rapidloop = 0;

        int firingpositionstarget =0;

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");


        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftFront.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        leftBack.setDirection(DcMotor.Direction.REVERSE); //inverting later
        rightFront.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        LauncherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        Scooper = hardwareMap.get(DcMotorEx.class, "Scooper");


        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");


        //zeros the encoders and sets the run using encoder mode
        //VariablePowerLauncherAbstract.initializeLauncher(LauncherFL,LauncherFR);
        LauncherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LauncherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.initialize();
        pinpoint.resetPosAndIMU();   // resets encoders and IMU




        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            double leftstickinputy = gamepad1.left_stick_y; // Forward/backward negative because it's naturally inverted
            double leftstickinputx = gamepad1.left_stick_x; // side to side
            double targetturn = gamepad1.right_stick_x; // Turning

            //slowermovement for the guner
            double leftstickinputy2 = gamepad2.left_stick_y / 6;
            double leftstickinputx2 = gamepad2.left_stick_x / 6;
            double targetturn2 = gamepad2.right_stick_x / 4;

            double[] currentrobotlocation = getRobotCoordinates();

            double currentrelativeheading = pinpoint.getHeading(AngleUnit.RADIANS);



            //Calls FieldOrientedDriving function and sets motor power
            double[] motorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy, -leftstickinputx, targetturn, currentrelativeheading);
            double[] smallmotorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy2, -leftstickinputx2, targetturn2, currentrelativeheading);

            double BRmotorpower = motorpowerarray[0] + smallmotorpowerarray[0];
            double BLmotorpower = motorpowerarray[1] + smallmotorpowerarray[1];
            double FRmotorpower = motorpowerarray[2] + smallmotorpowerarray[2];
            double FLmotorpower = motorpowerarray[3] + smallmotorpowerarray[3];


            //assigns power to each motor based on gamepad inputs
            rightBack.setPower(BRmotorpower);
            leftBack.setPower(BLmotorpower);
            rightFront.setPower(FRmotorpower);
            leftFront.setPower(FLmotorpower);


            //limelight
            limelightposupdate(limelight);

            //auto rangeing commands
            motortargetspeedradians = autoLaunch();
            if (gamepad2.left_trigger >= 0.3) {
                motortargetspeedradians = 0;
            }
            // sets the velocity of the motors
            LauncherFL.setVelocity(-motortargetspeedradians, AngleUnit.RADIANS);
            currentleftmotorvelocity = LauncherFL.getVelocity(AngleUnit.RADIANS);
            if( Math.abs(currentleftmotorvelocity +motortargetspeedradians) < .02){
                gamepad2.rumble( .75,.75,50);
            }
            if(gamepad1.a){
                targetdrumangle = 0.27;
                targetdrumslot = 0;
                int iloadedballarray =0;
                for (Balls loadedball : drumBallColors){
                    if (iloadedballarray >2) {
                        break;
                    }
                    drumBallColors[iloadedballarray] = unknown;
                    iloadedballarray++;
                }
                iloadedballarray = 0;
            }

            if (gamepad2.a) {//firing bin controls
                targetfiringpinangle = firingpinnullposition - .32  ;

            } else {
                targetfiringpinangle = firingpinnullposition;

                firingpositionstarget = gamepad2.x ? 0:
                                 gamepad2.y ? 1:
                                 gamepad2.b ? 2:
                                         firingpositionstarget;


                /*
                targetdrumangle = gamepad2.x ? .1 ://Firing angles
                                gamepad2.y ? .42 :
                                gamepad2.b ? .76 :
                                gamepad1.x ? .27 ://loading angles
                                gamepad1.y ? .6 :
                                gamepad1.b ? .92 :
                                    targetdrumangle;*/
            }

            Balls loadedcolor = colorDetection(colorSensor1, colorSensor2);
            if (gamepad1.left_bumper){
                Scooper.setVelocity(999, AngleUnit.RADIANS);
            }
            else if (gamepad1.right_bumper){
                Scooper.setVelocity(-999, AngleUnit.RADIANS);
                if (loadedcolor != unknown && targetdrumslot < 3 && timer.milliseconds() > 600){
                    timer.reset();
                    drumBallColors[targetdrumslot] = loadedcolor;
                    telemetry.addLine("ball Detected");
                    targetdrumslot++;


                }
            }
            else Scooper.setVelocity(0, AngleUnit.RADIANS);



            //clamps the target to 3

            if(gamepad1.right_bumper){
                targetdrumslot = Math.min(targetdrumslot,2);
                targetdrumangle = drumlocations[targetdrumslot];
            }else if (gamepad2.x || gamepad2.y || gamepad2.b){
                firingpositionstarget = Math.min(firingpositionstarget,2);
                targetdrumangle = firingpositions[firingpositionstarget];
            }



            telemetry.addData("loaded balls",drumBallColors[0].name());
            telemetry.addData("loaded balls",drumBallColors[1].name());
            telemetry.addData("loaded balls",drumBallColors[2].name());
            telemetry.addData("selected slot",targetdrumslot);

            //MAG Dump code
            //test time offsets
            if (gamepad2.dpad_up) {//use timesrs use cancle when not held
                rapidtime.reset();
                fullunloadflag = true;
            }
            rapidloop =  rapidtime.milliseconds();
            if (fullunloadflag) {
                // state machine this for next comp
                if (rapidloop < 500) DrumServo.setPosition(firingpositions[0]);// 500 ms for drumb
                if (rapidloop > 500 && rapidloop < 700) FiringPinServo.setPosition(.98 - .32);// 200ms for firing in
                if (rapidloop > 700 && rapidloop < 900) FiringPinServo.setPosition(.98);

                if (rapidloop > 900 && rapidloop < 1400) DrumServo.setPosition(firingpositions[0]);
                if (rapidloop > 1400 && rapidloop < 1600) FiringPinServo.setPosition(.98 - .32);
                if (rapidloop > 1600 && rapidloop < 1800) FiringPinServo.setPosition(.98);

                if (rapidloop > 1800 && rapidloop < 2300) DrumServo.setPosition(firingpositions[0]);
                if (rapidloop > 2500 && rapidloop < 2700) FiringPinServo.setPosition(.98 - .32);
                if (rapidloop > 2700 && rapidloop < 2900) FiringPinServo.setPosition(.98);

            }

            DrumServo.setPosition(targetdrumangle);
            telemetry.addData("drumangle", targetdrumangle);
            FiringPinServo.setPosition(targetfiringpinangle);

            if (gamepad1.dpad_down) pinpoint.resetPosAndIMU();   // resets encoders and IMU

            //wihle holding down the bumpers moves the intake
            if (gamepad1.left_bumper) Scooper.setVelocity(999, AngleUnit.RADIANS);
            else if (gamepad1.right_bumper) Scooper.setVelocity(-999, AngleUnit.RADIANS);
            else Scooper.setVelocity(0, AngleUnit.RADIANS);


            boolean autoAimPressed = gamepad2.right_stick_button && !autoAimLast;
            autoAimLast = gamepad2.right_stick_button;

            if (autoAimPressed){

                double[] robotcoordinates = RobotPosition.getRobotCoordinates();
                double angleinradians = (robotcoordinates[5] * 3.1415926535)/180;
                Pose2d startPose = new Pose2d(robotcoordinates[0],robotcoordinates[1],angleinradians);

                MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
                double arctanintermediatex = shoottargetx-robotcoordinates[0];
                double arctanintermediatey;
                double usedy;

                if (TeamColorRED) usedy = shoottargetyred;
                else usedy = shoottargetyblue;
                arctanintermediatey = usedy - robotcoordinates[1];


                double robotautoaimtargetangle = atan2(arctanintermediatey, arctanintermediatex);

                telemetry.addData("robat aurot aim target angle", robotautoaimtargetangle);
                telemetry.update();

                //setrobot angle to robot auto aim tareet
                while (Math.abs(robotcoordinates[5] - robotautoaimtargetangle) > .03) {
                    Action rotatetotargetangle = drive.actionBuilder(startPose)
                            .turnTo(robotautoaimtargetangle)
                            .build();
                    Actions.runBlocking(rotatetotargetangle);
                    startPose = drive.localizer.getPose();
                    modifyRobotCoordinates(robotcoordinates[0],robotcoordinates[1],robotcoordinates[2],robotcoordinates[3],robotcoordinates[4],atan2(startPose.heading.imag, startPose.heading.real));
                    robotcoordinates = RobotPosition.getRobotCoordinates();
                }



            }


            pinpoint.update();

            telemetry.addLine("All Speeds are in Jacks Per Second");
            telemetry.addData("Motors' Target Rate of Rotation ", motortargetspeedradians);
            telemetry.addData("Left Motor Actual Rate of Rotation", currentleftmotorvelocity);
            /*//telemetry.addData("Right Motor Actual Rate of Rotation", currentrightmotorvelocity);
            //telemetry.addData("rightmotorraw", rawrightmotorvelocity);
            telemetry.addData("Left Motor difference in Rate of Rotation", motortargetspeedradians - currentleftmotorvelocity);
            //telemetry.addData("Right Motor difference in Rate of Rotation", motortargetspeedradians+currentrightmotorvelocity);
            //telemetry.addData("Left Motor Speed at Wheel Surface meters per second",currentleftmotorvelocity*launcherwheelradiusm);
            //telemetry.addData("Right Motor Speed at Wheel Surface meters per second",currentrightmotorvelocity*launcherwheelradiusm);
            telemetry.addData("drim target servoangle", targetdrumangle);
            telemetry.addData("firingpin target servoangle", targetfiringpinangle);
            telemetry.addData("rotation perceived", currentrelativeheading);
            telemetry.addData("robotx", currentrobotlocation[0]);
            telemetry.addData("roboty", currentrobotlocation[1]);*/
            telemetry.update();
            }
        }
    }

