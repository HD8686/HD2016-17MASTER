package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.hdcode.R;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDAutoDiagnostics;
import org.firstinspires.ftc.hdlib.Values;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;



/**
 * Created by Height Differential on 10/8/2016.
 */
@Autonomous(name = "Vuforia Testing", group = "Testing")
public class Vuforia_Testing extends HDOpMode {


    HDAutoDiagnostics mHDAutoDiagnostics;
    HDNavX navX;
    DriveHandler robotDrive;
    HDStateMachine SM;
    VuforiaTrackables beacons;
    @Override
    public void Initialize() {
        navX = new HDNavX();
        robotDrive = new DriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDAutoDiagnostics = new HDAutoDiagnostics(this, mDisplay,robotDrive);
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = Values.Vuforia.VuforiaKey;
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Lego");
        beacons.get(3).setName("Gears");
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(DriveHandler.Side.Left);
    }

    @Override
    public void Start() {
        beacons.activate();
    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
                for(VuforiaTrackable beac : beacons){
                    OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();

                    if(pose != null){
                        VectorF translation = pose.getTranslation();
                        if(beac.getName() == "Wheels")
                        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(1, (beac.getName() + "- Translation: " + Math.round(translation.get(1)/Values.Constants.mmPerInch)));
                        if(beac.getName() == "Tools")
                            HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(2, (beac.getName() + "- Translation: " + Math.round(translation.get(1)/Values.Constants.mmPerInch)));
                        if(beac.getName() == "Lego")
                            HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(3, (beac.getName() + "- Translation: " + Math.round(translation.get(1)/Values.Constants.mmPerInch)));
                        if(beac.getName() == "Gears")
                            HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(4, (beac.getName() + "- Translation: " + Math.round(translation.get(1)/Values.Constants.mmPerInch)));
                    }
                }
                telemetry.update();
        }
    }



}
