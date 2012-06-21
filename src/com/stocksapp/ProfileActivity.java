package com.stocksapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

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

        ImageView iv = ((ImageView) findViewById(R.id.pro_img_pic));
        new GetPic(iv, userId).execute();
    }

    public class GetPic extends AsyncTask<Void, Void, Bitmap> {

        ImageView iv = null;
        long userId;

        public GetPic(ImageView iv, String userId) {
            this.iv = iv;
            this.userId = Integer.parseInt(userId.substring(2));
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL img_value = null;
            try {
                img_value = new URL("http://graph.facebook.com/"+userId+"/picture?type=large");
                Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                return mIcon1;
            }
            catch (Exception e) {
                Log.d(DEBUG, "err: "+e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            iv.setImageBitmap(b);
        }
    }

    public void portfolioClicked(View v) {
        finish();
    }

    public void friendsClicked(View v) {
        Intent i = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(i);
        this.finish();
    }
}