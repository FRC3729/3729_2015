
package org.usfirst.frc.team3729.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {

    Input _input;
    Drive _drive;
    Mechanisms _mech;
    Timer auto_timer;
    CameraServer server;

    protected void disabled()
    {
    	System.out.println("in Disabled");
    	SmartDashboard.putString("DB/String 0", "== DISABLED ==");
    	_drive.stopmotors();
    	_mech.stopmotors();
    }

    protected void robotInit()
    {
        _input = Input.getInstance();
        _drive = Drive.getInstance();
        _mech = Mechanisms.getInstance();
        auto_timer = new Timer();
        
        //Print banner
        System.out.println(" ______ ______ ______ ______\n|__    |      |__    |  __  |\n|__    |_     |    __|__    |\n|______| |____|______|______|\n");
        System.out.println("This robot complies with Asimov's Laws of Robotics:");
        System.out.println("\t~> 1. A robot may not injure a human being or,\n\t      through inaction, allow a human being to come to harm.");
        System.out.println("\t~> 2. A robot must obey the orders given to it by human beings,\n\t      except where such orders would conflict with the First Law.");
        System.out.println("\t~> 3. A robot must protect its own existence as long as\n\t      such protection does not conflict with the First or Second Laws.");
        
        //Setup Camera 
        server = CameraServer.getInstance();
        server.setQuality(51);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
        
        //Start Threads
        _drive.start();
        _mech.start();
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous(){
        System.out.println("In auto");
        SmartDashboard.putString("DB/String 0", "== AUTONOMOUS ==");
        _mech.auto_state = 0;
        auto_timer.start();
        while (isEnabled()){
            System.out.println("auto_timer: " + auto_timer.get());
            if (auto_timer.get() >= 15) {
            	_mech.auto_state = 8;
            }
            switch (_mech.auto_state) {
            case 0: //resets arms
            	_mech.autoarm(0);
            	break;
//            case 1: //Drives Forward
//            	_drive.Hdrive(.02, -.3, 0);
//            	Timer.delay(1);
//            	_drive.stopmotors();
//            	_mech.auto_state = 2;
//            	break;
//            case 2: //Pinches
//            	_mech.autoarm(1);
//            	break;
//            case 3: //Lifts up
//            	_mech.elevator0.set(.7);
//            	_mech.elevator1.set(.7);
//            	Timer.delay(1);
//            	_mech.stopmotors();
//            	_mech.auto_state = 4;
//            	break;
            case 4: //Stalls lift and drives reverse
            	_drive.Hdrive(.04, .3, 0);
//            	_mech.elevator0.set(.2);
//            	_mech.elevator1.set(.2);
            	Timer.delay(5);
            	_drive.stopmotors();
//            	_mech.stopmotors();
            	_mech.auto_state = 8;
            	break;
//            case 5: //Drops container
//            	_mech.elevator0.set(-.3);
//            	_mech.elevator1.set(-.3);
//            	Timer.delay(.5);
//            	_mech.stopmotors();
//            	_mech.auto_state = 6;
//            	break;
//            case 6: //Resets arms
//            	_mech.autoarm(0);
//            case 7: //Drives Forward
//            	_drive.Hdrive(0, .3, 0);
//            	Timer.delay(1);
//            	_drive.stopmotors();
//            	_mech.auto_state = 8;
//            	break;
            case 8: //Stops all
            	_drive.stopmotors();
            	_mech.stopmotors();
            	break;
            }   
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl(){
        System.out.println("in OpControl");
        SmartDashboard.putString("DB/String 0", "== TELEOP ==");

        while (isEnabled())
        {
            // #### LIVE ROUTINES ####
        	_drive.run();
        	_mech.run();
            
            //!Testing values
            if (Params.testing_mech) {_mech.test();}
            if (Params.testing_input) {_input.test(); Timer.delay(0.1);}
            if (Params.testing_drive) {_drive.test();}
        }
    }
    public void test(){
    	SmartDashboard.putString("DB/String 0", "== TEST ==");
    	while (isTest() && isEnabled()) {
    		LiveWindow.run();
    		if (Params.testing_mech) {_mech.test();}
            if (Params.testing_input) {_input.test(); Timer.delay(0.1);}
            if (Params.testing_drive) {_drive.test();}
    	}
    }
}
