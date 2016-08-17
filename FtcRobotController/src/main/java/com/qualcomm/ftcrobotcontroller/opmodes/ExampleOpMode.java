package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDNavX;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.StateMachine;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.WaitTypes;

import java.util.logging.Logger;

/**
 * Created by Akash on 5/7/2016.
 */
public class ExampleOpMode extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDNavX navX;
    DriveHandler robotDrive;
    StateMachine SM;
    private enum exampleStates{
        delay,
        driveForward,
        driveBack,
        gyroTurn,
        DONE,
    }

    @Override
    public void Initialize() {
        navX = new HDNavX();
        SM = new StateMachine(robotDrive, navX);
        robotDrive = new DriveHandler(navX);
        robotDrive.resetEncoders();
    }

    @Override
    public void InitializeLoop() {
        Log.w("Test", String.valueOf(robotDrive.getEncoderCount()));
        telemetry.addData("Encoders", "Test" + robotDrive.getfrontLeft());
    }


    @Override
    public void Start() {
        SM.setState(exampleStates.delay);
        robotDrive.reverseSide(DriveHandler.Side.Diagonal);
    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            exampleStates states = (exampleStates) SM.getState();
                switch (states){
                    case delay:
                        SM.setNextState(exampleStates.driveForward, WaitTypes.Timer, 2.5);
                        break;
                    case driveForward:
                        SM.setNextState(exampleStates.driveBack, WaitTypes.EncoderCounts, 3000);
                        robotDrive.tankDrive(.4, .4);
                        break;
                    case driveBack:
                        SM.setNextState(exampleStates.gyroTurn, WaitTypes.EncoderCounts, 0);
                        robotDrive.tankDrive(-0.4, -0.4);
                        break;
                    case gyroTurn:
                        SM.setNextState(exampleStates.DONE, WaitTypes.PIDTarget);
                        robotDrive.gyroTurn(90);
                        break;
                    case DONE:
                            robotDrive.tankDrive(0,0);
                        break;

                }


        }


    }




}
