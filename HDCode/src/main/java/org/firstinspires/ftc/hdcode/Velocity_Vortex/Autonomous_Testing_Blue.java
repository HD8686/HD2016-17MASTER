package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;
import org.firstinspires.ftc.hdlib.Values;


/**
 * Created by Akash on 5/7/2016.
 */

@Autonomous(name = "Autonomous Testing Blue", group = "Testing")
public class Autonomous_Testing_Blue extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDRange range;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    OpticalDistanceSensor ODS_Back;
    private enum exampleStates{
        fastDriveToBeacon,
        driveToBeacon,
        wait,
        driveToDistance,
        gyroTurn,
        buttonPush1,
        fastDriveToBeacon2,
        driveToBeacon2,
        wait2,
        driveToDistance2,
        gyroTurn2,
        buttonPush2,
        hitCap,
        DONE,
    }

    @Override
    public void Initialize() {
        ODS_Back = hardwareMap.opticalDistanceSensor.get(Values.HardwareMapKeys.Right_ODS);
        navX = new HDNavX();
        range = new HDRange(Values.HardwareMapKeys.Range);
        robotDrive = new HDDriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(this, mDisplay,robotDrive, hardwareMap);
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
    }


    @Override
    public void Start() {
        SM.setState(exampleStates.fastDriveToBeacon);
        navX.getSensorData().zeroYaw();
    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            exampleStates states = (exampleStates) SM.getState();
                switch (states){
                    case fastDriveToBeacon:
                        SM.setNextState(exampleStates.driveToBeacon, HDWaitTypes.Timer, 2.6);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.25, 41.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case driveToBeacon:
                        SM.setNextState(exampleStates.wait, HDWaitTypes.ODStoLine, ODS_Back);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 41.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case wait:
                        SM.setNextState(exampleStates.driveToDistance, HDWaitTypes.Timer, 0.1);
                        robotDrive.motorBreak();
                        break;
                    case driveToDistance:
                        SM.setNextState(exampleStates.gyroTurn, HDWaitTypes.Range, range, 15.0);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case gyroTurn:
                        SM.setNextState(exampleStates.buttonPush1, HDWaitTypes.Timer, 1.0);
                        if(HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), -90, .5)){
                        robotDrive.motorBreak();
                        }else if(navX.getSensorData().getYaw() < -90){
                            robotDrive.tankDrive(.1, -.1);
                        }else if(navX.getSensorData().getYaw() > -90){
                            robotDrive.tankDrive(-.1, .1);
                        }
                        break;
                    case buttonPush1:
                        SM.setNextState(exampleStates.fastDriveToBeacon2, HDWaitTypes.Timer, 1.5);
                        robotDrive.motorBreak();
                        break;
                    case fastDriveToBeacon2:
                        SM.setNextState(exampleStates.driveToBeacon2, HDWaitTypes.Timer, 1.25);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(.4, 0.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case driveToBeacon2:
                        SM.setNextState(exampleStates.wait2, HDWaitTypes.ODStoLine, ODS_Back);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(.1, 0.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case wait2:
                        SM.setNextState(exampleStates.driveToDistance2, HDWaitTypes.Timer, 0.1);
                        robotDrive.motorBreak();
                        break;
                    case driveToDistance2:
                        SM.setNextState(exampleStates.gyroTurn2, HDWaitTypes.Range, range, 15.0);
                        if(range.getUSValue() > 15){
                            robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                        }else{
                            robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, -90.0, -90.0, navX.getSensorData().getYaw());
                        }
                        break;
                    case gyroTurn2:
                        SM.setNextState(exampleStates.buttonPush2, HDWaitTypes.Timer, 1.0);
                        if(HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), -90, .5)){
                            robotDrive.motorBreak();
                        }else if(navX.getSensorData().getYaw() < -90){
                            robotDrive.tankDrive(.1, -.1);
                        }else if(navX.getSensorData().getYaw() > -90){
                            robotDrive.tankDrive(-.1, .1);
                        }
                        break;
                    case buttonPush2:
                        SM.setNextState(exampleStates.hitCap, HDWaitTypes.Timer, 1.5);
                        robotDrive.motorBreak();
                        break;
                    case hitCap:
                        SM.setNextState(exampleStates.DONE, HDWaitTypes.Timer, 4.25);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.2, 215.0, 35.0, navX.getSensorData().getYaw());
                    case DONE:
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
