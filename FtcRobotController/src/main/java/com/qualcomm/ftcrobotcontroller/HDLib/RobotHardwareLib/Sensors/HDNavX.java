package com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.HDLib.HDLoopInterface;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;

/**
 * Created by Akash on 8/16/2016.
 */
public class HDNavX implements HDLoopInterface.LoopTimer{

    public navXPIDController.PIDResult yawPIDResult;
    private AHRS navx_device;
    public navXPIDController yawPIDController;
    public static HDNavX instance = null;

    public HDNavX(){
        instance = this;
        navx_device = AHRS.getInstance(HDOpMode.getInstance().hardwareMap.deviceInterfaceModule.get(Values.HardwareMapKeys.DeviceInterfaceModule),
                Values.HardwareMapKeys.NAVX_I2C,
                AHRS.DeviceDataType.kProcessedData,
                Values.NavX.NAVX_DEVICE_UPDATE_RATE_HZ);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.Start);
        yawPIDController = new navXPIDController( navx_device,
                navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Values.PIDSettings.MIN_MOTOR_OUTPUT_VALUE, Values.PIDSettings.MAX_MOTOR_OUTPUT_VALUE);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Values.PIDSettings.TOLERANCE_DEGREES);
        yawPIDController.setPID(Values.PIDSettings.YAW_PID_P, Values.PIDSettings.YAW_PID_I, Values.PIDSettings.YAW_PID_D);
        yawPIDController.enable(false);
    }

    public AHRS getSensorData(){
        return navx_device;
    }


    @Override
    public void InitializeLoopOp() {

    }

    @Override
    public void StartOp() {
        navx_device.zeroYaw();
        yawPIDResult = new navXPIDController.PIDResult();
    }

    @Override
    public void continuousCallOp() {

    }
}
