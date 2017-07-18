package org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors;


import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.navx.ftc.AHRS;
import org.firstinspires.ftc.navx.ftc.navXPIDController;

/**
 * Created by Akash on 8/16/2016.
 */
public class HDNavX implements HDLoopInterface.LoopTimer{
    public navXPIDController.PIDResult yawPIDResult;
    private AHRS navx_device;
    public navXPIDController yawPIDController;
    private static HDNavX instance = null;
    private boolean flipGyro = false;

    public HDNavX(){
        instance = this;
        navx_device = AHRS.getInstance(HDOpMode.getInstance().hardwareMap.deviceInterfaceModule.get(Values.HardwareMapKeys.DeviceInterfaceModule),
                Values.HardwareMapKeys.NAVX_I2C_PORT,
                AHRS.DeviceDataType.kProcessedData,
                Values.NavX.NAVX_DEVICE_UPDATE_RATE_HZ);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.Start);
        yawPIDController = new navXPIDController( navx_device,
                navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Values.PIDSettings.GYRO_MIN_MOTOR_OUTPUT_VALUE, Values.PIDSettings.GYRO_MAX_MOTOR_OUTPUT_VALUE);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Values.PIDSettings.TOLERANCE_DEGREES);
        yawPIDController.setPID(Values.PIDSettings.YAW_PID_P, Values.PIDSettings.YAW_PID_I, Values.PIDSettings.YAW_PID_D);
        yawPIDController.enable(false);
    }

    public void flipGyro(boolean yes){
        flipGyro = yes;
    }

    public float getYaw(){
        if(!flipGyro)
        return navx_device.getYaw();
        else
            return (navx_device.getYaw() + 180);
    }

    public void zeroYaw(){
        navx_device.zeroYaw();
    }

    public static HDNavX getInstance(){
        return instance;
    }

    public AHRS getSensorData(){
        return navx_device;
    }


    @Override
    public void InitializeLoopOp() {

    }

    @Override
    public void StartOp() {
        yawPIDResult = new navXPIDController.PIDResult();
    }

    @Override
    public void continuousCallOp() {

    }
}
