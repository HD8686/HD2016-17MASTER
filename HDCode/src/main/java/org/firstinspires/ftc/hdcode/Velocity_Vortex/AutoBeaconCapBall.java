package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import org.firstinspires.ftc.hdlib.Alliance;
import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;
import org.firstinspires.ftc.hdlib.Values;

/**
 * Created by Akash on 10/20/2016.
 */
public class AutoBeaconCapBall implements HDAuto{

    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDMRRange rangeButtonPusher;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    HDMROpticalDistance ODS_Back;

    private double delay;
    private Alliance alliance;
    private HDAutonomous.StartPosition startPosition;

    private enum State {
        delay,
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

    public AutoBeaconCapBall(double delay, Alliance alliance, HDAutonomous.StartPosition startPosition){
        this.delay = delay;
        this.alliance = alliance;
        this.startPosition = startPosition;

        mHDDiagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(),robotDrive);
        SM = new HDStateMachine(robotDrive, navX);

        robotDrive = new HDDriveHandler(navX);


        navX = new HDNavX();
        rangeButtonPusher = new HDMRRange(Values.HardwareMapKeys.Range_Button_Pusher);
        ODS_Back = new HDMROpticalDistance(Values.HardwareMapKeys.ODS_Back);

        robotDrive.resetEncoders();
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
        robotDrive.setAlliance(alliance);

        SM.setState(State.delay);

    }

    @Override
    public void start() {
        navX.getSensorData().zeroYaw();
    }

    @Override
    public void runLoop(double elapsedTime) {
        if(SM.ready()){
            State states = (State) SM.getState();
            switch (states){
                case delay:
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, delay);
                    robotDrive.motorBreak();
                    break;
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
                    SM.setNextState(State.gyroTurn, HDWaitTypes.Range, rangeButtonPusher, 15.0);
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case gyroTurn:
                    SM.setNextState(State.buttonPush1, HDWaitTypes.Timer, 1.0);
                    if(alliance == Alliance.BLUE_ALLIANCE) {
                        if (HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), -90, .5))
                            robotDrive.motorBreak();
                        else if (navX.getSensorData().getYaw() < -90)
                            robotDrive.tankDrive(.1, -.1);
                        else if (navX.getSensorData().getYaw() > -90)
                            robotDrive.tankDrive(-.1, .1);
                    }else if(alliance == Alliance.RED_ALLIANCE){
                        if(HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), 90, .5))
                            robotDrive.motorBreak();
                        else if(navX.getSensorData().getYaw() > 90)
                            robotDrive.tankDrive(-.1, .1);
                        else if(navX.getSensorData().getYaw() < 90)
                            robotDrive.tankDrive(.1, -.1);
                    }
                    break;
                case buttonPush1:
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
                    SM.setNextState(State.gyroTurn2, HDWaitTypes.Range, rangeButtonPusher, 15.0);
                    if(rangeButtonPusher.getUSValue() > 15){
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                    }else{
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, -90.0, -90.0, navX.getSensorData().getYaw());
                    }
                    break;
                case gyroTurn2:
                    SM.setNextState(State.buttonPush2, HDWaitTypes.Timer, 1.0);
                    if(alliance == Alliance.BLUE_ALLIANCE) {
                        if (HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), -90, .5))
                            robotDrive.motorBreak();
                        else if (navX.getSensorData().getYaw() < -90)
                            robotDrive.tankDrive(.1, -.1);
                        else if (navX.getSensorData().getYaw() > -90)
                            robotDrive.tankDrive(-.1, .1);
                    }else if(alliance == Alliance.RED_ALLIANCE){
                        if(HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), 90, .5))
                            robotDrive.motorBreak();
                        else if(navX.getSensorData().getYaw() > 90)
                            robotDrive.tankDrive(-.1, .1);
                        else if(navX.getSensorData().getYaw() < 90)
                            robotDrive.tankDrive(.1, -.1);
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
