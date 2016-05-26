package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDGyro;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Akash on 5/25/2016.
 */
public class FieldCentricTankDrive extends HDOpMode {
    HDGyro mGyro;
    public HDDashboard mDisplay;
    DriveHandler robotDrive;

    @Override
    public void InitializeLoop() {

    }

    @Override
    public void Initialize() {
        mDisplay = new HDDashboard(telemetry);
        mGyro = new HDGyro(Values.HardwareMapKeys.Gyro);
        robotDrive = new DriveHandler();
    }

    @Override
    public void continuousRun() {
        //DISABLE MOTORS FOR FIRST TEST REMEMBER;
        robotDrive.fieldCentricTank(gamepad1.left_stick_x,gamepad1.left_stick_y);
    }

    @Override
    public void Start() {
        robotDrive.setOldSteve();
        robotDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }
}
