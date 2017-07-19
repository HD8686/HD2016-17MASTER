package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.hdlib.General.Alliance;
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
public class AutoSecondBeaconDefenseFirst implements HDAuto{

    HDDiagnosticDisplay diagnosticDisplay;
    HDStateMachine SM;
    HDRobot robot;
    private boolean comeBackToSecondBeacon = false;

    private double timerFailsafe = 0.0;
    private double delay;
    private Alliance alliance;

    private enum State {
        delay,
        arcTurn,
        wait1,
        fastDriveToBeacon,
        wait2,
        turnToBeacon,
        wait4,
        driveForward,
        wait5,
        driveToBeacon,
        wait3,
        driveToDistance,
        buttonPush,
        USFailsafe2,
        waitCheckBeacon,
        comeBackToBeacon2Failsafe1,
        comeBackToBeacon2Failsafe2,
        backUp,
        fastDriveToBeacon2,
        backToDefense,
        wait6,
        turnToCenter,
        wait7,
        driveToDefense,
        shoot1,
        shoot2,
        shoot3,
        shoot4,
        wait8,
        raiseCollector,
        done,
    }

    public AutoSecondBeaconDefenseFirst(double delay, Alliance alliance){
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

    @Override
    public void runLoop(final double elapsedTime) {
        if(SM.ready()){
            State states = (State) SM.getState();
            switch (states){
                case delay:
                    SM.setNextState(State.arcTurn, HDWaitTypes.Timer, delay);
                    robot.driveHandler.motorBrake();
                    robot.shooter.raiseCollector();
                    robot.shooter.setCollectorPower(0);
                    robot.shooter.setAcceleratorPower(0);
                    robot.navX.zeroYaw();
                    break;
                case arcTurn:
                    if(robot.navX.getYaw() > 13){
                        robot.driveHandler.motorBrake();
                        SM.resetValues();
                        SM.setState(State.wait1);
                    }else{
                        if(robot.navX.getYaw() > 6){
                            robot.driveHandler.tankDrive(0.0, -0.1);
                        }else {
                            robot.driveHandler.tankDrive(0.0, -0.2);
                        }
                    }
                    break;
                case wait1:
                    SM.setNextState(State.fastDriveToBeacon, HDWaitTypes.Timer, 0.2);
                    robot.shooter.lowerCollector();
                    robot.driveHandler.motorBrake();
                    break;
                case fastDriveToBeacon:
                    Log.w("EncCnt", String.valueOf(robot.driveHandler.getEncoderCount()));
                    robot.shooter.setCollectorPower(0.5);
                    robot.shooter.setAcceleratorPower(-1);
                    if(robot.driveHandler.getEncoderCount() < -4000){
                        SM.resetValues();
                        robot.driveHandler.motorBrake();
                        Log.w("EncCnt", String.valueOf(elapsedTime));
                        SM.setState(State.wait2);
                    }
                    robot.driveHandler.tankDrive(-1.0,-1.0);
                    break;
                case wait2:
                    SM.setNextState(State.turnToBeacon, HDWaitTypes.Timer, 0.1);
                    robot.driveHandler.motorBrake();
                    break;
                case turnToBeacon:
                    SM.setNextState(State.wait4, HDWaitTypes.PIDTarget);
                    robot.driveHandler.gyroTurn(90.0);
                    break;
                case wait4:
                    SM.setNextState(State.driveForward, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    break;
                case driveForward:
                    SM.setNextState(State.driveToBeacon, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.4, (90-180), 90.0, robot.navX.getYaw());
                    break;
                case wait5:
                    SM.setNextState(State.buttonPush, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToBeacon:
                    SM.setNextState(State.wait3, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.075, (0-180), 90.0, robot.navX.getYaw());
                    break;
                case wait3:
                    SM.setNextState(State.buttonPush, HDWaitTypes.Timer, 0.2);
                    robot.driveHandler.motorBrake();
                    break;
                case buttonPush:
                    SM.runOnce(new Runnable() {
                        @Override
                        public void run() {
                            timerFailsafe = elapsedTime + 3;
                        }
                    });
                    robot.shooter.setFlywheelPower(0.41);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.07, -90.0, 90.0, robot.navX.getYaw());
                    if((timerFailsafe < elapsedTime || !robot.buttonPusher.pushButton(alliance)) &&
                            (robot.buttonPusher.readLeftColor() != HDButtonPusher.beaconColor.INCONCLUSIVE && robot.buttonPusher.readRightColor() != HDButtonPusher.beaconColor.INCONCLUSIVE) ){
                        robot.driveHandler.motorBrake();
                        SM.resetValues();
                        SM.setState(State.waitCheckBeacon);
                    }
                    break;
                case waitCheckBeacon:
                    SM.setNextState(State.backUp, HDWaitTypes.Timer, 0.25);
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
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.5, -180.0, 90.0, robot.navX.getYaw());
                    break;
                case comeBackToBeacon2Failsafe2:
                    SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.Timer, 0.20);
                    robot.driveHandler.motorBrake();
                    break;
                case backUp:
                    if(comeBackToSecondBeacon){
                        SM.setNextState(State.comeBackToBeacon2Failsafe1, HDWaitTypes.Timer, 0.5);
                    } else{
                        SM.setNextState(State.backToDefense, HDWaitTypes.Timer, 0.8);
                    }
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(.15, 90, 90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case fastDriveToBeacon2:
                    SM.setNextState(State.wait5, HDWaitTypes.ODStoLine, robot.ODS_Back);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.15, 0.0, 90.0, robot.navX.getYaw());
                    break;
                case backToDefense:
                    SM.setNextState(State.wait6, HDWaitTypes.Timer, 0.8);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(0.75, 90, 90, robot.navX.getYaw());
                    robot.buttonPusher.retractLeftServo();
                    robot.buttonPusher.retractRightServo();
                    break;
                case wait6:
                    SM.setNextState(State.turnToCenter, HDWaitTypes.Timer, 0.1);
                    robot.driveHandler.motorBrake();
                    robot.driveHandler.resetEncoders();
                    break;
                case turnToCenter:
                    if(robot.navX.getYaw() < 0.0){
                        robot.driveHandler.motorBrake();
                        SM.resetValues();
                        SM.setState(State.wait7);
                    }else{
                        robot.driveHandler.tankDrive(-0.35,0.35);
                    }
                    break;
                case wait7:
                    SM.setNextState(State.driveToDefense, HDWaitTypes.Timer, 0.1);
                    robot.driveHandler.motorBrake();
                    break;
                case driveToDefense:
                    SM.setNextState(State.wait8, HDWaitTypes.Timer, 1.25);
                    robot.driveHandler.mecanumDrive_Polar_keepFrontPos(-0.7 , 0.0, 0.0, robot.navX.getYaw());
                    break;
                case wait8:
                    SM.setNextState(State.shoot1, HDWaitTypes.Timer, 0.2);
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
                    SM.setNextState(State.raiseCollector, HDWaitTypes.Timer, 2.0);
                    robot.shooter.setFlywheelPower(0.41);
                    robot.shooter.setCollectorPower(0.6);
                    robot.shooter.setAcceleratorPower(1.0);
                    break;
                case raiseCollector:
                    SM.setNextState(State.done, HDWaitTypes.Timer, 2.0);
                    robot.shooter.setFlywheelPower(0.0);
                    robot.shooter.setCollectorPower(0.0);
                    robot.shooter.setAcceleratorPower(0.0);
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
                    robot.driveHandler.motorBrake();
                    break;
            }
        }
    }
}