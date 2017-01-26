package org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Akash on 1/25/2017.
 */
public class HDCap {

    DcMotor capMotor;
    final int liftExtended = 1500;
    final int liftRetracted = 0;

    public HDCap(DcMotor capMotor){
        this.capMotor = capMotor;
        capMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        capMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        capMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        capMotor.setPower(0);
    }

    public void extendLift(){
        capMotor.setPower(0.25);
        capMotor.setTargetPosition(liftExtended);
    }

    public void retractLift(){
        capMotor.setPower(0.1);
        capMotor.setTargetPosition(liftRetracted);
    }

}
