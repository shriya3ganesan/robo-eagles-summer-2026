package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;



public class myConstants {
    @Config
    public static class intake {
        public static double intakeLIntakePos = 0.35;
        public static double intakeRIntakePos = 0.65;
        public static double intakeLoutakePos = 0;
        public static double intakeRoutakePos = 1;
    }
    @Config
    public static class Turret{
        public static double KpTurret = 0.03;
        public static double KiTurret = 0.002;
        public static double KdTurret =0;
        public static double Pflywheel = 24;
        public static double Fflywheel = 15;
        public static double Iflywheel=0.5;
        public static double  Dflywheel=0.5;
       public static double TURRET_MIN = -170;
        public static double TURRET_MAX =  170;
    }
    @Config
    public static class Spindex{
        public static double KpSpindex = 0.015;
        public static double KiSpindex = 0.002;
        public static double KdSpindex =0;

    }
}
