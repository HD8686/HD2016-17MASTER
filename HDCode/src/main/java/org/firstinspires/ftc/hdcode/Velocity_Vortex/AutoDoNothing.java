package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;

/**
 * Created by Height Differential on 10/21/2016.
 */
public class AutoDoNothing implements HDAuto{

    HDRobot robot;
    HDDiagnosticDisplay mHDDiagnosticDisplay;

    public AutoDoNothing(Alliance alliance){
        robot = new HDRobot(alliance);
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(),robot.driveHandler);
    }


    @Override
    public void start() {
        robot.navX.zeroYaw();
    }

    @Override
    public void runLoop(double elapsedTime) {

    }


}