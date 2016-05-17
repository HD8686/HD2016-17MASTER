package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Servo.HDServo;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by akash on 5/7/2016.
 */
public class ExampleOpMode extends HDOpMode {
    DriveHandler robotDrive;
    HDDashboard mDashboard;
    HDServo mServoClimber;

    private enum exampleStates{
        delay,
        servoStep,
        VLF
    }


    @Override
    public void Initialize() {
        //Initialize Variables
        mDashboard = new HDDashboard(telemetry);
        robotDrive = new DriveHandler();
        mServoClimber = new HDServo(Values.HardwareMapKeys.climberServo, Values.ServoSpeedStats.HS_755HB, Values.ServoInit.climberServoInit);
        //Init Settings
        robotDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    @Override
    public void InitializeLoop() {
        mDashboard.displayPrintf(1, "Servo Position" + mServoClimber.getCurrPosition());
    }

    @Override
    public void Start() {
        mServoClimber.setPosition(.1, .1); //Added Scaling Code but still needs testing.
        robotDrive.tankDrive(.1, .1);
    }

    @Override
    public void continuousRun() {
        mDashboard.displayPrintf(1,"Servo Position" + mServoClimber.getCurrPosition());
    }




}
