package org.firstinspires.ftc.hdlib.OpModeManagement;

import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash on 10/19/2016.
 */
public class HDDiagnosticBackend {
    private List<HDServo> servoList;
    private List<HDMRRange> rangeList;
    private List<HDMROpticalDistance> odsList;
    private List<HDMRColor> colorList;

    public HDDiagnosticBackend(){
        servoList = new ArrayList<HDServo>();
        rangeList = new ArrayList<HDMRRange>();
        odsList = new ArrayList<HDMROpticalDistance>();
        colorList = new ArrayList<HDMRColor>();
    }

    public void addServo(HDServo servo){
        servoList.add(servo);
    }

    public void addRange(HDMRRange range){
        rangeList.add(range);
    }

    public void addODS(HDMROpticalDistance ods){
        odsList.add(ods);
    }

    public void addColor(HDMRColor color){
        colorList.add(color);
    }

    public List<HDServo> getServo(){
        return servoList;
    }

    public List<HDMRRange> getRange(){
        return rangeList;
    }

    public List<HDMRColor> getColor(){
        return colorList;
    }

    public List<HDMROpticalDistance> getODS(){
        return odsList;
    }

}
