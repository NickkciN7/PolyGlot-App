package com.example.polyglot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Map;

public class messageActivity extends AppCompatActivity {
    private TextView mName;
    private ImageView mProfPic;
    private EditText mMessageToSend;
    ArrayList<TextView> messageViews =  new ArrayList<TextView>(); //pre existing
    //ArrayList<TextView> messageViews =  new ArrayList<TextView>();
    private String[][] From_Message_Array;

    private ViewGroup mMessageLayout;

    RequestQueue mRequestQueue;
    ProgressDialog progressDialog;

    String urlSendMessage = constants.urlBeginning + "/addNewMessage.php";
    String urlGetMessages = constants.urlBeginning + "/getMessages.php";
    String urlCheckNewMessages = constants.urlBeginning + "/checkNewMessages.php";

    SharedPreferences prefUser;
    SharedPreferences prefPartner;
    private int countdownTime = 2000; //milliseconds before next timer call to check for new messages
    static boolean activeBool = false; //the timer and volley seem to keep running even when I exit the activity so I am declaring this and making it false onPause and using it with the timer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        prefUser = getSharedPreferences("userInfo", Context.MODE_PRIVATE);       ///These 2 need to be in onCreate. error if declared above.
        prefPartner = getSharedPreferences("partnerInfo", Context.MODE_PRIVATE);

        mRequestQueue = Volley.newRequestQueue(this);


        mMessageToSend = findViewById(R.id.editTMessage);
        mName = findViewById(R.id.name);
        mProfPic = findViewById(R.id.profPic);

        mMessageLayout = findViewById(R.id.scrollableLinear);

        mName.setText(prefPartner.getString("user","noneFound"));

        imageFileMethods IFM = new imageFileMethods();
        if(IFM.read(this, "partnerPicture") != "failedToRead") {
            String encodedImage = IFM.read(this, "partnerPicture");
            Bitmap bitmap = IFM.decodeBase64(encodedImage);
            mProfPic.setImageBitmap(bitmap);
        }

        volleyGetExistingMessages(); //starts by checking server if messages already exist between the 2 users and displays them
    }

    @Override
    protected void onResume() {
        super.onResume();
        activeBool = true;
        timerForCheckMessages(countdownTime); //need to call again because once false the timer stops and isnt called again
    }

    @Override
    protected void onPause() {  ///causes message not to load or send??
        super.onPause();
        activeBool = false;
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

    public void startProfileActivity(View view){ //upon clicking user picture
        Intent intent = new Intent(this, partnerProfileActivity.class);
        startActivity(intent);
    }


    private int lastMessageID = -1; //for keeping track of last message id of conversation in database(will use when seeing if partner sent message while in message activity see last section of this page)

    private int index = 0; //used for the array list of message views(particularly in the send message section) to modify text view generated

                //***************************for displaying preexisting messages*******************************\\

    public  void displayMessages(){
        for(int i = 0; i < From_Message_Array.length; i++){ //get 2 dimensional string array, 0 index F,T, 1 index the actual message
            messageViews.add(new TextView(this));
            textViewSettings(i);
            mMessageLayout.addView(messageViews.get(i));
            index++;
        }

        //automatically scrolls to bottom of texts created
        messageViews.get(index-1).setFocusableInTouchMode(true);
        messageViews.get(index-1).setFocusable(true);
        mMessageLayout.requestFocus();
        messageViews.get(index-1).setFocusableInTouchMode(false);
        messageViews.get(index-1).setFocusable(false);

        timerForCheckMessages(countdownTime); //starts timer that checks periodically for new messages from partner on server **
    }

    public void textViewSettings(int i){
        messageViews.get(i).setText(From_Message_Array[i][1]);
        messageViews.get(i).setTextSize(20);
        if(Integer.parseInt(From_Message_Array[i][0]) == prefUser.getInt("userID", -1)){ //if fromUser id obtained from server is equal to currently signed in user's id
            messageViews.get(i).setGravity(Gravity.RIGHT);
            messageViews.get(i).setTextColor(getResources().getColor(R.color.usermessage));
        }
        else{ //if form partner
            messageViews.get(i).setTextColor(getResources().getColor(R.color.partnermessage));
        }
    }

    private void volleyGetExistingMessages(){
        String url = Uri.parse(urlGetMessages).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Post Data: " + response, Toast.LENGTH_LONG).show();
                //progressDialog.dismiss();
                Log.d("Text on webpage: ", "" + response);
                checkResponseInitial(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Volley error" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", Integer.toString(prefUser.getInt("userID",-1))); //give user ID to server
                params.put("partnerID", Integer.toString(prefPartner.getInt("userID",-1)));
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

    private void checkResponseInitial(String res){
        if(res.equals("conversation not found")){
            timerForCheckMessages(countdownTime); //starts timer to check for messages from partner
        }
        else{
            //1)response from server comes in form "   'fromUserID' "userFromDel" 'message' "messageDel"..... 'lastMessageID'     "
            //2)first split by "messageDel"(a delimeter i am using to split) to get all messages separated and lastMessageID of entire conversation is last index after split
            //3)loop through the split array except the last index(since as explained above the last index does not contain a message)
            //4)as the array is looped through make another string array that will have 2 indexes. it is made by splitting each index of array above by
            //the delimeter I created "userFromDel". now this 2 index array contains [0] -> from user id [1] -> the actual message
            //5) store this from ID and message inside multidimensional array From_Message_Array. this will be referenced to create text view messages in the layout


            String[] firstSplit = res.split("messageDel");
            lastMessageID = Integer.parseInt(firstSplit[firstSplit.length -1 ]); //sets lastMessageID to what the last message id of the conversation was
            Log.d("lastMessageID", Integer.toString(lastMessageID));
            From_Message_Array = new String[firstSplit.length-1][2]; //declared initially at top. size declared here
            for(int i = 0; i < firstSplit.length -1; i++){
                String[] fromAndMessage = firstSplit[i].split("userFromDel");
                From_Message_Array[i][0] = fromAndMessage[0];
                From_Message_Array[i][1] = fromAndMessage[1];
            }
            displayMessages(); //declared above the volley method above. will reference this From_Message_Array to make text views on layout
        }
    }








                //***************************for displaying messages sent by user*******************************\\
    public void sendMessage(View view){
        //final float scale = this.getResources().getDisplayMetrics().density;
        //int pixels = (int) (250 * scale + 0.5f);
        //Log.d("pixles", Integer.toString(pixels));

        messageViews.add(new TextView(this));
        String message = mMessageToSend.getText().toString().trim();



        //adds new line to end of each message
        ArrayList<Character> chars = new ArrayList<Character>();
        Log.d("not loop", message);
        for (char c : message.toCharArray()) {
            chars.add(c);
        }

        StringBuilder builder = new StringBuilder(chars.size());
        for(Character ch: chars)
        {
            builder.append(ch);
        }
        builder.append('\n');
        String messageFinal = builder.toString();

        messageViews.get(index).setText(messageFinal);
        //mMessageToSend.getText().clear();
        mMessageToSend.setText("", TextView.BufferType.EDITABLE);

        messageViews.get(index).setTextSize(20);
        messageViews.get(index).setGravity(Gravity.RIGHT);
        messageViews.get(index).setTextColor(getResources().getColor(R.color.usermessage));

        //for focusing to make scroll layout go to bottom
        messageViews.get(index).setFocusableInTouchMode(true);
        messageViews.get(index).setFocusable(true);

        mMessageLayout.addView(messageViews.get(index));
        mMessageLayout.requestFocus();

        messageViews.get(index).setFocusableInTouchMode(false);
        messageViews.get(index).setFocusable(false);

        //mMessageToSend.setFocusable(true);
        //mMessageToSend.requestFocus();

        index++;
        volleySendMessage(messageFinal);

    }

    //user, partner, message
    private void volleySendMessage(final String message){
        String url = Uri.parse(urlSendMessage).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Post Data: " + response, Toast.LENGTH_LONG).show();
                //progressDialog.dismiss();
                Log.d("message sent", message);
                Log.d("Text on webpage: ", "" + response);
                lastMessageID = Integer.parseInt(response);
                Log.d("lastMessageID", Integer.toString(lastMessageID));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Volley error" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", Integer.toString(prefUser.getInt("userID",-1))); //give user ID to server
                params.put("partnerID", Integer.toString(prefPartner.getInt("userID",-1)));
                params.put("message", message);
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


    //***************************for displaying messages sent by partner while message activity is open*******************************\\
    //maybe do that timer thing we used in the dice roll and have a volley request in it to check if new message. maybe return last message id in send message above and store that
    //here check to see if conversations table has new lastMessageID, if it does add new messages from partner last message id stored here +1 ++++...


    private void volleyCheckNewMessages(){
        String url = Uri.parse(urlCheckNewMessages).toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Text on webpageNEW: ", "" + response);
                timerForCheckMessages(countdownTime); //restarts timer
                checkResponseNewMessages(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Volley error" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", Integer.toString(prefUser.getInt("userID",-1))); //give user ID to server
                params.put("partnerID", Integer.toString(prefPartner.getInt("userID",-1)));
                params.put("lastMessageID", Integer.toString(lastMessageID));
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

    private void timerForCheckMessages(final long timetoupdate){

        if(activeBool == true) { //did this because timer and volley kept running even after exiting message activity

            new CountDownTimer(timetoupdate, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    Log.d("in countdowntimer", "onFinish: ");
                    volleyCheckNewMessages();
                }
            }.start();
        }
    }

    private void checkResponseNewMessages(String res){
        Log.d("in check response", res);
        if(!res.equals("conversation not found") & !res.equals("no new messages")) {
            String[] splitRes = res.split("messageDel");
            lastMessageID = Integer.parseInt(splitRes[splitRes.length-1]); //sets lastMessageID to what the last message id of the conversation was
            Log.d("lastMessageID", Integer.toString(lastMessageID));
            for (int i = 0; i < splitRes.length - 1; i++) {
                messageViews.add(new TextView(this));
                messageViews.get(index).setText(splitRes[i]);
                messageViews.get(index).setTextSize(20);
                messageViews.get(index).setGravity(Gravity.LEFT);
                messageViews.get(index).setTextColor(getResources().getColor(R.color.partnermessage));
                mMessageLayout.addView(messageViews.get(index));
                index++;
            }
            messageViews.get(index-1).setFocusableInTouchMode(true); //scroll down when partner sends new message
            messageViews.get(index-1).setFocusable(true);
            mMessageLayout.requestFocus();
            messageViews.get(index-1).setFocusableInTouchMode(false);
            messageViews.get(index-1).setFocusable(false);
        }
    }
}

