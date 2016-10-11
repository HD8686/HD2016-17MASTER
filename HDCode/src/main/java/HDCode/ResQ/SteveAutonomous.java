package HDCode.ResQ;

import android.app.Activity;
import android.graphics.Color;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import HSCode.R;


/**
 * Created by Height Differential on 1/17/2016.
 */

@Disabled
@Autonomous(name = "2015-16 RES-Q Autonomous",group = "RES-Q")
/**
 * Created by Height Differential on 1/17/2016.
 */
public class SteveAutonomous extends HDAuto {
    public void runOpMode() throws InterruptedException {
        dashboard = new HDDashboard_2015(telemetry); //Create dashboard for telemetry
        dashboard.clearDisplay(); //Clear Dashboard
        doMenus(); //Run autonomous configuration menus
        AssignHardwareMap(); //Assign hardware devices to map,
        Init(); //Sets servo values
        BeforeStartGyroCal(); //Custom gyro calibration method, which calibrates the gyro every 30 seconds.
        waitForStart(); //Wait for start button to be pressed
        AfterStartGyroCal(); //If the gyro was in a middle of a calibration, wait for it to finish
        ProgramRunTime.reset(); //Reset program run timer.
        Finished = false; //Set finished variable to false(Change this to true to exit state machine loops)
        if (alliance == Alliance.RED_ALLIANCE) { //If menu selection was Red Alliance, run selected configuration
            if (autonconfigs == AutonomousConfigs.Offense) {
                CLIMBERRED(this);
            } else {
                if (defenseconfigs == DefenseConfigs.ForwardAndDefend) {
                    ForwardAndDefend(this);
                }
                else if(defenseconfigs == DefenseConfigs.Protect){
                    Protect(this);
                }
                else if(defenseconfigs == DefenseConfigs.SideDefend)
                {
                    SideDefend(this);
                }
            }
        }
        if (alliance == Alliance.BLUE_ALLIANCE) { //If menu selection was Blue Alliance, run selected configuration.
            if (autonconfigs == AutonomousConfigs.Offense) {
                CLIMBERBLUE(this);
            } else {
                if (defenseconfigs == DefenseConfigs.ForwardAndDefend) {
                    ForwardAndDefend(this);
                }
                else if(defenseconfigs == DefenseConfigs.Protect){
                    Protect(this);
                }
                else if(defenseconfigs == DefenseConfigs.SideDefend)
                {
                    SideDefend(this);
                }
            }
        }
        StopLED();
    }

    void StopLED(){
        //Set The Screen Back To Transparent and turn off LED's.
        LED1.setPulseWidthPeriod(20000);
        LED1.setPulseWidthOutputTime(1);
        LED2.setPulseWidthPeriod(20000);
        LED2.setPulseWidthOutputTime(1);
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    void doMenus() throws InterruptedException{ //Runs the menus
        while(Finished == false){   //While user is still in menu
            waitForNextHardwareCycle();
            dashboard.displayPrintf(3, "Press A to Continue");
            if(alliance.equals(Alliance.RED_ALLIANCE)){ //if Alliance is Red alliance, highlight the red alliance option
                dashboard.displayPrintf(1, "*" + String.valueOf(Alliance.RED_ALLIANCE));
                dashboard.displayPrintf(2, String.valueOf(Alliance.BLUE_ALLIANCE));
            }
            else{   //If alliance is Blue alliance then highlight that option
                dashboard.displayPrintf(1, String.valueOf(Alliance.RED_ALLIANCE));
                dashboard.displayPrintf(2, "*" + String.valueOf(Alliance.BLUE_ALLIANCE));
            }
            if(gamepad1.dpad_right != LastValR) { //If Dpad right wasn't pressed last time loop ran, but is now, then advance the menu options.
                if (gamepad1.dpad_right) {
                    alliance = alliance.next();
                }
            }
            if(LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                if (gamepad1.a)
                    Finished = true;
            }
            LastValR = gamepad1.dpad_right;
            LastValA = gamepad1.a;
        }
        Finished = false; //Reset finished for next loop
        while(Finished == false){   //While user is still in menu
            waitForNextHardwareCycle();
            dashboard.displayPrintf(3, "Press A to Continue");
            if(autonconfigs.equals(AutonomousConfigs.Offense)){ //Display menu options and highlight current option in selection
                dashboard.displayPrintf(1, "*" + String.valueOf(AutonomousConfigs.Offense));
                dashboard.displayPrintf(2, String.valueOf(AutonomousConfigs.Defense));
            }
            else{
                dashboard.displayPrintf(1, String.valueOf(AutonomousConfigs.Offense));
                dashboard.displayPrintf(2, "*" + String.valueOf(AutonomousConfigs.Defense));
            }
            if(gamepad1.dpad_right != LastValR) {//If Dpad right wasn't pressed last time loop ran, but is now, then advance the menu options.
                if (gamepad1.dpad_right) {
                    autonconfigs = autonconfigs.next();
                }
            }
            if(LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                if (gamepad1.a)
                    Finished = true;
            }
            LastValR = gamepad1.dpad_right;
            LastValA = gamepad1.a;
        }
        Finished = false;
        if(autonconfigs == AutonomousConfigs.Offense) {
            while (Finished == false) {   //While user is still in menu
                waitForNextHardwareCycle();
                dashboard.displayPrintf(3, "Press A to Continue");
                if (ballconfig.equals(BallConfigs.NoBallClear)) { //Display menu options and highlight current option in selection
                    dashboard.displayPrintf(1, "*" + String.valueOf(BallConfigs.NoBallClear));
                    dashboard.displayPrintf(2, String.valueOf(BallConfigs.BallClear));
                } else {
                    dashboard.displayPrintf(1, String.valueOf(BallConfigs.NoBallClear));
                    dashboard.displayPrintf(2, "*" + String.valueOf(BallConfigs.BallClear));
                }
                if (gamepad1.dpad_right != LastValR) {//If Dpad right wasn't pressed last time loop ran, but is now, then advance the menu options.
                    if (gamepad1.dpad_right) {
                        ballconfig = ballconfig.next();
                    }
                }
                if (LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                    if (gamepad1.a)
                        Finished = true;
                }
                LastValR = gamepad1.dpad_right;
                LastValA = gamepad1.a;
            }
            Finished = false;
            if (ballconfig.equals(BallConfigs.NoBallClear)) {
                while (Finished == false) {   //While user is still in menu
                    waitForNextHardwareCycle();
                    dashboard.displayPrintf(6, "Press A to Continue");
                    if (offenseconfigs.equals(OffenseConfigs.OffenseStay)) { //Display menu options and highlight current option in selection
                        dashboard.displayPrintf(1, "*" + String.valueOf(OffenseConfigs.OffenseStay));
                        dashboard.displayPrintf(2, String.valueOf(OffenseConfigs.OffenseLeaveDefense));
                        dashboard.displayPrintf(3, String.valueOf(OffenseConfigs.OffenseLeaveFloorGoal));
                        dashboard.displayPrintf(4, String.valueOf(OffenseConfigs.OffenseLeaveCorner));
                        dashboard.displayPrintf(5, String.valueOf(OffenseConfigs.OffenseLeaveBackup));
                    } else if(offenseconfigs.equals(OffenseConfigs.OffenseLeaveDefense)){
                        dashboard.displayPrintf(1, String.valueOf(OffenseConfigs.OffenseStay));
                        dashboard.displayPrintf(2, "*" + String.valueOf(OffenseConfigs.OffenseLeaveDefense));
                        dashboard.displayPrintf(3, String.valueOf(OffenseConfigs.OffenseLeaveFloorGoal));
                        dashboard.displayPrintf(4, String.valueOf(OffenseConfigs.OffenseLeaveCorner));
                        dashboard.displayPrintf(5, String.valueOf(OffenseConfigs.OffenseLeaveBackup));
                    } else if(offenseconfigs.equals(OffenseConfigs.OffenseLeaveFloorGoal)){
                        dashboard.displayPrintf(1, String.valueOf(OffenseConfigs.OffenseStay));
                        dashboard.displayPrintf(2, String.valueOf(OffenseConfigs.OffenseLeaveDefense));
                        dashboard.displayPrintf(3, "*" + String.valueOf(OffenseConfigs.OffenseLeaveFloorGoal));
                        dashboard.displayPrintf(4, String.valueOf(OffenseConfigs.OffenseLeaveCorner));
                        dashboard.displayPrintf(5, String.valueOf(OffenseConfigs.OffenseLeaveBackup));
                    } else if(offenseconfigs.equals(OffenseConfigs.OffenseLeaveCorner)){
                        dashboard.displayPrintf(1, String.valueOf(OffenseConfigs.OffenseStay));
                        dashboard.displayPrintf(2, String.valueOf(OffenseConfigs.OffenseLeaveDefense));
                        dashboard.displayPrintf(3, String.valueOf(OffenseConfigs.OffenseLeaveFloorGoal));
                        dashboard.displayPrintf(4, "*" + String.valueOf(OffenseConfigs.OffenseLeaveCorner));
                        dashboard.displayPrintf(5, String.valueOf(OffenseConfigs.OffenseLeaveBackup));
                    } else{
                        dashboard.displayPrintf(1, String.valueOf(OffenseConfigs.OffenseStay));
                        dashboard.displayPrintf(2, String.valueOf(OffenseConfigs.OffenseLeaveDefense));
                        dashboard.displayPrintf(3, String.valueOf(OffenseConfigs.OffenseLeaveFloorGoal));
                        dashboard.displayPrintf(4, String.valueOf(OffenseConfigs.OffenseLeaveCorner));
                        dashboard.displayPrintf(5, "*" + String.valueOf(OffenseConfigs.OffenseLeaveBackup));
                    }
                    if (gamepad1.dpad_right != LastValR) {//If Dpad right wasn't pressed last time loop ran, but is now, then advance the menu options.
                        if (gamepad1.dpad_right) {
                            offenseconfigs = offenseconfigs.next();
                        }
                    }
                    if (LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                        if (gamepad1.a)
                            Finished = true;
                    }
                    LastValR = gamepad1.dpad_right;
                    LastValA = gamepad1.a;
                }
            }
        }
        if(autonconfigs == AutonomousConfigs.Defense) {
            Finished = false;
            while (Finished == false) {   //While user is still in menu
                waitForNextHardwareCycle();
                dashboard.displayPrintf(5, "Press A to Continue");
                if (defenseconfigs.equals(DefenseConfigs.SideDefend)) { //Display menu options and highlight current option in selection
                    dashboard.displayPrintf(1, "*" + String.valueOf(DefenseConfigs.SideDefend));
                    dashboard.displayPrintf(2, String.valueOf(DefenseConfigs.ForwardAndDefend));
                    dashboard.displayPrintf(3, String.valueOf(DefenseConfigs.Protect));
                } else if (defenseconfigs.equals(DefenseConfigs.ForwardAndDefend)) {
                    dashboard.displayPrintf(1, String.valueOf(DefenseConfigs.SideDefend));
                    dashboard.displayPrintf(2, "*" + String.valueOf(DefenseConfigs.ForwardAndDefend));
                    dashboard.displayPrintf(3, String.valueOf(DefenseConfigs.Protect));
                } else {
                    dashboard.displayPrintf(1, String.valueOf(DefenseConfigs.SideDefend));
                    dashboard.displayPrintf(2, String.valueOf(DefenseConfigs.ForwardAndDefend));
                    dashboard.displayPrintf(3, "*" + String.valueOf(DefenseConfigs.Protect));
                }
                if (gamepad1.dpad_right != LastValR) {//If Dpad right wasn't pressed last time loop ran, but is now, then advance the menu options.
                    if (gamepad1.dpad_right) {
                        defenseconfigs = defenseconfigs.next();
                    }
                }
                if (LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                    if (gamepad1.a)
                        Finished = true;
                }
                LastValR = gamepad1.dpad_right;
                LastValA = gamepad1.a;
            }
            Finished = false;
            dashboard.clearDisplay();
            if (defenseconfigs == DefenseConfigs.ForwardAndDefend || defenseconfigs == DefenseConfigs.SideDefend) {
                while (Finished == false) {   //While user is still in menu
                    waitForNextHardwareCycle();
                    dashboard.displayPrintf(1, "Tiles To Go: " + String.valueOf(DefenseTiles));
                    dashboard.displayPrintf(2, "Press A to Continue");
                    if (gamepad1.dpad_right != LastValR) {  //This part allows you to choose how many tiles to go for defense before you cross the 10 second line. Similar to delay selection code
                        if (gamepad1.dpad_right) {
                            DefenseTiles = DefenseTiles + 1;
                        }
                    }
                    if (gamepad1.dpad_left != LastValL) {
                        if (gamepad1.dpad_left) {
                            DefenseTiles = DefenseTiles - 1;
                        }
                    }
                    if (LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                        if (gamepad1.a)
                            Finished = true;
                    }
                    LastValR = gamepad1.dpad_right;
                    LastValA = gamepad1.a;
                    LastValL = gamepad1.dpad_left;
                }
            }
        }
        Finished = false;
        dashboard.clearDisplay();
        while (Finished == false) {   //While user is still in menu
            waitForNextHardwareCycle();
            dashboard.displayPrintf(1, "Delay: " + String.valueOf(delay));  //Delay code to delay autonomous
            dashboard.displayPrintf(2, "Press A to Continue");
            if(delay < 0){  //Delay selection which uses DPad to allow infinite delay options.
                delay = 0;
            }
            if (gamepad1.dpad_right != LastValR) {
                if (gamepad1.dpad_right) {
                    delay = delay + 1;
                }
            }
            if(gamepad1.dpad_left != LastValL){
                if(gamepad1.dpad_left){
                    delay = delay - 1;
                }
            }
            if (LastValA != gamepad1.a) { //Select option if gamepad a is newly pressed and move on to next part of menu.
                if (gamepad1.a)
                    Finished = true;
            }
            LastValR = gamepad1.dpad_right;
            LastValA = gamepad1.a;
            LastValL = gamepad1.dpad_left;
        }

        dashboard.clearDisplay();   //Clear the display and then show selected options
        dashboard.displayPrintf(0, alliance.toString());
        dashboard.displayPrintf(3, "Delay: " + delay);
        if(autonconfigs.equals(AutonomousConfigs.Offense)) {    //Show offense options if offense was selected and vice versa for defense.
            dashboard.displayPrintf(1, offenseconfigs.toString());
            dashboard.displayPrintf(2, ballconfig.toString());
        }
        else{
            dashboard.displayPrintf(1, autonconfigs.toString());
            dashboard.displayPrintf(2, defenseconfigs.toString());
        }
        Finished = false;
        GyroTimer.reset();
    }


    void AssignHardwareMap() throws InterruptedException{   //Get references to hardware devices
        LED1 = hardwareMap.pwmOutput.get("LED");
        LED2 = hardwareMap.pwmOutput.get("LED2");
        frontLeft = hardwareMap.dcMotor.get("motor_1");
        ClimberServo = hardwareMap.servo.get("servo_4");
        WinchServo = hardwareMap.servo.get("servo_1");
        PenguinLeft = hardwareMap.servo.get("servo_2");
        PenguinRight = hardwareMap.servo.get("servo_3");
        AllClear = hardwareMap.servo.get("allclear");
        AllClear2 = hardwareMap.servo.get("allclear2");
        frontRight = hardwareMap.dcMotor.get("motor_3");
        backLeft = hardwareMap.dcMotor.get("motor_2");
        backRight = hardwareMap.dcMotor.get("motor_4");
        Pinion = hardwareMap.servo.get("pinion");
        Tilt = hardwareMap.servo.get("tilt");
        Touch1 = hardwareMap.touchSensor.get("touch1");
        Touch2 = hardwareMap.touchSensor.get("touch2");
        mColor = hardwareMap.colorSensor.get("color");
        optical = hardwareMap.opticalDistanceSensor.get("optical");
        optical2 = hardwareMap.opticalDistanceSensor.get("optical2");
        optical3 = hardwareMap.opticalDistanceSensor.get("optical3");
        mGyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        WinchLock = hardwareMap.servo.get("servo_5");
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);
    }

    void BeforeStartGyroCal() throws InterruptedException{  //Gyro calibration method (Waits 30 seconds before recalibration)
        while(!opModeIsActive()){   //While still in init phase
            waitForNextHardwareCycle();
            dashboard.displayPrintf(4, "Time until new gyro calibration: " + String.valueOf(HDUtil.round(33.5 - GyroTimer.time(), 3))); //Display time left until new gyro calibration
            if(GyroTimer.time() <  .5){ //If gyro timer is less than .5 seconds(When it is reset)
                mGyro.calibrate();  //Calibrate gyro
                while (!opModeIsActive() && GyroTimer.time()< 3.5){ //Wait 3.5 seconds for gyro recalibration
                    waitForNextHardwareCycle();
                    dashboard.displayPrintf(4, "Gyro Calibrating :" + String.valueOf(HDUtil.round(4- GyroTimer.time(),3)) + " Seconds Remaining");
                }
            }
            if(GyroTimer.time() > 33.5){ //Once it has been 33.5 seconds (30 + 3.5 gyro calibration time) reset the gyrotimer to restart the calibration.
                GyroTimer.reset();
            }
        }
    }

    void AfterStartGyroCal() throws InterruptedException{   //Finish calibrating if it didn't finish during init hopefully this wont happen!
        if(GyroTimer.time() < 3.5){ //If gyro time was less than 3.5 seconds then check if gyro is calibrating
            if(mGyro.isCalibrating()){
                while (opModeIsActive() && GyroTimer.time()< 3.5){ //If gyro was calibrating then finish the calibration
                    waitForNextHardwareCycle();
                    telemetry.addData("GyroTimer", HDUtil.round(GyroTimer.time(), 3));
                    telemetry.addData("1", "Gyro Calibrating :" + String.valueOf(HDUtil.round(4- GyroTimer.time(),3)) + " Seconds Remaining");
                }
            }
            else { //If gyro wasn't calibrating and time was less than 3.5 seconds it must have not gotten to calibration command in time, so start calibration and wait 3.5 seconds.
                mGyro.calibrate();
                while (opModeIsActive() && GyroTimer.time()< 3.5){
                    waitForNextHardwareCycle();
                    telemetry.addData("GyroTimer", GyroTimer.time());
                    telemetry.addData("1", "Gyro Calibrating :" + String.valueOf(HDUtil.round(4- GyroTimer.time(),3)) + " Seconds Remaining");
                }
            }
        }
    }

    void Init() throws InterruptedException{    //Reset drive encoders and then set all devices start positions.
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LED2.setPulseWidthPeriod(20000);
        LED2.setPulseWidthOutputTime(19500);
        LED1.setPulseWidthPeriod(20000);
        LED1.setPulseWidthOutputTime(19500);
        WinchServo.setPosition(.88);
        WinchLock.setPosition(1);
        ClimberServo.setPosition(0.8);
        PenguinLeft.setPosition(1);
        PenguinRight.setPosition(0);
        AllClear.setPosition(0.495);
        AllClear2.setPosition(.52);
        Tilt.setPosition(.68);
        Pinion.setPosition(.5);
        mColor.enableLed(false);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);    //Run using encoders in order to use speed control
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}