package com.example.carpark;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Restriction{
    private String Description;
    private int DisabilityExt;
    private int Duration;
    private int EffectiveOnPH;
    private Date StartTime;
    private Date EndTime;
    private String Exemption;
    private int FromDay;
    private int ToDay;
    private String TypeDesc;


    public Restriction(String description, int disabilityExt, int duration, int effectiveOnPH, Date startTime,
                       Date endTime, String exemption, int fromDay, int toDay, String typeDesc) {
        Description = description;
        DisabilityExt = disabilityExt;
        Duration = duration;
        EffectiveOnPH = effectiveOnPH;
        StartTime = startTime;
        EndTime = endTime;
        Exemption = exemption;
        FromDay = fromDay;
        ToDay = toDay;
        TypeDesc = typeDesc;
    }
    public boolean isDateInRestriction(Date date) {
        if(this.StartTime==null) {
            return false;
        }
        if(date.getTime()>this.StartTime.getTime() && date.getTime()<this.EndTime.getTime()) {
            return true;
        }
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isCurrentInRestriction() {
        if(this.StartTime==null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss");
        String t = dateFormat.format(cal.getTime());
        try {
            Date current = dateFormat.parse(t);
            if(current.getTime()>this.StartTime.getTime() && current.getTime()<this.EndTime.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        return false;
    }
    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }
    public int getDisabilityExt() {
        return DisabilityExt;
    }
    public void setDisabilityExt(int disabilityExt) {
        DisabilityExt = disabilityExt;
    }
    public int getDuration() {
        return Duration;
    }
    public void setDuration(int duration) {
        Duration = duration;
    }
    public int getEffectiveOnPH() {
        return EffectiveOnPH;
    }
    public void setEffectiveOnPH(int effectiveOnPH) {
        EffectiveOnPH = effectiveOnPH;
    }
    public Date getStartTime() {
        return StartTime;
    }
    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }
    public Date getEndTime() {
        return EndTime;
    }
    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }
    public String getExemption() {
        return Exemption;
    }
    public void setExemption(String exemption) {
        Exemption = exemption;
    }
    public int getFromDay() {
        return FromDay;
    }
    public void setFromDay(int fromDay) {
        FromDay = fromDay;
    }
    public int getToDay() {
        return ToDay;
    }
    public void setToDay(int toDay) {
        ToDay = toDay;
    }
    public String getTypeDesc() {
        return TypeDesc;
    }
    public void setTypeDesc(String typeDesc) {
        TypeDesc = typeDesc;
    }
}