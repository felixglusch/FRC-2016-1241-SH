package com.team1241.frc2016;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import com.team1241.frc2016.commands.CameraTrack;
import com.team1241.frc2016.commands.ConveyorCommand;
import com.team1241.frc2016.commands.LiveTrack;
import com.team1241.frc2016.commands.SetShooterPower;
import com.team1241.frc2016.commands.SetShooterSpeed;
import com.team1241.frc2016.commands.ShootCommand;
import com.team1241.frc2016.commands.Test;
import com.team1241.frc2016.commands.TurnTurret;
import com.team1241.frc2016.commands.auto.*;
import com.team1241.frc2016.commands.defence.*;
import com.team1241.frc2016.pid.Constants;
import com.team1241.frc2016.subsystems.*;
import com.team1241.frc2016.utilities.DataOutput;
import com.team1241.frc2016.utilities.ShooterTest;
import com.team1241.frc2016.utilities.ToggleBoolean;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public static PowerDistributionPanel pdp;
	
	//Subsystems
	public static Drivetrain drive;
	public static Shooter shooter;
	public static Intake intake;
	public static Conveyor conveyor;
	
	public static Constants constants;
	public static OI oi;
//	public static DataOutput output;
	
	Preferences pref;
	double kp;
	double ki;
	double kd;
	double power;
	public static double outerRPM;
	public static double spyRPM;
	
    Command autonomousCommand;
    public SendableChooser autoChooser;
    public SendableChooser defenceChooser;
    public SendableChooser locationChooser;
    public SendableChooser endLocationChooser;
//    public SendableChooser backLocationChooser;
//    public SendableChooser backDefenceChooser;
    
    public static int defenceLocation;
    public static int selectedDefence;
    public static int autoNumber;
    public static int endLocation;
//    public static int backLocation;
//    public static int backDefence;
    
    CameraServer server;
    
    public static boolean connected = false;
    public static double runTime = 0;
    public static int timer = 0;
    
    ToggleBoolean toggle = new ToggleBoolean();
    ShooterTest autoTune = new ShooterTest();
//    LiveTrack liveTrack = new LiveTrack();
    CameraTrack liveTrack = new CameraTrack(-1);
    SetShooterPower fullPower = new SetShooterPower(0);
    
    public static DigitalOutput fFlash = new DigitalOutput(6);
    public static DigitalOutput sFlash = new DigitalOutput(7);
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	pdp = new PowerDistributionPanel();
    	
		oi = new OI();
//		constants = new Constants("pidValues.txt");
//		output = new DataOutput("data.txt");
		pref = Preferences.getInstance();
		
		drive = new Drivetrain();
		shooter = new Shooter();
		intake = new Intake();
		conveyor = new Conveyor();
		
		// Camera Server
//		server = CameraServer.getInstance();
//        server.setQuality(50);
//        server.startAutomaticCapture("cam0");
		
		
        // instantiate the command used for the autonomous period
		
		defenceChooser = new SendableChooser();
		defenceChooser.addObject("Portcullis", NumberConstants.PORTCULLIS);
		defenceChooser.addObject("Cheval de Frise", NumberConstants.CHEVAL);
		defenceChooser.addObject("SallyPort", NumberConstants.SALLYPORT);
		defenceChooser.addObject("DrawBridge", NumberConstants.DRAWBRIDGE);
		defenceChooser.addDefault("Rock Wall", NumberConstants.ROCKWALL);
		defenceChooser.addDefault("Rough Terrain", NumberConstants.ROUGHTERRAIN);
		
		SmartDashboard.putData("Defence Mode", defenceChooser);
		
		locationChooser = new SendableChooser();
		
		locationChooser.addObject("2", 1);
		locationChooser.addDefault("3", 2);
		locationChooser.addObject("4", 3);
		locationChooser.addObject("5", 4);
		
		SmartDashboard.putData("Location", locationChooser);
		
		endLocationChooser = new SendableChooser();
		
		endLocationChooser.addDefault("Default", NumberConstants.DEFAULT);
		endLocationChooser.addObject("Left", NumberConstants.LEFT);
		endLocationChooser.addObject("Center", NumberConstants.CENTER);
		endLocationChooser.addObject("Right", NumberConstants.RIGHT);
		
		SmartDashboard.putData("End Location", endLocationChooser);
		
//		backLocationChooser = new SendableChooser();
//		
//		backLocationChooser.addDefault("None", 0);
//		backLocationChooser.addObject("2", 1);
//		backLocationChooser.addObject("3", 2);
//		backLocationChooser.addObject("4", 3);
//		backLocationChooser.addObject("5", 4);
//		
//		SmartDashboard.putData("Back Location", backLocationChooser);
//
//		backDefenceChooser = new SendableChooser();
//		
//		backDefenceChooser.addDefault("None", 0);
//		backDefenceChooser.addObject("SallyPort", NumberConstants.SALLYPORT);
//		backDefenceChooser.addObject("DrawBridge", NumberConstants.DRAWBRIDGE);
//		backDefenceChooser.addObject("Rock Wall", NumberConstants.ROCKWALL);
//		backDefenceChooser.addObject("Rough Terrain", NumberConstants.ROUGHTERRAIN);
//		
//		SmartDashboard.putData("Back Defence", backDefenceChooser);
		
		autoChooser = new SendableChooser();
		autoChooser.addDefault("No Auto", 0);
		autoChooser.addObject("SpyShot", 1);
		autoChooser.addObject("Breach", 2);
		autoChooser.addDefault("Breach&Shoot", 3);
		autoChooser.addObject("Two Ball", 4);
		
		SmartDashboard.putData("Autonomous", autoChooser);
		
		updateSmartDashboard();
    }
     
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		updateSmartDashboard();
		
		fFlash.set(false);
		sFlash.set(false);
		
		defenceLocation = (int) locationChooser.getSelected();
    	selectedDefence = (int) defenceChooser.getSelected();
    	endLocation = (int) endLocationChooser.getSelected();
//    	backLocation = (int) backLocationChooser.getSelected();
//    	backDefence = (int) backDefenceChooser.getSelected();
    	autoNumber = (int) autoChooser.getSelected();
    	
    	switch(autoNumber) {
    	case 0:
    		autonomousCommand = (Command) new NoAuto();
    		break;
    	case 1:
    		autonomousCommand = (Command) new SpyShotAuton();
    		break;
    	case 2:
    		autonomousCommand = (Command) new BreachAuton(selectedDefence);
    		break;
    	case 3:
    		autonomousCommand = (Command) new BreachShootAuton(defenceLocation, selectedDefence, endLocation);
    		break;
    	case 4:
    		autonomousCommand = (Command) new TwoBallAuton(selectedDefence);
    		break;
    	}
	}

	
    public void autonomousInit() {
        // schedule the autonomous command (example)
    	conveyor.extendHolder();
    	drive.reset();
    	shooter.reset();
    	resetColor();
    	
    	autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        updateSmartDashboard();
    }
    
    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
    	if(autonomousCommand!=null)
    		autonomousCommand.cancel();
    	
    	shooter.retractPop();
    	conveyor.retractHolder();
    	conveyor.setContains(false);
    	drive.reset();
    	resetColor();
    	
    	new ConveyorCommand().start();
    	
    	kp = pref.getDouble("kp", 0.0);
    	ki = pref.getDouble("ki", 0.0);
    	kd = pref.getDouble("kd", 0.0);
    	
    	Robot.shooter.cameraPID.changePIDGains(kp, ki, kd);
    	
//    	power = pref.getDouble("power", 0.0);
    	
//    	new CameraTrack(-1).start();
//    	new ShooterTest().start();
    	
    }
    

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    public void disabledInit(){
    }

    /**
     * This function is called periodically during operator control
     */
//    DoubleSolenoid a = new DoubleSolenoid(12, 0, 7);
//    DoubleSolenoid b = new DoubleSolenoid(12, 1, 4);
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        LiveWindow.run();
        updateSmartDashboard();
        
        
//        if(oi.getDriveYButton())  {
//        	fFlash.set(true);
//        	sFlash.set(true);
//        }
//        else if(oi.getDriveXButton()) {
//        	fFlash.set(false);
//        	sFlash.set(true);
//        }
//        else if(oi.getDriveBButton()) {
//        	fFlash.set(true);
//        	sFlash.set(false);
//        }
//        else if(oi.getDriveAButton()) {
//        	fFlash.set(false);
//        	sFlash.set(false);
//        }
        
//	     if(oi.getDriveXButton()) {
//	    	 a.set(DoubleSolenoid.Value.kForward);
//	    	 b.set(DoubleSolenoid.Value.kForward);
//	     }
//	     else {
//	    	 a.set(DoubleSolenoid.Value.kReverse);
//	    	 b.set(DoubleSolenoid.Value.kReverse);
//	     }
//	     else if(oi.getDriveBButton()) {
//	    	 shooter.turnTurret(-pref.getDouble("turretPower", 0.0));
//	     }
//	     else {
//	    	 shooter.turnTurret(0);
//	     }
	     
//        if(Robot.shooter.getXCoordinates()>-1)
//        	new CameraTrack(-1).start();
//        else 
//        	Robot.shooter.turnTurret(0);
        outerRPM = pref.getDouble("outerRPM", 0.0);
        spyRPM = pref.getDouble("reverseOuterRPM", 0.0);
        fullPower.changePower(pref.getDouble("power", 0.0));
        
//        Robot.shooter.setSpeed(power);
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    
    
    public void updateSmartDashboard() {
    	//DriveTrain
    	SmartDashboard.putNumber("LeftDrive Encoder", drive.getLeftEncoderDist());
        SmartDashboard.putNumber("RightDrive Encoder", drive.getRightEncoderDist());
        SmartDashboard.putNumber("Gyro Yaw", drive.getYaw());
        SmartDashboard.putNumber("Gyro Pitch", drive.getPitch());
        SmartDashboard.putNumber("Gyro Roll", drive.getRoll());
        
        //Shooter
        SmartDashboard.putBoolean("ShooterState", shooter.getShooterState());
        SmartDashboard.putNumber("Turret Angle", shooter.getTurretAngle());
        SmartDashboard.putNumber("Shooter RPM", shooter.getRPM());
        SmartDashboard.putBoolean("Can Shoot", shooter.shooterPID.isDone());
        
        //Popper
        SmartDashboard.putBoolean("Holders", conveyor.getHoldState());
        SmartDashboard.putBoolean("Detects Ball", !conveyor.getOptic());
        SmartDashboard.putBoolean("Contains Ball", conveyor.getContains());
        
        //Arm
        SmartDashboard.putNumber("Arm Pot", intake.getPotValue());
        
        //Detects target
        if(Robot.shooter.getXCoordinates()!=-1)
        	SmartDashboard.putBoolean("Detects Target", true);
    	else
    		SmartDashboard.putBoolean("Detects Target", false);
        
        //On Target
        if(Math.abs(Robot.shooter.pixelToDegree(Robot.shooter.getXCoordinates())-NumberConstants.cameraOffset) <= 0.8)
        	SmartDashboard.putBoolean("Tracked", true);
		else
			SmartDashboard.putBoolean("Tracked", false);
        
        //Camera Tracking
        SmartDashboard.putNumber("X", shooter.getXCoordinates());
        SmartDashboard.putNumber("degree", shooter.pixelToDegree(shooter.getXCoordinates()));
        SmartDashboard.putNumber("Angle diff", shooter.pixelToDegree(shooter.getXCoordinates())-NumberConstants.cameraOffset);
        SmartDashboard.putNumber("Setpoint", shooter.getTurretAngle()-shooter.pixelToDegree(shooter.getXCoordinates())+NumberConstants.cameraOffset);
        SmartDashboard.putNumber("distance", shooter.getDistanceToTarget());
        SmartDashboard.putNumber("Target width", shooter.targetWidthPixels());
        
        //Connection to the Kangaroo
        NetworkTable server = NetworkTable.getTable("SmartDashboard");
        double temp = -1;
        timer++; 
        try{
        	temp = server.getDouble("RUN_TIME", -1);
        }
        catch(Exception ex){
        }
        if(timer > 10 && (temp ==-1|| runTime==temp)) {
        	connected = false;
        	timer = 0;
        }
        else if(timer>10) {
        	timer = 0;
        	runTime = temp;
        	connected = true;
        }
        
        SmartDashboard.putBoolean("PC Connected", connected);
        
        //Autonomous
        SmartDashboard.putNumber("defenceLocation", defenceLocation);
        SmartDashboard.putNumber("selectedDefence", selectedDefence);
        SmartDashboard.putNumber("autoNumber", autoNumber);
        SmartDashboard.putNumber("endLocation", endLocation);

//        SmartDashboard.putData(Scheduler.getInstance());
//        SmartDashboard.putData("Calibrate Shooter", autoTune);
//        SmartDashboard.putData("LiveTrack", liveTrack);
//        SmartDashboard.putData("Test Shooter", fullPower);
//        SmartDashboard.putData("Test Drive", new DriveCommand(50, 1, 0, 5));
//        SmartDashboard.putData("Test DrivePath", new DrivePath("0,0","22.6,40","30,0","30,122",3.5,0.5, true));
//        SmartDashboard.putBoolean("Live", LiveTrack.tracking);
    }
    
    public static void resetColor() {
    	if(DriverStation.getInstance().getAlliance()==DriverStation.Alliance.Blue) {
			fFlash.set(true);
        	sFlash.set(false);
		}
		else {
			fFlash.set(false);
			sFlash.set(true);
		}
    }
}
