package com.stocksapp;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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

	Facebook facebook = new Facebook("207018342738085");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button b = (Button)findViewById(R.id.button_main_fb);
		b.setOnClickListener(new MyClickListener(0));

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
		Toast.makeText(StocksAppActivity.this, "yo yo yo", Toast.LENGTH_LONG).show();
		Log.d(getString(R.string.APP), "data: "+data.toString());

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void facebookConnect() {
		facebook.authorize(this, new String[] { "email", "user_relationships" }, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
				Log.d(getString(R.string.APP), "complete: "+values.toString());
				if(facebook.isSessionValid()) {
					
					try {
						JSONObject jObject = new JSONObject(facebook.request("me")); 
						Log.d(getString(R.string.APP), jObject.toString());
						
						Intent i = new Intent(StocksAppActivity.this, StockPage.class);
						
						i.putExtra("firstName", jObject.getString("first_name"));
						i.putExtra("id", jObject.getString("id"));
						
						startActivity(i);
						
						
					} catch (Exception e) {
						Log.d(getString(R.string.APP), "json err: "+e.getMessage());
					}
				}
				else {
					Log.d(getString(R.string.APP), "not session valid");
				}
			}

			@Override
			public void onFacebookError(FacebookError error) {
				Log.d(getString(R.string.APP), "er1: "+error.toString());
			}

			@Override
			public void onError(DialogError e) {
				Log.d(getString(R.string.APP), "er2: "+e.toString());
			}

			@Override
			public void onCancel() {
				Log.d(getString(R.string.APP), "canceled");
			}
		});
	}
}