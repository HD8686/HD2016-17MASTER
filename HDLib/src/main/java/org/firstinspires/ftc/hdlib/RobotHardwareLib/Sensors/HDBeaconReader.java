package org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors;

/**
 * Created by Akash on 10/20/2016.
 */
public class HDBeaconReader {
    HDMRColor leftColor;
    HDMRColor rightColor;

    public enum beaconColor{
        RED,
        BLUE,
        INCONCLUSIVE
    }

    public HDBeaconReader(HDMRColor left, HDMRColor right){
        leftColor = left;
        rightColor = right;
        leftColor.getSensor().enableLed(false);
        rightColor.getSensor().enableLed(false);
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


