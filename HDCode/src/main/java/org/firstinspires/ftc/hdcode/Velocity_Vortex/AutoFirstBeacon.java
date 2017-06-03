package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.util.Log;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.HDGeneralLib;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDButtonPusher;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.StateMachines.HDWaitTypes;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;

/**
 * Created by Akash on 10/20/2016.
 */
public class AutoFirstBeacon implements HDAuto{

    HDDiagnosticDisplay diagnosticDisplay;
    HDStateMachine SM;
    HDRobot robot;
    private boolean comeBackToFirstBeacon = true;


    private double timerFailsafe = 0.0;
    private double var = 0.0;
    private double delay;
    private int USFailsafeCounter = 0;
    private Alliance alliance;
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
        USFailsafe,
        buttonPush1,
        waitCheckBeacon,
        backUp,
        wait7,
        shoot1,
        shoot2,
        shoot3,
        shoot4,
        turnToCorner,
        wait6,
        driveToCorner,
        done,
    }

    public AutoFirstBeacon(double delay, HDAutonomous.Shoot shoot, Alliance alliance){
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
                    SM.setNextState(State.buttonPush1, HDWaitTypes.Timer, 0.1);
                    robot.driveHandler.motorBrake();
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
                    break;
                case buttonPush1:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                            var = elapsedTime + 0.15;
                        }
                    });
                    if(shoot == HDAutonomous.Shoot.SHOOT) {
                        robot.shooter.setFlywheelPower(0.345);
                    }
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.07, 90.0, -90.0, robot.navX.getYaw());
                    if((timerFailsafe < elapsedTime || !robot.buttonPusher.pushButton(alliance)) &&
                            (robot.buttonPusher.readLeftColor() != HDButtonPusher.beaconColor.INCONCLUSIVE && robot.buttonPusher.readRightColor() != HDButtonPusher.beaconColor.INCONCLUSIVE) ){
                        robot.driveHandler.motorBrake();
                        SM.resetValues();
                        SM.setState(State.waitCheckBeacon);
                    }
                    break;
                case waitCheckBeacon:
                    if(shoot == HDAutonomous.Shoot.SHOOT) {
                        SM.setNextState(State.backUp, HDWaitTypes.Timer, (3 - (elapsedTime - (var - 0.15))) + 0.5);
                        robot.shooter.setFlywheelPower(0.345);
                        if (elapsedTime < var) {
                            robot.shooter.setCollectorPower(-.6);
                            robot.shooter.setAcceleratorPower(-1);
                        } else {
                            robot.shooter.setCollectorPower(0);
                            robot.shooter.setAcceleratorPower(0);
                        }
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
                    if(USFailsafeCounter > 5){
                        SM.setNextState(State.wait7, HDWaitTypes.Timer, 0.8);
                    }else {
                        SM.setNextState(State.wait7, HDWaitTypes.Timer, .8);
                    }
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.15, -90, -90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case wait7:
                    SM.setNextState(State.shoot1, HDWaitTypes.Timer, 0.2);
                    if(shoot == HDAutonomous.Shoot.SHOOT){
                        if(comeBackToFirstBeacon){
                            USFailsafeCounter = 0;
                            shoot = HDAutonomous.Shoot.NOSHOOT;
                            SM.setNextState(State.firstBeaconFailsafe, HDWaitTypes.Timer, 1.0);
                        }else{
                            SM.setNextState(State.shoot1, HDWaitTypes.Timer, 1.0);
                        }
                    }else {
                        if(comeBackToFirstBeacon){
                            USFailsafeCounter = 0;
                            SM.setNextState(State.firstBeaconFailsafe, HDWaitTypes.Timer, 0.25);
                        }else{
                            SM.setNextState(State.shoot1, HDWaitTypes.Timer, 0.25);
                        }
                    }
                    robot.driveHandler.motorBrake();
                    break;
                case shoot1:
                    SM.setNextState(State.shoot2, HDWaitTypes.Timer, 0.4);
                    robot.shooter.setCollectorPower(0);
                    robot.shooter.setAcceleratorPower(0);
                    break;
                case shoot2:
                    SM.setNextState(State.shoot3, HDWaitTypes.Timer, 0.1);
                    robot.shooter.setCollectorPower(-.6);
                    robot.shooter.setAcceleratorPower(-1);
                    break;
                case shoot3:
                    SM.setNextState(State.shoot4, HDWaitTypes.Timer, 0.2);
                    robot.shooter.setCollectorPower(0);
                    robot.shooter.setAcceleratorPower(0);
                    break;
                case shoot4:
                    if(elapsedTime > 20.0){
                        robot.driveHandler.motorBrake();
                        SM.resetValues();
                        SM.setState(State.turnToCorner);
                    }
                    robot.shooter.setFlywheelPower(0.345);
                    robot.shooter.setCollectorPower(0.6);
                    robot.shooter.setAcceleratorPower(1.0);
                    break;
                case firstBeaconFailsafe:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.5);
                    robot.shooter.setFlywheelPower(0.0);
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
                    break;
                case turnToCorner:
                    SM.setNextState(State.wait6, HDWaitTypes.Timer, 3.0);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.25, 180.5, -45.0, robot.navX.getYaw());
                    robot.shooter.setFlywheelPower(0.0);
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
                    break;
                case wait6:
                    SM.setNextState(State.driveToCorner, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    robot.driveHandler.resetEncoders();
                    break;
                case driveToCorner:
                    if(robot.driveHandler.getEncoderCount() < -800){
                        SM.resetValues();
                        robot.driveHandler.motorBrake();
                        SM.setState(State.done);
                    }
                    robot.driveHandler.tankDrive(-0.2,-0.2);
                    robot.shooter.raiseCollector();
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