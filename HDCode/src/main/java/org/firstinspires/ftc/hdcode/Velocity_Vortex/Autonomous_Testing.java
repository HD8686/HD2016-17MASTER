package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDAutoDiagnostics;
import org.firstinspires.ftc.hdlib.Values;


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
    OpticalDistanceSensor ODS_Right;
    private enum exampleStates{
        delay,
        driveToBeacon,
        gyroTurn,
        DONE,
    }

    @Override
    public void Initialize() {
        ODS_Right = hardwareMap.opticalDistanceSensor.get(Values.HardwareMapKeys.Right_ODS);
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
                        SM.setNextState(exampleStates.driveToBeacon, HDWaitTypes.Timer, 2.0);
                        break;
                    case driveToBeacon:
                        SM.setNextState(exampleStates.gyroTurn, HDWaitTypes.ODS, ODS_Right);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 40.0, 0.0);
                        break;
                    case gyroTurn:
                        SM.setNextState(exampleStates.DONE, HDWaitTypes.PIDTarget);
                        robotDrive.gyroTurn(0);
                    case DONE:
                        //This is a example of our libraries runOnce state machine method: this will only be ran once even though its in a state machine:
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run() {
                                robotDrive.motorBreak();
                            }
                        };
                        SM.runOnce(r1);
                        break;

                }


        }


    }




}
