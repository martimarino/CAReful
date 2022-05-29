package it.unipi.dii.careful.ui.profile;

import java.util.ArrayList;
import java.util.Objects;

import it.unipi.dii.careful.ui.history.Trip;

public class Session{

    private String username;
    private String name;
    private String surname;
    private String email;
    private ArrayList<Trip> tripList;

    public Session (){                   /* not logged user */
        this.username = "anonymous";
        this.tripList = null;
    }

    public boolean isLogged() {
        return !Objects.equals(username, "anonymous");
    }

    public void logout(){
        this.username = "anonymous";
        this.tripList = null;
        this.name = "";
        this.surname = "";
        this.email = "";
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList getTripList() {
        return tripList;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTripList(ArrayList tripList) {
        this.tripList = tripList;
    }
}




