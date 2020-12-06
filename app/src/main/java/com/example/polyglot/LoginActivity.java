package com.example.polyglot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    imageFileMethods iFM = new imageFileMethods();
    private EditText mUserIDEdit, mUsernameEdit, mPasswordEdit;
    private String  userID, username, password;
    private String[] userInfo; // will contain user info from the server
    RequestQueue mRequestQueue; //for volley
    String urlLogin = constants.urlBeginning + "/login.php";
    ProgressDialog progressDialog;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserIDEdit = findViewById(R.id.userIDEdit);
        mUsernameEdit = findViewById(R.id.userNameEdit);
        mPasswordEdit = findViewById(R.id.passwordEdit);

        mRequestQueue = Volley.newRequestQueue(this);
        pref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public void loginClick(View view) {
        //storing info from edit texts in strings
        userID = mUserIDEdit.getText().toString().trim();
        username = mUsernameEdit.getText().toString().trim();
        password = mPasswordEdit.getText().toString().trim();
        if(userID.length() == 0 | username.length() == 0| password.length() == 0){
            Toast.makeText(getApplicationContext(), "Please Enter Information For All Fields", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Attempting...");
        progressDialog.show();
        String url = Uri.parse(urlLogin).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Post Data: " + response, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Log.d("Text on webpage: ", "" + response);
                checkResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Volley error" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", userID); //give user ID to server
                params.put("user", username);
                params.put("pw", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void checkResponse(String res){
        if(res.equals("no result")){
            Toast.makeText(getApplicationContext(), "The UserID does not exist", Toast.LENGTH_LONG).show();
        }
        else{
            if(res.equals("User Name or Password is incorrect")){
                Toast.makeText(getApplicationContext(), "User Name or Password is incorrect", Toast.LENGTH_LONG).show();
            }
            else{
                userInfo = res.split("uniqueDelimeter"); //splitting based on delimeter I output with php from server
                /*for(int i = 0; i < userInfo.length; i++){ //testing aray
                    Log.d("info loop", userInfo[i]);
                }*/

                iFM.write(this, userInfo[7], "profilePictureBase64"); //stores base 64 image


                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("userID", Integer.parseInt(userInfo[0])); //unique user id given by server database
                editor.putString("user", userInfo[1]);
                editor.putString("password", userInfo[2]);
                editor.putString("gender", userInfo[3]);
                editor.putInt("age", Integer.parseInt(userInfo[4]));
                editor.putString("native_language", userInfo[5]);
                editor.putString("bio", userInfo[6]);
                editor.apply();

                allowLogin();
            }
        }
    }

    private void allowLogin(){
        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }
}