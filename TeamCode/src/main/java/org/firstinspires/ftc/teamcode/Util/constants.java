package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.hardware.DcMotor;

/** (import static org.firstinspires.ftc.teamcode.Util.constants.*;)
 * import it by doing this and then you can just type something like
 * (EX: Drive.MAX_POWER)
 * or whatever you want to use
 * you could also do something like
 * (import static org.firstinspires.ftc.teamcode.Util.constants.motor.*;)
 * and then you can use any variable you need in the subset
 * (EX: MAX_POWER)
*/
public final class constants {
    /**
     * hardware map names
     */
    public static final class PART_NAMES{
        public static final String FL = "FrontLeft"; //despite what you may think these are constants
        public static final String FR = "FrontRight"; //yes I know names are constant it's crazy
        public static final String BL = "BackLeft";
        public static final String BR = "BackRight";
        public static final String DrumServo = "DrumServo";
        public static final String FiringPinServo = "FiringPinServo";
        public static final String odomhub = "odomhub";
        public static final String limelight = "limelight";

    }

    /**
     * general motor initialization steps and ranges
     */
    public static final class MOTOR{
        //remember to change these later to use actual numbers and explain them
        public static final double MAX_POWER = 0;
        public static final double MIN_POWER = 0;
        public static final DcMotor.Direction FL_DIR = DcMotor.Direction.FORWARD;
        public static final DcMotor.Direction BL_DIR = DcMotor.Direction.FORWARD;
        public static final DcMotor.Direction FR_DIR = DcMotor.Direction.REVERSE;
        public static final DcMotor.Direction BR_DIR = DcMotor.Direction.REVERSE;
    }

    /**
     * things that were constant in PID
     */
    public static final class PID{
        public static final double xproportionalConstant = 0;
        public static final double xintegralConstant = 0;
        public static final double xderivativeConstant = 0;
        public static final double yproportionalConstant = 0;
        public static final double yintegralConstant = 0;
        public static final double yderivativeConstant = 0;
        public static final double turnproportionalConstant = 0;
        public static final double turnintegralConstant = 0;
        public static final double turnderivativeConstant = 0;
    }

    /**
     * facts about field that do not change or should not
     * such as lengths
     */
    public static final class FIELD{
        public static final double FIELD_SIZE = 3.6576;
        public static final double FIELD_HALF = 3.6576/2;

        public static final double mtoin = 39.370787;
    }

    /**
     * conversions for measurements
     */
    public static final class Conversions{
        public static final double InToM = 25.4*1000;
    }
    /**
     * hardware stuff about the robot
     */
    public static final class RobotStats{
        public static final double launchAngle = 30; //find out what all of these actually are
        public static final double limelightYOffset = 0;
        public static final double limelightXOffset = 0;
        public static final double firingpinnullposition = .98;
        public static final double firingpinfiringposition = firingpinnullposition-.32;
        public static final double WheelRadius = 0.03730625; //meters
        public static final double ShaftRadius = 0.008; //meters
        public static final double MaxRPM = 117.0; //confirm later but this is what website said
        public static final double MinRPM = 0;
        public static final double ApriltagOffsetX = 0.1524; // meters
        public static final double ApriltagOffsetY = 0.2032; // meters
        public static final double ApriltagOffsetZ = 0.2794; // meters 38.5-29.5=11in
        public static final double ENCODER_TICKS_PER_OUTPUT_REV = 1425.1; // Encoder resolution obtained from website
        public static final double GEARBOX_RATIO = 50.9; // guess
        public static final double MAX_SHOOTING_RANGE = 5.173; //max distance it has to shoot diagonal of field
        public static final double LAUNCH_HEIGHT = 0.3175; //meters

    }
    public static final class Physics{
        public static final double GRAVITY = 9.81; // m/s² (or 32.2 ft/s²);
        public static final double EPSILON = 1e-9; // For floating-point comparisons
    }
}
