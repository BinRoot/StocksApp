package com.stocksapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

		Stock s1 = new Stock("Ted and Marcy", 204, 1.2, -1.0, 3.0, 4.0);
		Stock s2 = new Stock("Jeremy Lin", 401, 2.2, -2.0, 1.0, -2.0);
		Stock s3 = new Stock("Americon Idol", 66, 2.6, -1.1, 1.0, -2.2);

		sa.addStock(s1);
		sa.addStock(s2);
		sa.addStock(s3);
		sa.addStock(s1);
		sa.addStock(s2);
		sa.addStock(s3);
		sa.notifyDataSetChanged();
		
		((Button)findViewById(R.id.button_stock_friends)).setOnClickListener(new LowerTabOnClickListener(BUTTON_FRIENDS));
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

	public class StockListAdapter extends BaseAdapter {

		ArrayList<Stock> stockList;
		int mode;

		public StockListAdapter(int mode) {
			stockList = new ArrayList<Stock>();
			this.mode = mode;
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

			((TextView)v.findViewById(R.id.text_stocklist_name)).setText(stockList.get(position).getName());

			if(mode == MODE_HOUR) {
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(stockList.get(position).getPercentChangeByLastHour() +" %");
			}
			else if(mode == MODE_DAY) {
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(stockList.get(position).getPercentChangeByLastDay() +" %");
			}
			else if(mode == MODE_WEEK) {
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(stockList.get(position).getPercentChangeByLastWeek() +" %");
			}
			else if(mode == MODE_MONTH) {
				((TextView)v.findViewById(R.id.text_stocklist_percent)).setText(stockList.get(position).getPercentChangeByLastMonth() +" %");
			}

			((TextView)v.findViewById(R.id.text_stocklist_worth)).setText(stockList.get(position).getCurrentValue()+"");

			return v;
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
		sa.setMode(MODE_HOUR);
		sa.notifyDataSetChanged();
	}

	public void WeekClicked(View v) {
		sa.setMode(MODE_DAY);
		sa.notifyDataSetChanged();
	}

	public void MonthClicked(View v) {
		sa.setMode(MODE_WEEK);
		sa.notifyDataSetChanged();
	}

	public void YearClicked(View v) {
		sa.setMode(MODE_MONTH);
		sa.notifyDataSetChanged();
	}


}
