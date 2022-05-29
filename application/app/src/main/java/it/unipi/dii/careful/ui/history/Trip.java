package it.unipi.dii.careful.ui.history;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import java.time.LocalDateTime;

import it.unipi.dii.careful.ui.DatabaseHelper;
import it.unipi.dii.careful.ui.newtrip.NewTripFragment;

public class Trip {

    String timeDeparture;
    String timeArrival;
    String score;
    String departure;
    String arrival;
    Context context;
    DatabaseHelper databaseHelper;

    public Trip (String t1, String t2, String s, String point1, String point2, Context context){
        timeDeparture = t1;
        timeArrival = t2;
        score = s;
        departure = point1;
        arrival = point2;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public Trip (String t1, String t2, String s, String point1, String point2){
        timeDeparture = t1;
        timeArrival = t2;
        score = s;
        departure = point1;
        arrival = point2;
    }

    public String getTimeDeparture() {
        return timeDeparture;
    }

    public String getTimeArrival() {
        return timeArrival;
    }


    public String getScore() {
        return score;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setTimeDeparture(LocalDateTime timeDeparture) {
        this.timeDeparture = String.valueOf(timeDeparture);
    }

    public void setTimeArrival(LocalDateTime timeArrival) {
        this.timeArrival = String.valueOf(timeArrival);
    }

    public void setScore(int score) {
        this.score = String.valueOf(score);
    }

    public void setDeparture(Location departure) {
        this.departure = String.valueOf(departure);
    }

    public void setArrival(Location arrival) {
        this.arrival = String.valueOf(arrival);
    }

    public void insertData() {
        if (databaseHelper.checkUsername(NewTripFragment.session.getUsername()) == false) {
            databaseHelper.insertHistory(NewTripFragment.session.getUsername(), String.valueOf(getTimeDeparture()),
                    String.valueOf(getTimeArrival()), String.valueOf(getScore()),
                    getDeparture(), getArrival());
        } else {
            Toast.makeText(context, "Please sign in to save your next trip!", Toast.LENGTH_SHORT).show();
        }
    }
    public void updateHistory() {
        NewTripFragment.session.setTripList(databaseHelper.retrieveHistory(NewTripFragment.session.getUsername()));
    }
}
