package com.example.grobomac.train;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by grobomac on 11/12/17.
 */

public class UploadFiles extends Service {
    AmazonS3 s3;
    TransferUtility transferUtility;
    boolean success = true;
    File fileToUpload2;
    String name1;
    String dl_no;
    ClientConfiguration clientConf;
    public UploadFiles(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(UploadFiles.this, "up started", Toast.LENGTH_SHORT).show();
        //TODO do something useful
        clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(15 * 1000);
        clientConf.setSocketTimeout(120000);
        clientConf.setMaxErrorRetry(5);
        clientConf.setMaxConnections(8);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            name1 = extras.getString("name");
            dl_no = extras.getString("dl");
        }
        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();

        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                if(isInternetOn()&&Check_Files_Presence()){
                    Log.i("HERE", "Previous files are present start uploading them..");
                    //Toast.makeText(UploadFiles.this, "upload_service Started", Toast.LENGTH_SHORT).show();
                    uploadRest();

                } else{
                    Log.i("HERE", "No Previous Files here..");
                  //  Toast.makeText(UploadFiles.this, "upload_service Stoped", Toast.LENGTH_SHORT).show();
                    stopSelf();

                }
            }



        },10, 60000);


        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this,RestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    public void credentialsProvider(){

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-south-1:0836e764-a60b-405d-a70c-d0ce44c90260", // Identity Pool ID
                Regions.AP_SOUTH_1  // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    /**
     *  Create a AmazonS3Client constructor and pass the credentialsProvider.
     * @param credentialsProvider
     */
    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){

        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider,clientConf);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));


    }

    public void setTransferUtility(){

        transferUtility = new TransferUtility(s3, getApplicationContext());
    }
    public final boolean isInternetOn() {

        ConnectivityManager cm = (ConnectivityManager) UploadFiles.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                // Toast.makeText(CameraService.this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                success =  true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                // Toast.makeText(CameraService.this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                success= true;
            }
        } else {
            // not connected to the internet
            Toast.makeText(UploadFiles.this,"NO INTERNET PLEASE CHECK", Toast.LENGTH_SHORT).show();
            success=  false;
        }
        return success;

    }
    public final boolean Check_Files_Presence(){
        Boolean filepresence = true;
        File f = null;
        File[] paths;
        try{
            f = new File(Environment.getExternalStorageDirectory()  + "/" + "/Pictures/"+ "/CameraSample/");

            paths = f.listFiles();
            int count = 0;

            for(File path:paths) {
                String name = path.getName();
                if (name.endsWith(".mp4")) {
                    count++;


                }
            }
            if (count==0){
                filepresence = false;
            }
            else{
                filepresence = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return filepresence;

    }
    public synchronized void uploadRest(){
        File f = null;
        File[] paths;

        try{
            f = new File(Environment.getExternalStorageDirectory() + "/" + "/Pictures/"+ "/CameraSample/");

            paths = f.listFiles();
            while(f.list().length>0) {
                for (File path : paths) {
                    String part1;
                    String name = path.getName();
                    if (name.contains(".")) {
                        String[] parts = name.split(".");
                         part1 = parts[0]; // 004
                        String part2 = parts[1]; // 034556
                    } else {
                        throw new IllegalArgumentException("String " + name + " does not contain .");
                    }
                    //Toast.makeText(CameraService.this,"-"+path+"->"+name, Toast.LENGTH_SHORT).show();
                    String p = path.toString();
                    // Toast.makeText(UploadFiles.this,"path"+"--"+p+"&"+name, Toast.LENGTH_SHORT).show();
                    // fileUpload2(name, p);
                    fileToUpload2 = new File(p);
                    TransferObserver transferObserver2 = transferUtility.upload(
                            "train-faces/TrainingData/"+"/"+part1,     /* The bucket to upload to */
                            name,    /* The key for the uploaded object */
                            fileToUpload2/* The file where the data to upload exists */
                            );
                    transferObserverListener2(transferObserver2);
                    wait(5000);
                }
            }
            stopSelf();



        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void  transferObserverListener2(final TransferObserver transferObserver2){

        transferObserver2.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.COMPLETED.equals(transferObserver2.getState())) {
                    Toast.makeText(UploadFiles.this, "File Upload 2 complete", Toast.LENGTH_SHORT).show();
                    fileToUpload2.delete();

                }
                Log.e(TAG, "statechange"+state+"");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage=0;
                try {

                    percentage = (int) (bytesCurrent/bytesTotal * 100);
                    Log.e(TAG,"percentage"+percentage +"");

                }

                catch (ArithmeticException e) {

                    //System.out.println("Division by zero not Possible!");

                }

                if (percentage==100){
                    //fileToUpload2.delete();
                }

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG,"error"+"error");

            }

        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
