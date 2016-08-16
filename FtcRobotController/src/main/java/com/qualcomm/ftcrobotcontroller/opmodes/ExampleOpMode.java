package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDGyro;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Servo.HDServo;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.StateMachine;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.WaitTypes;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;

/**
 * Created by Akash on 5/7/2016.
 */
public class ExampleOpMode extends HDOpMode {
    DriveHandler robotDrive;
    HDServo mServoClimber;
    StateMachine SM;
    HDGyro mGyro;
    private enum exampleStates{
        delay,
        servoStepUp,
        servoStepDown,
        DONE,
    }

    @Override
    public void Initialize() {
        SM = new StateMachine(robotDrive);
        mGyro = new HDGyro(Values.HardwareMapKeys.Gyro);
        robotDrive = new DriveHandler();
        mServoClimber = new HDServo(Values.HardwareMapKeys.climberServo, Values.ServoSpeedStats.HS_755HB, Values.ServoInit.climberServoInit);
        robotDrive.resetEncoders();
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
                        SM.setNextState(exampleStates.servoStepUp, WaitTypes.Timer, 2.5);
                        break;
                    case servoStepUp:
                        SM.setNextState(exampleStates.servoStepDown, WaitTypes.EncoderCounts, 3000);
                        mServoClimber.setPosition(.1, .5); //Added Scaling Code but still needs testing.
                        robotDrive.tankDrive(.1, .1);
                        break;
                    case servoStepDown:
                        SM.setNextState(exampleStates.DONE, WaitTypes.EncoderCounts, 0);
                        mServoClimber.setPosition(.8, .5);
                        robotDrive.tankDrive(-0.1, -0.1);
                        break;
                    case DONE:
                            robotDrive.tankDrive(0,0);
                        break;

                }


        }


    }




}
