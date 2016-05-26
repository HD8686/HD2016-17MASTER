package com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDGyro;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by Akash on 5/7/2016.
 */

public class DriveHandler {


    private DcMotor DHfrontLeft,DHfrontRight,DHbackLeft,DHbackRight;
    private HardwareMap mHardwareMap;
    private static DriveHandler instance = null;

    public DriveHandler(){
        if(HDOpMode.getInstance() == null){
            throw new NullPointerException("HDOpMode not running!");
        }
        InitMotors();
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
        DHfrontLeft.setMode(RunMode);
        DHfrontRight.setMode(RunMode);
        DHbackLeft.setMode(RunMode);
        DHbackRight.setMode(RunMode);
    }
    public void setOldSteve() {
        DHfrontLeft.setDirection(DcMotor.Direction.FORWARD);
        DHfrontRight.setDirection(DcMotor.Direction.REVERSE);
        DHbackLeft.setDirection(DcMotor.Direction.FORWARD);
        DHbackRight.setDirection(DcMotor.Direction.REVERSE);
    }


    public double getEncoderCount(){
        return ((DHfrontLeft.getCurrentPosition()+
                DHfrontRight.getCurrentPosition()+
                DHbackLeft.getCurrentPosition()+
                DHbackRight.getCurrentPosition())/4);
    }

    public void setMotorSpeeds(double[] Speeds){
        DHfrontLeft.setPower(Range.clip(Speeds[0], -1, 1));
        DHfrontRight.setPower(Range.clip(Speeds[1], -1, 1));
        DHbackLeft.setPower(Range.clip(Speeds[2], -1, 1));
        DHbackRight.setPower(Range.clip(Speeds[3], -1, 1));
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

    public void fieldCentricTank(double X1, double Y1){
        double direction_Cmd = 0;
        double Speed = 0;
        double angle_error = 0;
        double clockwiseCmd = 0;
        double leftSpeed = 0;
        double rightSpeed = 0;
        HDOpMode.getInstance().telemetry.addData("Gyro", HDGyro.getInstance().getHeading());
        Speed = Math.max(Math.abs(X1), Math.abs(Y1));
        direction_Cmd = Math.atan2(X1, Y1) * (180/Math.PI); //This may need to be negative? Add telemetry to track direction_CMD and run it without motors.
        HDOpMode.getInstance().telemetry.addData("direction", String.valueOf(direction_Cmd));
        angle_error  = direction_Cmd - HDGyro.getInstance().getHeading();
        while(angle_error > 180){
            angle_error -= 360;
        }
        while(angle_error < -180){
            angle_error +=360;
        }
        if(angle_error > 100){
            angle_error -= 180;
            Speed = -Speed;
        } else if(angle_error < -100){
            angle_error += 180;
            Speed = -Speed;
        }
        clockwiseCmd = angle_error/45;
        leftSpeed = Speed + clockwiseCmd;
        rightSpeed = Speed - clockwiseCmd;
        HDOpMode.getInstance().telemetry.addData("left", leftSpeed);
        HDOpMode.getInstance().telemetry.addData("right", rightSpeed);
        tankDrive(leftSpeed, rightSpeed);

    }




    public static DriveHandler getInstance(){
        return instance;
    }


}
