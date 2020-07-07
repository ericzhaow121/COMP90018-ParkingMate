package com.example.carpark;

public class BaytoRestriction {
    private int BayID;
    private int DeviceID;
    private Restriction[] res;

    // This class map bayID to a list of restriction(each bay will have 6 restriction)
    public BaytoRestriction(int bayID, int deviceID, Restriction[] res) {
        BayID = bayID;
        DeviceID = deviceID;
        this.res = res;
    }
    public int getBayID() {
        return BayID;
    }
    public void setBayID(int bayID) {
        BayID = bayID;
    }
    public int getDeviceID() {
        return DeviceID;
    }
    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }
    public Restriction[] getRes() {
        return res;
    }
    public void setRes(Restriction[] res) {
        this.res = res;
    }
}