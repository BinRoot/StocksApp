package com.stocksapp;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class StocksAppActivity extends Activity {

	final int FACEBOOK_MODE = 0;
	
	SharedPreferences settings;

	Facebook facebook = new Facebook("207018342738085");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/*
         * Get existing access_token if any
         */
		settings = getPreferences(MODE_PRIVATE);
        String access_token = settings.getString("access_token", null);
        long expires = settings.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        if(facebook.isSessionValid()) {
        	try {
				JSONObject jObject = new JSONObject(facebook.request("me")); 
				Log.d(getString(R.string.APP), jObject.toString());
				
				Intent i = new Intent(StocksAppActivity.this, StockActivity.class);
				
				i.putExtra("firstName", jObject.getString("first_name"));
				i.putExtra("id", jObject.getString("id"));
				
				startActivity(i);
				
				
			} catch (Exception e) {
				Log.d(getString(R.string.APP), "json err: "+e.getMessage());
			}
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                    
                    try {
						JSONObject jObject = new JSONObject(facebook.request("me")); 
						Log.d(getString(R.string.APP), jObject.toString());
						
						Intent i = new Intent(StocksAppActivity.this, StockActivity.class);
						
						String facebook_id = jObject.getString("id");
						
						i.putExtra("firstName", jObject.getString("first_name"));
						i.putExtra("id", facebook_id);
						
						
						StockDataAPI.getInstance().postID(facebook_id);
						
						
						startActivity(i);
						
						
					} catch (Exception e) {
						Log.d(getString(R.string.APP), "json err: "+e.getMessage());
					}
                }
    
                @Override
                public void onFacebookError(FacebookError error) {}
    
                @Override
                public void onError(DialogError e) {}
    
                @Override
                public void onCancel() {}
            });
        }
        
		
		
		Button b = (Button)findViewById(R.id.button_main_fb);
		b.setOnClickListener(new MyClickListener(0));

		((MyApplication) this.getApplication()).setFacebook(facebook);
		
	}

	public class MyClickListener implements OnClickListener {

		int mode;

		public MyClickListener(int mode) {
			this.mode = mode;
		}

		@Override
		public void onClick(View v) {
			if(mode==FACEBOOK_MODE) {
				Toast.makeText(StocksAppActivity.this, "Logging in", Toast.LENGTH_SHORT).show();
				facebookConnect();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(StocksAppActivity.this, "Authorizing...", Toast.LENGTH_SHORT).show();
		//Log.d(getString(R.string.APP), "data: "+data.toString());

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void facebookConnect() {
		
		
		/*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                    
                    try {
						JSONObject jObject = new JSONObject(facebook.request("me")); 
						Log.d(getString(R.string.APP), jObject.toString());
						
						Intent i = new Intent(StocksAppActivity.this, StockActivity.class);
						
						i.putExtra("firstName", jObject.getString("first_name"));
						i.putExtra("id", jObject.getString("id"));
						
						startActivity(i);
						
						
					} catch (Exception e) {
						Log.d(getString(R.string.APP), "json err: "+e.getMessage());
					}
                }
    
                @Override
                public void onFacebookError(FacebookError error) {}
    
                @Override
                public void onError(DialogError e) {}
    
                @Override
                public void onCancel() {}
            });
        }
        else {
        	//Intent i = new Intent(StocksAppActivity.this, StockActivity.class);
			
        	try {
	        	Facebook fb = ((MyApplication) this.getApplication()).getFacebook();
	        	JSONObject jObject = new JSONObject(fb.request("me")); 
				Log.d(getString(R.string.APP), jObject.toString());
				
				Intent i = new Intent(StocksAppActivity.this, StockActivity.class);
				
				i.putExtra("firstName", jObject.getString("first_name"));
				i.putExtra("id", jObject.getString("id"));
				
				startActivity(i);
        	}
        	catch (Exception e) {}
        	
        }
		
	}
	
	public void storeLoggedIn(String facebook_id) {
		settings = StocksAppActivity.this.getSharedPreferences("account", 0);
        settings.edit().putBoolean("loggedin", true).commit();
        settings.edit().putString("facebook_id", facebook_id).commit();
	}
	
	public String alreadyLoggedIn() {
		SharedPreferences settings = StocksAppActivity.this.getSharedPreferences("account", 0);
        if( settings.getBoolean("loggedin", false) ) {
        	return settings.getString("facebook_id", null);
        }
        return null;
	}
	
	public void storeID(String facebook_id) {
		SharedPreferences settings = StocksAppActivity.this.getSharedPreferences("account", 0);
        settings.edit().putString("facebook_id", facebook_id).commit();
	}
	
	public String getStoredID() {
		SharedPreferences settings = StocksAppActivity.this.getSharedPreferences("account", 0);
        return settings.getString("facebook_id", null);
	}
}