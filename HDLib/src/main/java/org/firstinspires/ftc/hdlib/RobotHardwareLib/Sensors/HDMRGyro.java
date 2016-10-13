package org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;

import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;


/**
 * Created by Akash on 5/19/2016.
 */
public class HDMRGyro implements HDLoopInterface.LoopTimer {
    private ModernRoboticsI2cGyro mGyro;
    public static boolean isReady = false;
    private double lastGyroCal = 0.0;
    private static HDMRGyro instance = null;
    private enum gyroCalibration{
        gyroCalibration,
        waitForGyroCalibration,
    }
    private gyroCalibration currGyroCal = gyroCalibration.gyroCalibration;


    public HDMRGyro(String gyroHMkey){
        this.mGyro = (ModernRoboticsI2cGyro) HDOpMode.getInstance().hardwareMap.gyroSensor.get(gyroHMkey);
        instance = this;
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.InitializeLoop);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.ContinuousRun);
    }

    public static HDMRGyro getInstance(){
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
        if((HDGeneralLib.getCurrentTimeSeconds() < lastGyroCal + 3) && !isReady){
            isReady = false;
            HDOpMode.getInstance().telemetry.addData("Gyro", "Gyro Is Calibrating");
            HDOpMode.getInstance().telemetry.addData("Gyro1", "Time Left: " + String.valueOf(Math.round((lastGyroCal+3) - HDGeneralLib.getCurrentTimeSeconds())) + " Seconds");
          } else{
            HDLoopInterface.getInstance().deregister(this, HDLoopInterface.registrationTypes.InitializeLoop);
            HDLoopInterface.getInstance().deregister(this, HDLoopInterface.registrationTypes.ContinuousRun);
            isReady = true;
        }
    }



    @Override
    public void InitializeLoopOp() {
        switch (currGyroCal){
            case gyroCalibration:
                lastGyroCal = HDGeneralLib.getCurrentTimeSeconds();
                mGyro.calibrate();
                currGyroCal = gyroCalibration.waitForGyroCalibration;
                break;
            case waitForGyroCalibration:
                HDOpMode.getInstance().telemetry.addData("Gyro", "Wait for Gyro Recalibration");
                HDOpMode.getInstance().telemetry.addData("Gyro1", "Time Left: " + String.valueOf(Math.round(lastGyroCal + 30 - HDGeneralLib.getCurrentTimeSeconds())) + " Seconds");
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
