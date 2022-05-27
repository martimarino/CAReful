package it.unipi.dii.inattentivedrivers.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.ui.history.Trip;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME2 = "clown.db";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE user"
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "username TEXT NOT NULL, " +
            "name TEXT NOT NULL, " +
            "surname TEXT NOT NULL, " +
            "email TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "repeatPassword TEXT NOT NULL)";

    private static final String CREATE_TABLE2_SQL = "CREATE TABLE history"
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "username TEXT NOT NULL, " +
            "timeDeparture TEXT NOT NULL, " +
            "timeArrival TIME NOT NULL, " +
            "score TEXT NOT NULL, " +
            "departure TEXT NOT NULL, " +
            "arrival TEXT NOT NULL)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME2, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
        db.execSQL(CREATE_TABLE2_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS history");
    }

    public boolean Insert(String username, String name, String surname, String email, String password, String repeatPassword){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("repeatPassword", repeatPassword);
        long result = sqLiteDatabase.insert("user", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkUsername(String username){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user WHERE username=?", new String[]{username});
        if(cursor.getCount() > 0){
            return false;
        } else {
            return true;
        }
    }

    public Boolean CheckLogin(String username_, String password_){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user WHERE username=? AND password=?", new String[]{username_, password_});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Trip> retrieveHistory(String username_){
        ArrayList<Trip> arrayList = new ArrayList<Trip>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM history WHERE username=?", new String[]{username_});
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String timeDeparture = cursor.getString(cursor.getColumnIndex("timeDeparture"));
            @SuppressLint("Range") String timeArrival = cursor.getString(cursor.getColumnIndex("timeArrival"));
            @SuppressLint("Range") String score = cursor.getString(cursor.getColumnIndex("score"));
            @SuppressLint("Range") String departure = cursor.getString(cursor.getColumnIndex("departure"));
            @SuppressLint("Range") String arrival = cursor.getString(cursor.getColumnIndex("arrival"));
            Trip trip = new Trip(timeDeparture,timeArrival, score, departure, arrival);
            arrayList.add(trip);
        }
        return arrayList;
    }


    public boolean insertHistory(String username, String timeDeparture, String timeArrival, String score, String departure, String arrival){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("timeDeparture", timeDeparture);
        contentValues.put("timeArrival", timeArrival);
        contentValues.put("score", score);
        contentValues.put("departure", departure);
        contentValues.put("arrival", arrival);
        long result = sqLiteDatabase.insert("history", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

}
