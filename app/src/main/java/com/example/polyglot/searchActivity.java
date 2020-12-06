package com.example.polyglot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchActivity extends AppCompatActivity {
    //2147483647/369572(amount of characters of base 64 compressed henderson picture) ~ 5810 images can be stored in a string. I will probably never get close to that many images being passed back by the server


    //ProgressDialog progressDialog;
    RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    String urlSearchUsers = constants.urlBeginning + "/searchLanguage.php";

    private Spinner spinner;
    private ViewGroup mLinearLayout;
    TextView tv;
    profileTile pt;

    ArrayList<profileTile> newTiles =  new ArrayList<profileTile>();
    String[] unbroken; //get Users From Volley Post Request Returns array each index contains one of the lines returned on Login activity

    SharedPreferences prefUser;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        prefUser = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = Integer.toString(prefUser.getInt("userID", -1));
        Log.d("userid", userID);
        mRequestQueue = Volley.newRequestQueue(this);

        mLinearLayout = findViewById(R.id.scrollableLinear);
        //pt = new profileTile(this);

        //mLinearLayout.addView(pt);

        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                String item = (String)adapterView.getItemAtPosition(i);
                generateTiles(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home_Button:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //lang is choice with spinner

    public void generateTiles(String lang){
        Log.d("lang", lang);
        if(lang.equals("--Select--")) return;
        newTiles.clear(); //clear arraylist of profile tiles
        mLinearLayout.removeAllViews(); //to get rid of tiles from other language results
        getUsersFromServer(lang); //will modify "unbroken" array declared above and used in fillUserInfo Method, call check response from server and generate tiles or not

    }



    public void getUsersFromServer(final String lang){

        progressDialog = new ProgressDialog(searchActivity.this);
        progressDialog.setTitle("Loading Profiles Of Language");
        progressDialog.setMessage("Attempting...");
        progressDialog.show();
        String url = Uri.parse(urlSearchUsers).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Post Data: " + response, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Log.d("Text on webpage: ", "" + response);
                checkResponse(response); //determine if response from server means tiles should be made
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
                params.put("language", lang);
                params.put("userID", userID);
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
            Toast.makeText(getApplicationContext(), "No Users Were Found", Toast.LENGTH_LONG).show();
        }
        else{
            //usersFound = true;
            unbroken = res.split("uniqueLineDelimeter");
            for(int i = 0; i< unbroken.length; i++){
                Log.d("unbroken loop", unbroken[i]);
            }
            generateTilesContinue();
        }
    }

    private void generateTilesContinue(){
        List<String[]> UsersInfo = new ArrayList<String[]>();
        //Log.d("in users found if", "yaaay");
        for(int i = 0; i < unbroken.length; i++){
            UsersInfo.add(unbroken[i].split("uniqueDelimeter"));   //breaks each line by ` and stores in string array, inside array list of string arrays //theinfo is userinfo list passed
        }


        for(int i = 0; i < UsersInfo.size(); i++){
            newTiles.add(new profileTile(this, UsersInfo.get(i)[0], UsersInfo.get(i)[1], UsersInfo.get(i)[4], UsersInfo.get(i)[6], UsersInfo.get(i)[5], UsersInfo.get(i)[2], UsersInfo.get(i)[3]));
            //newTiles.add(new profileTile(this, "a", "b", "c", "d" ,"e")); //this works
            //newTiles.add(new profileTile(this)); //this works
            mLinearLayout.addView(newTiles.get(i));
        }

    }






    //called within profile tile object. profile tile contains all the user information received from the server. When clicked this info is placed
    //into a partner info preference file. The partner image is stored as base 64 into a file called partnerPicture. These files will be accessed
    // when clicking their picture or when messaging
    public void startProfileActivity(String userID, String user, String gender,  int age, String natlang, String bio, String base64Image){
        SharedPreferences namedSharedPref = getSharedPreferences("partnerInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = namedSharedPref.edit();
        editor.putInt("userID", Integer.parseInt(userID)); //unique user id given by server database
        editor.putString("user", user);
        editor.putString("gender", gender);
        editor.putInt("age", age);
        editor.putString("native_language", natlang);
        editor.putString("bio", bio);
        editor.apply();

        //write for image
        imageFileMethods IFM = new imageFileMethods();
        IFM.write(this, base64Image, "partnerPicture");
        Intent intent = new Intent(this, partnerProfileActivity.class);
        startActivity(intent);
    }

}