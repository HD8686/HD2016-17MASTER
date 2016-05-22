package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDGyro;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Servo.HDServo;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.StateMachine;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.StateTracker;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.WaitTypes;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Akash on 5/7/2016.
 */
public class ExampleOpMode extends HDOpMode {
    public HDDashboard mDisplay;
    DriveHandler robotDrive;
    HDServo mServoClimber;
    StateMachine SM;
    StateTracker StateManager;
    HDGyro mGyro;

    private enum exampleStates{
        delay,
        servoStepUp,
        servoStepDown,
    }

    @Override
    public void Initialize() {
        mDisplay = new HDDashboard(telemetry);
        mGyro = new HDGyro(Values.HardwareMapKeys.Gyro);
        robotDrive = new DriveHandler();
        StateManager = new StateTracker(robotDrive);
        SM = new StateMachine(StateManager);
        mServoClimber = new HDServo(Values.HardwareMapKeys.climberServo, Values.ServoSpeedStats.HS_755HB, Values.ServoInit.climberServoInit);
        robotDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    @Override
    public void InitializeLoop() {

    }


    @Override
    public void Start() {
        SM.setState(exampleStates.delay);
        robotDrive.setOldSteve();
    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            exampleStates states = (exampleStates) SM.getState();
                switch (states){
                    case delay:
                        SM.setState(exampleStates.servoStepUp);
                        robotDrive.tankDrive(.25,.25);
                        StateManager.setWait(WaitTypes.EncoderCounts, 500);
                        //StateManager.setWait(WaitTypes.Timer, 2.5);
                        break;
                    case servoStepUp:
                        mServoClimber.setPosition(.1, .5); //Added Scaling Code but still needs testing.
                        SM.setState(exampleStates.servoStepDown);
                        StateManager.setWait(WaitTypes.Timer, 3);
                        break;
                    case servoStepDown:
                        mServoClimber.setPosition(.8,.5);
                        break;

                }


        }


    }




}
