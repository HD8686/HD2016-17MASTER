package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.lang.annotation.Target;

/**
 * Created by akash on 5/7/2016.
 */
public class HDServo {
    private Servo mServo;
    private double steppingRate = 10;
    private double currPosition = 0;
    private double targetPosition = 0;
    double prevTime = 0;

    public HDServo(String servoName){
        if(HDOpMode.getInstance().hardwareMap.servo.get(servoName) == null){
            throw new NullPointerException("Servo is null");
        }
        this.mServo = HDOpMode.getInstance().hardwareMap.servo.get(servoName);
        HDOpMode.getInstance().ServoObjList.add(this);
    }

    public double getCurrPosition(){
        return currPosition;
    }

    public void setSpeed(double Speed){
        steppingRate = Range.clip(Speed,0,100);
    }

    public void setPosition(double Position){
        if(targetPosition != currPosition) {
            this.prevTime = System.currentTimeMillis() / 1000;
            targetPosition = Range.clip(Position, 0, 1);
        }
    }

    public void stopServo(){
        targetPosition = currPosition;
    }

    public void SpeedPositionManager(){
        if(targetPosition != currPosition){
            double currTime = System.currentTimeMillis()/1000;
            double posChange = steppingRate * (currTime - prevTime);

            if(currPosition < targetPosition){
                currPosition = currPosition + posChange;
                if(currPosition > targetPosition){
                    currPosition = targetPosition;
                }
            } else if(currPosition > targetPosition){
                currPosition = currPosition - posChange;
                if(currPosition < targetPosition){
                    currPosition = targetPosition;
                }
            }
            prevTime = currTime;
            mServo.setPosition(currPosition);
        }
    }

}
