package org.firstinspires.ftc.teamcode.Robot;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.LocalizationPacket;

public interface Localization {
    public void update();
    public LocalizationPacket getLocalizationPacket();
    public void overrideLocalization(double x, double y, double angle);
}
