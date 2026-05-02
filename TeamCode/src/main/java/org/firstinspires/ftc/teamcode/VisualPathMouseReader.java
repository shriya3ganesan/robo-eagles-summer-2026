package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.HashMap;

/**
 * VisualPathMouseReader
 *
 * Reads raw HID boot-protocol mouse delta data from a USB optical mouse
 * plugged into the Control Hub. Intended as an odometry input source for
 * VisualPath.
 *
 * Usage:
 * Call start() once, poll getDeltaX() / getDeltaY() each loop, then call
 * stop() on cleanup.
 *
 * getDeltaX/Y() consumes accumulated distance, making it thread-safe
 */

public class VisualPathMouseReader {

    // HID boot-protocol identifiers
    private static final int HID_SUBCLASS_BOOT = 0x01;
    private static final int HID_PROTOCOL_MOUSE = 0x02;

    private static final int TRANSFER_TIMEOUT_MS = 50;
    private static final int PACKET_SIZE = 8;

    // Accumulated deltas written by poll thread, reset by odometry thread after usage
    private volatile int accDeltaX = 0;
    private volatile int accDeltaY = 0;

    // USB Device Management Objects
    private final UsbManager usbManager;

    private UsbDevice mouseDevice;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;

    private Thread pollThread;
    private volatile boolean running = false;

    // Program Configuration
    private boolean invertX = false;
    private boolean invertY = false;

    private double sensorDPI;
    private boolean usingSensorDPI = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * @param activity The FTC OpMode's activity context (cast hardwareMap.appContext).
     */
    public VisualPathMouseReader(Activity activity) {
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
    }

    /**
     * @param activity The FTC OpMode's activity context (cast hardwareMap.appContext).
     * @param sensorDPI The DPI of the sensor being used, required for getting measurement conversions.
     */
    public VisualPathMouseReader(Activity activity, double sensorDPI) {
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        this.sensorDPI = sensorDPI;
        this.usingSensorDPI = true;
    }

    /**
     * @param activity The FTC OpMode's activity context (cast hardwareMap.appContext).
     * @param invertX Pass true to invert deltaX on consumption.
     * @param invertY Pass true to invert deltaY on consumption.
     */
    public VisualPathMouseReader(Activity activity, boolean invertX, boolean invertY) {
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        this.invertX = invertX;
        this.invertY = invertY;
    }

    /**
     * @param activity The FTC OpMode's activity context (cast hardwareMap.appContext).
     * @param invertX Pass true to invert deltaX on consumption.
     * @param invertY Pass true to invert deltaY on consumption.
     * @param sensorDPI The DPI of the sensor being used, required for getting measurement conversions.
     */
    public VisualPathMouseReader(Activity activity, boolean invertX, boolean invertY, double sensorDPI) {
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        this.invertX = invertX;
        this.invertY = invertY;
        this.sensorDPI = sensorDPI;
        this.usingSensorDPI = true;
    }

    // -------------------------------------------------------------------------
    // Lifecycle Methods
    // -------------------------------------------------------------------------

    /**
     * Gets the first USB HID boot-mouse, claims it, and
     * starts the background poll thread.
     *
     * @return true if the mouse was found and opened successfully, false otherwise.
     */
    public boolean start() {
        if (!findMouseDevice()) return false;
        if (!openDevice())      return false;

        running = true;
        pollThread = new Thread(this::pollLoop);
        pollThread.setDaemon(true);
        pollThread.start();
        return true;
    }

    /**
     * Stops the poll thread and releases the USB device.
     * Intended to be called during OpMode cleanup
     */
    public void stop() {
        running = false;
        if (pollThread != null) {
            try {pollThread.join(200);} catch (InterruptedException ignored) {}
        }
        closeDevice();
    }

    // -------------------------------------------------------------------------
    // Movement Delta Accessors
    // -------------------------------------------------------------------------

    /**
     * @return Accumulated X displacement since the last call
     * By default, positive X = mouse moved right.
     * Resets accumulated delta X
     */
    public synchronized int consumeDeltaX() {
        int v = accDeltaX;
        accDeltaX = 0;
        if (this.invertX) return -v; else return v;
    }

    /**
     * @return Accumulated Y displacement since the last call
     * By default, positive Y = mouse moved down.
     * Resets accumulated delta Y
     */
    public synchronized int consumeDeltaY() {
        int v = accDeltaY;
        accDeltaY = 0;
        if (this.invertY) return -v; else return v;
    }

    /**
     * @return An Integer Array: {deltaX, deltaY}.
     * Resets both accumulated delta X and Y.
     */
    public synchronized int[] consumeDelta() {
        int[] d = {accDeltaX, accDeltaY};
        accDeltaX = 0;
        accDeltaY = 0;
        return d;
    }

    /** True if the device is open and the poll thread is running. */
    public boolean isRunning() {return running;}

    /** Device name string for telemetry / logging, or null if not connected. */
    public String getDeviceName() {
        return mouseDevice != null ? mouseDevice.getDeviceName() : null;
    }

    // -------------------------------------------------------------------------
    // Movement Converters
    // -------------------------------------------------------------------------

    /**
     * @param value The raw delta value on either x/y.
     * @return A measurement in inches based on DPI.
     */
    public double convertDeltaToInch(int value) throws Exception {
        if (this.usingSensorDPI) return (double) value / this.sensorDPI;
        else throw new Exception("No sensor DPI provided.");
    }

    // -------------------------------------------------------------------------
    // Internal Device Discovery
    // -------------------------------------------------------------------------

    private boolean findMouseDevice() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            for (int i = 0; i < device.getInterfaceCount(); i++) {
                UsbInterface iface = device.getInterface(i);
                if (iface.getInterfaceClass() == UsbConstants.USB_CLASS_HID &&
                    iface.getInterfaceSubclass() == HID_SUBCLASS_BOOT &&
                    iface.getInterfaceProtocol() == HID_PROTOCOL_MOUSE) {
                        mouseDevice  = device;
                        usbInterface = iface;
                        return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Internal Device Open/Close
    // -------------------------------------------------------------------------
    private boolean openDevice() {
        if (!usbManager.hasPermission(mouseDevice)) return false;

        connection = usbManager.openDevice(mouseDevice);
        if (connection == null) return false;

        if (!connection.claimInterface(usbInterface, true)) {
            connection.close();
            return false;
        }

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType()      == UsbConstants.USB_ENDPOINT_XFER_INT &&
                    ep.getDirection() == UsbConstants.USB_DIR_IN) {
                inEndpoint = ep;
                break;
            }
        }

        return inEndpoint != null;
    }

    private void closeDevice() {
        if (connection != null) {
            if (usbInterface != null) connection.releaseInterface(usbInterface);
            connection.close();
            connection = null;
        }
    }

    // -------------------------------------------------------------------------
    // Internal Poll Loop
    //
    // Byte 0 : buttons  [0=Left, 1=Right, 2=Middle] (Bit index 0, 1, 2)
    // Byte 1 : X delta  (signed 8-bit)
    // Byte 2 : Y delta  (signed 8-bit)
    // Byte 3 : wheel    (signed 8-bit)
    // -------------------------------------------------------------------------
    private void pollLoop() {
        byte[] buf = new byte[PACKET_SIZE];
        while (running) {
            int len = connection.bulkTransfer(inEndpoint, buf, buf.length, TRANSFER_TIMEOUT_MS);
            if (len >= 3) {
                synchronized (this) {
                    accDeltaX += buf[1];
                    accDeltaY += buf[2];
                }
            }
        }
    }
}