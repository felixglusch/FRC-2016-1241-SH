package com.team1241.frc2016.utilities;

import java.util.Arrays;

import com.team1241.frc2016.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ShooterTest extends Command {
	private static int[] staticRPM = new int[]{4000, 4100, 4200, 4500, 5000};
	private static double[] power = new double[staticRPM.length];
	
	private static double rpm = 0;
	private static double current = 0;
	private static int state = 0;
	private static Timer timer = new Timer();
	private static Timer target = new Timer();
	private static boolean atTarget = false;
	
	private LineRegression lR = new LineRegression();
	
	@Override
	protected void initialize() {
		rpm = 0.62;
		state = 0;
		atTarget = false;
		timer.start();
	}	

	@Override
	protected void execute() {
		rpm = Math.round(rpm*10000.0)/10000.0;
		current = Robot.shooter.getRPM();
		Robot.shooter.setSpeed(rpm);
		if(atTarget) {
			if(target.get()==0) {
				target.start();
				System.out.println("started");
			}
			
			current = Robot.shooter.getRPM();
			if(Math.abs(staticRPM[state]-current) >50) {
				atTarget = true;
			} else if(target.get()>0.4) {
				power[state] = rpm;
				state++;
				atTarget = false;
			}
		}
		else if (timer.get()>0.3) {
			target.stop();
			target.reset();
			System.out.println("Sensor:" + current + " power:" + rpm);
			
			current = Robot.shooter.getRPM();
			if(Math.abs(staticRPM[state]-current) <50) {
				atTarget = true;
			}
			else if(staticRPM[state]>current) {
				if(Math.abs(staticRPM[state]-current) >200)
					rpm = rpm+0.01;
				else
					rpm = rpm+0.0025;
			}
			else if(staticRPM[state]<current) {
				if(Math.abs(staticRPM[state]-current) >200)
					rpm = rpm+0.01;
				else
					rpm = rpm+0.0025;
			}
			timer.reset();
		}
	}

	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return state==staticRPM.length;
	}
	
	@Override
	protected void end() {
		// TODO Auto-generated method stub
		Robot.shooter.setSpeed(0);
		timer.stop();
		System.out.println(Arrays.toString(power));
		
		lR.setValues(staticRPM, power);
		System.out.println("Slope: " + lR.getSlope() + "intercept: " + lR.getIntercept());
	}
	
	@Override
	protected void interrupted() {
		// TODO Auto-generated method stub
		Robot.shooter.setSpeed(0);
		timer.stop();
		System.out.println(Arrays.toString(power));
	}

}
