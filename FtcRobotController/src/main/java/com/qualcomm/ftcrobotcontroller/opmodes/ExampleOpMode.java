package com.qualcomm.ftcrobotcontroller.opmodes;

import android.os.Handler;

import com.qualcomm.ftcrobotcontroller.HDLib.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.HDServo;
import com.qualcomm.ftcrobotcontroller.HDLib.Names;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by akash on 5/7/2016.
 */
public class ExampleOpMode extends HDOpMode {
    DriveHandler robotDrive;
    HDDashboard mDashboard;
    HDServo mServoClimber;


    @Override
    public void Initialize() {
        mDashboard = new HDDashboard(telemetry);
        mServoClimber = new HDServo(Names.climbersServo);
        robotDrive = new DriveHandler();
        robotDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    @Override
    public void InitializeLoop() {

    }

    @Override
    public void Start() {
        mServoClimber.setSpeed(10);
        mServoClimber.setPosition(.5);
        robotDrive.tankDrive(.1, .1);
    }

    @Override
    public void continuousRun() {

    }


}
