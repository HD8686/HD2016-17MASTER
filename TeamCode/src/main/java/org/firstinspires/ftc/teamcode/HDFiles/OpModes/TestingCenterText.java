/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode.HDFiles.OpModes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.R;

import java.util.HashMap;
import java.util.Map;

@Autonomous(name="Test Center Text", group="Testing")
public class TestingCenterText extends OpMode
{
    TextPaint mPaint;

    @Override
    public void init()
    {
        mPaint = ((TextView) ((Activity) hardwareMap.appContext).findViewById(R.id.textOpMode)).getPaint();
        float totalWidth = 1840;
        /*
        This piece of code is to test how large the screen is, you want to keep testing total width values until the ! barely reaches the first line
        int paddingSpaces = Math.round((totalWidth - mPaint.measureText("!"))/mPaint.measureText(" "));
        String format = "%" + (paddingSpaces + "!".length()) + "s";
        telemetry.addData("01", String.format(format, "!"));
        */

        for (int i = 1; i <= 10; i++) {
            char ch = (char)('A' + (int)(Math.random()*26));
            int len = (int)(Math.random()*16) + 2;
            String text = "";
            for (int j = 0; j < len; j++) text += ch;
            telemetry.addData(String.format("%02d", i), centeredText(mPaint, totalWidth, text));
        }
    }

    @Override
    public void loop()
    {

    }

    public String centeredText(TextPaint paint, float width, String text)
    {
        float textWidth = paint.measureText(text);
        int paddingSpaces = Math.round((width - textWidth)/2/paint.measureText(" "));
        String format = "%" + (paddingSpaces + text.length()) + "s";
        return String.format(format, text);
    }

}
