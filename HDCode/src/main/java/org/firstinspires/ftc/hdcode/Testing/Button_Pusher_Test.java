package org.firstinspires.ftc.hdcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDButtonPusher;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;
import org.firstinspires.ftc.hdlib.General.Values;


/**
 * Created by Akash on 10/10/2016.
 */

@Autonomous(name = "Button Pusher Test", group = "Testing")
public class Button_Pusher_Test extends HDOpMode {

    HDRobot robot;
    HDDiagnosticDisplay diagnosticDisplay;
    @Override
    public void initialize() {
    robot = new HDRobot(Alliance.BLUE_ALLIANCE);
    diagnosticDisplay = new HDDiagnosticDisplay(mDisplay, robot.driveHandler);
    }

    @Override
    public void initializeLoop() {
        robot.driveHandler.reverseSide(HDDriveHandler.Side.Left);
    }

    @Override
    public void Start() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
            diagnosticDisplay.addProgramSpecificTelemetry(1, "leftColorStatus: " + robot.buttonPusher.readLeftColor().toString());
            diagnosticDisplay.addProgramSpecificTelemetry(2, "rightColorStatus: " + robot.buttonPusher.readRightColor().toString());
    }

}
