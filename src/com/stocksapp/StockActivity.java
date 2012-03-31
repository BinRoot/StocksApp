package com.stocksapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

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
	final int MODE_DAY = 1;
	final int MODE_WEEK = 2;
	final int MODE_MONTH = 3;
	
	final int BUTTON_FRIENDS = 0;

	StockListAdapter sa;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stockmain);

		String firstName = (String) getIntent().getExtras().get("firstName");
		String id = (String) getIntent().getExtras().get("id");

		GetStockGraphTasks getGraphTask = new GetStockGraphTasks();
		getGraphTask.execute();

		ListView lv = (ListView) findViewById(R.id.list_stock_stocks);
		sa = new StockListAdapter(MODE_HOUR);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(sa);

		Stock s1 = new Stock("Ted and Marcy", 204, 1.4, -1.0, 3.0, 4.0);
		
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.add(new PointF(0, 0));
		points.add(new PointF(2, 60));
		points.add(new PointF(4, 80));
		points.add(new PointF(6, 90));
		points.add(new PointF(7, 70));
		points.add(new PointF(9, 40));
		s1.setPoints(points);
		
		
		Stock s2 = new Stock("Jeremy Lin", 401, 2.2, -2.0, 1.0, -2.0);
		points = new ArrayList<PointF>();
		points.add(new PointF(0, 30));
		points.add(new PointF(2, 35));
		points.add(new PointF(4, 32));
		points.add(new PointF(6, 25));
		points.add(new PointF(7, 28));
		points.add(new PointF(9, 31));
		points.add(new PointF(11, 25));
		points.add(new PointF(12, 28));
		points.add(new PointF(15, 31));
		s2.setPoints(points);
		
		Stock s3 = new Stock("Americon Idol", 66, 2.6, -1.1, 1.0, -2.2);
		points = new ArrayList<PointF>();
		points.add(new PointF(0, 20));
		points.add(new PointF(2, 41));
		points.add(new PointF(4, 44));
		points.add(new PointF(6, 45));
		points.add(new PointF(7, 40));
		points.add(new PointF(9, 38));
		points.add(new PointF(11, 37));
		points.add(new PointF(12, 36));
		points.add(new PointF(15, 38));
		s3.setPoints(points);
		
		Stock s4 = new Stock("Hunger Games", 800, 3.0, -4.2, 1.7, 2.9);
		points = new ArrayList<PointF>();
		points.add(new PointF(0, 70));
		points.add(new PointF(2, 80));
		points.add(new PointF(4, 30));
		points.add(new PointF(6, 35));
		points.add(new PointF(7, 34));
		points.add(new PointF(9, 45));
		points.add(new PointF(11, 42));
		points.add(new PointF(12, 50));
		points.add(new PointF(15, 51));
		s4.setPoints(points);
		
		Stock s5 = new Stock("Peyton Manning", 82, 0.1, 6.4, 5.1, 1.8);
		points = new ArrayList<PointF>();
		points.add(new PointF(0, 100));
		points.add(new PointF(2, 48));
		points.add(new PointF(4, 44));
		points.add(new PointF(6, 15));
		points.add(new PointF(7, 14));
		points.add(new PointF(9, 14));
		points.add(new PointF(11, 14));
		points.add(new PointF(12, 14));
		points.add(new PointF(15, 14));
		s5.setPoints(points);
		
		Stock s6 = new Stock("Game of Thrones", 201, -8.3, -2.3, -6.1, -5.1);
		points = new ArrayList<PointF>();
		points.add(new PointF(12, 40));
		points.add(new PointF(34, 100));
		points.add(new PointF(58, 100));
		points.add(new PointF(60, 200));
		points.add(new PointF(88, 80));
		points.add(new PointF(100, 100));
		points.add(new PointF(101, 90));
		points.add(new PointF(102, 80));
		points.add(new PointF(103, 80));
		s6.setPoints(points);

		sa.addStock(s1);
		sa.addStock(s2);
		sa.addStock(s3);
		sa.addStock(s4);
		sa.addStock(s5);
		sa.addStock(s6);
		sa.notifyDataSetChanged();
		
		((Button)findViewById(R.id.button_stock_friends)).setOnClickListener(new LowerTabOnClickListener(BUTTON_FRIENDS));
	
	
		String jsonStr = StockDataAPI.getInstance().getID(id);
		try {
			JSONObject joCredits = new JSONObject(jsonStr);
			String credits = joCredits.getString("credits");
			Log.d("StocksApp", "credits: "+credits);
			((TextView)findViewById(R.id.button_stock_money)).setText(credits);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		EditText et = (EditText)findViewById(R.id.edit_shares);
		et.addTextChangedListener(new ShareTextWatcher());
	}
	
	public class ShareTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			try {
				int num = Integer.parseInt(s.toString());
				int currentVal = sa.getStockList().get(sa.getPos()).getCurrentValue();
				int totCredits = currentVal * num;
				((TextView) findViewById(R.id.text_share_calculate)).setText("Total: "+totCredits);
			}
			catch (Exception e) {}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
		
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
				v.setBackgroundColor(Color.WHITE);
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
			else if(mode == MODE_DAY) {
				double percent = stockList.get(position).getPercentChangeByLastDay();
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(percent +" %");
				if(percent < 0.0) {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xffae2a0b);
				}
				else {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xff307910);
				}
			}
			else if(mode == MODE_WEEK) {
				double percent = stockList.get(position).getPercentChangeByLastWeek();
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(percent +" %");
				if(percent < 0.0) {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xffae2a0b);
				}
				else {
					((TextView)v.findViewById(R.id.text_stocklist_percent)).setTextColor(0xff307910);
				}
			}
			else if(mode == MODE_MONTH) {
				double percent = stockList.get(position).getPercentChangeByLastMonth();
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
			else if(sa.getMode()==MODE_DAY) {
				((TextView)findViewById(R.id.text_stock_percent)).setText(stock.getPercentChangeByLastDay()+" %");
			}
			else if(sa.getMode()==MODE_WEEK) {
				((TextView)findViewById(R.id.text_stock_percent)).setText(stock.getPercentChangeByLastWeek()+" %");
			}
			else if(sa.getMode()==MODE_MONTH) {
				((TextView)findViewById(R.id.text_stock_percent)).setText(stock.getPercentChangeByLastMonth()+" %");
			}
			
			((TextView)findViewById(R.id.text_stock_worth)).setText(stock.getCurrentValue()+"");

			LinearLayout ll = (LinearLayout) findViewById(R.id.chart);

			GraphAPI gAPI = GraphAPI.getInstance();
			gAPI.setParsedPair(stock.getPoints());
			float[] values = gAPI.getValues();
			String [] verlabels = gAPI.getVarLabels();
			String[] horlabels = gAPI.getHorLabels();
			
			//float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
			//String[] verlabels = new String[] { "2", "1", "0" };
			//String[] horlabels = new String[] { "445", "446", "447", "448" };
			GraphView graphView = new GraphView(StockActivity.this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);

			ll.removeAllViews();
			ll.addView(graphView);
			
			notifyDataSetChanged();
		}

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
		
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_deselected);
		findViewById(R.id.button_stock_week).setBackgroundResource(R.drawable.btn_timeweek_selected);
		findViewById(R.id.button_stock_month).setBackgroundResource(R.drawable.btn_timemonth_deselected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_deselected);
		
		sa.setMode(MODE_DAY);
		sa.notifyDataSetChanged();
		
		Stock curStock = sa.getStockList().get(sa.getPos());
		ArrayList<PointF> pList = curStock.getPoints();
		int totalSize = pList.size();
		int lastFew = totalSize/3;
		
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

	public void MonthClicked(View v) {
		
		
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_deselected);
		findViewById(R.id.button_stock_week).setBackgroundResource(R.drawable.btn_timeweek_deselected);
		findViewById(R.id.button_stock_month).setBackgroundResource(R.drawable.btn_timemonth_selected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_deselected);
		
		sa.setMode(MODE_WEEK);
		sa.notifyDataSetChanged();
		
		
		Stock curStock = sa.getStockList().get(sa.getPos());
		ArrayList<PointF> pList = curStock.getPoints();
		int totalSize = pList.size();
		int lastFew = totalSize/2;
		
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

	public void YearClicked(View v) {
		
		findViewById(R.id.button_stock_day).setBackgroundResource(R.drawable.btn_timeday_deselected);
		findViewById(R.id.button_stock_week).setBackgroundResource(R.drawable.btn_timeweek_deselected);
		findViewById(R.id.button_stock_month).setBackgroundResource(R.drawable.btn_timemonth_deselected);
		findViewById(R.id.button_stock_year).setBackgroundResource(R.drawable.btn_timeyear_selected);
		
		
		sa.setMode(MODE_MONTH);
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
		
		if( findViewById(R.id.relative_stock_trade).getVisibility() == View.VISIBLE ) {
			findViewById(R.id.list_stock_stocks).setVisibility(View.VISIBLE);
			findViewById(R.id.relative_stock_trade).setVisibility(View.GONE);
		}
		else {
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
		
	}
	
	public void TradeClicked(View v) {
		findViewById(R.id.list_stock_stocks).setVisibility(View.GONE);
		findViewById(R.id.relative_stock_trade).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.text_stock_stocks)).setText("Make a Trade");
	}
	
	public void BuyClicked(View v) {
		findViewById(R.id.button_stock_buy).setBackgroundResource(R.drawable.buy_on);
		findViewById(R.id.button_stock_sell).setBackgroundResource(R.drawable.sell_off);
	}
	
	public void SellClicked(View v) {
		findViewById(R.id.button_stock_buy).setBackgroundResource(R.drawable.buy_off);
		findViewById(R.id.button_stock_sell).setBackgroundResource(R.drawable.sell_on);
	}

}
