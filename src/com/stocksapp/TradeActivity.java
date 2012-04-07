package com.stocksapp;

import org.json.JSONException;
import org.json.JSONObject;

import API.DataAPI;
import Model.Stock;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TradeActivity extends Activity {
	
	final String DEBUG = "TradeActivity";
	
	int mode = 0;
	final int MODE_BUY = 0;
	final int MODE_SELL = 1;
	Stock stock;
	
	EditText et;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade);
		getMode();
		
		et = (EditText) findViewById(R.id.edit_trade_shares);
		TextWatcher stw = new ShareTextWatcher();
		et.addTextChangedListener(stw);
	}
	
	public class ShareTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			try {
				Log.d(DEBUG, "textwatcher:"+s.toString());
				int shares = Integer.parseInt(s.toString());
				int cost = shares*stock.getCurrentValue();

				((TextView)findViewById(R.id.text_trade_credits)).setText(cost+"");
			}
			catch (Exception e){
				Log.d(DEBUG, "textwatcher err: "+e.getMessage());
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		stock = ((MyApplication) TradeActivity.this.getApplication()).stock;
		
		((TextView)findViewById(R.id.text_trade_stock_name)).setText(stock.getName());
		((TextView)findViewById(R.id.text_trade_worth)).setText(stock.getCurrentValue()+"");
		
		int credits = ((MyApplication)TradeActivity.this.getApplication()).credits;
		((TextView)findViewById(R.id.button_trade_money)).setText(credits+"");
	}
	
	public void buysellClicked(View v) {
		Log.d(DEBUG, "clicked");
		// if buy is on, turn it off and turn sell on
		if(TradeActivity.this.findViewById(R.id.button_trade_buy).getTag().equals("on")) {
			TradeActivity.this.findViewById(R.id.button_trade_buy).setBackgroundResource(R.drawable.buy_off);
			TradeActivity.this.findViewById(R.id.button_trade_buy).setTag("off");

			TradeActivity.this.findViewById(R.id.button_trade_sell).setBackgroundResource(R.drawable.sell_on);
			TradeActivity.this.findViewById(R.id.button_trade_sell).setTag("on");
			
			mode = MODE_SELL;
		}
		// else buy is off, turn it on and turn sell off
		else {
			TradeActivity.this.findViewById(R.id.button_trade_buy).setBackgroundResource(R.drawable.buy_on);
			TradeActivity.this.findViewById(R.id.button_trade_buy).setTag("on");
			
			TradeActivity.this.findViewById(R.id.button_trade_sell).setBackgroundResource(R.drawable.sell_off);
			TradeActivity.this.findViewById(R.id.button_trade_sell).setTag("off");
			
			mode = MODE_BUY;
		}
	}
	
	public void getMode() {
		if(TradeActivity.this.findViewById(R.id.button_trade_buy).getTag().equals("on")) {
			mode = MODE_SELL;
		}
		else {
			mode = MODE_BUY;
		}
	}
	
	public void backClicked(View v) {
		TradeActivity.this.finish();
	}
	
	public void tradeClicked(View v) {
		
		(new BuySharesTask()).execute();
		
	}
	
	
	private class BuySharesTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(Void... v) {
			int shares = Integer.parseInt(et.getText().toString());
			if(shares>0) {
				String facebookID = ((MyApplication)TradeActivity.this.getApplication()).facebookID;
				return DataAPI.getInstance().marketPOST(facebookID, stock.getId(), shares);
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject jo) {
			if(jo!=null) {
				(new UpdateCreditsTask()).execute();
			}
			else {
				Toast.makeText(TradeActivity.this, "Transaction Failed!", Toast.LENGTH_LONG).show();
				cleanUp();
			}
		}
	}
	
	private class UpdateCreditsTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			String facebookID = ((MyApplication)TradeActivity.this.getApplication()).facebookID;
			JSONObject userGETJSON = DataAPI.getInstance().usersGET(facebookID);
			int newCredits = -1;
			try {
				newCredits = userGETJSON.getInt("credits");
			} catch (JSONException e) { newCredits = -2; }
			return newCredits;
		}

		@Override
		protected void onPostExecute(Integer newCredits) {
			((TextView)TradeActivity.this.findViewById(R.id.button_trade_money)).setText(newCredits+"");
			((MyApplication)TradeActivity.this.getApplication()).credits = newCredits;
			Toast.makeText(TradeActivity.this, "Transaction Successful!", Toast.LENGTH_LONG).show();
			cleanUp();
		}
	}
	
	public void cleanUp() {
		et.setText("");
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
}
