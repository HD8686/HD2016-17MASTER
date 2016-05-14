package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.lang.annotation.Target;

/**
 * Created by akash on 5/7/2016.
 */
public class HDServo implements HDLoopInterface.LoopTimer{
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
    }

    public double getCurrPosition(){
        return currPosition;
    }

    public void setPosition(double Position){
        try {   HDLoopInterface.getInstance().deregister(this); } catch (Exception e){}
        mServo.setPosition(Position);
    }

    public void setPosition(double Position, double Speed){
            this.targetPosition = Range.clip(Position, 0, 1);
            this.prevTime = System.currentTimeMillis();
            this.steppingRate = Math.abs(Speed)/1000;
            this.currPosition = mServo.getPosition();
            HDLoopInterface.getInstance().register(this);
    }

    public void stopServo(){
        targetPosition = currPosition;
    }


    @Override
    public void continuousCall() {
        if(targetPosition != currPosition){
            double currTime = System.currentTimeMillis();
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
            }else{
                HDLoopInterface.getInstance().deregister(this);
            }
            prevTime = currTime;
            mServo.setPosition(currPosition);
        }
    }
}
