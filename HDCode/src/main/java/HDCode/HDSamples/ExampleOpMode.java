package HDCode.HDSamples;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import hdlib.OpModeManagement.HDOpMode;
import hdlib.RobotHardwareLib.Drive.DriveHandler;
import hdlib.RobotHardwareLib.Sensors.HDNavX;
import hdlib.StateMachines.HDStateMachine;
import hdlib.StateMachines.HDWaitTypes;
import hdlib.Telemetry.HDAutoDiagnostics;


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
    HDAutoDiagnostics mHDAutoDiagnostics;
    HDNavX navX;
    DriveHandler robotDrive;
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
    public void Initialize() {
        navX = new HDNavX();
        robotDrive = new DriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDAutoDiagnostics = new HDAutoDiagnostics(this, mDisplay,robotDrive);
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(DriveHandler.Side.Left);
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
                        SM.setNextState(exampleStates.driveForward, HDWaitTypes.Timer, 200.5);
                        break;
                    case driveForward:
                        SM.setNextState(exampleStates.gyroTurn, HDWaitTypes.EncoderCounts, 2500);
                        robotDrive.VLF(0, DcMotor.Direction.FORWARD);
                        break;
                    case gyroTurn:
                        SM.setNextState(exampleStates.driveBack, HDWaitTypes.PIDTarget);
                        robotDrive.gyroTurn(90);
                        break;
                    case driveBack:
                        SM.setNextState(exampleStates.gyroTurn1, HDWaitTypes.EncoderCounts, 2100);
                        robotDrive.VLF(90, DcMotor.Direction.REVERSE);
                        break;
                    case gyroTurn1:
                        SM.setNextState(exampleStates.DONE, HDWaitTypes.PIDTarget);
                        robotDrive.gyroTurn(0);
                        break;
                    case DONE:
                        //This is a example of our libraries runOnce state machine method: this will only be ran once even though its in a state machine:
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
