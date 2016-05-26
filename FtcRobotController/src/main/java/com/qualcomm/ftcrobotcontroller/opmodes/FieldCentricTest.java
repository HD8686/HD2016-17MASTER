package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Height Differential on 5/20/2016.
 */
public class FieldCentricTest extends OpMode {
    private ModernRoboticsI2cGyro mGyro;
    private DcMotor DHfrontLeft,DHfrontRight,DHbackLeft,DHbackRight;
    private double direction_Cmd = 0;
    private double Speed = 0;
    double angle_error = 0;
    double clockwiseCmd = 0;
    double leftSpeed = 0;
    double rightSpeed = 0;
    @Override
    public void init() {
        mGyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        this.DHfrontLeft = hardwareMap.dcMotor.get(Values.HardwareMapKeys.frontLeft);
        this.DHfrontRight = hardwareMap.dcMotor.get(Values.HardwareMapKeys.frontRight);
        this.DHbackLeft = hardwareMap.dcMotor.get(Values.HardwareMapKeys.backLeft);
        this.DHbackRight = hardwareMap.dcMotor.get(Values.HardwareMapKeys.backRight);
        mGyro.calibrate();
        DHfrontLeft.setDirection(DcMotor.Direction.FORWARD);
        DHfrontRight.setDirection(DcMotor.Direction.REVERSE);
        DHbackLeft.setDirection(DcMotor.Direction.FORWARD);
        DHbackRight.setDirection(DcMotor.Direction.REVERSE);
        //RUN WITHOUT MOTORS BEFORE TESTING
        setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

    }

    @Override
    public void loop() {
        telemetry.addData("Gyro",-mGyro.getIntegratedZValue());
        Speed = Math.max(Math.abs(gamepad1.left_stick_x), Math.abs(gamepad1.left_stick_y));
        direction_Cmd = Math.atan2(gamepad1.left_stick_x, gamepad1.left_stick_y) * (180/Math.PI); //This may need to be negative? Add telemetry to track direction_CMD and run it without motors.
        telemetry.addData("direction", String.valueOf(direction_Cmd));
        angle_error  = direction_Cmd - -mGyro.getIntegratedZValue();
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
        leftSpeed = Speed +clockwiseCmd;
        rightSpeed = Speed - clockwiseCmd;
        telemetry.addData("left", leftSpeed);
        telemetry.addData("right", rightSpeed);
        tankDrive(leftSpeed, rightSpeed);

    }



    public void tankDrive(double LeftPower, double RightPower){
        LeftPower = Range.clip(LeftPower, -1, 1);
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
}
