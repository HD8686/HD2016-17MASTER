package com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive;

import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDNavX;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;

/**
 * Created by Akash on 5/7/2016.
 */

public class DriveHandler {

    public enum Side{
        Right,
        Left,
        Back,
    }
    public boolean firstRun = true;
    private DecimalFormat df;
    private DcMotor DHfrontLeft,DHfrontRight,DHbackLeft,DHbackRight;
    private HardwareMap mHardwareMap;
    private static DriveHandler instance = null;
    private DcMotorController.RunMode currRunMode = DcMotorController.RunMode.RUN_USING_ENCODERS;
    private HDNavX navX;

    public DriveHandler(HDNavX sensor){
        if(HDOpMode.getInstance() == null){
            throw new NullPointerException("HDOpMode not running!");
        }
        navX = sensor;
        InitMotors();
        setMode(currRunMode);
        instance = this;
    }

    private void InitMotors(){
        this.mHardwareMap = HDOpMode.getInstance().hardwareMap;
        this.DHfrontLeft = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.frontLeft);
        this.DHfrontRight = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.frontRight);
        this.DHbackLeft = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.backLeft);
        this.DHbackRight = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.backRight);
    }

    public void tankDrive(double LeftPower, double RightPower){
        LeftPower = Range.clip(LeftPower,-1,1);
        RightPower = Range.clip(RightPower,-1,1);
        DHfrontLeft.setPower(LeftPower);
        DHbackLeft.setPower(LeftPower);
        DHfrontRight.setPower(RightPower);
        DHbackRight.setPower(RightPower);
    }



    public void setMode(DcMotorController.RunMode RunMode){
        currRunMode = RunMode;
        DHfrontLeft.setMode(RunMode);
        DHfrontRight.setMode(RunMode);
        DHbackLeft.setMode(RunMode);
        DHbackRight.setMode(RunMode);
    }

    public void resetEncoders(){
        DHfrontLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        DHfrontRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        DHbackLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        DHbackRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        setMode(currRunMode);
    }

    public void reverseSide(Side reverse){
        if(reverse == Side.Left){
            DHfrontLeft.setDirection(DcMotor.Direction.REVERSE);
            DHfrontRight.setDirection(DcMotor.Direction.FORWARD);
            DHbackLeft.setDirection(DcMotor.Direction.REVERSE);
            DHbackRight.setDirection(DcMotor.Direction.FORWARD);
        }else if(reverse == Side.Right){
            DHfrontLeft.setDirection(DcMotor.Direction.FORWARD);
            DHfrontRight.setDirection(DcMotor.Direction.REVERSE);
            DHbackLeft.setDirection(DcMotor.Direction.FORWARD);
            DHbackRight.setDirection(DcMotor.Direction.REVERSE);
        }else if(reverse == Side.Back){
            DHfrontLeft.setDirection(DcMotor.Direction.FORWARD);
            DHfrontRight.setDirection(DcMotor.Direction.FORWARD);
            DHbackLeft.setDirection(DcMotor.Direction.REVERSE);
            DHbackRight.setDirection(DcMotor.Direction.REVERSE);
        }
    }


    public double getEncoderCount(){
        return ((DHfrontLeft.getCurrentPosition()+
                DHfrontRight.getCurrentPosition()+
                DHbackLeft.getCurrentPosition()+
                DHbackRight.getCurrentPosition())/4);
    }

    public String getEncoderCountDiag(){
        return "frontLeft: " + DHfrontLeft.getCurrentPosition() + ",frontRight: " + DHfrontRight.getCurrentPosition()
                + ",backRight: " + DHbackRight.getCurrentPosition() + ",backLeft: " + DHbackLeft.getCurrentPosition();
    }

    public void setMotorSpeeds(double[] Speeds){
        DHfrontLeft.setPower(Range.clip(Speeds[0], -1, 1));
        DHfrontRight.setPower(Range.clip(Speeds[1], -1, 1));
        DHbackLeft.setPower(Range.clip(Speeds[2], -1, 1));
        DHbackRight.setPower(Range.clip(Speeds[3], -1, 1));
    }
    
    public void setPowerFloat(){
        DHfrontLeft.setPowerFloat();
        DHfrontRight.setPowerFloat();
        DHbackLeft.setPowerFloat();
        DHbackRight.setPowerFloat();
    }


    //Angle is -180 to 180 degrees. Uses values from the Values Class.
    public void gyroTurn(double targetAngle){
        if(firstRun){
            navX.yawPIDController.setSetpoint(targetAngle);
            firstRun = false;
        }
        navX.yawPIDController.enable(true);
        df = new DecimalFormat("#.##");
        if (navX.yawPIDController.isNewUpdateAvailable(navX.yawPIDResult)) {
            if (navX.yawPIDResult.isOnTarget()) {
                setPowerFloat();
                HDOpMode.getInstance().telemetry.addData("Motor Output", df.format(0.00));
            } else {
                double output = navX.yawPIDResult.getOutput();
                tankDrive(output, -output);
                HDOpMode.getInstance().telemetry.addData("Motor Output", df.format(output) + ", " +
                        df.format(-output));
            }
        } else {
            /* No sensor update has been received since the last time  */
            /* the loop() function was invoked.  Therefore, there's no */
            /* need to update the motors at this time.                 */
        }
    }

    //Angle is -180 to 180 degrees. Uses values from the Values Class.
    public void VLF(double targetAngle){
        if(firstRun){
            navX.yawPIDController.setSetpoint(targetAngle);
            firstRun = false;
        }
        navX.yawPIDController.enable(true);
        df = new DecimalFormat("#.##");
        if (navX.yawPIDController.isNewUpdateAvailable(navX.yawPIDResult)) {
            if (navX.yawPIDResult.isOnTarget()) {
                tankDrive(Values.PIDSettings.DRIVE_SPEED_ON_TARGET,Values.PIDSettings.DRIVE_SPEED_ON_TARGET);
                HDOpMode.getInstance().telemetry.addData("Motor Output", df.format(Values.PIDSettings.DRIVE_SPEED_ON_TARGET) + ", " +
                        df.format(Values.PIDSettings.DRIVE_SPEED_ON_TARGET));
            } else {
                double output = navX.yawPIDResult.getOutput();
                tankDrive(limit(Values.PIDSettings.DRIVE_SPEED_ON_TARGET + output),limit(Values.PIDSettings.DRIVE_SPEED_ON_TARGET - output));
            }
        } else {
                /* No sensor update has been received since the last time  */
                /* the loop() function was invoked.  Therefore, there's no */
                /* need to update the motors at this time.                 */
        }
    }

    public double limit(double a) {
        return Math.min(Math.max(a, Values.PIDSettings.MIN_MOTOR_OUTPUT_VALUE), Values.PIDSettings.MAX_MOTOR_OUTPUT_VALUE);
    }

    /**
     * Drive method for Mecanum wheeled robots.
     *
     * A method for driving with Mecanum wheeled robots. There are 4 wheels
     * on the robot, arranged so that the front and back wheels are toed in 45 degrees.
     * When looking at the wheels from the top, the roller axles should form an X across the robot.
     *
     * This is designed to be directly driven by joystick axes.
     *
     * @param x The speed that the robot should drive in the X direction. [-1.0 to 1.0]
     * @param y The speed that the robot should drive in the Y direction.
     * This input is inverted to match the forward == -1.0 that joysticks produce. [-1.0 to 1.0]
     * @param rotation The rate of rotation for the robot that is completely independent of
     * the translation. [-1.0..1.0] (Basically the X2 value, so how much the robot should be rotating)
     * @param gyroAngle The current angle reading from the gyro.  Use this to implement field-oriented controls.
     */
    public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {

        double cosA = Math.cos(gyroAngle * (Math.PI / 180.0));
        double sinA = Math.sin(gyroAngle * (Math.PI / 180.0));

        double xIn = x * cosA - y * sinA;
        double yIn = x * sinA + y * cosA;

        double Motors[] = new double[4];
        Motors[0] = xIn + yIn + rotation; //kFrontLeft Motor
        Motors[1] = -xIn + yIn - rotation; //kFrontRight Motor
        Motors[2] = -xIn + yIn + rotation; //kRearLeft Motor
        Motors[3] = xIn + yIn - rotation; //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);
    }





    public static DriveHandler getInstance(){
        return instance;
    }


}
