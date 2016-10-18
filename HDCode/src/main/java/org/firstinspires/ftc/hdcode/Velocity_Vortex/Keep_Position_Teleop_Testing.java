package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp(name = "Keep_Position_Teleop_Testing", group = "Testing")
public class Keep_Position_Teleop_Testing extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    double latestGyroUpdate = 0.0;
    ElapsedTime keepPosition;
    @Override
    public void Initialize() {
        keepPosition = new ElapsedTime();
        navX = new HDNavX();
        robotDrive = new HDDriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(this, mDisplay,robotDrive);
        robotDrive.resetEncoders();
        keepPosition.reset();
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
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
                mHDDiagnosticDisplay.addProgramSpecificTelemetry(1,"Timer", String.valueOf(keepPosition.time()));
            }else if(keepPosition.time() > 1){
                robotDrive.gyroTurn(latestGyroUpdate);
            }else{
                robotDrive.motorBreak();
                latestGyroUpdate = navX.getSensorData().getYaw();
            }

        }


    }




}
