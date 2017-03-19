package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.hdlib.Controls.HDGamepad;
import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp
public class Lift_Testing extends HDOpMode implements HDGamepad.HDButtonMonitor{

    HDDiagnosticDisplay diagnosticDisplay;
    HDRobot robot;
    HDGamepad driverGamepad;
    HDGamepad servoBoyGamepad;


    @Override
    public void initialize() {
        robot = new HDRobot(Alliance.BLUE_ALLIANCE);
        diagnosticDisplay = new HDDiagnosticDisplay(mDisplay, robot.driveHandler);
        driverGamepad = new HDGamepad(gamepad1, this);
        servoBoyGamepad = new HDGamepad(gamepad2, this);
        robot.shooter.lowerCollector();
        robot.lift.lowerArms();
    }

    @Override
    public void initializeLoop() {

    }


    @Override
    public void Start(){
        driverGamepad.setGamepad(gamepad1);
        servoBoyGamepad.setGamepad(gamepad2);
        robot.shooter.resetEncoders();
        robot.lift.resetEncoders();
        robot.lift.raiseArms();
    }

    @Override
    public void continuousRun(double elapsedTime) {
        diagnosticDisplay.addProgramSpecificTelemetry(1, "Motor Power: " + String.valueOf(robot.capLift.getPower()));
        diagnosticDisplay.addProgramSpecificTelemetry(2, "Motor Target: " + String.valueOf(robot.capLift.getTargetPosition()));
        diagnosticDisplay.addProgramSpecificTelemetry(3, "Motor Position: " + String.valueOf(robot.capLift.getCurrentPosition()));
    }


    @Override
    public void buttonChange(HDGamepad instance, HDGamepad.gamepadButtonChange button, boolean pressed) {
        if(instance == driverGamepad){
            switch (button) {
                case A:
                    if(pressed)
                        robot.lift.extendLift();
                    break;
                case B:
                    if(pressed)
                        robot.lift.retractLift();
                    break;
                case X:
                    break;
                case Y:
                    break;
                case DPAD_LEFT:
                    break;
                case DPAD_RIGHT:
                    break;
                case DPAD_UP:
                    break;
                case DPAD_DOWN:
                    break;
                case LEFT_BUMPER:
                    break;
                case RIGHT_BUMPER:
                    break;
                case LEFT_TRIGGER:
                    break;
                case RIGHT_TRIGGER:
                    break;
                case START:
                    break;
            }
        }else if(instance == servoBoyGamepad){
            switch (button) {
                case A:
                    break;
                case B:
                    break;
                case X:
                    break;
                case Y:
                    break;
                case DPAD_LEFT:
                    break;
                case DPAD_RIGHT:
                    break;
                case DPAD_UP:
                    break;
                case DPAD_DOWN:
                    break;
                case LEFT_BUMPER:
                    break;
                case RIGHT_BUMPER:
                    break;
                case LEFT_TRIGGER:
                    break;
                case RIGHT_TRIGGER:
                    break;
                case START:
                    break;
            }
        }
    }
}
