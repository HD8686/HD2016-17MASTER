package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.HDLib.OpModeManagement.RegisterOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.OpModeManagement.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDNavX;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.StateMachine;
import com.qualcomm.ftcrobotcontroller.HDLib.StateMachines.WaitTypes;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Akash on 5/7/2016.
 */

@RegisterOpMode("ExampleOp")
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
        gyroTurn,
        driveBack,
        gyroTurn1,
        DONE,
    }

    @Override
    public void Initialize() {
        navX = new HDNavX();
        robotDrive = new DriveHandler(navX);
        SM = new StateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(DriveHandler.Side.Left);
        Log.w("Test", robotDrive.getEncoderCountDiag());
        telemetry.addData("Encoders", robotDrive.getEncoderCountDiag());
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
                        SM.setNextState(exampleStates.driveForward, WaitTypes.Timer, 2.5);
                        break;
                    case driveForward:
                        SM.setNextState(exampleStates.gyroTurn, WaitTypes.EncoderCounts, 2500);
                        robotDrive.VLF(0, DcMotor.Direction.FORWARD);
                        break;
                    case gyroTurn:
                        SM.setNextState(exampleStates.driveBack, WaitTypes.PIDTarget);
                        robotDrive.gyroTurn(90);
                        break;
                    case driveBack:
                        SM.setNextState(exampleStates.gyroTurn1, WaitTypes.EncoderCounts, 2100);
                        robotDrive.VLF(90, DcMotor.Direction.REVERSE);
                        break;
                    case gyroTurn1:
                        SM.setNextState(exampleStates.DONE, WaitTypes.PIDTarget);
                        robotDrive.gyroTurn(0);
                        break;
                    case DONE:
                        //This is a example of the runOnce state machine method: this will only be ran once even though its in a state machine:
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run() {
                                robotDrive.tankDrive(0,0);
                                Log.w("Testing", "Ran Once!");
                            }
                        };
                        SM.runOnce(r1);
                        Log.w("Testing", "Keeps Running!");
                        break;

                }


        }


    }




}
