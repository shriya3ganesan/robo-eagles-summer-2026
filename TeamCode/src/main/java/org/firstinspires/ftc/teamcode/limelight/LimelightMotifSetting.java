package org.firstinspires.ftc.teamcode.limelight;

import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.modifyRobotCoordinates;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;

import java.util.List;

public class LimelightMotifSetting {
    public static Balls[] limelightMotifSet(Limelight3A limelight){
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) { // checks if there is a target and if the target is an actual target

            List<LLResultTypes.FiducialResult> tags = result.getFiducialResults(); //get fiducial results basically just tells how many april tags it sees
            //List<LLResultTypes.FiducialResult>: so it makes a list at the size of the # of tags detected and has info on the id and position of the tag

            for (LLResultTypes.FiducialResult tag : tags) {
                int id = tag.getFiducialId();
                switch(id) {
                    case 21:
                        return(new Balls[]{green,purple,purple});

                    case 22:
                        return(new Balls[]{purple,green,purple});


                    case 23:
                        return(new Balls[]{purple,purple,green});

                }
            }
        }
        return (new Balls[]{unknown,unknown,unknown});
    }
}
