package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous
public class StateMachineAutonomous extends OpMode {
    HardwareTestbotRevised robot = new HardwareTestbotRevised();
    private static final String TFOD_MODEL_ASSET = "capstone2.tflite";
    private static final String[] LABELS = {
            "capstone",
    };
    private static final String VUFORIA_KEY =
            "Aduz/W3/////AAABmT8OMdd/BE5zqPW2gudw2RuOt8zA9TIKZ5HYv6Ec97unQ9b4cxX8Oh3zaR4iqBO5VJROuKpuJtN7NX/VnyqSjksKlD5iRSr9sLqhP2a27eADb8bUGJ9TdHfEQSNJNhsPlUGH47vzWjG6nTOoisxHORUNqxHAUeCOcHbdeVivWv5dSoOtQuQUY8D1E3K/DFVZYdRcWgx5txDfnrRapWR8cHhQhEiNrZShZbP4Dn/QFKc2S5dMb6jxN+Cgo4uaGH2MwZ0qII+RKQCsDde9V2t80DYT+CKl3gC/hW1cz8a6OPauf17BxX/bBocvQwSZ5BVlXW0W6XxaC72493EkrS1t163Ba+suwT7Z47ZDdqM28i2k";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;
    private enum State {
        SENSING_1ST,
        SENSING_2ND,
        DROP_IN_1ST,
        DROP_IN_2ND,
        DROP_IN_3RD,
        GO_TO_DUCK,
        SPIN_DUCK,
        PARK_IN_WAREHOUSE,
        DONE
    }
    private State state = State.SENSING_1ST;


    @Override
    public void init() {
        robot.init(hardwareMap);
    }

    @Override
    public void start() {
        state = State.SENSING_1ST;
    }

    @Override
    public void loop() {
        telemetry.addData("Current State: ", state);
        switch (state) {
            case SENSING_1ST:
                initVuforia();
                initTfod();
                boolean isTrue = false;
                if (tfod != null) {
                    tfod.activate();

                    // The TensorFlow software will scale the input images from the camera to a lower resolution.
                    // This can result in lower detection accuracy at longer distances (> 55cm or 22").
                    // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
                    // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
                    // should be set to the value of the images used to create the TensorFlow Object Detection model
                    // (typically 16/9).
                    tfod.setZoom(2.5, 16.0/9.0);
                }
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            i++;
                            telemetry.update();
                            if (recognition.getLabel().equals(LABELS[0])) {
                                telemetry.addData("Sensing: ", "True");
                                telemetry.addData("Confidence: ", recognition.getConfidence());
                                telemetry.update();
                                isTrue = true;
                            } else {
                                telemetry.addData("Sensing: ", "False");
                                telemetry.addData("Confidence: ", recognition.getConfidence());
                                telemetry.update();
                            }
                        }
                        if (isTrue == true) {
                            state = State.DROP_IN_1ST;
                        } else {
                            robot.camera.setPosition(0.3);
                            state = State.SENSING_2ND;
                        }
                    }
                }
            case SENSING_2ND:
                initVuforia();
                initTfod();
                initVuforia();
                initTfod();
                isTrue = false;
                if (tfod != null) {
                    tfod.activate();

                    // The TensorFlow software will scale the input images from the camera to a lower resolution.
                    // This can result in lower detection accuracy at longer distances (> 55cm or 22").
                    // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
                    // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
                    // should be set to the value of the images used to create the TensorFlow Object Detection model
                    // (typically 16/9).
                    tfod.setZoom(2.5, 16.0/9.0);
                }
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            i++;
                            telemetry.update();
                            if (recognition.getLabel().equals(LABELS[0])) {
                                telemetry.addData("Sensing: ", "True");
                                telemetry.addData("Confidence: ", recognition.getConfidence());
                                telemetry.update();
                                isTrue = true;
                            } else {
                                telemetry.addData("Sensing: ", "False");
                                telemetry.addData("Confidence: ", recognition.getConfidence());
                                telemetry.update();
                            }
                        }
                        if (isTrue == true) {
                            state = State.DROP_IN_2ND;
                        } else {
                            robot.camera.setPosition(0.3);
                            state = State.DROP_IN_3RD;
                        }
                    }
                }
            case DROP_IN_1ST:
                state = State.GO_TO_DUCK;
            case DROP_IN_2ND:
                state = State.GO_TO_DUCK;
            case DROP_IN_3RD:
                state = State.GO_TO_DUCK;
            case GO_TO_DUCK:
                state = State.SPIN_DUCK;
            case SPIN_DUCK:
                state = State.PARK_IN_WAREHOUSE;
            case PARK_IN_WAREHOUSE:
                state = State.DONE;
        }
    }
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the Tens  orFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.65f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}