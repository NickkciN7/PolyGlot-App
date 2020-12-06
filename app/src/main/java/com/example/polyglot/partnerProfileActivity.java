package com.example.polyglot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class partnerProfileActivity extends AppCompatActivity {

    private TextView mName, mGender, mAge, mID, mLanguage, mBio;
    private ImageView mProfPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_profile);

        mName = findViewById(R.id.name);
        mGender = findViewById(R.id.gender);
        mAge = findViewById(R.id.age);
        mLanguage = findViewById(R.id.natLang);
        mBio = findViewById(R.id.bio);


        mProfPic = findViewById(R.id.profPic);

        SharedPreferences pref = getSharedPreferences("partnerInfo", Context.MODE_PRIVATE);
        mName.setText(pref.getString("user","noneFound"));
        mGender.setText(pref.getString("gender","noneFound"));
        mAge.setText(Integer.toString(pref.getInt("age",-1)));
        mLanguage.setText(pref.getString("native_language","noneFound"));
        mBio.setText(pref.getString("bio","noneFound"));
        imageFileMethods imMet = new imageFileMethods();
        if(imMet.read(this, "partnerPicture") != "failedToRead") {
            String encodedImage = imMet.read(this, "partnerPicture");
            Bitmap bitmap = imMet.decodeBase64(encodedImage);
            mProfPic.setImageBitmap(bitmap);
        }
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


    public void startMessageActivity(View view){
        Intent intent = new Intent(this, messageActivity.class);
        startActivity(intent);
    }



}