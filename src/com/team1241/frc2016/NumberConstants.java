package com.team1241.frc2016;

/**
 * This class is used to save constant values that will be changed every once in awhile. This can include PID tuned
 * values or autonomous distances, etc...
 *
 * @author Mahrus Kazi
 * @author Bryan Kristiono
 * @since 2015-09-13
 */

//Practice
public class NumberConstants {
	//**************************************************************************
    //*************************** PID VALUES (DRIVE) ***************************
    //**************************************************************************
	
	//Competition
	public static final double pDrive 									 = 0.041;
	public static final double iDrive 									 = 0.0000008;
	public static final double dDrive 									 = 0.00;
	
	//**************************************************************************
    //**************************** PID VALUES (GYRO) ***************************
    //**************************************************************************
	
	//Competition
	public static final double pGyro 									 = 0.02;
	public static final double iGyro 									 = 0.00;
	public static final double dGyro 									 = 0.00;
	
	//**************************************************************************
    //*************************** PID VALUES (TURRET) **************************
    //**************************************************************************
	
	//Competition
	public static final double pTurret 									 = 0.025;
	public static final double iTurret 									 = 0.00;
	public static final double dTurret 									 = 0.005;
	
	//**************************************************************************
    //*************************** PID VALUES (CAMERA) **************************
    //**************************************************************************
	
	//Competition
	public static final double pCamera 									 = 0.03;
	public static final double iCamera 									 = 0.0003;
	public static final double dCamera 									 = 0.000;
	
	
	//**************************************************************************
    //************************** PID VALUES (SHOOTER) **************************
    //**************************************************************************
	
	//Competition
	public static final double pShooter									 = 0.00039;
	public static final double iShooter									 = 0.0000055;
	public static final double dShooter									 = 0.00;
	public static final double kForward									 = 0.0001628902407751;
	public static final double bForward									 = 0.048786729135007;
	
	//**************************************************************************
    //**************************** PID VALUES (ARM) ****************************
    //**************************************************************************
	
	//Competition
	public static final double pArm 									 = 0.006;
	public static final double iArm 									 = 0.00;
	public static final double dArm 									 = 0.00;
	
	//**************************************************************************
    //**************************OUTPUT VALUES (Shooter)*************************
    //**************************************************************************
	
	//Competition
	public static final int downArmAngle								= 500;
	//Arm all the way up
	public static final int upArmAngle									= 790;
	
	
	//**************************************************************************
	//***************************** SHOOTER NUMBERS ****************************
	//**************************************************************************
	
	//Competition
	public static final int outerShotRPM								= 4600;
	public static final int spyShotRPM									= 4200;
	public static final int badderShotRPM								= 4500;
	
	public static final int spyShotAngle								= -78;
	
	public static final double waitForPop								= 0.75;
	public static final double waitForHolder							= 0.15;
	public static final double waitForHolderClose						= 0.50;	

	//**************************************************************************
	//***************************** INTAKE NUMBERS *****************************
	//**************************************************************************
	
	public static final double intakeDist								= 12.2;
}
