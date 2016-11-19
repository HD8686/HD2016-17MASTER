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
        buttonPush1,
        backUp,
        wait4,
        fastDriveToBeacon,
        wait3,
        driveToLine2,
        done,
    }

    public AutoBeacon(double delay, Alliance alliance, HDAutonomous.StartPosition startPosition) {
        robot = new HDRobot(alliance);

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
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.1, alliance == Alliance.BLUE_ALLIANCE? 180.0 : -180.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait2:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance:
                    SM.setNextState(State.buttonPush1, HDWaitTypes.Range, robot.rangeButtonPusher, 11.0);
                    if(robot.rangeButtonPusher.getUSValue() > 11)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .01, alliance == Alliance.BLUE_ALLIANCE? 90.0 : -90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-.003*robot.rangeButtonPusher.getUSValue() - .01, alliance == Alliance.BLUE_ALLIANCE? 90.0 : -90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case buttonPush1:
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
                        SM.setState(State.fastDriveToBeacon);
                    }
                    break;
                case backUp:
                    SM.setNextState(State.wait4, HDWaitTypes.Timer, .75);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.2, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait4:
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case fastDriveToBeacon:
                    SM.setNextState(State.wait3, HDWaitTypes.Timer, 1.75);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.75, alliance == Alliance.BLUE_ALLIANCE? 0.0 : 0.0,  alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case wait3:
                    SM.setNextState(State.driveToLine2, HDWaitTypes.Timer, 0.5);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToLine2:
                    SM.setNextState(State.done, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.1, alliance == Alliance.BLUE_ALLIANCE? 180.0 : -180.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    break;
                case done:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            robot.driveHandler.motorBrake();
                            robot.buttonPusher.retractLeftServo();
                            robot.buttonPusher.retractRightServo();
                        }
                    });
                    break;
            }
        }
    }
}