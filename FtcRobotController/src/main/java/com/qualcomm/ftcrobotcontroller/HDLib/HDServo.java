package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by akash on 5/7/2016.
 */
public class HDServo {
    private Servo mServo;

    public HDServo(String servoName){
        if(HDOpMode.getInstance().hardwareMap.servo.get(servoName) == null){
            throw new NullPointerException("Servo is null");
        }
        this.mServo = HDOpMode.getInstance().hardwareMap.servo.get(servoName);
        HDOpMode.getInstance().ServoObjList.add(this);
    }



    public void SetPosition(double Position, double Speed){
        Position = Range.clip(Position,0,1);
        Speed = Range.clip(Speed,1,10);


    }

    public void SetPosition(double Position){
        Position = Range.clip(Position,0,1);
        mServo.setPosition(Position);
    }

    public void SpeedPositionManager(){

    }

}
