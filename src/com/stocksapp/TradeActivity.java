package com.stocksapp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.DataAPI;
import Model.Stock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class TradeActivity extends Activity {
	
	final String DEBUG = "TradeActivity";
	
	int mode = 0;
	final int MODE_BUY = 0;
	final int MODE_SELL = 1;
	
	int span = 1;
	final int SPAN_DAY = 0;
	final int SPAN_ALL= 1;
	
	Stock stock;
	
	EditText et;
	DecimalFormat df = new DecimalFormat("#0.0");
	
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
				if(s.toString().equals("")) {
					((TextView)findViewById(R.id.text_trade_credits)).setText(0+"");
				}
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
		((TextView)findViewById(R.id.text_trade_shares)).setText(stock.getShareCount()+"");
		
		
		if(span == SPAN_ALL) {
			((TextView)findViewById(R.id.text_trade_percent)).setText(df.format(stock.getPercentChangeAllTime())+"%");
		}
		else {
			((TextView)findViewById(R.id.text_trade_percent)).setText(df.format(stock.getPercentChangeByLastHour())+"%");
		}
		
		int credits = ((MyApplication)TradeActivity.this.getApplication()).credits;
		((TextView)findViewById(R.id.button_trade_money)).setText(credits+"");
		
		(new UpdateGraphTask()).execute(stock);
		
		(new UpdateCreditsTask()).execute();
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
			mode = MODE_BUY;
		}
		else {
			mode = MODE_SELL;
		}
	}
	
	public void backClicked(View v) {
		TradeActivity.this.finish();
	}
	
	public void tradeClicked(View v) {
		if(mode==MODE_BUY) {
			Log.d(DEBUG, "buying...");
			(new BuySharesTask()).execute();
		}
		else if(mode==MODE_SELL) {
			Log.d(DEBUG, "selling...");
			(new SellSharesTask()).execute();
		}
		
	}
	
	
	private class BuySharesTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(Void... v) {
			
			int shares = 0;
			try {
				shares = Integer.parseInt(et.getText().toString());
			} catch (Exception e) {}
			
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
				Toast.makeText(TradeActivity.this, "Transaction Successful!", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(TradeActivity.this, "Transaction Failed!", Toast.LENGTH_LONG).show();
				cleanUp();
			}
		}
	}
	
	private class SellSharesTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(Void... v) {
			int shares = 0;
			try {
				shares = Integer.parseInt(et.getText().toString());
			} catch (Exception e) {}
			
			if(shares>0) {
				String facebookID = ((MyApplication)TradeActivity.this.getApplication()).facebookID;
				return DataAPI.getInstance().portfolioPOST(facebookID, stock.getId(), shares);
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject jo) {
			if(jo!=null) {
				(new UpdateCreditsTask()).execute();
				Toast.makeText(TradeActivity.this, "Transaction Successful!", Toast.LENGTH_LONG).show();
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

			(new UpdatePortfolioListTask()).execute();
			
			cleanUp();
		}
	}
	
	public void cleanUp() {
		et.setText("");
		((TextView)findViewById(R.id.text_trade_credits)).setText("0");
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	public void portfolioClicked(View v) {
		Intent i = new Intent(TradeActivity.this, StockActivity.class);
		startActivity(i);
		TradeActivity.this.finish();
	}
	
	public void friendsClicked(View v) {
		Intent i = new Intent(TradeActivity.this, FriendsStockActivity.class);
		startActivity(i);
		TradeActivity.this.finish();
	}

	public void discoverClicked(View v) {
		Intent i = new Intent(TradeActivity.this, DiscoverActivity.class);
		startActivity(i);
		TradeActivity.this.finish();
	}
	
	private class UpdateGraphTask extends AsyncTask<Stock, Void, Stock> {
		@Override
		protected Stock doInBackground(Stock... stocks) {
			JSONObject jo = DataAPI.getInstance().performanceGET(stocks[0].getId());
			try {
				JSONArray ja = jo.getJSONArray("values");
				ArrayList<PointF> pts = new ArrayList<PointF>();
				for(int i=0; i<ja.length(); i++) {
					JSONObject jao = ja.getJSONObject(i);
					PointF pf = new PointF(jao.getInt("date"), jao.getInt("value"));
					pts.add(pf);
				}
				stocks[0].setPoints(pts);
			} catch (JSONException e) {
				Log.d(DEBUG, "onItemClick err: "+e.getMessage());
			}
			return stocks[0];
		}

		@Override
		protected void onPostExecute(Stock stock) {
			updateGraph(stock);
		}
	}
	
	public void updateGraph(Stock stock) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.chart_trade);

		// init example series data  
		Collections.sort(stock.getPoints(), new Comparator<PointF>() {
			@Override
			public int compare(PointF lhs, PointF rhs) {
				if(lhs.x<rhs.x) return -1;
				else if(lhs.x>rhs.x) return 1;
				else return 0;
			}
		});
		
		GraphViewData[] gViews = new GraphViewData[stock.getPoints().size()];
		for(int i=0; i<stock.getPoints().size(); i++) {
			GraphViewData gv = new GraphViewData(stock.getPoints().get(i).x, stock.getPoints().get(i).y);
			gViews[i] = gv;
		}
		
		GraphViewSeries exampleSeries = new GraphViewSeries(gViews);  
		
		GraphView graphView = new LineGraphView(  
		      this // context  
		      , "GraphViewDemo" // heading  
		);  
		graphView.addSeries(exampleSeries); // data  
		   
		graphView.setViewPort(stock.getPoints().get(stock.getPoints().size()-1).x-24, 24);  
		graphView.setScrollable(true);  
		// optional - activate scaling / zooming  
		graphView.setScalable(true);  
		
		// ((LineGraphView) graphView).setDrawBackground(true);
		
		ll.removeAllViews();
		ll.addView(graphView);
	}
	
	private class UpdatePortfolioListTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String facebookID = ((MyApplication)TradeActivity.this.getApplication()).facebookID;
			JSONObject portfolioGETJSON = DataAPI.getInstance().portfolioGET(facebookID);
			//Log.d(DEBUG, "protfolio GET: "+portfolioGETJSON.toString());
			try {
				JSONArray ja = portfolioGETJSON.getJSONArray("stocks");
				
				boolean found = false;
				for(int i=0; i<ja.length(); i++) {
					JSONObject jao = ja.getJSONObject(i);
					
					if(jao.getInt("stock_id")==stock.getId()) {
						found = true;
						
						int sharesCount = jao.getInt("share_count");
						int currentVal = jao.getInt("current_value");
						
						int openingPrice = jao.getInt("opening_price");
						int parValue = jao.getInt("par_value");	
						
						double percentChangeAll = ((double)(currentVal - parValue))/((double)parValue);
						double percentChangeDay = ((double)(currentVal - openingPrice))/((double)openingPrice);
						
						stock.setPercentChangeAllTime(percentChangeAll);
						stock.setPercentChangeByLastHour(percentChangeDay);
						
						stock.setShareCount(sharesCount);
						stock.setCurrentValue(currentVal);
						
						break;
					}
					else {
						continue;
					}
				}
				
				if(!found) {
					((TextView) findViewById(R.id.text_trade_shares)).setText(0+"");
				}
				
			} catch (JSONException e) {	
				Log.d(DEBUG, "portoflio JSON err: "+e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			((TextView) findViewById(R.id.text_trade_shares)).setText(stock.getShareCount()+"");
			((TextView) findViewById(R.id.text_trade_worth)).setText(stock.getCurrentValue()+"");
			
			if(span == SPAN_ALL) {
				((TextView) findViewById(R.id.text_trade_percent)).setText(df.format(stock.getPercentChangeAllTime())+"%");
			}
			else {
				((TextView) findViewById(R.id.text_trade_percent)).setText(df.format(stock.getPercentChangeByLastHour())+"%");
			}
		}

	}
	
	public void YearClicked(View v) {
		span = SPAN_ALL;
		
		((Button) findViewById(R.id.button_trade_year)).setBackgroundResource(R.drawable.btn_timeyear_selected);
		((Button) findViewById(R.id.button_trade_day)).setBackgroundResource(R.drawable.btn_timeday_deselected);
		
		((TextView) findViewById(R.id.text_trade_percent)).setText(df.format(stock.getPercentChangeAllTime())+"%");
	}
	
	public void DayClicked(View v) {
		span = SPAN_DAY;
		
		((Button) findViewById(R.id.button_trade_year)).setBackgroundResource(R.drawable.btn_timeyear_deselected);
		((Button) findViewById(R.id.button_trade_day)).setBackgroundResource(R.drawable.btn_timeday_selected);
		
		((TextView) findViewById(R.id.text_trade_percent)).setText(df.format(stock.getPercentChangeByLastHour())+"%");
	}
}
