package com.stocksapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.DataAPI;
import Model.Stock;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DiscoverActivity extends Activity {
	
	DiscoverAdapter da;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover);
		
		ListView lv = (ListView) findViewById(R.id.list_discover);
		da = new DiscoverAdapter();
		lv.setAdapter(da);
		lv.setOnItemClickListener(da);
		
		(new UpdateDiscoverListTask()).execute();
	}
	
	public class DiscoverAdapter extends BaseAdapter implements OnItemClickListener {

		ArrayList<Stock> stockList;
		
		public DiscoverAdapter() {
			stockList = new ArrayList<Stock>();
		}
		
		public void addStock(Stock s) {
			stockList.add(s);
		}
		
		@Override
		public int getCount() {
			return stockList.size();
		}

		@Override
		public Object getItem(int position) {
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
				v = getLayoutInflater().inflate(R.layout.discoveritem, null);
			}
			
			((TextView)v.findViewById(R.id.text_discoveritem_name)).setText(stockList.get(position).getName());
			((TextView)v.findViewById(R.id.text_discoveritem_value)).setText(stockList.get(position).getCurrentValue()+"");
			
			return v;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
			((MyApplication) DiscoverActivity.this.getApplication()).stock = stockList.get(pos);
			Intent i = new Intent(DiscoverActivity.this, TradeActivity.class);
			startActivity(i);
		}
		
	}
	
	
	private class UpdateDiscoverListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... stocks) {
			JSONObject jo = DataAPI.getInstance().marketGET();
			try {
				JSONArray ja = jo.getJSONArray("stocks");
				for(int i=0; i<ja.length(); i++) {
					Stock s = new Stock();
					
					JSONObject jao = ja.getJSONObject(i);
					String id = jao.getString("stock_id");
					String name = jao.getString("description");
					String currentPrice = jao.getString("current_value");
					String openingValue = jao.getString("opening_value");

					s.setName(name);
					s.setId(Integer.parseInt(id));
					s.setCurrentValue(Integer.parseInt(currentPrice));
					s.setOpeningPrice(Integer.parseInt(openingValue));
					
					da.addStock(s);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			da.notifyDataSetChanged();
		}
	}
	
	public void portfolioClicked(View v) {
		Intent i = new Intent(DiscoverActivity.this, StockActivity.class);
		startActivity(i);
		DiscoverActivity.this.finish();
	}
	
	public void friendsClicked(View v) {
		Intent i = new Intent(DiscoverActivity.this, FriendsStockActivity.class);
		startActivity(i);
		DiscoverActivity.this.finish();
	}
}
