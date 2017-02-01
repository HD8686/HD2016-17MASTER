package org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.apache.commons.lang3.math.NumberUtils;
import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.navx.ftc.navXPIDController;


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

    /**
     * Sets the alliance of the drive train, will reverse all angles if on red alliance, so we only need to program the blue version of a program.
     * @param alliance the alliance that we are on, either RED_ALLIANCE or BLUE_ALLIANCE
     */
    public void setAlliance(Alliance alliance){
        this.alliance = alliance;
    }


    /**
     * Init the motors and get them from the hardware map.
     */
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

    /**
     * Basic tank drive code, power for left side and power for right side.
     * @param LeftPower The power to set the left side motors to.
     * @param RightPower The power to set the right side motors to.
     */
    public void tankDrive(double LeftPower, double RightPower){
        LeftPower = Range.clip(LeftPower,-1,1);
        RightPower = Range.clip(RightPower,-1,1);
        DHfrontLeft.setPower(LeftPower);
        DHbackLeft.setPower(LeftPower);
        DHfrontRight.setPower(RightPower);
        DHbackRight.setPower(RightPower);
    }


    /**
     * Set the run mode of all of the drive train motors
     * @param RunMode Run mode to set them to.
     */
    public void setMode(DcMotor.RunMode RunMode){
        currRunMode = RunMode;
        DHfrontLeft.setMode(RunMode);
        DHfrontRight.setMode(RunMode);
        DHbackLeft.setMode(RunMode);
        DHbackRight.setMode(RunMode);
    }

    /**
     * Reset encoders of drive train motors, then put them back in their original runmodes.
     */
    public void resetEncoders(){
        DHfrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DHfrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DHbackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DHbackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        setMode(currRunMode);
    }

    /**
     * Reverse a side of the drive train to make sure both sides are going the same direction.
     * @param reverse The side to reverse
     */
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

    /**
     * Get the average encoder count of the drive train.
     * @return double value of average encoder count.
     */
    public double getEncoderCount(){
        return ((DHfrontLeft.getCurrentPosition()+
                DHfrontRight.getCurrentPosition()+
                DHbackLeft.getCurrentPosition()+
                DHbackRight.getCurrentPosition())/4);
    }

    /**
     * Get the encoder counts of the front motors, to be used in diagnostics.
     */
    public String getEncoderCountDiagFront(){
        return "frontLeft: " + DHfrontLeft.getCurrentPosition() + ", frontRight: " + DHfrontRight.getCurrentPosition();
    }

    /**
     * Get the encoder counts of the back motors, to be used in diagnostics.
     */
    public String getEncoderCountDiagBack(){
        return  "backRight: " + DHbackRight.getCurrentPosition() + ", backLeft: " + DHbackLeft.getCurrentPosition();
    }

    /**
     * Get the speed of the front motors, to be used in diagnostics.
     */
    public String getMotorSpeedDiagFront(){
        return "frontLeft: " + DHfrontLeft.getPower() + ", frontRight: " + DHfrontRight.getPower();
    }

    /**
     * Get the speed of the back motors, to be used in diagnostics.
     */
    public String getMotorSpeedDiagBack(){
        return "backRight: " + DHbackRight.getPower() + ", backLeft: " + DHbackLeft.getPower();
    }

    /**
     * Set the motors speeds separately, to be used with drive trains that need separate
     * speed values for each motor.
     * @param Speeds Array of motor speeds to be set.
     */
    public void setMotorSpeeds(double[] Speeds){
        DHfrontLeft.setPower(Range.clip(Speeds[0], -1.00000, 1.000000));
        DHfrontRight.setPower(Range.clip(Speeds[1], -1.00000, 1.000000));
        DHbackLeft.setPower(Range.clip(Speeds[2], -1.00000, 1.000000));
        DHbackRight.setPower(Range.clip(Speeds[3], -1.00000, 1.000000));
    }

    /**
     * Brake all of the drive train motors.
     */
    public void motorBrake(){
        DHfrontLeft.setPower(0);
        DHfrontRight.setPower(0);
        DHbackLeft.setPower(0);
        DHbackRight.setPower(0);
    }

    /**
     * Virtual Line Follower, or basically a way for a 2 side robot to keep going in a straight line.
     * It achieves this by adjusting left and right side power based on gyro angle.
     * @param targetAngle The angle to "follow", for example 0 would go straight.
     * @param direction The direction to go, backwards or forwards.
     */
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
        }
    }

    /**
     * A function that limits the values of the power to our chosen max and min motor values to enable good control of the robot.
     */
    public double limitVLF(double a) {
        return Math.min(Math.max(a, Values.PIDSettings.VLF_MIN_MOTOR_OUTPUT_VALUE), Values.PIDSettings.VLF_MAX_MOTOR_OUTPUT_VALUE);
    }

    /**
     * A function that limits the values of the power to our chosen max and min motor values to enable good control of the robot.
     */
    public double limitMecanum(double a) {
        return Math.min(Math.max(a, Values.PIDSettings.MECANUM_MIN_MOTOR_OUTPUT_VALUE), Values.PIDSettings.MECANUM_MAX_MOTOR_OUTPUT_VALUE);
    }

    /**
     * Polar version of Mecanum Drive code meant to be used when programming Autonomous,
     * converted from C++ in FRC's WPILib into Java by us for use in FTC
     * added a magnitudeMax capability so motors never go above 100% power.
     * @param magnitude The amount of power to use on a scale of 0 to 1
     * @param direction The angle to go in, so 0 would be straight, 90 would be right, etc
     * @param rotation Rotation to rotate the robot while going in a direction, on a scale of -1 to 1
     * @param gyroAngle The current yaw angle of the robot
     */
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
        Motors[2] = (cosD  * magnitude + rotation); //kRearLeft Motor
        Motors[3] = (sinD * magnitude - rotation); //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);
    }


    /**
     * Polar version of Mecanum Drive code meant to be used when programming Autonomous,
     * converted from C++ in FRC's WPILib into Java by us for use in FTC
     * added a magnitudeMax capability so motors never go above 100% power.
     * We added in a feature similar to VLF to make sure that the front of the robot always stays at a certain position,
     * allowing us to line up to things like the beacon, etc.
     * @param magnitude The amount of power to use on a scale of 0 to 1
     * @param direction The angle to go in, so 0 would be straight, 90 would be right, etc
     * @param angleToMaintain The angle the front of the robot should maintain while going in a direction
     * @param gyroAngle The current yaw angle of the robot
     */
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
        Motors[2] = (cosD  * magnitude + rotation); //kRearLeft Motor
        Motors[3] = (sinD * magnitude - rotation); //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);

    }


    /**
     * Cartesian Mecanum Drive code meant to be used with Joysticks,
     * converted from C++ in FRC's WPILib into Java by us for use in FTC
     * added a magnitudeMax capability so motors never go above 100% power.
     * @param x X value of joystick to go in
     * @param y Y value of joystick to go in
     * @param rotation The Rotation value, how much it should turn which is always skidsteer
     * @param gyroAngle The current yaw angle of the robot
     */
    public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {

        double cosA = Math.cos(gyroAngle * (Math.PI / 180.0));
        double sinA = Math.sin(gyroAngle * (Math.PI / 180.0));

        double xIn = x * cosA + y * sinA;
        double yIn = x * sinA - y * cosA;

        double Motors[] = new double[4];
        Motors[0] = xIn + yIn + rotation; //kFrontLeft Motor
        Motors[1] = -xIn + yIn - rotation; //kFrontRight Motor
        Motors[2] = (-xIn + yIn + rotation)*1.1875; //kRearLeft Motor
        Motors[3] = (xIn + yIn - rotation)*1.1875; //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);
    }

    /**
     * Cartesian Mecanum Drive code meant to be used with Joysticks,
     * converted from C++ in FRC's WPILib into Java by us for use in FTC
     * added a magnitudeMax capability so motors never go above 100% power.
     * We added in a feature similar to VLF to make sure that the front of the robot always stays at a certain position,
     * allowing us to line up to things like the beacon, etc.
     * @param x X value of joystick to go in
     * @param y Y value of joystick to go in
     * @param angleToMaintain The angle the front of the robot should maintain while going in a direction
     * @param gyroAngle The current yaw angle of the robot
     */
    public void mecanumDrive_Cartesian_keepFrontPos(double x, double y, double angleToMaintain, double gyroAngle) {
        if(alliance == Alliance.RED_ALLIANCE){
            angleToMaintain = -angleToMaintain;
        }
        double rotation = 0;
        if(firstRun){
            navX.yawPIDController.setOutputRange(Values.PIDSettings.GYRO_MIN_MOTOR_OUTPUT_VALUE, Values.PIDSettings.GYRO_MAX_MOTOR_OUTPUT_VALUE);
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


        double cosA = Math.cos(gyroAngle * (Math.PI / 180.0));
        double sinA = Math.sin(gyroAngle * (Math.PI / 180.0));

        double xIn = x * cosA + y * sinA;
        double yIn = x * sinA - y * cosA;

        double Motors[] = new double[4];
        Motors[0] = xIn + yIn + rotation; //kFrontLeft Motor
        Motors[1] = -xIn + yIn - rotation; //kFrontRight Motor
        Motors[2] = (-xIn + yIn + rotation)*1.1875; //kRearLeft Motor
        Motors[3] = (xIn + yIn - rotation)*1.1875; //kRearRight Motor

        double maxMagnitude = Math.abs(NumberUtils.max(Motors));

        if (maxMagnitude > 1.0) {
            for (int i=0; i < Motors.length ; i++) {
                Motors[i] = Motors[i] / maxMagnitude;
            }
        }

        setMotorSpeeds(Motors);
    }

    /**
     * Returns instance of HDDriveHandler which should only have one running instance at any given moment.
     */
    public static HDDriveHandler getInstance(){
        return instance;
    }


}
