package com.example.malix.takeabreak.technicalServices.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.malix.takeabreak.domain.datalayer.MessageDTO;
import com.example.malix.takeabreak.domain.datalayer.ProfileDTO;

import static android.content.ContentValues.TAG;

public class LocalDatabase extends SQLiteOpenHelper {

    private static final String mDatabaseName = "LocalDatabase";
    private static final int mDatabaseVersion = 2;

    //context
    public Context context;
    //Profile Table
    public static final String TABLE_profile = "profile";
    //Profile Table Columns
    public static final String COLUMN_pID = "profileID";
    public static final String COLUMN_pName = "profileName";
    public static final String COLUMN_pApps = "profileApps";

    //Profile Table
    public static final String TABLE_schedulehold = "scheduleHold";
    //Profile Table Columns
    public static final String COLUMN_sID = "scheduleID";
    public static final String COLUMN_sName = "scheduleName";
    public static final String COLUMN_startTime = "startTime";
    public static final String COLUMN_endTime = "endTime";

    //message Table
    public static final String TABLE_message = "message";
    //message Table Columns
    public static final String COLUMN_mID = "messageID";
    public static final String COLUMN_message = "message";


    public LocalDatabase(Context context) {
        super(context, mDatabaseName, null, mDatabaseVersion);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //profile Table
        String profileTableQuery =
                " CREATE TABLE  " + TABLE_profile
                        + "( "
                        + COLUMN_pID + "  INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE , "
                        + COLUMN_pName + "   TEXT NOT NULL , "
                        + COLUMN_pApps + "   TEXT NOT NULL "
                        + " );";
        db.execSQL(profileTableQuery);

        //Schedule hold Table
        String scheduleHoldTableQuery =
                " CREATE TABLE  " + TABLE_schedulehold
                        + "( "
                        + COLUMN_sID + "  INTEGER REFERENCES " + TABLE_profile +" ( "+ COLUMN_pID + " ) " + " PRIMARY KEY AUTOINCREMENT UNIQUE , "
                        + COLUMN_sName + "   TEXT NOT NULL , "
                        + COLUMN_startTime + "   TIME NOT NULL , "
                        + COLUMN_endTime + "TIME NOT NULL "
                        + " );";
        db.execSQL(scheduleHoldTableQuery);
        //mssage Table
        String messageTableQuery =
                " CREATE TABLE  " + TABLE_message
                        + "( "
                        + COLUMN_mID + "  INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE , "
                        + COLUMN_message + "   TEXT NOT NULL "
                        + " );";
        db.execSQL(messageTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_profile);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_message);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_schedulehold);
        onCreate(db);

    }

    public boolean addProfile( ProfileDTO profileDTO ){

        String selectedApps = convertArrayToString(profileDTO.getSelectedApps());

        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        //inserting data
        values.put(COLUMN_pName, profileDTO.getpName());
        values.put(COLUMN_pApps, selectedApps);

        Log.d("1" , "Addind"+profileDTO.getpName()+"to"+TABLE_profile);

        Log.d("2" ,"Addind"+selectedApps+"to"+TABLE_profile);

        long res = db.insert(TABLE_profile , null , values);
        db.close();
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean addMessage(MessageDTO messageDTO){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        //inserting data
        values.put(COLUMN_message, messageDTO.getMessage());

        Log.d("1" , "Adding"+messageDTO.getMessage()+"to"+TABLE_message);

        long res = db.insert(TABLE_message , null , values);
        db.close();
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateRecord( String newName , String oldName, ProfileDTO profileDTO , int id){

        String selectedApps = convertArrayToString(profileDTO.getSelectedApps());
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_profile + " SET "+COLUMN_pApps+" = '"+selectedApps+"' , " + COLUMN_pName +
                " = '" + newName + "' WHERE " + COLUMN_pID + " = '" + id + "'" +
                " AND " + COLUMN_pName + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
        return true;


    }

    public Cursor selctedApps(String profileName , int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "  SELECT  * FROM  " + TABLE_profile + "  WHERE   " + COLUMN_pName +
                " = '"+ profileName+"' AND "+ COLUMN_pID + " = '"+id+"'";
        Cursor data = db.rawQuery(query,null);
        return data;

    }
    public Cursor getData(){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_profile;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
    public Cursor getMessage(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_message;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
 // Delete MEthod
    public void deleteProfile(String profileName , int id) {


        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_profile + " WHERE "
                + COLUMN_pID + " = '" + id + "'" +
                " AND " + COLUMN_pName + " = '" + profileName + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + profileName + " from database.");
        db.execSQL(query);

  }

  public void deleteMessage(String message ){
      SQLiteDatabase db = this.getWritableDatabase();
      String query = "DELETE FROM " + TABLE_message + " WHERE "
              + COLUMN_message + " = '" + message + "'";
      Log.d(TAG, "deleteName: query: " + query);
      Log.d(TAG, "deleteName: Deleting " + message + " from database.");
      db.execSQL(query);
  }

    //ID
    public Cursor selectItemId( String name){
        SQLiteDatabase db;
        db = getWritableDatabase();
        String query = " SELECT "+COLUMN_pID+" FROM "+TABLE_profile+ " WHERE "+COLUMN_pName+ " = '"+name+"'";
        Cursor data = db.rawQuery(query,null);
        return data;

    }



    public static String strSeparator = ",";
    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void databasemessage( CharSequence text1 )
    {
        Context context  = getContext();
        CharSequence text = text1;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
