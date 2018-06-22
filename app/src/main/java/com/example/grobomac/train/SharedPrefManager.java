package com.example.grobomac.train;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by grobomac on 19/9/17.
 */

//here for this class we are using a singleton pattern

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_DRIVERID = "keyDRIVERNAME";
    private static final String KEY_DRIVERNAME = "keyDRIVERID";
    private static final String KEY_TRUCKID = "keyTRUCKID";
    private static final String KEY_TRAININGSTATUS = "keyTRAININGSTATUS";


    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DRIVERID, user.getDriverid());
        editor.putString(KEY_DRIVERNAME, user.getDrivername());
        editor.putString(KEY_TRUCKID, user.getTruckid());
        editor.putString(KEY_TRAININGSTATUS, user.getTrainingstatus());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVERID, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_DRIVERID, null),
                sharedPreferences.getString(KEY_DRIVERNAME, null),
                sharedPreferences.getString(KEY_TRUCKID, null),
                sharedPreferences.getString(KEY_TRAININGSTATUS, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
    }
}
