package org.firstinspires.ftc.team28420.processors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import java.util.ArrayList;
import java.util.List;

// TODO refactor everything
public class BallDetectionProcessor implements VisionProcessor {

    @Config
    public static class BallDetectionConf {
        public static Scalar lowGreen = new Scalar(35, 50, 50);
        public static Scalar highGreen = new Scalar(85, 255, 255);
        public static Scalar lowPurple = new Scalar(125, 50, 50);
        public static Scalar highPurple = new Scalar(160, 255, 255);
        public static double MIN_AREA = 15000; // минимальный размер объекта
        public static double kP = 0.0019;
    }

    private final Mat hsv = new Mat();
    private final Mat mask = new Mat();
    private final Mat hierarchy = new Mat();
    private final Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
    private final List<MatOfPoint> contours = new ArrayList<>();

    private BallColor detectedColor = BallColor.NONE;
    private Point ballPosition = null;
    private float ballRadius = 0;
    private double maxArea = 0;

    @Override
    public void init(int width, int height, CameraCalibration calibration) {}

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        detectedColor = BallColor.NONE;
        ballPosition = null;
        maxArea = 0;

        // ВНИМАНИЕ: Для зеленого цвета в OpenCV Hue (H) должен быть примерно от 35 до 85!
        // Проверь свои BallDetectionConf.lowGreen и highGreen! OpenCV использует Hue 0-180, а не 0-360.
        boolean foundGreen = detectBall(hsv, BallDetectionConf.lowGreen, BallDetectionConf.highGreen, BallColor.GREEN);

        if (!foundGreen) {
            detectBall(hsv, BallDetectionConf.lowPurple, BallDetectionConf.highPurple, BallColor.PURPLE);
        }

        // Imgproc.cvtColor(mask, frame, Imgproc.COLOR_GRAY2RGB);

        return null;
    }

    private boolean detectBall(Mat hsv, Scalar low, Scalar high, BallColor color) {
        Core.inRange(hsv, low, high, mask);

        // Filter noise
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        contours.clear();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint bestContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > BallDetectionConf.MIN_AREA && area > maxArea) {
                maxArea = area;
                bestContour = contour;
            }
        }

        if (bestContour != null) {
            Moments m = Imgproc.moments(bestContour);
            if (m.m00 != 0) {
                ballRadius = (float) Math.sqrt(maxArea / Math.PI);
                ballPosition = new Point(m.m10 / m.m00, m.m01 / m.m00);
                detectedColor = color;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasPxToDrawPx, Object userContext) {
        if (ballPosition != null) {
            Paint paint = new Paint();
            paint.setColor(detectedColor == BallColor.GREEN ? Color.GREEN : Color.MAGENTA);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            canvas.drawCircle((float) ballPosition.x * scaleBmpPxToCanvasPx, (float) ballPosition.y * scaleBmpPxToCanvasPx, ballRadius * scaleBmpPxToCanvasPx, paint);
        }
    }

    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData("Detected", detectedColor);
        telemetry.addData("Area", (int)maxArea);
    }

    public BallColor getDetectedColor() { return detectedColor; }
    public Point getBallPosition() { return ballPosition; }
    public double getBallArea() {
        if(ballPosition != null) return maxArea;
        return Double.NaN;
    }
    public enum BallColor {GREEN, PURPLE, NONE}
}