package org.usfirst.frc.team3729.robot;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Mechanisms extends Thread {
	private static Mechanisms INSTANCE = null;
	
	private Relay arm0;
	private Relay arm1;
	private Relay intake;
	
	public Talon elevator0;
	public Talon elevator1;
	
	private DigitalInput limit_arm0out;
	private DigitalInput limit_arm1out;
	
	public Encoder encoder_arm0;
	public Encoder encoder_arm1;
	
	private boolean encoder_reset = false;
	private int narrow_pinch = 0;
	public int auto_state = 8;
	
	Input _input;
	Robot _bot;
	
	private Mechanisms() {
		arm0 = new Relay(Params.port_Spike_arm[0]);
		arm1 = new Relay(Params.port_Spike_arm[1]);
		intake = new Relay(Params.port_Spike_intake);
		
		elevator0 = new Talon(Params.port_Talon_elevator[0]);
		elevator1 = new Talon(Params.port_Talon_elevator[1]);

		limit_arm0out = new DigitalInput(Params.port_Limit_arm[0]);
		limit_arm1out = new DigitalInput(Params.port_Limit_arm[1]);
		
		encoder_arm0 = new Encoder(Params.port_Encoder_aChannel_arm[0], Params.port_Encoder_bChannel_arm[0]);
		encoder_arm1 = new Encoder(Params.port_Encoder_aChannel_arm[1], Params.port_Encoder_bChannel_arm[1]);
		
		_input = new Input();
	}
	
	public static Mechanisms getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Mechanisms();
        
        return INSTANCE;
    }
	
	public void run() {
		intake();
		arms();
		elevatorsimple();
		//Dashboard Displays
		SmartDashboard.putDouble("DB/Slider 0", getPinch());//true if right button is pressed
		SmartDashboard.putBoolean("DB/LED 0", encoder_arm0.get() == Params.position_arm[0][getPinch()] && encoder_arm1.get() == Params.position_arm[1][getPinch()]);
		SmartDashboard.putBoolean("DB/LED 1", limit_arm0out.get());
		SmartDashboard.putBoolean("DB/LED 2", limit_arm1out.get());
	}
	
	//Give out testing values
	public void test() { 
		System.out.println("Left Arm Limit: " + limit_arm0out.get());
		System.out.println("Right Arm Limit: " + limit_arm1out.get());
		System.out.println("Left Arm Encoder: " + encoder_arm0.get());
		System.out.println("Right Arm Encoder: " + encoder_arm1.get());
		try {
			Thread.sleep(100); //Make testing values actually readable
		} catch (Exception e){
			System.out.println(e);
		}
	}
	
	//Controls belts on the arms
	private void intake() { 
		if (_input.getButton(2, 6)) { //Run the belts in
			intake.set(Relay.Value.kReverse);
			if (Params.testing_mech){ System.out.println("Intake going in");}
		} else if (_input.getButton(2, 5)) { //Run the belts out
			intake.set(Relay.Value.kForward);
			if (Params.testing_mech){ System.out.println("Intake going out");}
		} else {
			intake.set(Relay.Value.kOff);
		}
	}
	//Pinching Mechanism for the arms
	private void arms() { 
		if (_input.getAxis(2, 2) >= .75) { //Arms Separate
			if (!limit_arm0out.get()) {
				arm0.set(Relay.Value.kForward);
			}else{
				arm0.set(Relay.Value.kOff);
			}
			if (!limit_arm1out.get()) {
				arm1.set(Relay.Value.kReverse);
			}else{
				arm1.set(Relay.Value.kOff);
			}
			if (limit_arm0out.get() && limit_arm1out.get()) {
				encoder_reset = true;
			}
			if (Params.testing_mech){ System.out.println("arms out");}
		} else if (_input.getAxis(2, 3) >= .75) { //Arms Close
			if (encoder_reset) {
				if (encoder_arm0.get() > Params.position_arm[0][getPinch()]) {
					arm0.set(Relay.Value.kReverse);
				} else if (encoder_arm0.get() < Params.position_arm[0][getPinch()]){
					arm0.set(Relay.Value.kOff);
				}
				if (encoder_arm1.get() <= Params.position_arm[1][getPinch()]) {
					arm1.set(Relay.Value.kForward);
				} else if (encoder_arm1.get() > Params.position_arm[1][getPinch()]){
					arm1.set(Relay.Value.kOff);
				}
				if (Params.testing_mech){ System.out.println("arms in");}
			} else {
				if (!limit_arm0out.get()) {
					arm0.set(Relay.Value.kForward);
				}else{
					arm0.set(Relay.Value.kOff);
				}
				if (!limit_arm1out.get()) {
					arm1.set(Relay.Value.kReverse);
				}else{
					arm1.set(Relay.Value.kOff);
				}
				if (limit_arm0out.get() && limit_arm1out.get()) {
					encoder_reset = true;
				}
			}
			
		} else if (_input.getButton(2, 3) && !limit_arm0out.get()) { //Arms Shift Left
			arm0.set(Relay.Value.kForward);
			arm1.set(Relay.Value.kForward);
			encoder_reset = false;
			if (Params.testing_mech){ System.out.println("arms left");}
		} else if (_input.getButton(2, 2) && !limit_arm1out.get()) { //Arms Shift Right
			arm0.set(Relay.Value.kReverse);
			arm1.set(Relay.Value.kReverse);
			encoder_reset = false;
			if (Params.testing_mech){ System.out.println("arms right");}
		} else {
			arm0.set(Relay.Value.kOff);
			arm1.set(Relay.Value.kOff);
		} 
	}
	private int getPinch() {
		if (limit_arm0out.get()) {
			encoder_arm0.reset();
		}
		if (limit_arm1out.get()) {
			encoder_arm1.reset();
		}
		if (_input.xbox.getPOV() == 270) {
			encoder_reset = false;
			narrow_pinch = 0;
		} else if (_input.xbox.getPOV() == 90) {
			encoder_reset = false;
			narrow_pinch = 1;
		} else if (_input.xbox.getPOV() == 0){
			encoder_reset = false;
			narrow_pinch = 2;
		}
		return narrow_pinch;
	}
	
	private void elevatorsimple() {
		if (_input.getAxis(2, 5) <= 0.0) {
			elevator0.set(_input.getAxis(2,5));
			elevator1.set(_input.getAxis(2,5));
		} else {
			elevator0.set(_input.getAxis(2,5) * Params.speed_creep);
			elevator1.set(_input.getAxis(2,5) * Params.speed_creep);
		}
	}
	
	public void autoarm(int t){
		if(t == 0){	
			if(!limit_arm0out.get()){
				arm0.set(Relay.Value.kForward);
			}else{
				arm0.set(Relay.Value.kOff);
			}
			if(!limit_arm1out.get()){
				arm1.set(Relay.Value.kReverse);
			}else{
				arm1.set(Relay.Value.kOff);
			}
			if (limit_arm0out.get()) {
				encoder_arm0.reset();
			}
			if (limit_arm1out.get()) {
				encoder_arm1.reset();
			}
			if(limit_arm0out.get() && limit_arm1out.get()) {
				auto_state = 4;
			}
		}
		
		if(t == 1){
			if(encoder_arm0.get() >= Params.position_arm[0][2]){
				arm0.set(Relay.Value.kReverse);
			}else{
				arm0.set(Relay.Value.kOff);
				
			}
			if(encoder_arm1.get() <= Params.position_arm[1][2]){
				arm1.set(Relay.Value.kForward);
			}else{
				arm1.set(Relay.Value.kOff);
			}
			if ((encoder_arm0.get() < Params.position_arm[0][2]) && (encoder_arm1.get() > Params.position_arm[1][2])) {
				auto_state = 3;
			}
		}
		
		if(t==2){
			elevator0.set(.7);
			elevator1.set(.7);
		}
		if(t==3){
			elevator0.set(-.2);
			elevator1.set(-.2);
		}
	}
	
	//STOP EVERYTHING
	public void stopmotors() { 
		arm0.set(Relay.Value.kOff);
		arm1.set(Relay.Value.kOff);
		intake.set(Relay.Value.kOff);
		elevator0.set(0.0);
		elevator1.set(0.0);
	}
}
