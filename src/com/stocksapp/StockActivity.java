package com.stocksapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.DataAPI;
import API.GraphAPI;
import API.StockDataAPI;
import Model.Stock;
import View.GraphView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StockActivity extends Activity {

	final int MODE_HOUR = 0;
	final int MODE_ALL = 3;
	
	final int BUTTON_FRIENDS = 0;
	
	String DEBUG = "StockActivity";

	StockListAdapter sa;
	
	String facebookName;
	String facebookID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stockmain);
		
		ListView lv = (ListView) findViewById(R.id.list_stock_stocks);
		sa = new StockListAdapter(MODE_HOUR);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(sa);
		
		((Button)findViewById(R.id.button_stock_friends)).setOnClickListener(new LowerTabOnClickListener(BUTTON_FRIENDS));
	
		Bundle b = getIntent().getExtras();
		facebookName = b.getString("firstName");
		facebookID = b.getString("id");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// updates credits
		(new UpdateCreditsTask()).execute();
		
		// updates stock portfolio list
		(new UpdatePortfolioListTask()).execute();
		
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

			if(position == currentPos) {
				v.setBackgroundColor(0xffabbfcb);
			}
			else {
				v.setBackgroundColor(0x00000000);
			}
			
			((TextView)v.findViewById(R.id.text_stocklist_name)).setText(stockList.get(position).getName());

			if(mode == MODE_HOUR) {
				double percent = stockList.get(position).getPercentChangeByLastHour();
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText( percent+" %");
				
				if(percent < 0.0) {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xffae2a0b);
				}
				else {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xff307910);
				}
				
			}
			else if(mode == MODE_ALL) {
				double percent = stockList.get(position).getPercentChangeAllTime();
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(percent +" %");
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
			((Button)findViewById(R.id.button_stock_back)).setVisibility(View.VISIBLE);
			
			if(sa.getMode()==MODE_HOUR) {
				((TextView)findViewById(R.id.text_stock_percent)).setText(stock.getPercentChangeByLastHour()+" %");
			}
			else if(sa.getMode()==MODE_ALL) {
				((TextView)findViewById(R.id.text_stock_percent)).setText(stock.getPercentChangeAllTime()+" %");
			}
			
			((TextView)findViewById(R.id.text_stock_worth)).setText(stock.getCurrentValue()+"");

			(new UpdateGraphTask()).execute(stock);
			
			notifyDataSetChanged();
		}

	}
	
	/**
	 * Updates GraphView
	 * float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
	 * String[] verlabels = new String[] { "2", "1", "0" };
	 * String[] horlabels = new String[] { "445", "446", "447", "448" };
	 * @param stock
	 */
	public void updateGraph(Stock stock) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.chart);

		GraphAPI gAPI = GraphAPI.getInstance();
		gAPI.setParsedPair(stock.getPoints());
		float[] values = gAPI.getValues();
		String [] verlabels = gAPI.getVarLabels();
		String[] horlabels = gAPI.getHorLabels();
	
		GraphView graphView = new GraphView(StockActivity.this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);

		ll.removeAllViews();
		ll.addView(graphView);
	}

	private class GetStockGraphTasks extends AsyncTask<Void, Void, Void> {

		public GetStockGraphTasks() {

		}

		protected Void doInBackground(Void... urls) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.chart);

			float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
			String[] verlabels = new String[] { "2", "1", "0" };
			String[] horlabels = new String[] { "445", "446", "447", "448" };
			GraphView graphView = new GraphView(StockActivity.this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);

			ll.addView(graphView);

			return null;
		}

		protected void onProgressUpdate(Void... progress) {
		}

		protected void onPostExecute(Void result) {

		}
	}

	public void DayClicked(View v) {
		
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_selected);
		findViewById(R.id.button_stock_week).setBackgroundResource(R.drawable.btn_timeweek_deselected);
		findViewById(R.id.button_stock_month).setBackgroundResource(R.drawable.btn_timemonth_deselected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_deselected);
		
		Log.d("StocksAPI", "days clicked!");
		sa.setMode(MODE_HOUR);
		sa.notifyDataSetChanged();
		
		Stock curStock = sa.getStockList().get(sa.getPos());
		ArrayList<PointF> pList = curStock.getPoints();
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

		LinearLayout ll = (LinearLayout) findViewById(R.id.chart);
		GraphAPI gAPI = GraphAPI.getInstance();
		gAPI.setParsedPair(newPoints);
		float[] values = gAPI.getValues();
		String [] verlabels = gAPI.getVarLabels();
		String[] horlabels = gAPI.getHorLabels();
		
		GraphView graphView = new GraphView(StockActivity.this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);

		ll.removeAllViews();
		ll.addView(graphView);
	}

	public void WeekClicked(View v) {
		// do nothing
	}

	public void MonthClicked(View v) {
		// do nothing
	}

	public void YearClicked(View v) {
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_deselected);
		findViewById(R.id.button_stock_week).setBackgroundResource(R.drawable.btn_timeweek_deselected);
		findViewById(R.id.button_stock_month).setBackgroundResource(R.drawable.btn_timemonth_deselected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_selected);
		
		
		sa.setMode(MODE_ALL);
		sa.notifyDataSetChanged();
		
		Stock curStock = sa.getStockList().get(sa.getPos());
		ArrayList<PointF> pList = curStock.getPoints();
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

		LinearLayout ll = (LinearLayout) findViewById(R.id.chart);
		GraphAPI gAPI = GraphAPI.getInstance();
		gAPI.setParsedPair(newPoints);
		float[] values = gAPI.getValues();
		String [] verlabels = gAPI.getVarLabels();
		String[] horlabels = gAPI.getHorLabels();
		
		GraphView graphView = new GraphView(StockActivity.this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);

		ll.removeAllViews();
		ll.addView(graphView);
	}

	public void BackClicked(View v) {
		((TextView)findViewById(R.id.text_stock_stocks)).setText("Stocks");
		sa.setPos(-1);
		sa.notifyDataSetChanged();

		((TextView)findViewById(R.id.text_stock_portfolio_name)).setText("My Portfolio");
		((Button)findViewById(R.id.button_stock_trade)).setVisibility(View.GONE);
		((Button)findViewById(R.id.button_stock_back)).setVisibility(View.GONE);
		
		((TextView)findViewById(R.id.text_stock_percent)).setText("+ 4.3%");
		
		((TextView)findViewById(R.id.text_stock_worth)).setText("7324");
		
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.chart);
		float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
		String[] verlabels = new String[] { "2", "1", "0" };
		String[] horlabels = new String[] { "445", "446", "447", "448" };
		GraphView graphView = new GraphView(StockActivity.this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);
		ll.removeAllViews();
		ll.addView(graphView);
	}
	
	public void TradeClicked(View v) {
		// TODO: go to Trade activity
	}
	
	
	private class UpdateCreditsTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			JSONObject userGETJSON = DataAPI.getInstance().usersGET(facebookID);
			int newCredits = -1;
			try {
				newCredits = userGETJSON.getInt("credits");
				//Log.d(DEBUG, "newCredits: "+newCredits);
			} catch (JSONException e) { }
			return newCredits;
		}

		@Override
		protected void onPostExecute(Integer newCredits) {
			((TextView)findViewById(R.id.button_stock_money)).setText(newCredits+"");
		}
	}
	
	private class UpdatePortfolioListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			JSONObject portfolioGETJSON = DataAPI.getInstance().portfolioGET(facebookID);
			try {
				JSONArray ja = portfolioGETJSON.getJSONArray("stock");
				sa.clearList();
				for(int i=0; i<ja.length(); i++) {
					Stock newStock = new Stock();
					JSONObject jao = ja.getJSONObject(i);
					newStock.setName(jao.getString("name"));
					newStock.setId(jao.getInt("id"));
					newStock.setCurrentValue(jao.getInt("current_price"));
					newStock.setOpeningPrice(jao.getInt("opening_price"));
					newStock.setPurchasePrice(jao.getInt("purchase_price"));
					sa.addStock(newStock);
				}
			} catch (JSONException e) {	}
			return null;
		}

		@Override
		protected void onPostExecute(Void newCredits) {
			sa.notifyDataSetChanged();
		}

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
}
