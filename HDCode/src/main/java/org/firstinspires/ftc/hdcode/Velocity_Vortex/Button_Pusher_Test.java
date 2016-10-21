package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.I2cAddr;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDBeaconReader;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;
import org.firstinspires.ftc.hdlib.Values;


/**
 * Created by Akash on 10/10/2016.
 */

@Autonomous(name = "Button Pusher Test", group = "Testing")
public class Button_Pusher_Test extends HDOpMode {

    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    HDMROpticalDistance ODS_Back;
    HDMRRange Range_Button_Pusher;
    HDMRColor Color_Left_Button_Pusher;
    HDMRColor Color_Right_Button_Pusher;
    HDServo Servo_Button_Pusher_Left;
    HDServo Servo_Button_Pusher_Right;
    HDBeaconReader HDBeaconReader;

    @Override
    public void initialize() {
        SM = new HDStateMachine(robotDrive, navX);
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(mDisplay,robotDrive);

        navX = new HDNavX();
        Range_Button_Pusher = new HDMRRange(Values.HardwareMapKeys.Range_Button_Pusher);
        ODS_Back = new HDMROpticalDistance(Values.HardwareMapKeys.ODS_Back);
        Color_Left_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Left_Button_Pusher);
        Color_Right_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Right_Button_Pusher);
        Color_Left_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3a));
        Color_Right_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3c));
        HDBeaconReader = new HDBeaconReader(Color_Left_Button_Pusher, Color_Right_Button_Pusher);

        Servo_Button_Pusher_Left = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Left, Values.ServoSpeedStats.HS_785HB, 0, true);
        Servo_Button_Pusher_Right = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Right, Values.ServoSpeedStats.HS_785HB, 0, false);
        robotDrive = new HDDriveHandler(navX);

        robotDrive.resetEncoders();
        Color_Right_Button_Pusher.getSensor().enableLed(false);
        Color_Left_Button_Pusher.getSensor().enableLed(false);

    }

    @Override
    public void initializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
    }

    @Override
    public void Start() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
        if(SM.ready()){
            mHDDiagnosticDisplay.addProgramSpecificTelemetry(1, "leftColorStatus: " + HDBeaconReader.readLeftColor().toString());
            mHDDiagnosticDisplay.addProgramSpecificTelemetry(2, "rightColorStatus: " + HDBeaconReader.readRightColor().toString());
        }
    }

}
