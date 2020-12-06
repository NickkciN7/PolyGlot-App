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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private String[] serverResponse;
    RequestQueue mRequestQueue;
    String urlCreateUser = constants.urlBeginning + "/signup-submit.php";
    Bitmap bitmap;
    String imageData;
    private EditText mUserEdit, mPwEdit, mGenderEdit, mAgeEdit, mLanguageEdit, mBioEdit;
    private Button mPicButton, mCreateUserButton;
    private ImageView mProfPic;

    private String user;
    private String password;
    private String gender;
    private String age;
    private String natlang;
    private String bio;
    imageFileMethods m = new imageFileMethods();
    private Spinner spinGender, spinLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mRequestQueue = Volley.newRequestQueue(this); //??????this

        mUserEdit = findViewById(R.id.userNameEdit);
        mPwEdit = findViewById(R.id.pwEdit);
        //mGenderEdit = findViewById(R.id.genderEdit);
        mAgeEdit = findViewById(R.id.ageEdit);
        //mLanguageEdit = findViewById(R.id.languageEdit);
        mBioEdit = findViewById(R.id.bioEdit);

        mPicButton = findViewById(R.id.picButton);
        mCreateUserButton = findViewById(R.id.createUserButton);

        mProfPic = findViewById(R.id.profPic);

        spinLang = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
        spinLang.setAdapter(adapter);
        spinLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinLang.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                String item = (String)adapterView.getItemAtPosition(i);
                natlang = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinGender = findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_dropdown_item);
        spinGender.setAdapter(adapter2);
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinGender.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                String item = (String)adapterView.getItemAtPosition(i);
                gender = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    static final int REQUEST_IMAGE_GET = 1;
    static final int REQUEST_PERMISSION_GET = 2;

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
                mProfPic.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }




    public void createUserClick(View view) {

        user = mUserEdit.getText().toString().trim();
        password = mPwEdit.getText().toString().trim();

        age = mAgeEdit.getText().toString().trim();

        bio = mBioEdit.getText().toString().trim();
        /*Log.d("var", ""+ user);
        Log.d("var", ""+ password);
        Log.d("var", ""+ gender);
        Log.d("var", ""+ age);
        Log.d("var", ""+ natlang);
        Log.d("var", ""+ bio);*/

        //wont allow profile creation unless all information is filled
        try{
            imageData = m.imageToString(bitmap);
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Image was not chosen", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            int a = Integer.parseInt(age);
        }
        catch (NumberFormatException ex){
            Toast.makeText(getApplicationContext(), "Age must be filled with an integer", Toast.LENGTH_LONG).show();
            return;
        }
        if(user.equals("") | password.equals("") | gender.equals("--Select--") | natlang.equals("--Select--")|bio.equals("") ){
            Toast.makeText(getApplicationContext(), "You must fill in all other information", Toast.LENGTH_LONG).show();
            return;
        }



        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Profile");
        progressDialog.setMessage("Attempting...");
        progressDialog.show();
        String url = Uri.parse(urlCreateUser).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Post Data: " + response, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Log.d("Text on webpage: ", "" + response);

                serverResponse = response.split(",");

                checkResponse();
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
                params.put("imageData", imageData);
                params.put("user", user);
                params.put("pw", password);
                params.put("gender", gender);
                params.put("age", age);
                params.put("natlang", natlang);
                params.put("bio", bio);
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

    /*public void testRes(){
        if(serverResponse.length > 0) {
            for (int i = 0; i < serverResponse.length; i++) {
                Log.d("forloop", serverResponse[i]);
            }
        }
    }*/

    private void checkResponse(){
        //first index is database insert status, second is image upload status, third is unique userID from sql database(default is -1)
        if(serverResponse.length == 0) return;
        //if (serverResponse[0].equals("success") & serverResponse[1].equals("success") & Integer.parseInt(serverResponse[2]) > 0){
        if (serverResponse[0].equals("success")){
            Toast.makeText(getApplicationContext(), "Profile creation successful!", Toast.LENGTH_LONG).show();
            Log.d("Checkresponse", "inside if");
            preferencesMethod();
            storeImage();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Profile creation failed", Toast.LENGTH_LONG).show();
        }
    }

    private void preferencesMethod(){
        //save user info in preferences file
        SharedPreferences namedSharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = namedSharedPref.edit();
        editor.putInt("userID", Integer.parseInt(serverResponse[2])); //unique user id given by server database
        editor.putString("user", user);
        editor.putString("password", password);
        editor.putString("gender", gender);
        editor.putInt("age", Integer.parseInt(age));
        editor.putString("native_language", natlang);
        editor.putString("bio", bio);
        editor.apply();
        Log.d("preferencesMethod", "end");
    }

    private void storeImage(){

        m.write(this, imageData, "profilePictureBase64");
    }

    //from lecture 6 zybooks
    /*private void writeToInternalFile() throws IOException {
        FileOutputStream outputStream = openFileOutput("profilePictureBase64", Context.MODE_PRIVATE);
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(imageData); //store base 64 image
        writer.close();
    }*/

}
