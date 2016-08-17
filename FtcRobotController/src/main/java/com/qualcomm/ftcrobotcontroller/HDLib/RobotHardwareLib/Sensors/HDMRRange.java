package com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors;

import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;

/**
 * Created by Akash on 8/16/2016.
 */
public class HDMRRange {
    private static HDMRRange instance = null;
    I2cDeviceReader rangeReader;
    byte rangeReadings[];
    public HDMRRange(String rangeHMkey){
        rangeReader = new I2cDeviceReader(HDOpMode.getInstance().hardwareMap.i2cDevice.get(rangeHMkey), 0x28, 0x04, 2);
        instance = this;
    }

    public double getUSValue(){
        rangeReadings = rangeReader.getReadBuffer();
        return (rangeReadings[0] & 0xFF);
    }

    public double getODSValue(){
        rangeReadings = rangeReader.getReadBuffer();
        return (rangeReadings[1] & 0xFF);
    }

}
