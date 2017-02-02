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
public class AutoBeaconCornerVortex implements HDAuto{

    HDDiagnosticDisplay diagnosticDisplay;
    HDStateMachine SM;
    HDRobot robot;


    private double timerFailsafe = 0.0;
    private double delay;
    private Alliance alliance;
    private double var = 0.0;
    private boolean comeBackToFirstBeacon = false;


    private enum State {
        delay,
        fastDriveToBeacon,
        wait5,
        driveBack2,
        wait,
        driveToDistance,
        buttonPush1,
        waitCheckBeacon,
        backUp,
        driveToBeacon2,
        wait4,
        fastDriveToBeacon2,
        wait2,
        driveBack,
        wait3,
        driveToDistance2,
        buttonPush2,
        backUp2,
        wait7,
        fastDriveBackToBeacon,
        wait6,
        driveBackToBeacon,
        driveBack3,
        wait8,
        driveToDistance3,
        buttonPush3,
        driveToCornerVortex,
        driveUpCornerVortex,
        driveToCornerVortex1,
        done,
    }

    public AutoBeaconCornerVortex(double delay, Alliance alliance){
        robot = new HDRobot(alliance);

        this.delay = delay;
        this.alliance = alliance;
        SM = new HDStateMachine(robot.driveHandler, robot.navX);
        diagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(), robot.driveHandler);

        SM.setState(State.delay);
    }

    @Override
    public void start() {
        robot.navX.zeroYaw();
    }

    /*

    ADD SLOW DOWN TO BEFORE WE GET TO FIRST LINE FOR ACCURACY AND FIX  THE ENDING ANGLE.
     */
    @Override
    public void runLoop(final double elapsedTime) {
        if(SM.ready()){
            State states = (State) SM.getState();
            switch (states){
                case delay:
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, delay);
                    robot.driveHandler.motorBrake();
                    robot.shooter.lowerCollector();
                    robot.shooter.setCollectorPower(0.5);
                    robot.shooter.setAcceleratorPower(-1);
                    break;
                case fastDriveToBeacon:
                    SM.setNextState(State.wait5, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.25, 45.0, -90.0, robot.navX.getYaw());
                    break;
                case wait5:
                    SM.setNextState(State.driveBack2, HDWaitTypes.Timer, 0.125);
                    robot.driveHandler.motorBrake();
                    break;
                case driveBack2:
                    SM.setNextState(State.wait, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 180.0, -90.0, robot.navX.getYaw());
                    break;
                case wait:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.1);
                    robot.driveHandler.motorBrake();
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
                    break;
                case driveToDistance:
                    SM.setNextState(State.buttonPush1, HDWaitTypes.Range, robot.rangeButtonPusher, 11.0);
                    if(robot.rangeButtonPusher.getUSValue() > 11.0)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .005, 90.0, -90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-.003*robot.rangeButtonPusher.getUSValue() - .005, 90.0, -90.0, robot.navX.getYaw());
                    break;
                case buttonPush1:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                            var = elapsedTime + 0.4;
                        }
                    });
                    robot.driveHandler.motorBrake();
                    if(robot.shooter.getRPM() < 4000){
                        robot.shooter.setFlywheelPower(1);
                    }else{
                        robot.shooter.setFlywheelPower(0);
                    }
                    if(var > elapsedTime){
                        robot.shooter.setCollectorPower(-0.2);
                    }else{
                        robot.shooter.setCollectorPower(0.0);
                    }
                    if(timerFailsafe < elapsedTime || !robot.buttonPusher.pushButton(alliance)){
                        SM.resetValues();
                        SM.setState(State.waitCheckBeacon);
                    }
                    break;
                case waitCheckBeacon:
                    SM.setNextState(State.backUp, HDWaitTypes.Timer, 0.25);
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            comeBackToFirstBeacon = !robot.buttonPusher.checkBeaconDone(alliance);
                        }
                    });
                    if(robot.shooter.getRPM() < 4200){
                        robot.shooter.setFlywheelPower(1);
                    }else{
                        robot.shooter.setFlywheelPower(0);
                    }
                    break;
                case backUp:
                    SM.setNextState(State.wait4, HDWaitTypes.Timer, .8);
                    if(robot.shooter.getRPM() < 4200){
                        robot.shooter.setFlywheelPower(1);
                    }else{
                        robot.shooter.setFlywheelPower(0);
                    }
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.15, -90, -90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case wait4:
                    SM.setNextState(State.driveToBeacon2, HDWaitTypes.Timer, 2.0);
                    if(robot.shooter.getRPM() < 4200){
                        robot.shooter.setFlywheelPower(1);
                    }else{
                        robot.shooter.setFlywheelPower(0);
                    }
                    robot.shooter.setCollectorPower(1);
                    robot.shooter.setAcceleratorPower(1);
                    robot.driveHandler.motorBrake();
                    robot.driveHandler.motorBrake();
                    break;
                case driveToBeacon2:
                    robot.shooter.setFlywheelPower(0);
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
                    SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.Timer, 1.0);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.5, 0.0, -90.0, robot.navX.getYaw());
                    break;
                case fastDriveToBeacon2:
                    SM.setNextState(State.wait2, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.15, 0.0, -90.0, robot.navX.getYaw());
                    break;
                case wait2:
                    SM.setNextState(State.driveBack, HDWaitTypes.Timer, 0.25);
                    robot.driveHandler.motorBrake();
                    break;
                case driveBack:
                    SM.setNextState(State.wait3, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 180.0, -90.0, robot.navX.getYaw());
                    break;
                case wait3:
                    SM.setNextState(State.driveToDistance2, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance2:
                    SM.setNextState(State.buttonPush2, HDWaitTypes.Range, robot.rangeButtonPusher, 11.0);
                    if(robot.rangeButtonPusher.getUSValue() > 11.0)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .005, 90.0, -90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-.003*robot.rangeButtonPusher.getUSValue() - .005, 90.0, -90.0, robot.navX.getYaw());
                    break;
                case buttonPush2:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    robot.driveHandler.motorBrake();
                    if(timerFailsafe < elapsedTime || !robot.buttonPusher.pushButton(alliance)){
                        SM.resetValues();
                        SM.setState(State.backUp2);
                    }
                    break;
                case backUp2:
                    if(comeBackToFirstBeacon) {
                        SM.setNextState(State.wait6, HDWaitTypes.Timer, .8);
                    }else{
                        SM.setNextState(State.driveToCornerVortex1, HDWaitTypes.Timer, .8);
                    }
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.15, -90, -90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case wait6:
                    SM.setNextState(State.driveBackToBeacon, HDWaitTypes.Timer, 0.25);
                    robot.driveHandler.motorBrake();
                    break;
                case driveBackToBeacon:
                    SM.setNextState(State.fastDriveBackToBeacon, HDWaitTypes.Timer, 1.0);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.5, 180.0, -90.0, robot.navX.getYaw());
                    break;
                case fastDriveBackToBeacon:
                    SM.setNextState(State.wait7, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.15, 180.0, -90.0, robot.navX.getYaw());
                    break;
                case wait7:
                    SM.setNextState(State.driveBack3, HDWaitTypes.Timer, 0.25);
                    robot.driveHandler.motorBrake();
                    break;
                case driveBack3:
                    SM.setNextState(State.wait8, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 0.0, -90.0, robot.navX.getYaw());
                    break;
                case wait8:
                    SM.setNextState(State.driveToDistance3, HDWaitTypes.Timer, 0.25);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDistance3:
                    SM.setNextState(State.buttonPush3, HDWaitTypes.Range, robot.rangeButtonPusher, 11.0);
                    if(robot.rangeButtonPusher.getUSValue() > 11.0)
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.003*robot.rangeButtonPusher.getUSValue() + .005, 90.0, -90.0, robot.navX.getYaw());
                    else
                        robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-.003*robot.rangeButtonPusher.getUSValue() - .005, 90.0, -90.0, robot.navX.getYaw());
                    break;
                case buttonPush3:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    SM.setNextState(State.driveToCornerVortex, HDWaitTypes.ChangeColor, robot.buttonPusher);
                    robot.driveHandler.motorBrake();
                    if(timerFailsafe < elapsedTime || !robot.buttonPusher.pushButton(alliance)){
                        SM.resetValues();
                        SM.setState(State.driveToCornerVortex);
                    }
                    break;
                case driveToCornerVortex1:
                    SM.setNextState(State.done, HDWaitTypes.Timer, 2.8);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.6, 180, -90, robot.navX.getYaw());
                    break;
                case driveToCornerVortex:
                    SM.setNextState(State.driveUpCornerVortex, HDWaitTypes.Timer, 2.15);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.25, 207.0, -45.0, robot.navX.getYaw());
                    break;
                case driveUpCornerVortex:
                    SM.setNextState(State.done, HDWaitTypes.Timer, 0.75);
                    robot.driveHandler.tankDrive(-0.5,-0.5);
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