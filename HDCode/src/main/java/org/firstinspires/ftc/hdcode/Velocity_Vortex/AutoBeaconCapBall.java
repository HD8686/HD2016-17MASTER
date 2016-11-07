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
public class AutoBeaconCapBall implements HDAuto{

    HDDiagnosticDisplay diagnosticDisplay;
    HDStateMachine SM;
    HDRobot robot;


    private double timerFailsafe = 0.0;
    private double delay;
    private Alliance alliance;
    private HDAutonomous.StartPosition startPosition;

    private enum State {
        delay,
        fastDriveToBeacon,
        driveToBeacon,
        wait,
        driveToDistance,
        driveToDistanceTime,
        buttonPush1,
        fastDriveToBeacon2,
        driveToBeacon2,
        wait2,
        driveBack,
        wait3,
        driveToDistance2,
        driveToDistanceTime2,
        buttonPush2,
        hitCap,
        done,
    }

    public AutoBeaconCapBall(double delay, Alliance alliance, HDAutonomous.StartPosition startPosition){
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
        robot.navX.zeroYaw();
    }

    @Override
    public void runLoop(final double elapsedTime) {
        if(SM.ready()){
            State states = (State) SM.getState();
            switch (states){
                case delay:
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, delay);
                    robot.driveHandler.motorBrake();
                    break;
                case fastDriveToBeacon:
                    SM.setNextState(State.driveToBeacon, HDWaitTypes.Timer, 2.65);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, 45.0, -90.0, robot.navX.getYaw());
                    break;
                case driveToBeacon:
                    SM.setNextState(State.wait, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 43.0, -90.0, robot.navX.getYaw());
                    break;
                case wait:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.1);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance:
                    SM.setNextState(State.driveToDistanceTime, HDWaitTypes.Range, robot.rangeButtonPusher, 11.0);
                    if(robot.rangeButtonPusher.getUSValue() > 11.0)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .012, 90.0, -90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.05, -90.0, -90.0, robot.navX.getYaw());
                    break;
                case driveToDistanceTime:
                    SM.setNextState(State.buttonPush1, HDWaitTypes.Timer, 0.0);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 90.0, -90, robot.navX.getYaw());
                    break;
                case buttonPush1:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.ChangeColor, robot.buttonPusher);
                    robot.driveHandler.motorBrake();
                    robot.buttonPusher.pushButton(alliance);
                    if(timerFailsafe < elapsedTime){
                        SM.resetValues();
                        SM.setState(State.fastDriveToBeacon2);
                    }
                    break;
                case fastDriveToBeacon2:
                    SM.setNextState(State.driveToBeacon2, HDWaitTypes.Timer, 1.6);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.4, -7.0, -90.0, robot.navX.getYaw());
                    break;
                case driveToBeacon2:
                    SM.setNextState(State.wait2, HDWaitTypes.Timer, 0.4);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.125, 0.0, -90.0, robot.navX.getYaw());
                    break;
                case wait2:
                    SM.setNextState(State.driveBack, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    break;
                case driveBack:
                    SM.setNextState(State.wait3, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.05, 180.0, -90.0, robot.navX.getYaw());
                    break;
                case wait3:
                    SM.setNextState(State.driveToDistance2, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance2:
                    SM.setNextState(State.driveToDistanceTime2, HDWaitTypes.Range, robot.rangeButtonPusher, 11.0);
                    if(robot.rangeButtonPusher.getUSValue() > 11)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .012, 90.0, -90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.05, -90.0, -90.0, robot.navX.getYaw());
                    break;
                case driveToDistanceTime2:
                    SM.setNextState(State.buttonPush2, HDWaitTypes.Timer, 0.0);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 90.0, -90, robot.navX.getYaw());
                    break;
                case buttonPush2:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    SM.setNextState(State.hitCap, HDWaitTypes.ChangeColor, robot.buttonPusher);
                    robot.driveHandler.motorBrake();
                    robot.buttonPusher.pushButton(alliance);
                    if(timerFailsafe < elapsedTime){
                        SM.resetValues();
                        SM.setState(State.hitCap);
                    }
                    break;
                case hitCap:
                    SM.setNextState(State.done, HDWaitTypes.Timer, 4.25);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, 220.0, -45.0, robot.navX.getYaw());
                    break;
                case done:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            robot.driveHandler.motorBrake();
                        }
                    });
                    break;
            }
        }
    }
}
