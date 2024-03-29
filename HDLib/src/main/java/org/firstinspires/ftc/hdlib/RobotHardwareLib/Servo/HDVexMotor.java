package org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo;


import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;

/**
 * Created by Akash on 10/29/2016.
 */
public class HDVexMotor {
    private Servo mServo;
    private String servoHMName = "";
    private double currPower = 0.0;
    private static final double forwardSpeedValue = 1.0;
    private static final double backwardSpeedValue = 0.0;
    private static final double scaledBackwardSpeedValue = -1.0;
    private static final double scaledForwardSpeedValue = 1.0;

    public HDVexMotor(String servoName, Servo.Direction direction){
        if(HDOpMode.getInstance().hardwareMap.servo.get(servoName) == null){
            throw new NullPointerException("Servo is null");
        }
        this.servoHMName = servoName;
        HDOpMode.getInstance().diagnosticBackend.addVexMotor(this);
        this.mServo = HDOpMode.getInstance().hardwareMap.servo.get(servoName);
        this.mServo.setDirection(direction);
        this.mServo.setPosition(.5);
    }

    public void setDirection(Servo.Direction direction){
        this.mServo.setDirection(direction);
    }

    public void setPower(double Power){
        if(HDGeneralLib.isDifferenceWithin(Power, 0, 1)){
            this.mServo.setPosition((((forwardSpeedValue - backwardSpeedValue)*(Power - scaledBackwardSpeedValue))/(scaledForwardSpeedValue - scaledBackwardSpeedValue)) + backwardSpeedValue);
            currPower = Power;
        }else{
            throw new NullPointerException("HDVexMotor received value not within range");
        }
    }

    public String getName(){
        return servoHMName;
    }

    public double getCurrPower(){
        return currPower;
    }
}
