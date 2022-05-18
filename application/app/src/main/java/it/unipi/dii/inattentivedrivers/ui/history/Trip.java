package it.unipi.dii.inattentivedrivers.ui.history;

import android.location.Location;

import java.time.LocalDateTime;

public class Trip {

    public LocalDateTime date;
    public float duration;
    public float score;
    public Location departure;
    public Location arrival;

    public Trip (LocalDateTime l, float d, float s, Location point1, Location point2){
        date = l;
        duration = d;
        score = s;
        departure = point1;
        arrival = point2;
        //how many times you have been distracted for every sensor
        //or for how much time
    }

    public LocalDateTime getDate() {
        return date;
    }

    public float getDuration() {
        return duration;
    }

    public float getScore() {
        return score;
    }

    public Location getDeparture() {
        return departure;
    }

    public Location getArrival() {
        return arrival;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    public void setArrival(Location arrival) {
        this.arrival = arrival;
    }
}
