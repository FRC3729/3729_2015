package org.usfirst.frc.team3729.robot;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends Thread {
	private static Drive INSTANCE = null;
    
	private Talon leftMotor0;
    private Talon leftMotor1;
    private Talon rightMotor0;
    private Talon rightMotor1;
    private Talon centerMotor0;
    private Talon centerMotor1;
    
    private Ultrasonic sonar0;
    private Ultrasonic sonar1;
    
    Input _input;
    
    private Drive() {
        leftMotor0 = new Talon(Params.port_Talon_left[0]);
        leftMotor1 = new Talon(Params.port_Talon_left[1]);
        rightMotor0 = new Talon(Params.port_Talon_right[0]);
        rightMotor1 = new Talon(Params.port_Talon_right[1]);
        centerMotor0 = new Talon(Params.port_Talon_center[0]);
        centerMotor1 = new Talon(Params.port_Talon_center[1]);
        
        sonar0 = new Ultrasonic(Params.port_Sonar_in[0],Params.port_Sonar_out[0]);
        sonar0.setEnabled(true);
//        sonar0.setAutomaticMode(true);
        sonar1 = new Ultrasonic(Params.port_Sonar_in[1],Params.port_Sonar_out[1]);
        sonar1.setEnabled(true);
        sonar1.setAutomaticMode(true);
        
        _input = new Input();
    }
    
    public static Drive getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Drive();
        
        return INSTANCE;
    }
    
    public void run() {
    	//Drive Mode
    	if (_input.getButton(0, 1)) { //Creep on the first joystick
    		Hdrive(_input.getAxis(0,0) * Params.speed_creep, _input.getAxis(0,1) * Params.speed_creep, _input.getAxis(1,0));
    	} else if (_input.getButton(1, 1)) { //Creep on the second joystick
    		Hdrive(_input.getAxis(0,0), _input.getAxis(0,1), _input.getAxis(1,0) * Params.speed_creep);
    	} else if (_input.getButton(0, 3)) { //Drive Tank
    		tank(-_input.getAxis(1,1) * .75, _input.getAxis(0,1) * .75);
    	} else if (_input.getButton(1, 3)) { //Sonar Alignment
    		align();
    	} else { //Normal Drive with 6 wheel omni system
    		Hdrive(_input.getAxis(0,0), _input.getAxis(0,1), _input.getAxis(1,0));
    	} 	
    	//Dashboard Displays
    	if (Math.abs(sonar0.getRangeInches() - sonar1.getRangeInches()) <= 0.25) {
    		SmartDashboard.putString("DB/String 5", "!!ALIGNED!!");
    	} else {
    	SmartDashboard.putString("DB/String 5", "                 ");
    	}
    }
    
    //Drive values for testing
    public void test() {
    	System.out.println("Sonar0: " + sonar0.getRangeInches());
    	System.out.println("Sonar1: " + sonar1.getRangeInches());
    	System.out.println("left : " + leftMotor0.get() + ", " + leftMotor1.get());
    	System.out.println("Right : " + rightMotor0.get() + ", " + rightMotor1.get());
    	System.out.println("Center : " + centerMotor0.get() + ", " + centerMotor1.get());
    	try {
			Thread.sleep(100); //Make testing values actually readable
		} catch (Exception e){
			System.out.println(e);
		}
    }
    
    //!Sonar Auto-Align
    public void align() {
    	if (sonar0.getRangeInches() > sonar1.getRangeInches()) {
    		this.tank(Params.speed_creep, 0.0);
    	} else if (sonar0.getRangeInches() < sonar1.getRangeInches()) {
    		this.tank(0.0, -Params.speed_creep);
    	} else {
    		this.stopmotors();
    	}
    }
    
    //Drive Modes
    //!Tank Drive
    public void tank(double left, double right) {
        leftMotor0.set(left);
        leftMotor1.set(left);
        rightMotor0.set(right);
        rightMotor1.set(right);
    }    
    //!H Drive
    public void Hdrive(double x, double y, double z) {
        centerMotor0.set(z);
        centerMotor1.set(z);
    	double left = y-(x - .02);
    	double right = y+(x - .02);
        
        left = Params.clamp(left, -.95, .95);
        right = Params.clamp(right, -.95, .95);        
        leftMotor0.set(-left);
        leftMotor1.set(-left);
        rightMotor0.set(right);
        rightMotor1.set(right);
    }
    //!Quad Drive
    public void Quad(double x, double y, double z){
    	this.Hdrive(z, y, x);
    }
    //!Stopped
    public void stopmotors() {
        leftMotor0.set(0.0);
        leftMotor1.set(0.0);
        rightMotor0.set(0.0);
        rightMotor1.set(0.0);
        centerMotor0.set(0.0);
        centerMotor1.set(0.0);
    }

}
