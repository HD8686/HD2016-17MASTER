package org.firstinspires.ftc.teamcode.HDFiles.OpModes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HDFiles.HDLib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.StateMachines.WaitTypes;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry.HDAutoDiagnostics;

/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp(name = "MecanumTesting")
public class MecanumTeleopTesting extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDAutoDiagnostics mHDAutoDiagnostics;
    HDNavX navX;
    DriveHandler robotDrive;
    HDStateMachine SM;

    @Override
    public void Initialize() {
        navX = new HDNavX();
        robotDrive = new DriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDAutoDiagnostics = new HDAutoDiagnostics(this, mDisplay,robotDrive);
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(DriveHandler.Side.Left);
    }


    @Override
    public void Start(){
    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(1,"GamepadX:" + gamepad1.left_stick_x/2);
            HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(1,"GamepadY:" + gamepad1.left_stick_y/2);
            robotDrive.mecanumDrive_Cartesian(gamepad1.left_stick_x/2,-gamepad1.left_stick_y/2,gamepad1.right_stick_x/2,navX.getSensorData().getYaw());



        }


    }




}
