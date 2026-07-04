package org.firstinspires.ftc.teamcode.Robot;

import org.firstinspires.ftc.teamcode.SWEEP.Builder.Path;

public abstract class SWEEPRobot extends Robot{
    private Path currentPath;
    double defaultActionRange = 6; // Default range for actions to be triggered
    double defaultActionTime = 0; // Default time for actions to be triggered
    @Override
    public void autonomousUpdate(){
        //Localization update
        //Other sweep logic
    }

    public void setPath(Path path){
        currentPath = path;
        path.start();
    }
    public void followPath(){

    }
}
