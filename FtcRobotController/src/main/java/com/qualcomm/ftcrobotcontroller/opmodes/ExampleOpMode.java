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
        mDashboard.displayPrintf(1,"Servo Position" + mServoClimber.getCurrPosition());
        mServoClimber.setPosition(.8);
    }

    @Override
    public void Start() {
        //This needs to be tested.
        mServoClimber.setPosition(.1,.1); //Works now but needs scale testing, maybe input servo speed for scale? (Would Actually Probably Work) Add init position in start of servo init.
        //robotDrive.tankDrive(.1, .1);
    }

    @Override
    public void continuousRun() {
        mDashboard.displayPrintf(1,"Servo Position" + mServoClimber.getCurrPosition());
    }




}
