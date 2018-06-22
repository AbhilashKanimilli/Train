package com.example.grobomac.train;

/**
 * Created by abhi on 5/23/2018.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import static android.support.constraint.Constraints.TAG;



/**
 * Created by grobomac on 17/10/17.
 */

public class Upload extends IntentService {

    AmazonS3 s3;
    TransferUtility transferUtility;
    private String name,dl_no,truck_id, tstatus;
    File fileToUpload;
    int f=0;
    private String uuid;
    String latitude;
    String longitude;
    String time1;
    String date1;
    private int a=0;
    ClientConfiguration clientConf;

    // Must create a default constructor
    public Upload() {
        // Used to name the worker thread, important only for debugging.
        super("Upload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered

        clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(60 * 1000);
        clientConf.setSocketTimeout(120000);
        clientConf.setMaxErrorRetry(5);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //tstatus = extras.getString("status");
            name = extras.getString("name");
            dl_no = extras.getString("dl");
            //truck_id = extras.getString("tid");
           // latitude = extras.getString("lat");
           // longitude = extras.getString("lon");
            time1 = extras.getString("t");
            date1 = extras.getString("d");
        }
        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();


        fileToUpload = new File(Environment.getExternalStorageDirectory() + "/" + "/Pictures/"+ "/CameraSample/"+name+"_"+dl_no+"_"+date1+"_"+time1+"_"+".mp4");
        fileUpload();
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

    /**
     * This method is used to upload the file to S3 by using TransferUtility class
     *
     */
    public void fileUpload(){

        TransferObserver transferObserver = transferUtility.upload(

                "train-faces/TrainingData"+"/"+name+"_"+dl_no,     /* The bucket to upload to */
                name+"_"+dl_no+"_"+date1+"_"+time1+"_"+".mp4",    /* The key for the uploaded object */
                fileToUpload       /* The file where the data to upload exists */
        );

        transferObserverListener(transferObserver);
    }



    /**
     * This is listener method of the TransferObserver
     * Within this listener method, we get status of uploading and downloading file,
     * to display percentage of the part of file to be uploaded or downloaded to S3
     * It displays an error, when there is a problem in  uploading or downloading file to or from S3.
     * @param transferObserver
     */

    public void transferObserverListener(final TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.COMPLETED.equals(transferObserver.getState())) {
                    Toast.makeText(Upload.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                    fileToUpload.delete();
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
                //fileUpload();
            }

        });
    }
}
