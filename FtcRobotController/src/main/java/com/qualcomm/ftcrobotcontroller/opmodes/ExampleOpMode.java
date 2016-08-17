package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDMRGyro;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDNavX;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.StateMachine;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.WaitTypes;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;

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
    HDMRGyro mGyro;
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
        mGyro = new HDMRGyro(Values.HardwareMapKeys.Gyro);
        robotDrive = new DriveHandler(navX);
        robotDrive.resetEncoders();
    }

    @Override
    public void InitializeLoop() {

    }


    @Override
    public void Start() {
        SM.setState(exampleStates.delay);
        robotDrive.reverseSide(DriveHandler.Side.Right);
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
                        robotDrive.tankDrive(.1, .1);
                        break;
                    case driveBack:
                        SM.setNextState(exampleStates.DONE, WaitTypes.EncoderCounts, 0);
                        robotDrive.tankDrive(-0.1, -0.1);
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
