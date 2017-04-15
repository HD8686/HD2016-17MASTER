package org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;

/**
 * Created by Akash on 1/25/2017.
 */
public class HDCap {

    public enum liftMode{
        BOTTOM,
        TOP,
        DROP,
        CARRY
    }

    public DcMotor capMotor;
    HDServo leftCapArm;
    HDServo rightCapArm;
    public liftMode curLiftMode;
    final int liftExtended = 12000; //38000
    final int liftRetracted = 0; //-10
    final int dropPosition = 10000;
    final int movePosition = 1500;

    public HDCap(DcMotor capMotor, HDServo leftCapArm, HDServo rightCapArm){
        this.capMotor = capMotor;
        this.leftCapArm = leftCapArm;
        this.rightCapArm = rightCapArm;
        capMotor.setDirection(DcMotorSimple.Direction.FORWARD);
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
        curLiftMode = liftMode.TOP;
        double pos = capMotor.getCurrentPosition();
        if(pos > 9000){
            capMotor.setPower(1.0);
        }else if(pos > 6000){
            capMotor.setPower(0.75);
        }else if(pos > 3000){
            capMotor.setPower(0.5);
        }else if(pos > 2000){
            capMotor.setPower(0.25);
        }else if(pos > 1000){
            capMotor.setPower(0.2);
        }
        capMotor.setTargetPosition(liftExtended);
    }

    public void movePosition(){
        curLiftMode = liftMode.CARRY;
        capMotor.setPower(0.4);
        capMotor.setTargetPosition(movePosition);
    }

    public void setPower(double power){
        capMotor.setPower(power);
    }

    public void resetEncoders(DcMotor.RunMode afterMode){
        capMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        capMotor.setMode(afterMode);
    }

    public void setMotorMode(DcMotor.RunMode runMode){
        capMotor.setMode(runMode);
    }

    public void retractLift(){
        curLiftMode = liftMode.BOTTOM;
        double pos = capMotor.getCurrentPosition();
        if(pos > 7000){
            capMotor.setPower(1);
        }else{
            capMotor.setPower(0.1);
        }
        capMotor.setTargetPosition(liftRetracted);

    }

    public void dropPosition(){
        curLiftMode = liftMode.DROP;
        capMotor.setPower(0.4);
        capMotor.setTargetPosition(dropPosition);
    }

}
