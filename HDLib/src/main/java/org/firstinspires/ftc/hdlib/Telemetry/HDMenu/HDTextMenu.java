package org.firstinspires.ftc.hdlib.Telemetry.HDMenu;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by Akash on 10/16/2016.
 */
public class HDTextMenu extends HDMenu{
    private TreeMap<String, Object> choices = new TreeMap<String, Object>();
    private String menuName;
    private Gamepad gamepad1;
    private HDMenu nextMenu;
    private int currSelection = 0;
    private boolean oldLeft = true;
    private boolean oldRight = true;

    public HDTextMenu(String menuName, HDMenu nextMenu, Gamepad gamepad1){
        this.menuName = menuName;
        this.nextMenu = nextMenu;
        this.gamepad1 = gamepad1;
    }

    @Override
    public void runMenu() {
        while(!gamepad1.a) {
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
            if(gamepad1.dpad_left != oldLeft && gamepad1.dpad_left){
                currSelection = currSelection--;
            }
            if(gamepad1.dpad_right != oldRight && gamepad1.dpad_right){
                currSelection = currSelection++;
            }
            if(currSelection < 0)
                currSelection = 0;
            if(currSelection > choices.entrySet().size())
                currSelection = choices.entrySet().size();
            oldLeft = gamepad1.dpad_left;
            oldRight = gamepad1.dpad_right;
            HDOpMode.getInstance().idle();
        }
    }

    public void addChoice(String choiceName, Object choice){
        choices.put(choiceName, choice);
    }

    public Object getChoice(){
        return choices.values().toArray()[currSelection];
    }

    @Override
    public HDMenu getNextMenu() {
        return nextMenu;
    }
}
