package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.HDGeneralLib;
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
    private double var = 0.0;
    private double delay;
    private Alliance alliance;
    private boolean comeBackToFirstBeacon = false;
    private boolean comeBackToSecondBeacon = false;
    private HDAutonomous.Shoot shoot;

    private enum State {
        delay,
        fastDriveToBeacon,
        driveToBeaconFailsafe1,
        driveToBeaconFailsafe2,
        firstBeaconFailsafe,
        wait1,
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
        waitCheckBeacon2,
        backUp2,
        comeBackToBeacon2Failsafe1,
        comeBackToBeacon2Failsafe2,
        turnIntoCap,
        hitCap,
        done,
    }

    public AutoBeaconCapBall(double delay, HDAutonomous.Shoot shoot, Alliance alliance){
        robot = new HDRobot(alliance);

        this.delay = delay;
        this.shoot = shoot;
        this.alliance = alliance;
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
                    robot.shooter.lowerCollector();
                    robot.shooter.setCollectorPower(0.5);
                    robot.shooter.setAcceleratorPower(-1);
                    break;
                case fastDriveToBeacon:
                    SM.setNextState(State.wait1, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.225, 45.0, -90.0, robot.navX.getYaw());
                    if((robot.rangeButtonPusher.getUSValue() < 18.0) && (HDGeneralLib.isDifferenceWithin(-90.0, robot.navX.getYaw(), 15.0))){
                        SM.resetValues();
                        SM.setState(State.driveToBeaconFailsafe1);
                    }
                    break;
                case driveToBeaconFailsafe1:
                    SM.setNextState(State.driveToBeaconFailsafe2, HDWaitTypes.Timer, 0.125);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToBeaconFailsafe2:
                    SM.setNextState(State.wait, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.075, 210.0, -90.0, robot.navX.getYaw());
                    break;
                case wait1:
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
                            var = elapsedTime + 0.2;
                        }
                    });
                    robot.driveHandler.motorBrake();
                    if(shoot == HDAutonomous.Shoot.SHOOT) {
                        robot.shooter.setFlywheelPower(0.345);
                        if (elapsedTime < var) {
                            robot.shooter.setCollectorPower(-.6);
                            robot.shooter.setAcceleratorPower(-1);
                        } else {
                            robot.shooter.setCollectorPower(0);
                            robot.shooter.setAcceleratorPower(0);
                        }
                    }
                    if(timerFailsafe < elapsedTime || !robot.buttonPusher.pushButton(alliance)){
                        SM.resetValues();
                        SM.setState(State.waitCheckBeacon);
                    }
                    break;
                case waitCheckBeacon:
                    if(shoot == HDAutonomous.Shoot.SHOOT) {
                        SM.setNextState(State.backUp, HDWaitTypes.Timer, (3 - (elapsedTime - (var - 0.2))) + 0.5);
                    }else{
                        SM.setNextState(State.backUp, HDWaitTypes.Timer, 0.25);
                    }
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            if(!comeBackToFirstBeacon)
                                comeBackToFirstBeacon = !robot.buttonPusher.checkBeaconDone(alliance);
                            else
                                comeBackToFirstBeacon = false;
                            robot.buttonPusher.retractLeftServo();
                            robot.buttonPusher.retractRightServo();
                        }
                    });
                    break;
                case backUp:
                    SM.setNextState(State.wait4, HDWaitTypes.Timer, .8);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.15, -90, -90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case wait4:
                    if(shoot == HDAutonomous.Shoot.SHOOT){
                        if(comeBackToFirstBeacon){
                            shoot = HDAutonomous.Shoot.NOSHOOT;
                            SM.setNextState(State.firstBeaconFailsafe, HDWaitTypes.Timer, 1.0);
                        }else{
                            SM.setNextState(State.driveToBeacon2, HDWaitTypes.Timer, 1.0);
                        }
                        robot.shooter.setFlywheelPower(0.345);
                        robot.shooter.setCollectorPower(0.6);
                        robot.shooter.setAcceleratorPower(1.0);
                    }else {
                        if(comeBackToFirstBeacon){
                            SM.setNextState(State.firstBeaconFailsafe, HDWaitTypes.Timer, 0.25);
                        }else{
                            SM.setNextState(State.driveToBeacon2, HDWaitTypes.Timer, 0.25);
                        }
                    }
                    robot.driveHandler.motorBrake();
                    robot.driveHandler.motorBrake();
                    break;
                case firstBeaconFailsafe:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.5);
                    robot.shooter.setFlywheelPower(0.0);
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
                    break;
                case driveToBeacon2:
                    robot.shooter.setFlywheelPower(0);
                    robot.shooter.setCollectorPower(-0.5);
                    robot.shooter.setAcceleratorPower(0.0);
                    SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.Timer, 0.40);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(1.0, 0.0, -90.0, robot.navX.getYaw());
                    break;
                case fastDriveToBeacon2:
                    SM.setNextState(State.wait2, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.15, 0.0, -90.0, robot.navX.getYaw());
                    break;
                case wait2:
                    SM.setNextState(State.driveBack, HDWaitTypes.Timer, 0.25);
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.raiseCollector();
                    robot.driveHandler.motorBrake();
                    break;
                case driveBack:
                    SM.setNextState(State.wait3, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.1, 180.0, -90.0, robot.navX.getYaw());
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
                        SM.setState(State.waitCheckBeacon2);
                    }
                    break;
                case waitCheckBeacon2:
                    SM.setNextState(State.backUp2, HDWaitTypes.Timer, 0.25);
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            if(!comeBackToSecondBeacon)
                                comeBackToSecondBeacon = !robot.buttonPusher.checkBeaconDone(alliance);
                            else
                                comeBackToSecondBeacon = false;
                        }
                    });
                    break;
                case comeBackToBeacon2Failsafe1:
                    SM.setNextState(State.comeBackToBeacon2Failsafe2, HDWaitTypes.Timer, 0.35);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.5, 180.0, -90.0, robot.navX.getYaw());
                    break;
                case comeBackToBeacon2Failsafe2:
                    SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.Timer, 0.20);
                    robot.driveHandler.motorBrake();
                    break;
                case backUp2:
                    if(comeBackToSecondBeacon){
                        SM.setNextState(State.comeBackToBeacon2Failsafe1, HDWaitTypes.Timer, 0.5);
                    } else{
                        SM.setNextState(State.hitCap, HDWaitTypes.Timer, 0.8);
                    }
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.15, -90, -90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case hitCap:
                    SM.setNextState(State.turnIntoCap, HDWaitTypes.Timer, 2.25);
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.35, 208.0, -90.0, robot.navX.getYaw());
                    break;
                case turnIntoCap:
                    SM.setNextState(State.done, HDWaitTypes.Timer, 0.9);
                    if(alliance == Alliance.BLUE_ALLIANCE){
                        robot.driveHandler.tankDrive(0.0,.4);
                    }else{
                        robot.driveHandler.tankDrive(.4,0.0);
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