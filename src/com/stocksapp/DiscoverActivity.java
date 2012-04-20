package com.stocksapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
import android.widget.Toast;

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
		public void notifyDataSetChanged() {
			Collections.sort(stockList, new Comparator<Stock>() {
				@Override
				public int compare(Stock lhs, Stock rhs) {
					if(lhs.getCurrentValue() > rhs.getCurrentValue())
						return -1;
					else if(lhs.getCurrentValue() < rhs.getCurrentValue())
						return 1;
					return 0;
				}
			});
			super.notifyDataSetChanged();
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
	
	
	private class UpdateDiscoverListTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			findViewById(R.id.progress_discover).setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(Void... stocks) {
			JSONObject jo = DataAPI.getInstance().marketGET();
			
			if(jo==null) {
				return false;
			}
			
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
			
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean b) {
			if(b==true) {
				da.notifyDataSetChanged();
				findViewById(R.id.progress_discover).setVisibility(View.GONE);
			}
			else {
				Toast.makeText(DiscoverActivity.this, DiscoverActivity.this.getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
			}
			
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
