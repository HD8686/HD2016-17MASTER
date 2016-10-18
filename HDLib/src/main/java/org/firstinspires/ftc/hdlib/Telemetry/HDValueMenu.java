package org.firstinspires.ftc.hdlib.Telemetry;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Akash on 10/16/2016.
 */
public class HDValueMenu {

    private String menuName;
    private double minValue;
    private double maxValue;
    private double stepSize;
    private double startValue;
    private Gamepad gamepad1;



    public HDValueMenu(String menuName, double minValue, double maxValue, double stepSize, double startValue, Gamepad gamepad1){
        this.menuName = menuName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.startValue = startValue;
        this.gamepad1 = gamepad1;
    }





}
