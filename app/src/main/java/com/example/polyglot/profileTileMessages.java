package com.example.polyglot;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class profileTileMessages extends ConstraintLayout {
    private ConstraintLayout mLayout;


    public String mUserID;
    public String mUser;
    public String mLanguage;
    public String mImage;
    public String mBio;
    public String mGender;
    public String mLastMessage;
    public int mAge;

    private TextView mNameText, mLastMessageText;
    private ImageView mProfPic;


    public profileTileMessages(@NonNull Context context) {
        super(context);
        init();
    }

    public profileTileMessages(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public profileTileMessages(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public profileTileMessages(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public profileTileMessages(@NonNull Context context, String userId, String name, String lang, String picture, String bio, String gender, String age, String Message, String from) {
        super(context);
        init(userId, name, lang, picture, bio, gender, age, Message, from);
    }

    private void init(){
        inflate(getContext(),R.layout.profiletilemessages,this);

        //mLayout = findViewById(R.id.background);

        //mLastMessageText = findViewById(R.id.lastmessage);

    }

    private void init(String id, String userName, String lang, String picture, String bio, String gender, String age, String Message, String from){
        inflate(getContext(),R.layout.profiletilemessages,this);
        mUserID = id;

        mLayout = findViewById(R.id.background);

        mNameText = (TextView)findViewById(R.id.name);
        mNameText.setText(userName);
        mUser = userName;

        mLastMessageText = findViewById(R.id.lastmessage);
        mLastMessage = Message;

        mProfPic = (ImageView)findViewById(R.id.profPic);
        mImage = picture;
        setImage(picture);


        mLanguage = lang;
        setBackgroundImage();
        mBio = bio;
        mGender = gender;
        mAge = Integer.parseInt(age);

        if(from.equals("user")){ //if fromUser id obtained from server is equal to currently signed in user's id
            mLastMessageText.setText("YOU: " +Message);
            mLastMessageText.setTextColor(getResources().getColor(R.color.usermessage));
        }
        else{ //if form partner
            mLastMessageText.setText("PARTNER: " + Message);
            mLastMessageText.setTextColor(getResources().getColor(R.color.partnermessage));
        }
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
                ((messageListActivity) getContext()).startMessageActivity(mUserID, mUser, mGender, mAge, mLanguage, mBio, mImage);
                break;
        }
        return true;
    }
}
