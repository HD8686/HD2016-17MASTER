package org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;

/**
 * Created by Akash on 10/19/2016.
 */
public class HDMRColor{
    private String colorHMKey;
    private ModernRoboticsI2cColorSensor colorSensor;

    public HDMRColor(String rangeHMkey){
        colorSensor = HDOpMode.getInstance().hardwareMap.get(ModernRoboticsI2cColorSensor.class, rangeHMkey);
        this.colorHMKey = rangeHMkey;
        HDOpMode.getInstance().diagnosticBackend.addColor(this);
    }



    public String getName(){
        return this.colorHMKey;
    }

    public ModernRoboticsI2cColorSensor getSensor(){
        return colorSensor;
    }



}
