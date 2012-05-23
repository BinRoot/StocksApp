package com.stocksapp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.DataAPI;
import Model.Stock;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;


public class StockActivity extends Activity {

	final int MODE_HOUR = 0;
	final int MODE_ALL = 3;
	
	final int BUTTON_FRIENDS = 0;
	final int BUTTON_DISCOVER = 1;
	
	int secondsLeft = 15;
	int countDownTime = 30;
	
	String DEBUG = "StockActivity";

	StockListAdapter sa;
	
	String facebookName;
	String facebookID;
	
	Timer t;
	
	DecimalFormat df = new DecimalFormat("#0.0");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stockmain);
		
		Bundle b = getIntent().getExtras();
		if(b!=null) {
			facebookName = b.getString("firstName");
			facebookID = b.getString("id");
			((MyApplication) this.getApplication()).facebookName = facebookName;
			((MyApplication) this.getApplication()).facebookID = facebookID;
		}
		else {
			facebookName = ((MyApplication) this.getApplication()).facebookName;
			facebookID = ((MyApplication) this.getApplication()).facebookID;
		}
		
		ListView lv = (ListView) findViewById(R.id.list_stock_stocks);
		sa = new StockListAdapter(MODE_HOUR);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(sa);
		
		((Button)findViewById(R.id.button_stock_friends)).setOnClickListener(new LowerTabOnClickListener(BUTTON_FRIENDS));
		((Button)findViewById(R.id.button_stock_discover)).setOnClickListener(new LowerTabOnClickListener(BUTTON_DISCOVER));
	
		(new UpdateUserInDBTask()).execute();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();

		// updates stock portfolio list
		(new UpdatePortfolioListTask()).execute();
		
		StockTimerTask sTimer = new StockTimerTask();
		t = new Timer();
		t.schedule(sTimer, 100, 1000);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		t.cancel();
	}
	
	
	public class StockTimerTask extends TimerTask {

		@Override
		public void run() {
			if(secondsLeft<=0) {
				secondsLeft=countDownTime;
			}
			else if(secondsLeft<=1) {
				refreshMarket();
			}
			
			secondsLeft--;
		}
		
	}
	
	public void refreshMarket() {
		Log.d(DEBUG, "refreshing market...");
		
		if(sa.stockList!=null) {
			if(!sa.stockList.isEmpty()) {
				(new UpdateGraphDetailTask()).execute(sa.stockList.get(sa.currentPos));
			}
		}
		
		//(new UpdatePortfolioListTask()).execute();
	}
	
	public class LowerTabOnClickListener implements OnClickListener {

		int buttonMode;
		
		public LowerTabOnClickListener(int buttonMode) {
			this.buttonMode = buttonMode;
		}
		
		@Override
		public void onClick(View arg0) {
			if(buttonMode == BUTTON_FRIENDS) {
				Intent i = new Intent(StockActivity.this, FriendsStockActivity.class);
				startActivity(i);
				StockActivity.this.finish();
			}
			else if(buttonMode == BUTTON_DISCOVER) {
				Intent i = new Intent(StockActivity.this, DiscoverActivity.class);
				startActivity(i);
				StockActivity.this.finish();
			}
		}
	}

	public class StockListAdapter extends BaseAdapter implements OnItemClickListener {

		ArrayList<Stock> stockList;
		int mode;
		int currentPos = -1;

		public StockListAdapter(int mode) {
			stockList = new ArrayList<Stock>();
			this.mode = mode;
		}
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			
			if(stockList.isEmpty()) {
				displayHelp();
			}
			else {
				hideHelp();
			}
		}
		
		public void clearList() {
			stockList.clear();
		}
		
		public int getPos() {
			return currentPos;
		}
		
		public void setPos(int currentPos) {
			this.currentPos = currentPos;
		}
		
		public ArrayList<Stock> getStockList() {
			return stockList;
		}
		
		public int getMode() {
			return mode;
		}

		public void addStock(Stock s) {
			stockList.add(s);
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		@Override
		public int getCount() {
			return stockList.size();
		}

		@Override
		public Stock getItem(int position) {
			return stockList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(v==null) {
				v = getLayoutInflater().inflate(R.layout.stocklistitem, null);
			}
			
			
			if(currentPos==-1) {
				if(!stockList.isEmpty()) {
					currentPos = 0;
					onItemClick(null, null, 0, 0);
				}
			}

			if(position == currentPos) {
				v.setBackgroundColor(0xffabbfcb);
			}
			else {
				v.setBackgroundColor(0x00000000);
			}
			
			((TextView)v.findViewById(R.id.text_stocklist_name)).setText(stockList.get(position).getName());

			
			if(mode == MODE_HOUR) {
				double percent = stockList.get(position).getPercentChangeByLastHour();
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText( df.format(percent)+" %");
				
				if(percent < 0.0) {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xffae2a0b);
				}
				else {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xff307910);
				}
				
			}
			else if(mode == MODE_ALL) {
				double percent = stockList.get(position).getPercentChangeAllTime();
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(df.format(percent) +" %");
				if(percent < 0.0) {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xffae2a0b); //
				}
				else {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xff307910);
				}
			}
			
			((TextView)v.findViewById(R.id.text_stocklist_worth)).setText(stockList.get(position).getCurrentValue()+"");

			
			return v;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
			currentPos = pos;
			
			Stock stock = stockList.get(pos);
			((TextView)findViewById(R.id.text_stock_portfolio_name)).setText(stock.getName());
			((Button)findViewById(R.id.button_stock_trade)).setVisibility(View.VISIBLE);
			((ImageView)findViewById(R.id.button_stock_back)).setVisibility(View.VISIBLE);
			
			// TODO change to red
			
			if(sa.getMode()==MODE_HOUR) {
				if(stock.getPercentChangeByLastHour()<0.0) {
					((TextView)findViewById(R.id.text_stock_percent)).setTextColor(0xffae2a0b);
				}
				else {
					((TextView)findViewById(R.id.text_stock_percent)).setTextColor(0xff307910);
				}
				((TextView)findViewById(R.id.text_stock_percent)).setText(df.format(stock.getPercentChangeByLastHour())+" %");
			}
			else if(sa.getMode()==MODE_ALL) {
				if(stock.getPercentChangeAllTime()<0.0) {
					((TextView)findViewById(R.id.text_stock_percent)).setTextColor(0xffae2a0b);
				}
				else {
					((TextView)findViewById(R.id.text_stock_percent)).setTextColor(0xff307910);
				}
				((TextView)findViewById(R.id.text_stock_percent)).setText(df.format(stock.getPercentChangeAllTime())+" %");
			}
			
			((TextView)findViewById(R.id.text_stock_worth)).setText(stock.getCurrentValue()+"");

			stock.resetPoints();
			(new UpdateGraphTask()).execute(stock);
			
			
			secondsLeft = 1;
			
			notifyDataSetChanged();
		}

	}
	
	
	public void updateGraph(Stock stock) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.chart);

		
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
		
		
		GraphViewData[] gViewsBot = new GraphViewData[stock.getPoints().size()];
		for(int i=0; i<stock.getPoints().size(); i++) {
			GraphViewData gv = new GraphViewData(stock.getPoints().get(i).x, 0);
			gViewsBot[i] = gv;
		}
		
		GraphViewSeries exampleSeries = new GraphViewSeries(gViews);  
		
		GraphViewSeries botSeries = new GraphViewSeries(gViewsBot);
		
		
		GraphView graphView = new LineGraphView(  
		      this // context  
		      , "GraphViewDemo" // heading  
		);  
		graphView.addSeries(exampleSeries); // data  
		graphView.addSeries(botSeries); // data  
		   
		graphView.setViewPort(stock.getPoints().get(stock.getPoints().size()-1).x-0.015, 0.015);  
		graphView.setScrollable(true);  
		// optional - activate scaling / zooming  
		graphView.setScalable(true);  
		
		
		// ((LineGraphView) graphView).setDrawBackground(true);
		
		ll.removeAllViews();
		ll.addView(graphView);
	}


	public void DayClicked(View v) {
		
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_selected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_deselected);
		
		Log.d("StocksAPI", "days clicked!");
		sa.setMode(MODE_HOUR);
		sa.notifyDataSetChanged();
		
		if(sa.getStockList()==null) {
			return;
		}
		else if(sa.getStockList().isEmpty()){
			return;
		}
		
		Stock curStock = sa.getStockList().get(0);
		ArrayList<PointF> pList = curStock.getPoints();
		int curPos = sa.getPos();
		if(curPos>=0) {
			curStock = sa.getStockList().get(sa.getPos());
			pList = curStock.getPoints();
		}
		
		int totalSize = pList.size();
		int lastFew = totalSize/4;
		Log.d("StocksAPI", "last few: "+lastFew+"... "+lastFew/4);
		
		ArrayList<PointF> newPoints = new ArrayList<PointF>();
		for(int i=pList.size()-1; i>=pList.size()-lastFew; i--) {
			newPoints.add(pList.get(i));
		}
		
		Collections.sort(newPoints, new Comparator<PointF>() {

			@Override
			public int compare(PointF lhs, PointF rhs) {
				if(lhs.x < rhs.x) {
					return -1;
				}
				else if(lhs.x > rhs.x) {
					return 1;
				}
				else {
					return 0;
				}
			}
			
		});

		
	}

	public void WeekClicked(View v) {
		// do nothing
	}

	public void MonthClicked(View v) {
		// do nothing
	}

	public void YearClicked(View v) {
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_deselected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_selected);
		
		
		sa.setMode(MODE_ALL);
		sa.notifyDataSetChanged();
		
		if(sa.getStockList()==null) {
			return;
		}
		else if(sa.getStockList().isEmpty()){
			return;
		}
		
		Stock curStock = sa.getStockList().get(0);
		ArrayList<PointF> pList = curStock.getPoints();
		int curPos = sa.getPos();
		if(curPos>=0) {
			curStock = sa.getStockList().get(sa.getPos());
			pList = curStock.getPoints();
		}
		
		
		int totalSize = pList.size();
		int lastFew = totalSize;
		
		ArrayList<PointF> newPoints = new ArrayList<PointF>();
		for(int i=pList.size()-1; i>=pList.size()-lastFew; i--) {
			newPoints.add(pList.get(i));
		}
		
		Collections.sort(newPoints, new Comparator<PointF>() {

			@Override
			public int compare(PointF lhs, PointF rhs) {
				if(lhs.x < rhs.x) {
					return -1;
				}
				else if(lhs.x > rhs.x) {
					return 1;
				}
				else {
					return 0;
				}
			}
			
		});

		
	}

	public void BackClicked(View v) {
		((TextView)findViewById(R.id.text_stock_stocks)).setText("Stocks");
		sa.setPos(-1);
		sa.notifyDataSetChanged();

		((TextView)findViewById(R.id.text_stock_portfolio_name)).setText("My Portfolio");
		((Button)findViewById(R.id.button_stock_trade)).setVisibility(View.GONE);
		((ImageView)findViewById(R.id.button_stock_back)).setVisibility(View.GONE);
		
		((TextView)findViewById(R.id.text_stock_percent)).setText("");
		((TextView)findViewById(R.id.text_stock_worth)).setText("");
		
		
	}
	
	public void TradeClicked(View v) {
		((MyApplication)StockActivity.this.getApplication()).stock = sa.getItem(sa.currentPos);
		Intent i = new Intent(StockActivity.this, TradeActivity.class);
		startActivity(i);
	}
	
	
	private class UpdateCreditsTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			JSONObject userGETJSON = DataAPI.getInstance().usersGET(facebookID);
			
			if(userGETJSON == null) {
				return null;
			}
			
			int newCredits = -1;
			try {
				newCredits = userGETJSON.getInt("credits");
				//Log.d(DEBUG, "newCredits: "+newCredits);
			} catch (JSONException e) { }
			return newCredits;
		}

		@Override
		protected void onPostExecute(Integer newCredits) {
			if(newCredits!=null) {
				((TextView)findViewById(R.id.button_stock_money)).setText(newCredits+"");
				int nc = newCredits;
				((MyApplication)StockActivity.this.getApplication()).credits = (long) nc;
			}
			else {
				Toast.makeText(StockActivity.this, StockActivity.this.getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class UpdatePortfolioListTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			findViewById(R.id.progress_stocklist).setVisibility(View.VISIBLE);
			findViewById(R.id.list_stock_stocks).setVisibility(View.GONE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			JSONObject portfolioGETJSON = DataAPI.getInstance().portfolioGET(facebookID);
			//Log.d(DEBUG, "protfolio GET: "+portfolioGETJSON.toString());
			try {
				JSONArray ja = portfolioGETJSON.getJSONArray("stocks");
				//Log.d(DEBUG, "protfolio GET, ja: "+ja.toString());
				sa.clearList();
				for(int i=0; i<ja.length(); i++) {
					Stock newStock = new Stock();
					JSONObject jao = ja.getJSONObject(i);
					
					//Log.d(DEBUG, "stock: "+jao.toString());
					
					
					int currentVal = jao.getInt("current_value");
					int openingPrice = jao.getInt("opening_price");
					int parValue = jao.getInt("par_value");
					
					double percentChangeAll = ((double)(currentVal - parValue))/((double)parValue);
					double percentChangeDay = ((double)(currentVal - openingPrice))/((double)openingPrice);
					
					newStock.setPercentChangeAllTime(percentChangeAll);
					newStock.setPercentChangeByLastHour(percentChangeDay);
					
					newStock.setName(jao.getString("description"));
					newStock.setId(jao.getInt("stock_id"));
					newStock.setCurrentValue(currentVal);
					newStock.setParValue(parValue);
					newStock.setPurchasePrice(jao.getInt("purchase_price"));
					newStock.setOpeningPrice(openingPrice); 
					newStock.setShareCount(jao.getInt("share_count"));
					
					sa.addStock(newStock);
				}
			} catch (Exception e) {	
				Log.d(DEBUG, "portoflio JSON err: "+e.getMessage());
			}
			
			// found this fix on stackoverflow
			runOnUiThread(new Runnable() {
			     public void run() {
			    	 sa.notifyDataSetChanged();
			    }
			});
			
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void newCredits) {
			sa.notifyDataSetChanged();
			
			findViewById(R.id.progress_stocklist).setVisibility(View.GONE);
			findViewById(R.id.list_stock_stocks).setVisibility(View.VISIBLE);
		}

	}
	
	private class UpdateGraphTask extends AsyncTask<Stock, Void, Stock> {
		@Override
		protected Stock doInBackground(Stock... stocks) {
			JSONObject jo = DataAPI.getInstance().performanceGET(stocks[0].getId());
			try {
				JSONArray ja = jo.getJSONArray("performance");
				ArrayList<PointF> pts = stocks[0].getPoints();
				for(int i=0; i<ja.length(); i++) {
					JSONObject jao = ja.getJSONObject(i);
					
					Date d = new Date(jao.getInt("t")*1000);
					Log.d(DEBUG, "hourly plotting ("+d.getHours()+", "+jao.getInt("v")+")");
					PointF pf = new PointF( d.getHours() , jao.getInt("v")); 
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
	
	private class UpdateGraphDetailTask extends AsyncTask<Stock, Void, Stock> {
		@Override
		protected Stock doInBackground(Stock... stocks) {
			JSONObject jo = DataAPI.getInstance().performanceDETAIL(stocks[0].getId());
			try {
				JSONArray ja = jo.getJSONArray("performance");
				ArrayList<PointF> pts = stocks[0].getPoints();
				for(int i=0; i<ja.length(); i++) {
					JSONObject jao = ja.getJSONObject(i);
					
					Date d = new Date(jao.getInt("t")*1000);
					float xplot = (float)(d.getHours()+d.getMinutes()/60.0+d.getSeconds()/3600.0);
					
					
					if(!stocks[0].getPoints().isEmpty()) {
						if(xplot < stocks[0].getPoints().get(0).x) {
							xplot += 24;
						}
					}
					
					Log.d(DEBUG, "plotting ("+((float)(jao.getInt("t")))+", "+jao.getInt("v")+")");
					
					PointF pf = new PointF(xplot, jao.getInt("v")); 
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

	private class UpdateUserInDBTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			long cr = ((MyApplication)StockActivity.this.getApplication()).credits;
			((TextView)findViewById(R.id.button_stock_money)).setText(cr+""); 
		}
		
		@Override
		protected Void doInBackground(Void... stocks) {
			DataAPI.getInstance().usersPOST(facebookID, facebookName);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			Handler mHandler = new Handler();
			
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					(new UpdateCreditsTask()).execute();
				}
			});
		}
	}
	
	public void displayHelp() {
		((ImageView)findViewById(R.id.image_stock_help)).setVisibility(View.VISIBLE);
	}
	public void hideHelp() {
		((ImageView)findViewById(R.id.image_stock_help)).setVisibility(View.GONE);
	}
}
