package com.example.grobomac.train;

/**
 * Created by grobomac on 19/9/17.
 */

public class User {

    private String driverid, drivername, truckid, trainingstatus;

    public User(String driverid, String drivername, String truckid, String trainingstatus) {
        this.driverid = driverid;
        this.drivername = drivername;
        this.truckid = truckid;
        this.trainingstatus = trainingstatus;
    }

    public String getDriverid() {
        return driverid;
    }

    public String getDrivername() {
        return drivername;
    }

    public String getTruckid() {
        return truckid;
    }

    public String getTrainingstatus() { return trainingstatus; }

}
