package com.example.grobomac.train;

/**
 * Created by abhi on 5/24/2018.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText editTextdriverid, editTextname, editTextvno;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

      /*  //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        */

        editTextdriverid = (EditText) findViewById(R.id.editTextUsername);
        editTextname = (EditText) findViewById(R.id.editTextEmail);
        editTextvno = (EditText) findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                registerUser();
            }
        });



    }

    private void registerUser() {
        final String username = editTextdriverid.getText().toString().trim();
        final String email = editTextname.getText().toString().trim();
        final String password = editTextvno.getText().toString().trim();


        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            editTextdriverid.setError("Please enter Driver ID");
            editTextdriverid.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextname.setError("Please enter Driver Name");
            editTextname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextvno.setError("Enter Vehicle-No");
            editTextvno.requestFocus();
            return;
        }

        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            String train ="0";
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Driver_id", username);
                params.put("Driver_name", email);
                params.put("Truck_id", password);
                params.put("Trainingstatus", train);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
/*
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
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        */
                         finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Register.this,LoginActivity.class);
        startActivity(setIntent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

    }

}