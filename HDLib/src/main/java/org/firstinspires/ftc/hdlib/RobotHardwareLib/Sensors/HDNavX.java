package org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors;


import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.navx.ftc.AHRS;
import org.firstinspires.ftc.navx.ftc.navXPIDController;

/**
 * Created by Akash on 8/16/2016.
 */
public class HDNavX{

    private AHRS navx_device;

    private static HDNavX instance = null;

    public HDNavX(){
        instance = this;
        navx_device = AHRS.getInstance(HDOpMode.getInstance().hardwareMap.deviceInterfaceModule.get(Values.HardwareMapKeys.DeviceInterfaceModule),
                Values.HardwareMapKeys.NAVX_I2C_PORT,
                AHRS.DeviceDataType.kProcessedData,
                Values.NavX.NAVX_DEVICE_UPDATE_RATE_HZ);
    }

    public float getYaw(){
        return navx_device.getYaw();
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


}
