package org.firstinspires.ftc.teamcode.HDFiles.OpModes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.HDFiles.HDLib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.StateMachines.WaitTypes;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry.HDAutoDiagnostics;

/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp(name = "TeleopTestKeepPosition")
public class TeleopTestKeepPosition extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDAutoDiagnostics mHDAutoDiagnostics;
    HDNavX navX;
    DriveHandler robotDrive;
    HDStateMachine SM;
    double latestGyroUpdate = 0.0;
    ElapsedTime keepPosition;
    @Override
    public void Initialize() {
        keepPosition = new ElapsedTime();
        navX = new HDNavX();
        robotDrive = new DriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        mHDAutoDiagnostics = new HDAutoDiagnostics(this, mDisplay,robotDrive);
        robotDrive.resetEncoders();
        keepPosition.reset();
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(DriveHandler.Side.Left);
    }


    @Override
    public void Start() {

    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            if(Math.abs(gamepad1.left_stick_y) > 0 || Math.abs(gamepad1.right_stick_y) > 0){
                robotDrive.firstRun = true;
                navX.yawPIDController.enable(false);
                latestGyroUpdate = navX.getSensorData().getYaw();
                robotDrive.tankDrive(-gamepad1.left_stick_y/3,-gamepad1.right_stick_y/3);
                keepPosition.reset();
                mHDAutoDiagnostics.addProgramSpecificTelemetry(1,"Timer", String.valueOf(keepPosition.time()));
            }else if(keepPosition.time() > 1){
                robotDrive.gyroTurn(latestGyroUpdate);
            }else{
                robotDrive.motorBreak();
                latestGyroUpdate = navX.getSensorData().getYaw();
            }

        }


    }




}
