package com.example.grobomac.train;

/**
 * Created by abhi on 5/30/2018.
 */

public class Status {
    public String d_name;
    public String d_id;
    public String t_status;

    public Status(String dname, String d_id, String t_status) {

            super();

            this.d_name = dname;

            this.d_id = d_id;
            this.t_status = t_status;
        }

        public String getD_name() {

            return d_name;

        }

        public void setD_name(String d_name) {

            this.d_name = d_name;

        }

        public String getD_id() {

            return d_id;

        }

        public void setD_id(String d_id) {

            this.d_id = d_id;

        }
    public String getT_status() {

        return t_status;

    }

    public void setT_status(String t_status) {

        this.t_status = t_status;

    }


        @Override
        public String toString() {

            return d_name + " " + d_id+ " " + t_status;

        }


}