/*
Modern Robotics Range Example with range
Created 8/4/2016 by Colton Mehlhoff of Modern Robotics using FTC SDK 1.75
Reuse permitted with credit where credit is due

Configuration:
Range Sensor connected to a Core Device Interface with default I2C address of 0x28 named "range". This sensor should be selected as an I2C Device

This program can be run without a battery and Power Destitution Module

For more information, visit modernroboticsedu.com.
Support is available by emailing support@modernroboticsinc.com.
*/

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;

public class MRI_Range extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {

        I2cDevice range;
        range = hardwareMap.i2cDevice.get("range");
        I2cDeviceReader rangeReader = new I2cDeviceReader(range, 0x28, 0x04, 2);
        byte rangeReadings[];

        waitForStart();

        while (opModeIsActive()) {

            rangeReadings = rangeReader.getReadBuffer();
            telemetry.addData("US", (rangeReadings[0] & 0xFF));
            telemetry.addData("ODS", (rangeReadings[1] & 0xFF));

            waitOneFullHardwareCycle();
        }
    }
}
