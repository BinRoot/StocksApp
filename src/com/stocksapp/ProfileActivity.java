package com.stocksapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

public class ProfileActivity extends Activity {

    private final String DEBUG = "Profile";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Bundle b = getIntent().getExtras();
        String userId = "";
        String userName = "";
        Long userNet = (long)0;
        if(b!=null) {
            userId = b.getString("id").split(";")[0];
            userName = b.getString("id").split(";")[1];
            userNet = b.getLong("net");
        }
        else {
            Log.d(DEBUG, "Error: Profile couldn't find Extras");
            finish();
        }

        ((TextView)findViewById(R.id.pro_text_friendname)).setText(userName);
        ((TextView)findViewById(R.id.pro_text_friendrank)).setText(
                ((MyApplication)this.getApplication()).getRankValForNet(userNet)
        );

    }
}