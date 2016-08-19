package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

public class Diamonds_Commented_Teleop extends OpMode {

    DcMotorController controllerLeft; //Here we're defining the motor controllers. You guys seem to be using the HiTechnic Motor controllers which control 2 motors per controller.
    DcMotorController controllerRight;
    ServoController servoController; //Here we define the servo controller. Again this is the HiTechnic Servo Controller which supports 6 servo's per controller.

    int motorLB = 1; //Now because each controller takes 2 motors, we define which port each motor is plugged into.
    int motorLF = 2; //LB and LF are on the left motor controller and are in port 1 and 2
    int motorRF = 2; //RF and RB are on the right motor controller and are in port 2 and 1 respectively.
    int motorRB = 1;

    int flipperL = 6; //Each servo controller takes 6 servo's so here we're defining which port on the servo controller each mechanism is plugged into.
    int flipperR = 1; //flipperL is on port 6, flipperR is on port 1 and so on.
    int arm = 4;
    int claw = 2;

    /**
     * Constructor
     */
    public Diamonds_Commented_Teleop() {
        //This is not needed for a OpMode class.
    }

    /*
     * Code to run when the op mode is initialized goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
     */
    @Override
    public void init() { //This is run when you press the init button on the robot controller.
        //Here we tell the phones which motor controller is which,
        //On the robot controller phone, you name each motor controller and then define them here in order to assign the variable to the controller.
        controllerLeft = hardwareMap.dcMotorController.get("LeftController");
        controllerRight = hardwareMap.dcMotorController.get("RightController");
        servoController = hardwareMap.servoController.get("servos");
    }

    @Override
    public void loop() { //This is a continuous loop which is always running once you press the play button on the robot controller.

        float right = -gamepad1.right_stick_y; //This is where we get the basic gamepad y values for each stick. This is the up and down value of the left and right sticks.
        float left = -gamepad1.left_stick_y; //The Y value of the sticks is on a scale of -1 to 1.

        right = Range.clip(right, -1, 1); //This code makes sure the right and left values are within the range of -1 and 1 because if you give a value larger than 1 to a motor
        left = Range.clip(left, -1, 1); //It will crash because it can't go higher than 100% power. Motor power is also on a scale of -1 to 1.
        // This code technically isn't needed because the gamepad Y axis is on a scale of -1 to 1 anyway


        controllerLeft.setMotorPower(motorLB, -left); //Here they are setting the motor powers based on the gamepad values. The left side must be reversed which is why its -left.
        controllerLeft.setMotorPower(motorLF, -left); //the Left and right values come from earlier. These keep getting updated based on the gamepad values because the entire program
        controllerRight.setMotorPower(motorRB, right); //is on a loop.
        controllerRight.setMotorPower(motorRF, right);

        //Servo's have a range of 0-360 degrees but its transfered to a range of 0-1 in the programming, so each degree is around 0.003 value.
        //Overall this piece of code makes the flipperL servo stay at the position of .9 if the right bumper isn't pressed, but if it is held down the servo will go to the .47 position.
        if (gamepad1.right_bumper) { //Here we're saying that if the right_bumper is pressed then do whats in the if loop. It is a shorter version of if(gamepad1.right_bumper == true).
            servoController.setServoPosition(flipperL, .47); //Here we're setting the servo position if the right_bumper is pressed on the first gamepad.
        }
        else if (!(gamepad1.right_bumper)) { //Here we're checking if the gamepad1 right bumper isn't pressed, then setting the servo to the .9 value if right_bumper isn't pressed.
            //Technically this does not need to be a else-if statement it could just be a else statement
            //because the right_bumper only has two states, pressed or not pressed, because of this this could just be else{codehere}
            servoController.setServoPosition(flipperL, .9);
        }

        //This is a similar method to the code above for the right bumper.
        if (gamepad1.left_bumper) {
            servoController.setServoPosition(flipperR, .55);
        }
        else if (!(gamepad1.left_bumper)) {
            servoController.setServoPosition(flipperR, 0);
        }


        //This is a 180 degree servo
        if (gamepad1.a) { //Here we check if the A button on gamepad 1 is pressed.
            servoController.setServoPosition(arm, .9); //If it is pressed then set the arm servo to the position of .9
        }
        else if (gamepad1.b) { //If the A button wasn't pressed, then we check if the B button is pressed.
            servoController.setServoPosition(arm, .1); //If the B button was pressed we set the arm servo to the position of .1
        }
        else{ //If neither the A or B button was pressed then it will go to this.
            servoController.setServoPosition(arm, .5); //If neither A or B was pressed set the arm servo to a position of .5
        }

        //Refer to codeblock directly above to get a idea of what this does.
        if (gamepad1.x) {
            servoController.setServoPosition(claw, .3);
        }
        if (gamepad1.y) {
            servoController.setServoPosition(claw, .7);
        }


    }

    @Override
    public void stop(){

    }
}
