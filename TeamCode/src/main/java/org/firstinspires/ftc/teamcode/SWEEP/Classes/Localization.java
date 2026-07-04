package org.firstinspires.ftc.teamcode.SWEEP.Classes;

public interface Localization {
    public void update();
    public LocalizationPacket getLocalizationPacket();
    public void overrideLocalization(double x, double y, double angle);
}
