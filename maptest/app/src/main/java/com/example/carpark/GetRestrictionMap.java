package com.example.carpark;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class GetRestrictionMap {
    private static HashMap<Integer, BaytoRestriction> bays;

    public GetRestrictionMap(BufferedReader br){
        setRestrictionMap(br);
    }
    public HashMap<Integer, BaytoRestriction> getBayRestriction(){
        // get bay to restriction map
        return bays;
    }
    public static void setRestrictionMap(BufferedReader bf){
        // set the restriction to bays
        bays = new HashMap();
        try {
            String line = "";
            bf.readLine();
            DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss");
            String description;
            String disabilityExt;
            String duration;
            String effectiveOnPH;
            String startTime;
            String endTime;
            String exemption;
            String fromDay;
            String toDay;
            String typeDesc;

            while((line = bf.readLine())!=null) {
                String[] temp = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",-1);
                Restriction[] resList = new Restriction[6];
                for(int i = 0; i<6; i++) {
                    Restriction rule = null;
                    description = temp[2+i];
                    disabilityExt = temp[8+i];
                    duration = temp[14+i];
                    effectiveOnPH = temp[20+i];
                    startTime = temp[44+i];
                    endTime = temp[26+i];
                    exemption = temp[32+i];
                    fromDay = temp[38+i];
                    toDay = temp[50+i];
                    typeDesc = temp[56+i];
                    if(description.equals("")) {
                        rule = new Restriction("",0,0,0,null,null,"",0,0,"");
                    }else {
                        disabilityExt = disabilityExt.replaceAll("\"", "");
                        disabilityExt = disabilityExt.replaceAll(",", "");
                        duration = duration.replaceAll("\"", "");
                        duration = duration.replaceAll(",", "");
                        effectiveOnPH = effectiveOnPH.replaceAll("\"", "");
                        effectiveOnPH = effectiveOnPH.replaceAll(",", "");
                        int dis = Integer.parseInt(disabilityExt);
                        int dur = Integer.parseInt(duration);
                        int eff = Integer.parseInt(effectiveOnPH);

                        rule = new Restriction(description, dis, dur, eff, dateFormat.parse(startTime), dateFormat.parse(endTime),
                                exemption, Integer.parseInt(fromDay), Integer.parseInt(toDay), typeDesc);
                    }
                    resList[i] = rule;

                }

                BaytoRestriction bayRestriction = new BaytoRestriction(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),resList);
                bays.put(bayRestriction.getBayID(), bayRestriction);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public boolean isDateInRestriction(int bayID, int restrictionIndex, Date date){
        // Check which restriction will be fit for input date
        return bays.get(bayID).getRes()[restrictionIndex].isDateInRestriction(date);
    }
    public Restriction getCurrentRestriction(int bayID){
        // Check current time fit to which restriction
        int restrictionID = -1;
        for(int a = 0; a<bays.get(bayID).getRes().length; a++) {
            if (bays.get(bayID).getRes()[a].isCurrentInRestriction()) {
                restrictionID = a;
                break;
            }
        }
        return restrictionID==-1?null:bays.get(bayID).getRes()[restrictionID];
    }
    public boolean isCurrentInRestriction(int bayID, int restrictionIndex){
        return bays.get(bayID).getRes()[restrictionIndex].isCurrentInRestriction();
    }
}