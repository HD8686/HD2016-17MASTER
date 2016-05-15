package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.ftcrobotcontroller.HDVals.Keys;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Akash on 5/7/2016.
 */
public class DriveHandler {
    private DcMotor DHfrontLeft,DHfrontRight,DHbackLeft,DHbackRight;
    private HardwareMap mHardwareMap;

    public DriveHandler(){
        if(HDOpMode.getInstance() == null){
            throw new NullPointerException("HDOpMode not running!");
        }
        InitMotors();
    }

    private void InitMotors(){
        this.mHardwareMap = HDOpMode.getInstance().hardwareMap;
        this.DHfrontLeft = mHardwareMap.dcMotor.get(Keys.frontLeft);
        this.DHfrontRight = mHardwareMap.dcMotor.get(Keys.frontRight);
        this.DHbackLeft = mHardwareMap.dcMotor.get(Keys.backLeft);
        this.DHbackRight = mHardwareMap.dcMotor.get(Keys.backRight);
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

    public double getEncoderCount(){
        return ((DHfrontLeft.getCurrentPosition()+
                DHfrontRight.getCurrentPosition()+
                DHbackLeft.getCurrentPosition()+
                DHbackRight.getCurrentPosition())/4);
    }


}
