package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;

/**
 * Created by Height Differential on 10/21/2016.
 */
public class AutoDoNothing implements HDAuto{

    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDDriveHandler robotDrive;
    HDNavX navX;

    public AutoDoNothing(){
        robotDrive = new HDDriveHandler(navX);
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(),robotDrive);
        navX = new HDNavX();
    }


    @Override
    public void start() {

    }

    @Override
    public void runLoop(double elapsedTime) {

    }
}
