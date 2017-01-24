package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.util.Log;

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

@TeleOp(name = "HDTeleop", group = "Teleop")
public class HDTeleop extends HDOpMode implements HDGamepad.HDButtonMonitor{

    private enum DriveMode{
        TANK_DRIVE,
        MECANUM_FIELD_CENTRIC,
    }

    double driveSpeed = 0.6;
    HDDiagnosticDisplay diagnosticDisplay;
    HDRobot robot;
    DriveMode driveMode;
    HDGamepad driverGamepad;
    HDGamepad servoBoyGamepad;
    Alliance alliance;
    ElapsedTime intervalRun;



    //Flywheel RPM Calc Variables

    // Encoder
    double encoderCounts;         ///< current encoder count
    double encoderCountsPrev;    ///< current encoder count

    // velocity measurement
    double motorRPM;         ///< current velocity in rpm
    double prevCalcTime;          ///< Time of last velocity calculation

    // TBH control algorithm variables
    double targetRPM;        ///< targetRPM velocity
    double currentError;          ///< error between actual and targetRPM velocities
    double lastError;             ///< error last time update called
    double gain;                   ///< gain
    double flywheelMotorSpeed;                  ///< final flywheelMotorSpeed out of TBH (0.0 to 1.0)
    double previousFlywheelMotorSpeed;          ///< flywheelMotorSpeed at last zero crossing
    boolean firstCross;            ///< flag indicating first zero crossing
    double driveApprox;           ///< estimated open loop flywheelMotorSpeed

    static double flywheelTicksperRev = 1120;

    @Override
    public void initialize() {
        try {
            alliance = Alliance.retrieveAlliance(hardwareMap.appContext); //Retrieve Last Alliance from Autonomous.
        }catch (Exception e){
            alliance = Alliance.BLUE_ALLIANCE;
        }
        robot = new HDRobot(alliance);
        diagnosticDisplay = new HDDiagnosticDisplay(mDisplay, robot.driveHandler);
        driverGamepad = new HDGamepad(gamepad1, this);
        servoBoyGamepad = new HDGamepad(gamepad2, this);
        robot.shooter.raiseCollector();
        intervalRun = new ElapsedTime();
    }

    @Override
    public void initializeLoop() {

    }


    @Override
    public void Start(){
        if(robot.navX.getSensorData().isCalibrating()) {
            driveMode = DriveMode.TANK_DRIVE;
        } else {
            driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
        }
        driverGamepad.setGamepad(gamepad1);
        servoBoyGamepad.setGamepad(gamepad2);
        robot.shooter.lowerCollector();
        robot.shooter.resetEncoders();
        gain = 0.00025;
    }

    @Override
    public void continuousRun(double elapsedTime) {
        diagnosticDisplay.addProgramSpecificTelemetry(1, "Alliance: %s", alliance.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(2, "Drive Mode: %s", driveMode.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(3, "Drive Speed: "+ String.valueOf(driveSpeed *100) + " Percent");
        robotDrive();
        setVelocityControl(600, 0.375); //That needs a TON of tuning. (https://www.vexforum.com/index.php/15195-flywheel-velocity-control-revisited/0)
        if(intervalRun.milliseconds() > 35){
            intervalRun.reset();
            calculateFlywheelVelocity();
            diagnosticDisplay.addProgramSpecificTelemetry(4, "Motor Velocity: " + String.valueOf(motorRPM));
            updateFlywheelVelocity();
            diagnosticDisplay.addProgramSpecificTelemetry(5, "TBH Control: " + String.valueOf(flywheelMotorSpeed));
            diagnosticDisplay.addProgramSpecificTelemetry(6, "Flywheel Enc. Cnt: " + String.valueOf(robot.shooter.getFlywheelEncoderCount()));
            Log.w("Seconds, FlywheelVel", String.valueOf(motorRPM) + " ," + String.valueOf(elapsedTime));
            //robot.shooter.setFlywheelPower(flywheelMotorSpeed);
        }
    }

    private void setVelocityControl(double velocity, double predictedDrive){

        targetRPM = velocity;

        currentError = targetRPM - motorRPM;
        lastError = currentError;

        driveApprox = predictedDrive;

        firstCross = true;

        previousFlywheelMotorSpeed = 0;

    }

    private void calculateFlywheelVelocity(){

        double delta_ms;
        double delta_enc;

        encoderCounts = robot.shooter.getFlywheelEncoderCount();

        delta_ms = System.currentTimeMillis() - prevCalcTime;
        prevCalcTime = System.currentTimeMillis();

        delta_enc = (encoderCounts - encoderCountsPrev);

        encoderCountsPrev = encoderCounts;

        motorRPM = (1000.0 / delta_ms) * delta_enc * 60 / flywheelTicksperRev;


    }

    private void updateFlywheelVelocity(){
        currentError = targetRPM - motorRPM;

        flywheelMotorSpeed = flywheelMotorSpeed + (currentError * gain);

        if(flywheelMotorSpeed > 1){
            flywheelMotorSpeed = 1;
        }
        if(flywheelMotorSpeed < 0){
            flywheelMotorSpeed = 0;
        }

        if(Math.signum(currentError) != Math.signum(lastError)){
            if(firstCross){
                flywheelMotorSpeed = driveApprox;
                firstCross = false;
            }else{
                flywheelMotorSpeed = 0.5 * (flywheelMotorSpeed + previousFlywheelMotorSpeed);
            }


            previousFlywheelMotorSpeed = flywheelMotorSpeed;
        }

        lastError = currentError;
    }


    private void robotDrive(){
            switch (driveMode) {
                case TANK_DRIVE:
                    robot.driveHandler.tankDrive(-gamepad1.left_stick_y* driveSpeed, -gamepad1.right_stick_y* driveSpeed);
                    break;
                case MECANUM_FIELD_CENTRIC:
                    if(gamepad1.y){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*.5, gamepad1.left_stick_y*.5, 180.0, robot.navX.getYaw());
                    }else if(gamepad1.b){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*.5, gamepad1.left_stick_y*.5, -90.0, robot.navX.getYaw());
                    }else{
                        robot.driveHandler.mecanumDrive_Cartesian(gamepad1.left_stick_x * driveSpeed, gamepad1.left_stick_y * driveSpeed, gamepad1.right_stick_x * driveSpeed, robot.navX.getYaw());
                    }
                    break;
            }
    }

    @Override
    public void buttonChange(HDGamepad instance, HDGamepad.gamepadButtonChange button, boolean pressed) {
        if(instance == driverGamepad){
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
                    if(pressed && !robot.navX.getSensorData().isCalibrating())
                        driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
                    break;
                case DPAD_DOWN:
                    if(pressed)
                        driveMode = DriveMode.TANK_DRIVE;
                    break;
                case LEFT_BUMPER:
                    if(pressed){
                        driveSpeed = driveSpeed - 0.2;
                        driveSpeed = Range.clip(driveSpeed, 0.2,1);
                    }
                    break;
                case RIGHT_BUMPER:
                    if(pressed){
                        driveSpeed = driveSpeed + 0.2;
                        driveSpeed = Range.clip(driveSpeed, 0.2,1);
                    }
                    break;
                case LEFT_TRIGGER:
                    break;
                case RIGHT_TRIGGER:
                    break;
                case START:
                    if(pressed)
                        robot.navX.zeroYaw();
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
