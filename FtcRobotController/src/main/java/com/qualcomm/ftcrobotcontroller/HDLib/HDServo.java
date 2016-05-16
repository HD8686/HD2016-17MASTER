package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Akash on 5/7/2016.
 */
public class HDServo implements HDLoopInterface.LoopTimer{
    private Servo mServo;
    private double steppingRate = 0.0;
    private double currPosition = 0.0;
    private double targetPosition = 0.0;
    private double prevTime = 0.0;
    private double maxSpeed = 0.0;

    public HDServo(String servoName, double servoStats){
        if(HDOpMode.getInstance().hardwareMap.servo.get(servoName) == null){
            throw new NullPointerException("Servo is null");
        }
        this.mServo = HDOpMode.getInstance().hardwareMap.servo.get(servoName);
        this.maxSpeed = ((1/servoStats) * 60.0);
    }

    public double getCurrPosition(){
        return currPosition;
    }

    public void setPosition(double Position){
        HDLoopInterface.getInstance().deregister(this);
        this.mServo.setPosition(Position);
    }

    public void setPosition(double Position, double Speed){
            this.targetPosition = Range.clip(Position, 0, 1);
            this.prevTime = HDBaseLib.getCurrentTimeSeconds();
            this.steppingRate = Math.abs(Range.clip(Speed,0,1)) * this.maxSpeed;
            this.currPosition = mServo.getPosition();
            HDLoopInterface.getInstance().register(this);
    }

    public void stopServo(){
        targetPosition = currPosition;
    }


    @Override
    public void continuousCall() {
        if(targetPosition != currPosition){
            double currTime = HDBaseLib.getCurrentTimeSeconds();
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
