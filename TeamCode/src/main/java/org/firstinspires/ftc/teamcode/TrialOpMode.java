package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
import static org.firstinspires.ftc.teamcode.limelight.LimelightPosSetting.limelightposupdate;

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
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;



public class TrialOpMode extends LinearOpMode {

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

        Balls targetballcolor = unknown;

        double[] firingpositions = {.76,.1,.42};

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
        targetfiringpinangle = firingpinnullposition;
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

            if(gamepad1.right_trigger >= .5){
                rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }else{
                rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }


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
            /*
            targetdrumangle =
                                gamepad1.x ? .27 ://loading angles
                                gamepad1.y ? .6 :
                                gamepad1.b ? .92 :
                                    targetdrumangle;
             */
            if(gamepad1.x || gamepad1.a){
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
            } else if (gamepad1.y) {
                targetdrumangle = 0.6;
                targetdrumslot = 1;
                int iloadedballarray =1;
                for (Balls loadedball : drumBallColors){
                    if (iloadedballarray >2) {
                        break;
                    }
                    drumBallColors[iloadedballarray] = unknown;
                    iloadedballarray++;
                }
                iloadedballarray = 0;
            } else if (gamepad1.b) {
                targetdrumangle = 0.9;
                targetdrumslot = 2;
                int iloadedballarray = 2;
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
                drumBallColors[firingpositionstarget] = unknown;

            } else if(gamepad2.x || gamepad2.b) {


                targetballcolor = gamepad2.x ? green:
                                gamepad2.b ? purple:
                                targetballcolor;


                for(int i = 0; i <= 2; i++){
                    if(drumBallColors[i] == targetballcolor){
                        firingpositionstarget = i;
                        targetdrumangle = firingpositions[i];
                    }

                }
                /*
                for(Balls loadedcolor : drumBallColors){
                    if (loadedcolor == targetballcolor){
                        firingpositionstarget = iballselection;
                        if (firingpositionstarget > 2){
                            firingpositionstarget -= 3;
                        }
                        targetdrumangle = firingpositions[firingpositionstarget];
                    }
                    iballselection++;
                }*/




            }else{
                targetfiringpinangle = firingpinnullposition;
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
            }


            telemetry.addData("firingslot",firingpositionstarget);
            telemetry.addData("targetcolor",targetballcolor);
            telemetry.addData("loaded balls 0",drumBallColors[0].name());
            telemetry.addData("loaded balls 1",drumBallColors[1].name());
            telemetry.addData("loaded balls 2",drumBallColors[2].name());
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

            pinpoint.update();

            telemetry.addData("firing target loaded", firingpositionstarget);
            //telemetry.addLine("All Speeds are in Jacks Per Second");
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

