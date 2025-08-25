package com.poli.lms;

import java.time.LocalDate;

public class studentProperty {
    private int mid;
    private String deviceType;
    private String model;
    private String serial;
    private LocalDate entryDate;

    public studentProperty(int mid, String deviceType, String model, String serial, LocalDate entryDate) {
        this.mid = mid;
        this.deviceType = deviceType;
        this.model = model;
        this.serial = serial;
        this.entryDate = entryDate;
    }

    public int getMid() { return mid; }
    public String getDeviceType() { return deviceType; }
    public String getModel() { return model; }
    public String getSerial() { return serial; }
    public LocalDate getEntryDate() { return entryDate; }
}
