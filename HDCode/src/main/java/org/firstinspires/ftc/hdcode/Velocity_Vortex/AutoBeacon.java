package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;

/**
 * Created by Akash on 10/20/2016.
 */
public class AutoBeacon implements HDAuto {

    HDDiagnosticDisplay diagnosticDisplay;
    HDStateMachine SM;
    HDRobot robot;


    private double timerFailsafe = 0.0;
    private double delay;
    private Alliance alliance;
    private HDAutonomous.StartPosition startPosition;

    private enum State {
        delay,
        driveToBeacon,
        wait,
        driveToLine,
        wait2,
        driveToDistance,
        buttonPush,
        backUp,
        wait4,
        getOffLine,
        fastDriveToBeacon,
        wait3,
        driveToLine2,
        wait5,
        driveToDistance2,
        buttonPush2,
        done,
    }

    public AutoBeacon(double delay, Alliance alliance, HDAutonomous.StartPosition startPosition) {
        robot = new HDRobot();

        this.delay = delay;
        this.alliance = alliance;
        this.startPosition = startPosition;
        SM = new HDStateMachine(robot.driveHandler, robot.navX);
        diagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(), robot.driveHandler);

        SM.setState(State.delay);
    }

    @Override
    public void start() {
    }

    @Override
    public void runLoop(final double elapsedTime) {
        if (SM.ready()) {
            State states = (State) SM.getState();
            switch (states) {
                case delay:
                    SM.setNextState(State.driveToBeacon, HDWaitTypes.Timer, delay);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToBeacon:
                    SM.setNextState(State.wait, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    if(startPosition == HDAutonomous.StartPosition.TILE_1) {
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, alliance == Alliance.BLUE_ALLIANCE? 45.0 : -45.0,  alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    }else if(startPosition == HDAutonomous.StartPosition.TILE_2){
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, alliance == Alliance.BLUE_ALLIANCE? 60.0 : -60.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    }
                    break;
                case wait:
                    SM.setNextState(State.driveToLine, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToLine:
                    SM.setNextState(State.wait2, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, alliance == Alliance.BLUE_ALLIANCE? 180.0 : -180.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait2:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance:
                    SM.setNextState(State.buttonPush, HDWaitTypes.Range, robot.rangeButtonPusher, 9.0);
                    if(robot.rangeButtonPusher.getUSValue() > 9)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .005, alliance == Alliance.BLUE_ALLIANCE? 90.0 : -90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-.003*robot.rangeButtonPusher.getUSValue() - .005, alliance == Alliance.BLUE_ALLIANCE? 90.0 : -90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case buttonPush:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    SM.setNextState(State.backUp, HDWaitTypes.ChangeColor, robot.buttonPusher);
                    robot.driveHandler.motorBrake();
                    robot.buttonPusher.pushButton(alliance);
                    if(timerFailsafe < elapsedTime){
                        SM.resetValues();
                        SM.setState(State.backUp);
                    }
                    break;
                case backUp:
                    SM.setNextState(State.wait4, HDWaitTypes.Timer, 0.5);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.2, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait4:
                    SM.setNextState(State.getOffLine, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case getOffLine:
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, .675);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.5, alliance == Alliance.BLUE_ALLIANCE? 0.0 : 0.0,  alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case fastDriveToBeacon:
                    SM.setNextState(State.wait3, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.1, alliance == Alliance.BLUE_ALLIANCE? 0.0 : 0.0,  alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait3:
                    SM.setNextState(State.driveToLine2, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToLine2:
                    SM.setNextState(State.wait5, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.1, alliance == Alliance.BLUE_ALLIANCE? 180.0 : -180.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait5:
                    SM.setNextState(State.driveToDistance2, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance2:
                    SM.setNextState(State.buttonPush2, HDWaitTypes.Range, robot.rangeButtonPusher, 9.0);
                    if(robot.rangeButtonPusher.getUSValue() > 9)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .005, alliance == Alliance.BLUE_ALLIANCE? 90.0 : -90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-.003*robot.rangeButtonPusher.getUSValue() - .005, alliance == Alliance.BLUE_ALLIANCE? 90.0 : -90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case buttonPush2:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    SM.setNextState(State.done, HDWaitTypes.ChangeColor, robot.buttonPusher);
                    robot.driveHandler.motorBrake();
                    robot.buttonPusher.pushButton(alliance);
                    if(timerFailsafe < elapsedTime){
                        SM.resetValues();
                        SM.setState(State.done);
                    }
                    break;
                case done:
                    robot.driveHandler.motorBrake();
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
            }
        }
    }
}