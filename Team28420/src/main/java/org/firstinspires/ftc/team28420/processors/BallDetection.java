package org.firstinspires.ftc.team28420.processors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.team28420.config.BallDetectionConf;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

public class BallDetection implements VisionProcessor {
    private final Mat hsv = new Mat();
    private final Mat mask = new Mat();
    private final Mat hierarchy = new Mat();
    private final Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15));
    private final MatOfPoint2f contour2f = new MatOfPoint2f();
    private final List<MatOfPoint> contours = new ArrayList<>();
    private BallColor detectedColor = BallColor.NONE;
    private Point ballPosition = null;
    private float ballRadius = 0; // Initialize to 0
    private double maxArea = 0;
    private double frameWidth = 0;

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        this.frameWidth = width;
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        detectedColor = BallColor.NONE;
        ballPosition = null;

        if (!detectBall(hsv, BallDetectionConf.lowGreen, BallDetectionConf.highGreen, BallColor.GREEN)) {
            detectBall(hsv, BallDetectionConf.lowPurple, BallDetectionConf.highPurple, BallColor.PURPLE);
        }

        Imgproc.cvtColor(mask, frame, Imgproc.COLOR_GRAY2RGB);

        return null;
    }

    private boolean detectBall(Mat hsv, Scalar low, Scalar high, BallColor color) {
        Core.inRange(hsv, low, high, mask);

        Imgproc.GaussianBlur(mask, mask, new Size(15, 15), 0);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        contours.clear();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        boolean found = false;
        maxArea = 0;
        MatOfPoint bestContour = null;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);

            // 1. Area Check
            if (area > BallDetectionConf.MIN_AREA && area < BallDetectionConf.MAX_AREA) {

                // 2. Aspect Ratio Check (Filter out wide Robot Bumpers)
                Rect rect = Imgproc.boundingRect(contour);
                double ratio = (double) rect.width / rect.height;

                // A ball is 1.0. A robot bumper is usually > 2.0.
                // We allow 0.8 to 1.2 for a perfect circle.
                if (ratio < 0.8 || ratio > 1.2) {
                    continue;
                }

                // 3. Circularity Check
                contour.convertTo(contour2f, CvType.CV_32F);
                double perimeter = Imgproc.arcLength(contour2f, true);
                if (perimeter > 0) {
                    double circularity = (4 * Math.PI * area) / (perimeter * perimeter);

                    // If it is circular AND larger than the last one we found
                    if (circularity > BallDetectionConf.MIN_CIRCULARITY && area > maxArea) {
                        maxArea = area;
                        bestContour = contour;
                    }
                }
            }
        }

        if (bestContour != null) {
            Moments m = Imgproc.moments(bestContour);
            if (m.m00 != 0) {
                ballRadius = (float) Math.sqrt(maxArea / Math.PI);
                ballPosition = new Point(m.m10 / m.m00, m.m01 / m.m00);
                detectedColor = color;
                found = true;
            }
        }
        return found;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasPxToDrawPx, Object userContext) {
        if (ballPosition != null) {
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            // Draw circle on the detected ball
            canvas.drawCircle((float) ballPosition.x * scaleBmpPxToCanvasPx, (float) ballPosition.y * scaleBmpPxToCanvasPx, ballRadius * scaleBmpPxToCanvasPx, paint);
        }
    }

    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("max area:", maxArea);
        telemetry.addData("contours:", contours.size());
    }

    public BallColor getDetectedColor() {
        return detectedColor;
    }

    public Point getBallPosition() {
        return ballPosition;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public enum BallColor {GREEN, PURPLE, NONE}

}