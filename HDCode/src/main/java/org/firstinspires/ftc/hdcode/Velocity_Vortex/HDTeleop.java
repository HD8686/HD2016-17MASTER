package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp(name = "Mecanum Teleop Testing", group = "Testing")
public class HDTeleop extends HDOpMode {
    /* Remember that for robot drive, you need
     *to make sure that the motor hardware map
     * names are defined in the Values class.
     */
    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDDriveHandler robotDrive;
    HDStateMachine SM;

    @Override
    public void initialize() {
        navX = new HDNavX();
        robotDrive = new HDDriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(mDisplay,robotDrive);
    }

    @Override
    public void initializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
    }


    @Override
    public void Start(){
    }

    @Override
    public void continuousRun(double elapsedTime) {
        if(SM.ready()){
            HDDiagnosticDisplay.getInstance().addProgramSpecificTelemetry(1,"GamepadX:" + gamepad1.left_stick_x/2);
            HDDiagnosticDisplay.getInstance().addProgramSpecificTelemetry(1,"GamepadY:" + gamepad1.left_stick_y/2);
            robotDrive.mecanumDrive_Cartesian(gamepad1.left_stick_x/2,-gamepad1.left_stick_y/2,gamepad1.right_stick_x/2,navX.getSensorData().getYaw());
        }


    }




}
