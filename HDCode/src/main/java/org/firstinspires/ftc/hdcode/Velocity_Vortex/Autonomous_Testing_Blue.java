package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;
import org.firstinspires.ftc.hdlib.Values;


/**
 * Created by Akash on 5/7/2016.
 */


/**
 * THIS CLASS ONLY EXISTS FOR TESTING HDAutonomous, IT SHOULD BE DELETED ONCE WE ARE SURE.
 */
@Deprecated
@Disabled
@Autonomous(name = "Autonomous Testing Blue", group = "Testing")
public class Autonomous_Testing_Blue extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDMRRange range;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    HDMROpticalDistance ODS_Back;

    private enum State {
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
    public void initialize() {
        ODS_Back = new HDMROpticalDistance(Values.HardwareMapKeys.ODS_Back);
        navX = new HDNavX();
        range = new HDMRRange(Values.HardwareMapKeys.Range_Button_Pusher);
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
        SM.setState(State.fastDriveToBeacon);
        navX.getSensorData().zeroYaw();
    }

    @Override
    public void continuousRun(double elapsedTime) {
        if(SM.ready()){
            State states = (State) SM.getState();
                switch (states){
                    case fastDriveToBeacon:
                        SM.setNextState(State.driveToBeacon, HDWaitTypes.Timer, 2.6);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.25, 41.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case driveToBeacon:
                        SM.setNextState(State.wait, HDWaitTypes.ODStoLine, ODS_Back);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 41.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case wait:
                        SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.1);
                        robotDrive.motorBreak();
                        break;
                    case driveToDistance:
                        SM.setNextState(State.gyroTurn, HDWaitTypes.Range, range, 15.0);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case gyroTurn:
                        SM.setNextState(State.buttonPush1, HDWaitTypes.Timer, 1.0);
                        if(HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), -90, .5)){
                        robotDrive.motorBreak();
                        }else if(navX.getSensorData().getYaw() < -90){
                            robotDrive.tankDrive(.1, -.1);
                        }else if(navX.getSensorData().getYaw() > -90){
                            robotDrive.tankDrive(-.1, .1);
                        }
                        break;
                    case buttonPush1:
                        //Remeber to turn color Light off.
                        SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.Timer, 1.5);
                        robotDrive.motorBreak();
                        break;
                    case fastDriveToBeacon2:
                        SM.setNextState(State.driveToBeacon2, HDWaitTypes.Timer, 1.25);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(.4, 0.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case driveToBeacon2:
                        SM.setNextState(State.wait2, HDWaitTypes.ODStoLine, ODS_Back);
                        robotDrive.mecanumDrive_Polar_keepFrontPos(.1, 0.0, -90.0, navX.getSensorData().getYaw());
                        break;
                    case wait2:
                        SM.setNextState(State.driveToDistance2, HDWaitTypes.Timer, 0.1);
                        robotDrive.motorBreak();
                        break;
                    case driveToDistance2:
                        SM.setNextState(State.gyroTurn2, HDWaitTypes.Range, range, 15.0);
                        if(range.getUSValue() > 15){
                            robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                        }else{
                            robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, -90.0, -90.0, navX.getSensorData().getYaw());
                        }
                        break;
                    case gyroTurn2:
                        SM.setNextState(State.buttonPush2, HDWaitTypes.Timer, 1.0);
                        if(HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), -90, .5)){
                            robotDrive.motorBreak();
                        }else if(navX.getSensorData().getYaw() < -90){
                            robotDrive.tankDrive(.1, -.1);
                        }else if(navX.getSensorData().getYaw() > -90){
                            robotDrive.tankDrive(-.1, .1);
                        }
                        break;
                    case buttonPush2:
                        SM.setNextState(State.hitCap, HDWaitTypes.Timer, 1.5);
                        robotDrive.motorBreak();
                        break;
                    case hitCap:
                        SM.setNextState(State.DONE, HDWaitTypes.Timer, 4.25);
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
