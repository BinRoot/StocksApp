package com.stocksapp;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginActivity extends Activity {

	final int FACEBOOK_MODE = 0;
	String DEBUG = "LoginActivity";
	SharedPreferences settings;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Access facebook global object through MyApplication
		((MyApplication) this.getApplication()).facebook = new Facebook("207018342738085");

		/*
		 * Get existing access_token if any
		 * (Code from Facebook documentation)
		 */
		settings = getPreferences(MODE_PRIVATE);
		String access_token = settings.getString("access_token", null);
		long expires = settings.getLong("access_expires", 0);
		if(access_token != null) {
			((MyApplication) this.getApplication()).facebook.setAccessToken(access_token);
		}
		if(expires != 0) {
			((MyApplication) this.getApplication()).facebook.setAccessExpires(expires);
		}


		if(((MyApplication) this.getApplication()).facebook.isSessionValid()) {
			(new LogInTask(LoginActivity.this)).execute();
		}


		Button b = (Button)findViewById(R.id.button_main_fb);
		b.setOnClickListener(new MyClickListener(FACEBOOK_MODE));

	}

	public void login() {
		try {
			JSONObject jObject = new JSONObject(((MyApplication) this.getApplication()).facebook.request("me")); 
			String firstName = jObject.getString("first_name");
			String id = jObject.getString("id");
			Intent i = new Intent(LoginActivity.this, PortfolioActivity.class);
			i.putExtra("firstName", firstName);
			i.putExtra("id", id);
			startActivity(i);
		} catch (Exception e) {
			Log.d(DEBUG, "json err: "+e.getMessage());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(LoginActivity.this, "Authorizing...", Toast.LENGTH_SHORT).show();
		((MyApplication) this.getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
	}


	public void facebookConnect() {

		((MyApplication) this.getApplication()).facebook.authorize(this, new String[] {}, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
				Log.d(DEBUG, "onComplete!");
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("access_token", ((MyApplication) LoginActivity.this.getApplication()).facebook.getAccessToken());
				editor.putLong("access_expires", ((MyApplication) LoginActivity.this.getApplication()).facebook.getAccessExpires());
				editor.commit();

				(new LogInTask(LoginActivity.this)).execute();
			}

			@Override
			public void onFacebookError(FacebookError error) {
				Log.d(DEBUG, "err onFacebookError: "+error.toString());
			}

			@Override
			public void onError(DialogError e) {
				Log.d(DEBUG, "err onError: "+e.toString());
			}

			@Override
			public void onCancel() {
				Log.d(DEBUG, "err onCancel");
			}
		});

	}

	public class MyClickListener implements OnClickListener {

		int mode;

		public MyClickListener(int mode) {
			this.mode = mode;
		}

		@Override
		public void onClick(View v) {
			if(mode==FACEBOOK_MODE) {
				Toast.makeText(LoginActivity.this, "Logging in", Toast.LENGTH_SHORT).show();
				facebookConnect();
			}
		}
	}

	private class LogInTask extends AsyncTask<Void, Void, Void> {

		private Activity a;
		
		public LogInTask(Activity a) {
			this.a = a;
		}
		
		
		@Override
		protected void onPreExecute() {
			a.findViewById(R.id.button_main_fb).setVisibility(View.GONE);
			a.findViewById(R.id.progress_fb).setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			login();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					a.findViewById(R.id.button_main_fb).setVisibility(View.VISIBLE);
					a.findViewById(R.id.progress_fb).setVisibility(View.GONE);
				}
			}, 1000);
			
		}

	}

}