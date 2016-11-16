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
        fastDriveToBeacon,
        driveToBeacon,
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
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, delay);
                    robot.driveHandler.motorBrake();
                    break;
                case fastDriveToBeacon:
                    if(startPosition == HDAutonomous.StartPosition.TILE_1) {
                        SM.setNextState(State.driveToBeacon, HDWaitTypes.Timer, 2.65);
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, alliance == Alliance.BLUE_ALLIANCE? 45.0 : -45.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    }else if(startPosition == HDAutonomous.StartPosition.TILE_2){
                        SM.setNextState(State.driveToBeacon, HDWaitTypes.Timer, 3.3);
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, alliance == Alliance.BLUE_ALLIANCE? 60.0 : -60.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    }
                    break;
                case driveToBeacon:
                    SM.setNextState(State.done, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    if(startPosition == HDAutonomous.StartPosition.TILE_1) {
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, alliance == Alliance.BLUE_ALLIANCE? 45.0 : -45.0,  alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    }else if(startPosition == HDAutonomous.StartPosition.TILE_2){
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, alliance == Alliance.BLUE_ALLIANCE? 60.0 : -60.0, alliance == Alliance.BLUE_ALLIANCE? -90.0 : 90.0, robot.navX.getYaw());
                    }
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