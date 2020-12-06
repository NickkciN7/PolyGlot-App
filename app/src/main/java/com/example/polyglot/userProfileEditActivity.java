package com.example.polyglot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class userProfileEditActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE_GET = 1;
    static final int REQUEST_PERMISSION_GET = 2;
    imageFileMethods iFM = new imageFileMethods(); //to access methods for image
    RequestQueue mRequestQueue; //for volley
    Bitmap bitmap; //store bitmap when choosing image
    String urlUpdateUser = constants.urlBeginning + "/updateUser.php";
    ProgressDialog progressDialog;
    private EditText mUserEdit, mBioEdit; String user, bio; //set to edit text when updating
    private Button mPicButton, mUpdateUserButton;
    private ImageView mProfPic; String imageData; //image data is bitmap converted to base 64 string
    private Boolean[] edited = {false, false, false}; //to keep track of which of the three views were edited to update database (0->picture, 1->username, 2->bio)
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        mRequestQueue = Volley.newRequestQueue(this);

        pref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        mProfPic = findViewById(R.id.profPic);

        mUserEdit = findViewById(R.id.userNameEdit);
        mUserEdit.setText(pref.getString("user","noneFound"));
        mBioEdit = findViewById(R.id.bioEdit);
        mBioEdit.setText(pref.getString("bio","noneFound"));


        if(iFM.read(this,"profilePictureBase64") != "failedToRead") {
            String encodedImage = iFM.read(this, "profilePictureBase64");
            Bitmap bitmap = iFM.decodeBase64(encodedImage);
            mProfPic.setImageBitmap(bitmap);
        }
        else{
            Log.d("read", "failed");
        }
    }


    public void picButtonClick(View view){
        //getting permission for using photos
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_GET);
            } else {
                startGetImageIntent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //handling the request for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_PERMISSION_GET:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startGetImageIntent();
                }
                else{
                    Toast.makeText(this, "Denied permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //actually starts the intent to get the images
    public void startGetImageIntent(){
        //Log.d("startGetImageIntent:", "inside");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_GET) {
            Uri filePath = data.getData();

            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d("chose image", "size: " + (bitmap.getRowBytes() * bitmap.getHeight()) );
                mProfPic.setImageBitmap(bitmap);
                edited[0] = true;
            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }



    public void updateUserClick(View view) {

        if(edited[0]){ //if picture was changed
            imageData = iFM.imageToString(bitmap); //convert to base 64 to send to server
        }
        user = mUserEdit.getText().toString().trim();
        if(!pref.getString("user","noneFound").equals(user)){ //if edit text different than user text in preferences
            edited[1] = true;
        }
        bio = mBioEdit.getText().toString().trim();
        if(!pref.getString("bio","noneFound").equals(bio)){
            edited[2] = true;
        }

        progressDialog = new ProgressDialog(userProfileEditActivity.this);
        progressDialog.setTitle("Updating Profile On Server");
        progressDialog.setMessage("Attempting...");
        progressDialog.show();
        //volley post part
        String url = Uri.parse(urlUpdateUser).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Post Data: " + response, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Log.d("Text on webpage: ", "" + response);
                //serverResponse = response.split(",");
                //testRes();
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
                params.put("userID", Integer.toString(pref.getInt("userID",-1))); //give user ID to server
                if(edited[0]){
                    params.put("imageData", imageData);
                }
                if(edited[1]){
                    params.put("user", user);
                }
                if(edited[2]){
                    params.put("bio", bio);
                }
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
         if(res.contains("failure")){  //the response contains "parameter: success or failure, (next parameter)..." so if failure is in string returned by server at least one of the updates failed
             Toast.makeText(getApplicationContext(), "Update failed. Parameters success/failed status: " + res, Toast.LENGTH_LONG).show();
         }
         else{
             Toast.makeText(getApplicationContext(), "Update succeeded!", Toast.LENGTH_LONG).show();
             updatePref();
             finish();
         }
    }
    private void updatePref(){
        SharedPreferences.Editor editor = pref.edit();
        Log.d("updatePref", "beginning");
        if(edited[0]){  //image
            iFM.write(this, imageData, "profilePictureBase64"); //stores base 64 in profilePictureBase64 file(see imageFileMethods class)
            Log.d("updatePref", "image");
        }
        if(edited[1]){  //username
            editor.putString("user", user); //updating user preference
            Log.d("updatePref", "user");
        }
        if(edited[2]){  //bio
            editor.putString("bio", bio);
            Log.d("updatePref", "bio");
        }
        editor.apply();
    }


}