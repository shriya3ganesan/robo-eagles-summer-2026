package org.firstinspires.ftc.teamcode.launcher;
import static org.firstinspires.ftc.teamcode.NonOpModes.PID.PIDOld.settingMotorPIDPowerYaw;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.robottranslationx;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.robottranslationy;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.robotyaw;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public abstract class AutoFirePower extends LinearOpMode {
    static ElapsedTime timer = new ElapsedTime();

    public static double firingpowermultiplierconst = 1.6;
    public static double firingpoweraddingconst = 1.7;
    public static void initializeLauncher(DcMotorEx LauncherFL,DcMotorEx LauncherFR) {




        //zeros the encoders and sets the run using encoder mode
        LauncherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LauncherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //LauncherFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //LauncherFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }



    public static double  autoLaunch(){
        double ShootTargetY;
        double ShootTargetX = -3.6576/2.1;
        if( !TeamColorRED) {
            ShootTargetY = -3.6576/2.1;
        } else{
            ShootTargetY = 3.6576/2.1;
        }
        double firingpower = (getFiringDistance(ShootTargetX,ShootTargetY) * firingpowermultiplierconst) + firingpoweraddingconst;
        
        /*double[] drumLocations = {0.2, 0.5, 0.8};// should probably make the drumb slots into objects
        int i = 0;
        for (double drumSlot: drumBallColors) {//slot finding loop
            if (drumSlot == TargetBallColor) {
                DrumServo.setPosition(drumLocations[i]);
                break;
            }
            if (i > 2) {
                return(0);
            }
            i++;
        }*/


        //if (firingpower < 6) firingpower += 0.15;
        return(firingpower);



    }
    public static double getFiringDistance(double targetx , double targety){
        double xdistance = Math.abs(targetx - robottranslationx);
        double ydistance = Math.abs(targety - robottranslationy);

        //pythangroniaun theorum to determine the distance
        return (Math.sqrt((xdistance * xdistance) + (ydistance * ydistance)));
    }
    public static double rotateToTarget(double targetx, double targety){
        double turnangle = Math.atan2(targety - robottranslationy, targetx - robottranslationx);
        while (turnangle!=robotyaw){
            double stopwatchtimer = timer.milliseconds();
            double turnoutputMotorPower = settingMotorPIDPowerYaw(stopwatchtimer);
            return(turnoutputMotorPower);

        }
        return 0;
    }
}
