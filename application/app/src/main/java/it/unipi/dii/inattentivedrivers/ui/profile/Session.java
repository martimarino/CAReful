package it.unipi.dii.inattentivedrivers.ui.profile;

import android.location.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.ui.history.Trip;

public class Session {
    public String username;
    public ArrayList<Trip> arrayList;


    public Session (String username, ArrayList arrayList){
        this.username = username;
        this.arrayList = arrayList;
    }

    public ArrayList getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList arrayList) {
        this.arrayList = arrayList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}




