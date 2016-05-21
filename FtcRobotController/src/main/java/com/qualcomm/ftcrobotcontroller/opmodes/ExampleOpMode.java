package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

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
import com.qualcomm.robotcore.hardware.GyroSensor;

import java.security.Key;

/**
 * Created by Akash on 5/7/2016.
 */
public class ExampleOpMode extends HDOpMode {
    DriveHandler robotDrive;
    HDDashboard mDashboard;
    HDServo mServoClimber;
    StateMachine SM;
    StateTracker StateManager;
    HDGyro mGyro;

    private enum exampleStates{
        delay,
        servoStep,
    }

    @Override
    public void Initialize() {
        mDashboard = new HDDashboard(telemetry);
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
    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            exampleStates states = (exampleStates) SM.getState();
                switch (states){
                    case delay:
                        SM.setState(exampleStates.servoStep);
                        StateManager.setWait(WaitTypes.Timer, 2.5);
                        break;
                    case servoStep:
                        mServoClimber.setPosition(.1, .5); //Added Scaling Code but still needs testing.
                        StateManager.setWait(WaitTypes.Timer, 3);
                        break;
                }


        }


    }




}
