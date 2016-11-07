package org.firstinspires.ftc.hdcode.HDSamples;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

@Disabled
@Autonomous(name = "ExampleOpMode", group = "HDSamples")
public class ExampleOpMode extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    private enum exampleStates{
        delay,
        driveForward,
        gyroTurn,
        driveBack,
        gyroTurn1,
        DONE,
    }

    @Override
    public void initialize() {
        navX = new HDNavX();
        robotDrive = new HDDriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(mDisplay,robotDrive);
    }

    @Override
    public void initializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
    }


    @Override
    public void Start() {
        SM.setState(exampleStates.delay);
    }

    @Override
    public void continuousRun(double elapsedTime) {
        if(SM.ready()){
            exampleStates states = (exampleStates) SM.getState();
                switch (states){
                    case delay:
                        SM.setNextState(exampleStates.driveForward, HDWaitTypes.Timer, 200.5);
                        break;
                    case driveForward:
                        SM.setNextState(exampleStates.driveBack, HDWaitTypes.EncoderCounts, 2500);
                        robotDrive.VLF(0, DcMotor.Direction.FORWARD);
                        break;
                    case driveBack:
                        SM.setNextState(exampleStates.DONE, HDWaitTypes.EncoderCounts, 2100);
                        robotDrive.VLF(90, DcMotor.Direction.REVERSE);
                        break;
                    case DONE:
                        //This is a example of our libraries runOnce state machine method: this will only be ran once even though its in a state machine:
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run() {
                                robotDrive.tankDrive(0,0);
                                Log.w("HDCode", "Ran Once!");
                            }
                        };
                        SM.runOnce(r1);
                        Log.w("HDCode", "Keeps Running!");
                        break;

                }


        }


    }




}
