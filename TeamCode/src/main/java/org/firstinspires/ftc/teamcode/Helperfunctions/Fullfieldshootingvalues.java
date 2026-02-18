package org.firstinspires.ftc.teamcode.Helperfunctions;

import com.arcrobotics.ftclib.util.InterpLUT;

public class Fullfieldshootingvalues {
    InterpLUT Hoodlut = new InterpLUT();
    InterpLUT  Flywheellut = new InterpLUT();
    Boolean isBlue;
    double firstNumber;
    double lastNumber;

    public Fullfieldshootingvalues(String goalColor){
        isBlue = goalColor.equalsIgnoreCase("blue");


        //Hood Angle Data (Distance, Hood Angle)
        firstNumber = 30;
        Hoodlut.add(30,0.15);
        Hoodlut.add(36.5, 0.36);
        Hoodlut.add(42.5, 0.4);
        Hoodlut.add(48.5, 0.6);
        Hoodlut.add(54.5, 0.72);
        Hoodlut.add(60.5, 0.92);

        //FlywheelSpeed Data (Distance, FlywheelSpeed)
        Flywheellut.add(30,-1200);
        Flywheellut.add(36.5, -1200);
        Flywheellut.add(42.5, -1200);
        Flywheellut.add(48.5, -1200);
        Flywheellut.add(54.5, -1200);
        Flywheellut.add(60.5, -1200);
        lastNumber = 60.5;


        Hoodlut.createLUT();
        Flywheellut.createLUT();
    }

    public double flywheelspeedlut(double x, double y){
        double distance;
        if (isBlue==true){
             distance = Math.sqrt(x-8*x-8 +(136-y)*(136-y));
        }else{
            distance = Math.sqrt((131-x)*(131-x) +(137-y)*(137-y));
        }
        if (distance>lastNumber){
            return -1200;
        }else if(distance<firstNumber){
            return -1050;

        }else {
            return Flywheellut.get(distance);
        }


    }
    public double hoodanglelut(double x, double y){
        double distance;
        if (isBlue==true){
            distance = Math.sqrt(x*x +(144-y)*(144-y));
        }else{
            distance = Math.sqrt((144-x)*(144-x) +(144-y)*(144-y));
        }
        if (distance>lastNumber){
            return 1;
        }else if (distance<firstNumber){
            return 0;

        } else{
            return Hoodlut.get(distance);
        }

    }
    public double getDistance(double x, double y){
        if (isBlue==true){
             return Math.sqrt(x*x +(144-y)*(144-y));
        }else{
            return Math.sqrt((144-x)*(144-x) +(144-y)*(144-y));
        }

    }






}
