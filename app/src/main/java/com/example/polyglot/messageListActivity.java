package com.example.polyglot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

//maybe have update on resume so if user goes back from message Activity the new message order is shown
public class messageListActivity extends AppCompatActivity {

    RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    String urlGetConversations = constants.urlBeginning + "/getConversations.php";
    private ViewGroup mLinearLayout;
    ArrayList<profileTileMessages> newTiles =  new ArrayList<profileTileMessages>();
    private TextView noConversations;
    SharedPreferences prefUser;
    SharedPreferences prefPartner;
    String[] unbroken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipeLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
                pullToRefresh.setRefreshing(false);
            }
        });
        prefUser = getSharedPreferences("userInfo", Context.MODE_PRIVATE);       ///These 2 need to be in onCreate. error if declared above.
        prefPartner = getSharedPreferences("partnerInfo", Context.MODE_PRIVATE);

        mRequestQueue = Volley.newRequestQueue(this);

        mLinearLayout = findViewById(R.id.scrollableLinear);

        noConversations = findViewById(R.id.noMessages);
        getConversationsFromServer(false);
    }
    //for app bar with home icon
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
    public void startMessageActivity(String userID, String user, String gender,  int age, String natlang, String bio, String base64Image){
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
        Intent intent = new Intent(this, messageActivity.class);
        startActivity(intent);
    }



    public void getConversationsFromServer(Boolean bool){

        progressDialog = new ProgressDialog(messageListActivity.this);
        progressDialog.setTitle("Loading Conversations");
        progressDialog.setMessage("Attempting...");
        progressDialog.show();



        String url = Uri.parse(urlGetConversations).toString();

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
                params.put("userID", Integer.toString(prefUser.getInt("userID", -1)));
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
            noConversations.setVisibility(View.VISIBLE);
        }
        else{
            unbroken = res.split("uniqueLineDelimeter");
            /*for(int i = 0; i< unbroken.length; i++){
                Log.d("unbroken loop", unbroken[i]);
            }*/
            //Log.d("unbroken", Integer.toString(unbroken.length));
            generateTiles();
        }
    }

    private void generateTiles(){
        List<String[]> UsersInfo = new ArrayList<String[]>();
        //Log.d("in users found if", "yaaay");
        for(int i = 0; i < unbroken.length; i++){
            UsersInfo.add(unbroken[i].split("uniqueDelimeter"));   //breaks each line by ` and stores in string array, inside array list of string arrays //theinfo is userinfo list passed
            //Log.d("UsersInfo size", Integer.toString(UsersInfo.get(i).length));
        }

        Log.d("message", UsersInfo.get(0)[0]);
        Log.d("username", UsersInfo.get(0)[2]);
        Log.d("from", UsersInfo.get(0)[8]);
        for(int i = 0; i < UsersInfo.size(); i++){
            //newTiles.add(new profileTileMessages(this,id,user,lang,pic,bio,gender,age,message));
            newTiles.add(new profileTileMessages(this, UsersInfo.get(i)[1], UsersInfo.get(i)[2], UsersInfo.get(i)[5], UsersInfo.get(i)[7],UsersInfo.get(i)[6],UsersInfo.get(i)[3],UsersInfo.get(i)[4],UsersInfo.get(i)[0],UsersInfo.get(i)[8]));
            //newTiles.add(new profileTileMessages(this,"a","a","a","a","a","a","a","a","a" ) );
            mLinearLayout.addView(newTiles.get(i));
        }
    }
}