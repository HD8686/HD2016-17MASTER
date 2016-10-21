package org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems;

import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;

/**
 * Created by Akash on 10/20/2016.
 */
public class HDButtonPusher {
    HDMRColor leftColor;
    HDMRColor rightColor;
    HDServo leftPusher;
    HDServo rightPusher;


    public enum beaconColor{
        RED,
        BLUE,
        INCONCLUSIVE
    }

    public HDButtonPusher(HDMRColor left, HDMRColor right, HDServo leftServo, HDServo rightServo){
        leftColor = left;
        rightColor = right;
        leftColor.getSensor().enableLed(false);
        rightColor.getSensor().enableLed(false);
        leftPusher = leftServo;
        rightPusher = rightServo;
    }

    public void extendRightServo(){
        rightPusher.setPosition(1);
    }

    public void extendLeftServo(){
        leftPusher.setPosition(1);
    }

    public void retractRightServo(){
        rightPusher.setPosition(0);
    }

    public void retractLeftServo(){
        leftPusher.setPosition(0);
    }



    public beaconColor readLeftColor(){
        int redValue = leftColor.getSensor().red();
        int greenValue = leftColor.getSensor().green();
        int blueValue = leftColor.getSensor().blue();
        boolean isRed = redValue > blueValue && redValue > greenValue;
        boolean isBlue = blueValue > redValue && blueValue > greenValue;
        if(isRed && isBlue){
            return beaconColor.INCONCLUSIVE;
        } else if(isRed){
            return beaconColor.RED;
        } else if(isBlue){
            return beaconColor.BLUE;
        } else{
            return beaconColor.INCONCLUSIVE;
        }
    }

    public beaconColor readRightColor(){
        int redValue = rightColor.getSensor().red();
        int greenValue = rightColor.getSensor().green();
        int blueValue = rightColor.getSensor().blue();
        boolean isRed = redValue > blueValue && redValue > greenValue;
        boolean isBlue = blueValue > redValue && blueValue > greenValue;
        if(isRed && isBlue){
            return beaconColor.INCONCLUSIVE;
        } else if(isRed){
            return beaconColor.RED;
        } else if(isBlue){
            return beaconColor.BLUE;
        } else{
            return beaconColor.INCONCLUSIVE;
        }
    }



}


