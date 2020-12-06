package com.example.polyglot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.w3c.dom.Text;

public class profileTile extends ConstraintLayout {
    //private String IMAGEID = "IMAGEID";
    //int tile = -100;
    public String mUserID;
    public String mUser;
    public String mLanguage;
    public String mImage;
    public String mBio;
    public String mGender;
    public int mAge;

    private TextView mBioText, mNatLangText, mNameText;
    private ImageView mProfPic;
    private ConstraintLayout mLayout;


    public profileTile(@NonNull Context context) {
        super(context);
        init();
    }

    public profileTile(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public profileTile(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public profileTile(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    public profileTile(@NonNull Context context,String userId, String name, String lang, String picture, String bio, String gender, String age) {
        super(context);
        init(userId, name, lang, picture, bio, gender, age);
    }


    private void init(){
        inflate(getContext(),R.layout.profiletile,this);
        mBioText = (TextView)findViewById(R.id.bio);
        mNatLangText = (TextView)findViewById(R.id.natLang);
        mProfPic = (ImageView)findViewById(R.id.profPic);
    }

    private void init(String id, String userName, String lang, String picture, String bio, String gender, String age){
        inflate(getContext(),R.layout.profiletile,this);
        mUserID = id;

        mLayout = findViewById(R.id.background);

        mNameText = (TextView)findViewById(R.id.name);
        mNameText.setText(userName);
        mUser = userName;

        mNatLangText = (TextView)findViewById(R.id.natLang);
        mNatLangText.setText(lang);
        mLanguage = lang;
        setBackgroundImage();

        mBioText = (TextView)findViewById(R.id.bio);
        mBioText.setText(bio);
        mBio = bio;

        mProfPic = (ImageView)findViewById(R.id.profPic);
        setImage(picture);
        mImage = picture;

        mGender = gender;
        mAge = Integer.parseInt(age);

    }

    public void setBackgroundImage(){
        switch (mLanguage){
            case "English":
                mLayout.setBackground(getResources().getDrawable(R.drawable.english));
                break;
            case "French":
                mLayout.setBackground(getResources().getDrawable(R.drawable.french));
                break;
            case "German":
                mLayout.setBackground(getResources().getDrawable(R.drawable.german));
                break;
            case "Spanish":
                mLayout.setBackground(getResources().getDrawable(R.drawable.spanish));
                break;
            case "Japanese":
                mLayout.setBackground(getResources().getDrawable(R.drawable.japanese));
                break;

        }
    }


    public void setImage(String base64Image){
        imageFileMethods iFM = new imageFileMethods();
        Bitmap bitmap = iFM.decodeBase64(base64Image);
        mProfPic.setImageBitmap(bitmap);
    }



    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                switch (mLanguage){
                    case "English":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.englishclick));
                        break;
                    case "French":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.frenchclick));
                        break;
                    case "German":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.germanclick));
                        break;
                    case "Spanish":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.spanishclick));
                        break;
                    case "Japanese":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.japaneseclick));
                        break;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                switch (mLanguage){
                    case "English":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.english));
                        break;
                    case "French":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.french));
                        break;
                    case "German":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.german));
                        break;
                    case "Spanish":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.spanish));
                        break;
                    case "Japanese":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.japanese));
                        break;

                }
                break;
            case MotionEvent.ACTION_UP:
                switch (mLanguage){
                    case "English":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.english));
                        break;
                    case "French":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.french));
                        break;
                    case "German":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.german));
                        break;
                    case "Spanish":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.spanish));
                        break;
                    case "Japanese":
                        mLayout.setBackground(getResources().getDrawable(R.drawable.japanese));
                        break;

                }
                ((searchActivity) getContext()).startProfileActivity(mUserID, mUser, mGender, mAge, mLanguage, mBio, mImage);
                break;
        }
        return true;
    }
}