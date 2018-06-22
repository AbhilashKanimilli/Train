package com.example.grobomac.train;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import android.support.v4.app.Fragment;


/**
 * Created by abhi on 3/7/2018.
 */

public class Login extends Fragment implements View.OnClickListener {
    Boolean success;
    Button myButton;
    EditText editTextdriverid;
    TextView textView;
    TextView textView1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.login, container, false);

        editTextdriverid = (EditText)myView.findViewById(R.id.driverid);
        myButton = (Button) myView.findViewById(R.id.buttonLogin);
        textView =(TextView) myView.findViewById(R.id.textViewRegister);
        textView1 =(TextView) myView.findViewById(R.id.textViewstatus);
        textView.setOnClickListener(this);
        textView1.setOnClickListener(this);
        myButton.setOnClickListener(this);
        return myView;
    }
    @Override
    public void onClick(View v) {
        // implements your things
        switch (v.getId()) {
            case R.id.buttonLogin:
               userLogin();
                break;
            case R.id.textViewRegister:
                if (!isInternetOn()){
                    cDialog();
                }else {
                    startActivity(new Intent(getActivity(), Register.class));
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                break;
            case R.id.textViewstatus:
                if (!isInternetOn()){
                    cDialog();
                }else {
                    startActivity(new Intent(getActivity(), StatusActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            default:
                break;
        }
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public final boolean isInternetOn() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
           // Toast.makeText(getActivity(),"NO INTERNET PLEASE CHECK", Toast.LENGTH_SHORT).show();
            success=  false;
        }
        return success;

    }
    private void cDialog() {

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        alertDlg.setMessage("No INTERNET CONNECTION" + " Please Connect to"+" Mobile data or WIFI to Proceed")

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDlg.create().show();
    }
    private void userLogin() {
        //first getting the values
        final String Driver_id = editTextdriverid.getText().toString();

        if (!isInternetOn()){
            cDialog();
        }

        //validating inputs
        if (TextUtils.isEmpty(Driver_id)) {
            editTextdriverid.setError("Please enter your Driver ID");
            editTextdriverid.requestFocus();
            return;
        }


        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getString("Driver_id"),
                                userJson.getString("Driver_name"),
                                userJson.getString("Truck_id"),
                                userJson.getString("Trainingstatus")

                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getActivity().getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        //finish();
                        editTextdriverid.setText("");
                        Profile newFragment = new Profile();
                       // Bundle args = new Bundle();
                       // args.putString("Key", Slecteditem);
                       // newFragment.setArguments(args);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);


// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.addToBackStack(null);

// Commit the transaction
                        transaction.commit();
                        //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Invalid Driver ID", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Driver_id",Driver_id);


                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }



}
