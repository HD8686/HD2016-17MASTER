package com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDGeneralLib;
import com.qualcomm.ftcrobotcontroller.HDLib.HDLoopInterface;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Akash on 5/19/2016.
 */
public class HDGyro implements HDLoopInterface.LoopTimer {
    private ModernRoboticsI2cGyro mGyro;
    public static boolean isReady = false;
    private double lastGyroCal = 0.0;
    private static HDGyro instance = null;
    private enum gyroCalibration{
        gyroCalibration,
        waitForGyroCalibration,
    }
    private gyroCalibration currGyroCal = gyroCalibration.gyroCalibration;


    public HDGyro(String gyroHMkey){
        this.mGyro = (ModernRoboticsI2cGyro) HDOpMode.getInstance().hardwareMap.gyroSensor.get(gyroHMkey);
        instance = this;
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.InitializeLoop);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.ContinuousRun);
    }

    public static HDGyro getInstance(){
        return instance;
    }


    public double getHeading(){

        //Modern Robotics Z value is counterclockwise, so convert it to clockwise
        return -mGyro.getIntegratedZValue();
    }

    public void resetIntegrator(){
        mGyro.resetZAxisIntegrator();
    }




    @Override
    public void continuousCallOp() {
        if((HDGeneralLib.getCurrentTimeSeconds() < lastGyroCal + 6) && !isReady){
            isReady = false;
            HDDashboard.getInstance().displayPrintf(10, "Waiting on gyro before starting stateMachine");
        } else{
            HDLoopInterface.getInstance().deregister(this, HDLoopInterface.registrationTypes.InitializeLoop);
            HDLoopInterface.getInstance().deregister(this, HDLoopInterface.registrationTypes.ContinuousRun);
            isReady = true;
        }
    }



    @Override
    public void InitializeLoopOp() {
        Log.w("Gyro", "2");
        switch (currGyroCal){
            case gyroCalibration:
                lastGyroCal = HDGeneralLib.getCurrentTimeSeconds();
                mGyro.calibrate();
                currGyroCal = gyroCalibration.waitForGyroCalibration;
                break;
            case waitForGyroCalibration:
                HDDashboard.getInstance().displayPrintf(8, "Seconds until next gyro calibration: " + String.valueOf((lastGyroCal + 30) - HDGeneralLib.getCurrentTimeSeconds()));
                if(HDGeneralLib.getCurrentTimeSeconds() >= lastGyroCal + 30){
                    currGyroCal = gyroCalibration.gyroCalibration;
                }
                break;

        }
    }

    @Override
    public void StartOp() {

    }
}
