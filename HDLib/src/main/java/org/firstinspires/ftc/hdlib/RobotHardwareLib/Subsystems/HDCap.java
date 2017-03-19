package org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;

/**
 * Created by Akash on 1/25/2017.
 */
public class HDCap {

    DcMotor capMotor;
    HDServo leftCapArm;
    HDServo rightCapArm;
    final int liftExtended = 32500;
    final int liftRetracted = 0; //-10

    public HDCap(DcMotor capMotor, HDServo leftCapArm, HDServo rightCapArm){
        this.capMotor = capMotor;
        this.leftCapArm = leftCapArm;
        this.rightCapArm = rightCapArm;
        capMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        capMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        capMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        capMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        capMotor.setPower(0);
        lowerArms();
    }

    public void lowerArms(){
        leftCapArm.setPosition(.41);
        rightCapArm.setPosition(.23);
    }

    public void raiseArms(){
        leftCapArm.setPosition(0.5);
        rightCapArm.setPosition(.16);
    }

    public void extendLift(){
        capMotor.setPower(.35);
        capMotor.setTargetPosition(liftExtended);
    }

    public void resetEncoders(){
        capMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        capMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void retractLift(){
        capMotor.setPower(-0.2);
        capMotor.setTargetPosition(liftRetracted);

    }

}
