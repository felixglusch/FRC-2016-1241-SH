package com.team1241.frc2016.commands.defence;

import com.team1241.frc2016.NumberConstants;
import com.team1241.frc2016.commands.auto.*;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoRockWall extends CommandGroup {
    
    public  AutoRockWall() {
        //Drive to defense
//    	addParallel(new ContinousMotion(0.6, 40, 1.5));
    	addParallel(new ContinousMotion(1.0, 40, 1.5));
    	addSequential(new RunArm(NumberConstants.downArmAngle+150, 0.6, 1.5));
    	
    	//Drives over the defense
    	addParallel(new RunArm(NumberConstants.downArmAngle+25, 1, 2.75));
    	addSequential(new ContinousMotion(1.0, 65, 2.75));
    	addSequential(new DriveCommand(15, 1, 0, 0.75));
    }
}
