package it.unipi.dii.inattentivedrivers.ui.history;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import java.time.LocalDateTime;

import it.unipi.dii.inattentivedrivers.ui.DatabaseHelper;
import it.unipi.dii.inattentivedrivers.ui.profile.Session;
import it.unipi.dii.inattentivedrivers.ui.profile.SignInSignUp;

public class Trip {

    public String timeDeparture;
    public String timeArrival;
    public String score;
    public String departure;
    public String arrival;
    public Context context;
    DatabaseHelper databaseHelper;

    public Trip (String t1, String t2, String s, String point1, String point2, Context context){
        timeDeparture = t1;
        timeArrival = t2;
        score = s;
        departure = point1;
        arrival = point2;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        //how many times you have been distracted for every sensor
        //or for how much time
    }

    public Trip (String t1, String t2, String s, String point1, String point2){
        timeDeparture = t1;
        timeArrival = t2;
        score = s;
        departure = point1;
        arrival = point2;
        //how many times you have been distracted for every sensor
        //or for how much time
    }

    public String getTimeDeperture() {
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
        databaseHelper.insertHistory(SignInSignUp.session.username, String.valueOf(getTimeDeperture()),
                String.valueOf(getTimeArrival()), String.valueOf(getScore()),
                getDeparture(), getArrival());
    }

}
