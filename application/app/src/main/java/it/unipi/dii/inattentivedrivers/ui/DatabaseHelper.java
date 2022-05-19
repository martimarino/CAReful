package it.unipi.dii.inattentivedrivers.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME2 = "in.db";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE user"
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "username TEXT NOT NULL, " +
            "name TEXT NOT NULL, " +
            "surname TEXT NOT NULL, " +
            "email TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "repeatPassword TEXT NOT NULL)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME2, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
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

    public Boolean CheckUsername(String username){
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
}
