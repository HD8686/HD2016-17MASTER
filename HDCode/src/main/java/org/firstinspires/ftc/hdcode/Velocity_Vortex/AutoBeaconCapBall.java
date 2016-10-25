package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.Alliance;
import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDButtonPusher;
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
    HDServo Servo_Button_Pusher_Left;
    HDServo Servo_Button_Pusher_Right;
    HDButtonPusher mHDButtonPusher;
    HDMRColor Color_Left_Button_Pusher;
    HDMRColor Color_Right_Button_Pusher;


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
        driveBack,
        wait3,
        driveToDistance2,
        gyroTurn2,
        buttonPush2,
        hitCap,
        done,
    }

    public AutoBeaconCapBall(double delay, Alliance alliance, HDAutonomous.StartPosition startPosition){
        this.delay = delay;
        this.alliance = alliance;
        this.startPosition = startPosition;

        navX = new HDNavX();
        rangeButtonPusher = new HDMRRange(Values.HardwareMapKeys.Range_Button_Pusher);
        ODS_Back = new HDMROpticalDistance(Values.HardwareMapKeys.ODS_Back);

        robotDrive = new HDDriveHandler(navX);

        mHDDiagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(),robotDrive);
        SM = new HDStateMachine(robotDrive, navX);

        Color_Left_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Left_Button_Pusher);
        Color_Right_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Right_Button_Pusher);
        Color_Left_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3a));
        Color_Right_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3c));
        Servo_Button_Pusher_Left = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Left, Values.ServoSpeedStats.HS_785HB, 0, 0, 0.5, Servo.Direction.FORWARD);
        Servo_Button_Pusher_Right = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Right, Values.ServoSpeedStats.HS_785HB, 0, 0.1, 0.5, Servo.Direction.REVERSE);
        mHDButtonPusher = new HDButtonPusher(Color_Left_Button_Pusher, Color_Right_Button_Pusher, Servo_Button_Pusher_Left, Servo_Button_Pusher_Right);

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
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.25, 43.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case driveToBeacon:
                    SM.setNextState(State.wait, HDWaitTypes.ODStoLine, ODS_Back);
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 43.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case wait:
                    SM.setNextState(State.driveToDistance, HDWaitTypes.Timer, 0.1);
                    robotDrive.motorBreak();
                    break;
                case driveToDistance:
                    SM.setNextState(State.gyroTurn, HDWaitTypes.Range, rangeButtonPusher, 15.0);
                    if(rangeButtonPusher.getUSValue() > 15.0)
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                    else
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, -90.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case gyroTurn:
                    SM.setNextState(State.buttonPush1, HDWaitTypes.Timer, 1.25);
                    robotDrive.constantGyroTurnLowSpeed(-90);
                    break;
                case buttonPush1:
                    SM.setNextState(State.fastDriveToBeacon2, HDWaitTypes.ChangeColor, mHDButtonPusher);
                    robotDrive.motorBreak();
                    mHDButtonPusher.pushButton(alliance);
                    break;
                case fastDriveToBeacon2:
                    SM.setNextState(State.driveToBeacon2, HDWaitTypes.Timer, 1.6);
                    mHDButtonPusher.retractLeftServo();
                    mHDButtonPusher.retractRightServo();
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.4, -10.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case driveToBeacon2:
                    SM.setNextState(State.wait2, HDWaitTypes.Timer, 0.5);
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.125, 0.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case wait2:
                    SM.setNextState(State.driveBack, HDWaitTypes.Timer, 0.2);
                    robotDrive.motorBreak();
                    break;
                case driveBack:
                    SM.setNextState(State.wait3, HDWaitTypes.ODStoLine, ODS_Back);
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 180.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case wait3:
                    SM.setNextState(State.driveToDistance2, HDWaitTypes.Timer, 0.2);
                    robotDrive.motorBreak();
                    break;
                case driveToDistance2:
                    SM.setNextState(State.gyroTurn2, HDWaitTypes.Range, rangeButtonPusher, 15.0);
                    if(rangeButtonPusher.getUSValue() > 15)
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, 90.0, -90.0, navX.getSensorData().getYaw());
                    else
                        robotDrive.mecanumDrive_Polar_keepFrontPos(0.1, -90.0, -90.0, navX.getSensorData().getYaw());
                    break;
                case gyroTurn2:
                    SM.setNextState(State.buttonPush2, HDWaitTypes.Timer, 1.25);
                    robotDrive.constantGyroTurnLowSpeed(-90);
                    break;
                case buttonPush2:
                    SM.setNextState(State.hitCap, HDWaitTypes.ChangeColor, mHDButtonPusher);
                    robotDrive.motorBreak();
                    mHDButtonPusher.pushButton(alliance);
                    break;
                case hitCap:
                    SM.setNextState(State.done, HDWaitTypes.Timer, 4.25);
                    mHDButtonPusher.retractLeftServo();
                    mHDButtonPusher.retractRightServo();
                    robotDrive.mecanumDrive_Polar_keepFrontPos(0.2, 215.0, 35.0, navX.getSensorData().getYaw());
                    break;
                case done:
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
