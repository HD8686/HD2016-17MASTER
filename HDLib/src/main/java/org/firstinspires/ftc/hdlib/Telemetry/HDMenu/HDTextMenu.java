package org.firstinspires.ftc.hdlib.Telemetry.HDMenu;

import android.util.Log;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Akash on 10/16/2016.
 */
public class HDTextMenu extends HDMenuManager {
    private TreeMap<String, Object> choices = new TreeMap<String, Object>();
    private String menuName;
    private HDMenuManager nextMenu;
    private int currSelection = 0;
    private boolean oldLeft = true;
    private boolean oldRight = true;

    public HDTextMenu(String menuName, HDMenuManager nextMenu){
        this.menuName = menuName;
        this.nextMenu = nextMenu;
    }

    @Override
    public void runMenu() {
        while(!HDOpMode.getInstance().gamepad1.a && !HDOpMode.getInstance().isStopRequested()) {
            HDDashboard.getInstance().displayPrintf(1, HDDashboard.textPosition.Centered, "HD Text Menu: %s", menuName);
            HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "Press the A button to continue");
            int curLine = 3;
            for(Map.Entry<String, Object> entry: choices.entrySet()){
                if(curLine - 3 == currSelection){
                    HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, "*" + entry.getKey());
                }else{
                    HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, entry.getKey());
                }
                curLine++;
            }
            if(HDOpMode.getInstance().gamepad1.dpad_left != oldLeft && HDOpMode.getInstance().gamepad1.dpad_left){
                currSelection = currSelection - 1;
            }
            if(HDOpMode.getInstance().gamepad1.dpad_right != oldRight && HDOpMode.getInstance().gamepad1.dpad_right){
                currSelection = currSelection + 1;
            }
            if(currSelection < 0) {
                currSelection = 0;
            }
            if(currSelection >= choices.entrySet().size()-1) {
                currSelection = choices.entrySet().size()-1;
            }
            oldLeft = HDOpMode.getInstance().gamepad1.dpad_left;
            oldRight = HDOpMode.getInstance().gamepad1.dpad_right;
            HDDashboard.getInstance().refreshDisplay();
            HDOpMode.getInstance().idle();
        }

        while(HDOpMode.getInstance().gamepad1.a && !HDOpMode.getInstance().isStopRequested()){
            HDOpMode.getInstance().idle();
        }
    }

    @Override
    public String getSelectionDisplay() {
        return menuName + ": " + choices.keySet().toArray()[currSelection];
    }

    public void addChoice(String choiceName, Object choice){
        choices.put(choiceName, choice);
    }

    public Object getChoice(){
        return choices.values().toArray()[currSelection];
    }

    @Override
    public HDMenuManager getNextMenu() {
        return nextMenu;
    }
}
