package org.firstinspires.ftc.hdlib;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDButtonPusher;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;

/**
 * Created by Akash on 10/31/2016.
 */
public class HDRobot {

    /*
     Sensors
     */
    public HDNavX navX;
    public HDMROpticalDistance ODS_Back;

    /*
     Drive Subsystem
     */
    public HDDriveHandler driveHandler;

    /*
     Button Pusher Subsystem
     */
    public HDServo Servo_Button_Pusher_Left;
    public HDServo Servo_Button_Pusher_Right;
    public HDMRColor Color_Left_Button_Pusher;
    public HDMRColor Color_Right_Button_Pusher;
    public HDMRRange rangeButtonPusher;
    public HDButtonPusher buttonPusher;


    public HDRobot(Alliance alliance){
        /*
        Initialize Sensors
         */
        navX = new HDNavX();
        rangeButtonPusher = new HDMRRange(Values.HardwareMapKeys.Range_Button_Pusher);
        ODS_Back = new HDMROpticalDistance(Values.HardwareMapKeys.ODS_Back);

        /*
        Drive Subsystem
         */
        driveHandler = new HDDriveHandler(navX);
        driveHandler.resetEncoders();
        driveHandler.reverseSide(HDDriveHandler.Side.Left);
        driveHandler.setAlliance(alliance);

        /*
        Button Pusher Subsystem
         */
        Color_Left_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Left_Button_Pusher);
        Color_Right_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Right_Button_Pusher);
        Color_Left_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3a));
        Color_Right_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3c));
        Servo_Button_Pusher_Left = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Left, Values.ServoSpeedStats.HS_785HB, 0.055, 0, 1, Servo.Direction.FORWARD);
        Servo_Button_Pusher_Right = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Right, Values.ServoSpeedStats.HS_785HB, 0.514, 0, 1, Servo.Direction.REVERSE); //.514
        buttonPusher = new HDButtonPusher(Color_Left_Button_Pusher, Color_Right_Button_Pusher, Servo_Button_Pusher_Left, Servo_Button_Pusher_Right);
    }

}