package HDCode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import hdlib.OpModeManagement.HDOpMode;
import hdlib.RobotHardwareLib.Drive.DriveHandler;
import hdlib.RobotHardwareLib.Sensors.HDNavX;
import hdlib.StateMachines.HDStateMachine;
import hdlib.StateMachines.HDWaitTypes;
import hdlib.Telemetry.HDAutoDiagnostics;


/**
 * Created by Akash on 5/7/2016.
 */

@Autonomous(name = "Autonomous Testing", group = "Testing")
public class Autonomous_Testing extends HDOpMode {
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
        driveToBeacon,
        gyroTurn,
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
                        SM.setNextState(exampleStates.driveToBeacon, HDWaitTypes.Timer, 2);
                        break;
                    case driveToBeacon:
                        SM.setNextState(exampleStates.gyroTurn, HDWaitTypes.Timer, 2);
                        robotDrive.mecanumDrive_Polar(.5,44,0); //Change this to maintain frontal position. To Test :)
                        break;
                    case gyroTurn:
                        SM.setNextState(exampleStates.DONE, HDWaitTypes.PIDTarget);
                        robotDrive.gyroTurn(1);
                    case DONE:
                        //This is a example of our libraries runOnce state machine method: this will only be ran once even though its in a state machine:
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run() {
                                robotDrive.tankDrive(0,0);
                            }
                        };
                        SM.runOnce(r1);
                        break;

                }


        }


    }




}
