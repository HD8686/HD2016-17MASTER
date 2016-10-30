package org.firstinspires.ftc.hdlib.RobotHardwareLib.Motor;


import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;

/**
 * Created by Akash on 10/29/2016.
 */
public class HDVexMotor {
    private Servo mServo;
    private double currSpeed = 0.0;
    private String servoHMName = "";
    private static final double ForwardSpeedValue = 1.0;
    private static final double BackwardSpeedValue = 0.0;
    private static final double scaledRangeBackwards = -1.0;
    private static final double scaledRangeForwards = 1.0;

    HDVexMotor(String servoName, Servo.Direction direction){
        if(HDOpMode.getInstance().hardwareMap.servo.get(servoName) == null){
            throw new NullPointerException("Servo is null");
        }
        this.servoHMName = servoName;
        this.mServo = HDOpMode.getInstance().hardwareMap.servo.get(servoName);
        this.mServo.setDirection(direction);
        this.mServo.setPosition(.5);
    }

    public void setDirection(Servo.Direction direction){
        this.mServo.setDirection(direction);
    }

    public void setPower(double Power){
        if(HDGeneralLib.isDifferenceWithin(Power, 0, 1)){
            this.mServo.setPosition((((ForwardSpeedValue-BackwardSpeedValue)*(Power - scaledRangeBackwards))/(scaledRangeForwards-scaledRangeBackwards)) + BackwardSpeedValue);
        }else{
            throw new NullPointerException("HDVexMotor received value not within range");
        }
    }

    public String getName(){
        return servoHMName;
    }
}
