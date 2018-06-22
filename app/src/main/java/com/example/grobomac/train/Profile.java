package com.example.grobomac.train;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by abhi on 3/7/2018.
 */

public class Profile extends Fragment implements View.OnClickListener{
    Button myButton, myButton1,myButton2;
    TextView textViewId, textViewUsername, textViewEmail, textViewTrainingstatus;
    Boolean camerasupport;
    private String cameraId;
    static String  latitude;
    static String longitude;

    private Location mLastLocation;
    private final int REQUEST_LOCATION = 200;
    int MY_CAMERA_REQUEST_CODE = 100;
    private final int REQUEST_CHECK_SETTINGS = 300;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String name, dl_no, truck_id, staus, tstatus0 = "0", tstatus1 = "1", tstatus2 = "2", tstatus = "4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        View myView = inflater.inflate(R.layout.prof, container, false);
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }


        textViewId = (TextView) myView.findViewById(R.id.textViewId);
        textViewUsername = (TextView) myView.findViewById(R.id.textViewDrivername);
        textViewEmail = (TextView) myView.findViewById(R.id.textViewTruckid);
        textViewTrainingstatus = (TextView) myView.findViewById(R.id.textViewstatus);


        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        //setting the values to the textviews
        textViewId.setText(String.valueOf(user.getDriverid()));
        textViewUsername.setText(user.getDrivername());
        textViewEmail.setText(user.getTruckid());
        textViewTrainingstatus.setText(user.getTrainingstatus());

        name = user.getDrivername();
        dl_no = String.valueOf(user.getDriverid());
        truck_id = user.getTruckid();
        staus = user.getTrainingstatus();

        myButton = (Button) myView.findViewById(R.id.buttonmanual);
        myButton.setOnClickListener(this);
        return myView;
    }
    @Override
    public void onClick(View v) {
        // implements your things
        switch (v.getId()) {
            case R.id.buttonmanual:
                Intent intent = new Intent(getActivity(),MainActivity.class);
                Bundle b = new Bundle();
                b.putString("key", name); //Your id
                b.putString("keyid",dl_no);
                intent.putExtras(b);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().getFragmentManager().popBackStack();
                break;

        }
    }






    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        } else {
                           // mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                // latitudePosition.setText(String.valueOf(mLastLocation.getLatitude()));
                                //longitudePosition.setText(String.valueOf(mLastLocation.getLongitude()));
                                //getAddressFromLocation(mLastLocation, getApplicationContext(), new GeoCoderHandler());
                            }
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // user does not want to update setting. Handle it in a way that it will to affect your app functionality
                        Toast.makeText(getActivity(), "User does not update location setting", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



}

