package org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.apache.commons.lang3.math.NumberUtils;
import org.firstinspires.ftc.hdlib.Alliance;
import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.Values;

import java.text.DecimalFormat;



/**
 * Created by Akash on 5/7/2016.
 */

public class HDDriveHandler {

    public enum Side{
        Right,
        Left,
        Back,
    }
    public boolean firstRun = true;
    private DecimalFormat df;
    private DcMotor DHfrontLeft,DHfrontRight,DHbackLeft,DHbackRight;
    private HardwareMap mHardwareMap;
    private static HDDriveHandler instance = null;
    private DcMotor.RunMode currRunMode = DcMotor.RunMode.RUN_USING_ENCODER;
    private HDNavX navX;
    private Alliance alliance = Alliance.BLUE_ALLIANCE;

    public HDDriveHandler(HDNavX sensor){
        if(HDOpMode.getInstance() == null){
            throw new NullPointerException("HDOpMode not running!");
        }
        navX = sensor;
        InitMotors();
        setMode(currRunMode);
        instance = this;
    }

    public void setAlliance(Alliance alliance){
        this.alliance = alliance;
    }



    private void InitMotors(){
        this.mHardwareMap = HDOpMode.getInstance().hardwareMap;
        this.DHfrontLeft = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.frontLeft);
        this.DHfrontRight = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.frontRight);
        this.DHbackLeft = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.backLeft);
        this.DHbackRight = mHardwareMap.dcMotor.get(Values.HardwareMapKeys.backRight);
        DHfrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DHfrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DHbackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DHbackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void tankDrive(double LeftPower, double RightPower){
        LeftPower = Range.clip(LeftPower,-1,1);
        RightPower = Range.clip(RightPower,-1,1);
        DHfrontLeft.setPower(LeftPower);
        DHbackLeft.setPower(LeftPower);
        DHfrontRight.setPower(RightPower);
        DHbackRight.setPower(RightPower);
    }



    public void setMode(DcMotor.RunMode RunMode){
        currRunMode = RunMode;
        DHfrontLeft.setMode(RunMode);
        DHfrontRight.setMode(RunMode);
        DHbackLeft.setMode(RunMode);
        DHbackRight.setMode(RunMode);
    }

    public void resetEncoders(){
        DHfrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DHfrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DHbackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DHbackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

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

    public String getEncoderCountDiagFront(){
        return "frontLeft: " + DHfrontLeft.getCurrentPosition() + ", frontRight: " + DHfrontRight.getCurrentPosition();
    }

    public String getEncoderCountDiagBack(){
        return  "backRight: " + DHbackRight.getCurrentPosition() + ", backLeft: " + DHbackLeft.getCurrentPosition();
    }

    public String getMotorSpeedDiagFront(){
        return "frontLeft: " + DHfrontLeft.getPower() + ", frontRight: " + DHfrontRight.getPower();
    }

    public String getMotorSpeedDiagBack(){
        return "backRight: " + DHbackRight.getPower() + ", backLeft: " + DHbackLeft.getPower();
    }
    public void setMotorSpeeds(double[] Speeds){
        DHfrontLeft.setPower(Range.clip(Speeds[0], -1, 1));
        DHfrontRight.setPower(Range.clip(Speeds[1], -1, 1));
        DHbackLeft.setPower(Range.clip(Speeds[2], -1, 1));
        DHbackRight.setPower(Range.clip(Speeds[3], -1, 1));
    }

    public void motorBreak(){
        DHfrontLeft.setPower(0);
        DHfrontRight.setPower(0);
        DHbackLeft.setPower(0);
        DHbackRight.setPower(0);
    }

    public void gyroTurn(double targetAngle){
        if(alliance == Alliance.RED_ALLIANCE){
            targetAngle = -targetAngle;
        }
        if(firstRun){
            navX.yawPIDController.setOutputRange(Values.PIDSettings.GYRO_MIN_MOTOR_OUTPUT_VALUE, Values.PIDSettings.GYRO_MAX_MOTOR_OUTPUT_VALUE);
            navX.yawPIDController.setSetpoint(targetAngle);
            firstRun = false;
        }
        navX.yawPIDController.enable(true);
        df = new DecimalFormat("#.##");
        if (navX.yawPIDController.isNewUpdateAvailable(navX.yawPIDResult)) {
            if (navX.yawPIDResult.isOnTarget()) {
                motorBreak();
            } else {
                double output = (navX.yawPIDResult.getOutput());
                tankDrive(output, -output);
            }
        }
    }

    public void constantGyroTurnLowSpeed(double targetAngle){
        if(alliance == Alliance.RED_ALLIANCE){
            targetAngle = -targetAngle;
        }
        if (HDGeneralLib.isDifferenceWithin(navX.getSensorData().getYaw(), targetAngle, .5))
            motorBreak();
        else if (navX.getSensorData().getYaw() < targetAngle)
            tankDrive(.1, -.1);
        else if (navX.getSensorData().getYaw() > targetAngle)
            tankDrive(-.1, .1);
    }

    //Angle is -180 to 180 degrees. Uses values from the Values Class.
    public void VLF(double targetAngle, DcMotor.Direction direction){
        if(alliance == Alliance.RED_ALLIANCE){
            targetAngle = -targetAngle;
        }
        if(firstRun){
            navX.yawPIDController.setOutputRange(Values.PIDSettings.VLF_MIN_MOTOR_OUTPUT_VALUE, Values.PIDSettings.VLF_MAX_MOTOR_OUTPUT_VALUE);
            navX.yawPIDController.setSetpoint(targetAngle);
            firstRun = false;
        }
        navX.yawPIDController.enable(true);
        df = new DecimalFormat("#.##");
        if (navX.yawPIDController.isNewUpdateAvailable(navX.yawPIDResult)) {
            if (navX.yawPIDResult.isOnTarget()) {
                if(direction == DcMotor.Direction.FORWARD) {
                    tankDrive((Values.PIDSettings.DRIVE_SPEED_ON_TARGET), (Values.PIDSettings.DRIVE_SPEED_ON_TARGET));
                }else{
                    tankDrive(-(Values.PIDSettings.DRIVE_SPEED_ON_TARGET), -(Values.PIDSettings.DRIVE_SPEED_ON_TARGET));
                }
            } else {
                double output = navX.yawPIDResult.getOutput();
                if(direction == DcMotor.Direction.FORWARD) {
                    tankDrive(limitVLF((Values.PIDSettings.DRIVE_SPEED_ON_TARGET + output)), limitVLF((Values.PIDSettings.DRIVE_SPEED_ON_TARGET - output)));
                }else{
                    tankDrive(limitVLF((-Values.PIDSettings.DRIVE_SPEED_ON_TARGET + output)), limitVLF((-Values.PIDSettings.DRIVE_SPEED_ON_TARGET - output)));
                }
            }
        } else {
                /* No sensor update has been received since the last time  */
                /* the loop() function was invoked.  Therefore, there's no */
                /* need to update the motors at this time.                 */
        }
    }

    public double limitVLF(double a) {
        return Math.min(Math.max(a, Values.PIDSettings.VLF_MIN_MOTOR_OUTPUT_VALUE), Values.PIDSettings.VLF_MAX_MOTOR_OUTPUT_VALUE);
    }

    public double limitMecanum(double a) {
        return Math.min(Math.max(a, Values.PIDSettings.MECANUM_MIN_MOTOR_OUTPUT_VALUE), Values.PIDSettings.MECANUM_MAX_MOTOR_OUTPUT_VALUE);
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

    public void mecanumDrive_Polar(double magnitude, double direction, double rotation, double gyroAngle){
        if(alliance == Alliance.RED_ALLIANCE){
            direction = -direction;
            rotation = -rotation;
        }

        magnitude = limitMecanum(magnitude) * Math.sqrt(2.0);
        direction = direction - gyroAngle;
        double dirInRad = (direction + 45.0) * Math.PI/180;

        double cosD = Math.cos(dirInRad);
        double sinD = Math.sin(dirInRad);

        double Motors[] = new double[4];
        Motors[0] = sinD * magnitude + rotation; //kFrontLeft Motor
        Motors[1] = cosD * magnitude - rotation; //kFrontRight Motor
        Motors[2] = cosD  * magnitude - rotation; //kRearLeft Motor
        Motors[3] = sinD * magnitude - rotation; //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);
    }

    public void mecanumDrive_Polar_keepFrontPos(double magnitude, double direction, double angleToMaintain, double gyroAngle){
        if(alliance == Alliance.RED_ALLIANCE){
            direction = -direction;
            angleToMaintain = -angleToMaintain;
        }

        double rotation = 0;
        direction = direction - gyroAngle;
        if(firstRun){
            navX.yawPIDController.setOutputRange(Values.PIDSettings.STURN_MIN_MOTOR_OUTPUT_VALUE, Values.PIDSettings.STURN_MAX_MOTOR_OUTPUT_VALUE);
            navX.yawPIDController.setSetpoint(angleToMaintain);
            firstRun = false;
        }
        navX.yawPIDController.enable(true);
        if (navX.yawPIDController.isNewUpdateAvailable(navX.yawPIDResult)) {
            if (navX.yawPIDResult.isOnTarget()) {
                    rotation = 0;
            } else {
                double output = navX.yawPIDResult.getOutput();
                    rotation = output;
            }
        }
        magnitude = limitMecanum(magnitude) * Math.sqrt(2.0);

        double dirInRad = (direction + 45.0) * Math.PI/180;

        double cosD = Math.cos(dirInRad);
        double sinD = Math.sin(dirInRad);

        double Motors[] = new double[4];
        Motors[0] = sinD * magnitude + rotation; //kFrontLeft Motor
        Motors[1] = cosD * magnitude - rotation; //kFrontRight Motor
        Motors[2] = cosD  * magnitude + rotation; //kRearLeft Motor
        Motors[3] = sinD * magnitude - rotation; //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);

    }





    public static HDDriveHandler getInstance(){
        return instance;
    }


}
