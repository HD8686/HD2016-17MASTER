package org.firstinspires.ftc.hdlib.Telemetry.HDMenu;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

/**
 * Created by Akash on 10/16/2016.
 */
public class HDNumberMenu extends HDMenuManager {

    private String menuName;
    private double minValue;
    private double maxValue;
    private double stepSize;
    private Gamepad gamepad1;
    private HDMenuManager nextMenu;
    private double currValue;
    private boolean oldLeft = true;
    private boolean oldRight = true;
    private String units;

    public HDNumberMenu(String menuName, double minValue, double maxValue, double stepSize, double startValue, String units, HDMenuManager nextMenu, Gamepad gamepad1){
        this.menuName = menuName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.gamepad1 = gamepad1;
        this.nextMenu = nextMenu;
        this.currValue = startValue;
        this.units = units;
    }


    @Override
    public void runMenu() {
        while(!gamepad1.a) {
            HDDashboard.getInstance().displayPrintf(1, HDDashboard.textPosition.Centered, "HD Number Menu: %s", menuName);
            HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "Current Value: %d %s", currValue, units);
            HDDashboard.getInstance().displayPrintf(3, HDDashboard.textPosition.Centered, "Press the A button to continue");
            if(gamepad1.dpad_left != oldLeft && gamepad1.dpad_left){
                currValue = currValue - stepSize;
            }
            if(gamepad1.dpad_right != oldRight && gamepad1.dpad_right){
                currValue = currValue + stepSize;
            }
            if(currValue < minValue)
                currValue = minValue;
            if(currValue > maxValue)
                currValue = maxValue;
            oldLeft = gamepad1.dpad_left;
            oldRight = gamepad1.dpad_right;
            HDDashboard.getInstance().refreshDisplay();
            HDOpMode.getInstance().idle();
        }
    }

    @Override
    public String getSelectionDisplay() {
        return menuName + ": " + String.valueOf(currValue);
    }

    @Override
    public HDMenuManager getNextMenu() {
        return nextMenu;
    }

    public double getValue(){
        return currValue;
    }
}
