package org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 8/16/2016.
 */
public class HDRange {
    private static HDRange instance = null;
    private static String  rangeHMKey;
    private ModernRoboticsI2cRangeSensor rangeRead;
    public HDRange(String rangeHMkey){
        rangeRead = HDOpMode.getInstance().hardwareMap.get(ModernRoboticsI2cRangeSensor.class, rangeHMkey);
        instance = this;
        this.rangeHMKey = rangeHMkey;
        HDDiagnosticDisplay.rangeList.add(this);
    }

    public double getUSValue(){
        return rangeRead.rawUltrasonic();
    }

    public double getODSValue(){
        return rangeRead.rawOptical();
    }

    public String getName(){
        return rangeHMKey;
    }

    public double getODSValueCM(){
        return rangeRead.cmOptical();
    }

}