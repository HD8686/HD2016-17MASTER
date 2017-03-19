package org.firstinspires.ftc.hdlib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDButtonPusher;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDCap;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDShooter;

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

    /*
     Shooter Subsystem
     */
    public HDServo leftCollectorServo;
    public HDServo rightCollectorServo;
    public DcMotor collectorMotor;
    public DcMotor flywheel1;
    public DcMotor flywheel2;
    public HDVexMotor accelerator1;
    public HDVexMotor accelerator2;
    public HDShooter shooter;

      /*
      HD Cap Ball Subsystem
       */
    public HDCap lift;
    public DcMotor capLift;
    public HDServo leftCapArm;
    public HDServo rightCapArm;

    public HDRobot(Alliance alliance){
        if(!HDOpMode.getInstance().isStopRequested()) {
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
            driveHandler.reverseSide(HDDriveHandler.Side.Right);
            driveHandler.setAlliance(alliance);

        /*
        Button Pusher Subsystem
         */
            Color_Left_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Left_Button_Pusher);
            Color_Right_Button_Pusher = new HDMRColor(Values.HardwareMapKeys.Color_Right_Button_Pusher);
            Color_Left_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3a));
            Color_Right_Button_Pusher.getSensor().setI2cAddress(I2cAddr.create8bit(0x3c));
            Servo_Button_Pusher_Left = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Left, Values.ServoSpeedStats.HS_785HB, 0.056, 0, 1, Servo.Direction.FORWARD);
            Servo_Button_Pusher_Right = new HDServo(Values.HardwareMapKeys.Servo_Button_Pusher_Right, Values.ServoSpeedStats.HS_785HB, 0.515, 0, 1, Servo.Direction.REVERSE);
            buttonPusher = new HDButtonPusher(Color_Left_Button_Pusher, Color_Right_Button_Pusher, Servo_Button_Pusher_Left, Servo_Button_Pusher_Right);

        /*
        Shooter Subsystem
         */
            leftCollectorServo = new HDServo("leftCollector", Values.ServoSpeedStats.HS_755MG, 0.42, 0, 1, Servo.Direction.REVERSE);
            rightCollectorServo = new HDServo("rightCollector", Values.ServoSpeedStats.HS_755MG, 0.43, 0, 1, Servo.Direction.FORWARD);
            flywheel1 = HDOpMode.getInstance().hardwareMap.dcMotor.get("Flywheel_1");
            flywheel2 = HDOpMode.getInstance().hardwareMap.dcMotor.get("Flywheel_2");
            collectorMotor = HDOpMode.getInstance().hardwareMap.dcMotor.get("Collector1");
            accelerator1 = new HDVexMotor("Vex1", Servo.Direction.REVERSE);
            accelerator2 = new HDVexMotor("Vex2", Servo.Direction.REVERSE);
            shooter = new HDShooter(leftCollectorServo, rightCollectorServo, collectorMotor, flywheel1, flywheel2, accelerator1, accelerator2);
        /*
        Cap Ball Subsystem
         */
            capLift  = HDOpMode.getInstance().hardwareMap.dcMotor.get("liftMotor");
            leftCapArm = new HDServo("leftCapArm", Values.ServoSpeedStats.HS_785HB, 0.41, 0, 1, Servo.Direction.FORWARD);
            rightCapArm = new HDServo("rightCapArm", Values.ServoSpeedStats.HS_785HB, 0.23, 0, 1, Servo.Direction.FORWARD);
            lift = new HDCap(capLift, leftCapArm, rightCapArm);
        }
    }

}
