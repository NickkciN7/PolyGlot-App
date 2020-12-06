package com.example.polyglot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class userProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView mName, mGender, mAge, mID, mLanguage, mBio;
    private ImageView mProfPic;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mName = findViewById(R.id.name);
        mGender = findViewById(R.id.gender);
        mAge = findViewById(R.id.age);
        mID = findViewById(R.id.userID);
        mLanguage = findViewById(R.id.natLang);
        mBio = findViewById(R.id.bio);


        mProfPic = findViewById(R.id.profPic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mName.setText(pref.getString("user","noneFound"));
        mGender.setText(pref.getString("gender","noneFound"));
        mAge.setText(Integer.toString(pref.getInt("age",-1)));
        mID.setText("ID: " + Integer.toString(pref.getInt("userID",-1)));
        mLanguage.setText(pref.getString("native_language","noneFound"));
        mBio.setText(pref.getString("bio","noneFound"));
        imageFileMethods imMet = new imageFileMethods();
        if(imMet.read(this, "profilePictureBase64") != "failedToRead") {
            String encodedImage = imMet.read(this, "profilePictureBase64");
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

    public void startEditActivity(View view) {
        Intent intent = new Intent(this, userProfileEditActivity.class);
        startActivity(intent);
    }

    public void signOutClick(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to sign out?");
        dialog.setTitle("Sign Out");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = pref.edit();
                editor.clear(); //main activity will start login_sign_up activity until a new user is created or log in when no userID variable in preferences(for example if user exits app before creating a new user or signing in then main activity starts)
                editor.commit();
                Intent intent = new Intent(userProfileActivity.this, LogIn_SignUp_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //get rid of every activity except the one that will start
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }
}