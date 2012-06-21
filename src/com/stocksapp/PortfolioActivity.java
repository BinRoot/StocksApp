package com.stocksapp;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.TrendsAPI;
import Model.Trend;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class PortfolioActivity extends Activity {

	String DEBUG = "Portfolio";
	String facebookName;
	String facebookID;
	int pingCount = 0;
	boolean portfolioScreen = true;
	boolean tradeScreen = false;
	char graphMode = 'd';
	long currentStockId = -1;
	HashMap<Double, Long> portPointMap;
	HashMap<Double, Long> trendPointMap;


	DecimalFormat df = new DecimalFormat("#0.0");


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.portfolio);

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


		new LoginUserTask().execute();

        findViewById(R.id.port_btn_port).setBackgroundResource(R.drawable.btn_portfolio_pressed);
	}

    @Override
    public void onResume() {
        super.onResume();

    }

	private class LoginUserTask extends AsyncTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			long credits = TrendsAPI.getInstance().login("FB"+facebookID, facebookName);
			return credits;
		}

		@Override
		protected void onPostExecute(Long credits) {
			Log.d(DEBUG, "logged in as "+facebookName);

			// update credits value in GUI
			DecimalFormat df = new DecimalFormat();
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setGroupingSeparator(',');
			df.setDecimalFormatSymbols(dfs);
			String creditsStr = df.format(credits);
			((TextView)findViewById(R.id.port_text_credits)).setText(creditsStr);
			((MyApplication) PortfolioActivity.this.getApplication()).credits = credits;

			new CheckStocksTask().execute();
		}

	}

	private class CheckStocksTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			TrendsAPI.getInstance().checkStocks("FB"+facebookID, PortfolioActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void credits) {
			LinearLayout ll = (LinearLayout)PortfolioActivity.this.findViewById(R.id.port_linear_trends);
			ll.removeAllViews();

			int size = ((MyApplication) PortfolioActivity.this.getApplication()).trendMap.size();
			pingCount = 0;

			for(Long stockId : ((MyApplication) PortfolioActivity.this.getApplication()).trendMap.keySet() ) {

				Trend t = ((MyApplication) PortfolioActivity.this.getApplication()).trendMap.get(stockId);
				String name = t.getName();
				long currentHourVal = t.getCurrentHourVal();
				double percent = t.getDayPercent();
				String picture = t.getPicture();
				View v = getLayoutInflater().inflate(R.layout.trenditem, null);
				((TextView)v.findViewById(R.id.trenditem_text_curval)).setText(currentHourVal+"");
				((TextView)v.findViewById(R.id.trenditem_text_name)).setText(name+"");
				((TextView)v.findViewById(R.id.trenditem_text_percent)).setText(df.format(percent)+"%");
				v.setTag(stockId);

				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						long vStockId = (Long)v.getTag();
						Log.d(DEBUG, "tag: "+vStockId);
						// TODO: go to trade screen
                        findViewById(R.id.port_btn_port).setBackgroundResource(R.drawable.btn_portfolio_normal);

						trendClicked(vStockId);
					}
				});

				v.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_DOWN) {
							v.findViewById(R.id.trenditem_rel_main).setBackgroundColor(getResources().getColor(R.color.blueselect));
							((TextView)v.findViewById(R.id.trenditem_text_name)).setTextColor(getResources().getColor(R.color.blueshine));
							((TextView)v.findViewById(R.id.trenditem_text_curval)).setTextColor(getResources().getColor(R.color.blueshine));
						}
						if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
							v.findViewById(R.id.trenditem_rel_main).setBackgroundColor(0xffffffff);
							((TextView)v.findViewById(R.id.trenditem_text_name)).setTextColor(getResources().getColor(R.color.dblue));
							((TextView)v.findViewById(R.id.trenditem_text_curval)).setTextColor(getResources().getColor(R.color.dblue));
						}

						return false;
					}
				});

				ll.addView(v);

				new PingAllStockTask(size).execute(stockId);
			}

		}

	}


	private class PingAllStockTask extends AsyncTask<Long, Void, Void> {

		int size;

		public PingAllStockTask(int size) {
			this.size = size;
		}

		@Override
		protected Void doInBackground(Long... stockId) {
			TrendsAPI.getInstance().pingStock(stockId[0], PortfolioActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void credits) {
			Log.d(DEBUG, "pingCount: "+pingCount);
			pingCount++;
			if(pingCount>=size) {
				Log.d(DEBUG, "done!");
				updatePortfolio(size);
			}
		}
	}

	public void updatePortfolio(int size) {
		HashMap<Double, Long> pointMap = new HashMap<Double, Long>();
		int totalNumStock = size;
		double totalTrend = 0.0;
		for(Long stockId : ((MyApplication) PortfolioActivity.this.getApplication()).trendMap.keySet() ) {
			Trend t = ((MyApplication) PortfolioActivity.this.getApplication()).trendMap.get(stockId);
			String hourlyPointList = t.getHourlyPointList();
			String hourlyPointListArr[] = hourlyPointList.split(";");
			totalTrend += t.getTrend();
			for(int i=0; i<hourlyPointListArr.length-1; i+=2) {
				int hour = Integer.parseInt(hourlyPointListArr[i]);
				long val = Long.parseLong(hourlyPointListArr[i+1]);
				Long oldVal = pointMap.get(round(hour));
				if(oldVal == null) {
					oldVal = (long) 0;
				}
				pointMap.put(round((double) hour), oldVal+val);
			}
		}

		double avgTrend = totalTrend/(size+0.0);

		Double lastHr = (double) 0;
		long lastVal = 0;
		for(Double hour : pointMap.keySet()) {
			double avg = (pointMap.get(round(hour))+0.0)/(totalNumStock+0.0);
			pointMap.put(round(hour), (long) avg);
			if(hour>lastHr) {
				lastHr = hour;
				lastVal = pointMap.get(round(hour));
			}
		}
		lastHr++;

		pointMap.put(round(lastHr), (long) (lastVal+avgTrend));
		Log.d(DEBUG, "printing point map from updatePortfolio");
		printPointMap(pointMap);

		new FakePointsTask(pointMap, R.id.chart, -1, 2, 0, 24).execute();
	}
	
	public void printPointMap(HashMap<Double, Long> pointMap) {
		ArrayList<Double> pointMapList = new ArrayList<Double>(pointMap.keySet());
		Collections.sort(pointMapList);
		
		for(Double d : pointMapList) {
			Log.d(DEBUG, "("+d+", "+pointMap.get(round(d))+")");
		}
	}

	// TODO: assumption is that pointMap has the future hour val
	private void graphPortfolio(HashMap<Double, Long> pointMap, int chartId, long stockId, int leftBound, int rightBound) {


		LinearLayout ll = (LinearLayout) findViewById(chartId);


		double finHr = 0;
		boolean hit = false;
		long highestVal = 0;
		for(double i=0; i<24; i++) {
			
			Long valAtHr = pointMap.get(round(i));
			
			if(valAtHr==null && hit==false) {
				continue;
			}
			else {
				hit = true;
			}
			
			
			if(valAtHr == null) {
				finHr = i-1;
				break;
			}
			else {
				if(valAtHr>highestVal) highestVal = valAtHr;
				Log.d(DEBUG, "("+i+", "+valAtHr+")");
			}
		}
		
		Log.d(DEBUG, "pointMap in graphPortfolio: "+pointMap);
		Log.d(DEBUG, "round(finHr): "+round(finHr));

		long prevVal;
		if(finHr>0) prevVal = pointMap.get(round(finHr-1));
		else prevVal = pointMap.get(round(finHr));

		long finVal = pointMap.get(round(finHr));

		long delta = finVal - prevVal;
		double percentChange = (finVal-prevVal+0.0)/(prevVal+0.0);

		String outDelta = "";
		if(delta>=0) {
			outDelta = "+"+delta;
		}
		else {
			outDelta = "-"+delta;
		}

		((TextView)findViewById(R.id.port_text_percent)).setText(outDelta+"  ("+df.format(percentChange)+"%)");



		GraphViewData[] tViews = new GraphViewData[2];
		//GraphViewData[] gViews = new GraphViewData[pointMap.keySet().size()];
		GraphViewData[] zViews = new GraphViewData[2];

		
		double chour = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);
		double cmin = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE);
		double csec = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.SECOND);
		double curTime = chour+(cmin/60)+(csec/3600);

		//ArrayList<Double> pointList = new ArrayList<Double>(pointMap.keySet());
		//Collections.sort(pointList);
		ArrayList<GraphViewData> graphViewList = new ArrayList<GraphView.GraphViewData>();
		double resolution = 1;
		
		double newLeft = leftBound;
		double newRight = rightBound;
		
		if(graphMode=='n') {
			resolution = 0.001;
			double interval = 10.0; // minutes
			
			for(double k=0; k<60; k+=interval) {
				if(cmin>k) {
					newLeft = (round(k/60.0))+leftBound;
					newRight = newLeft+(round(interval/60.0)+0.001);
				}
				else break;
			}
		}
		else if(graphMode=='d') {
			resolution = 0.1;
		}

		Log.d(DEBUG, "newLeft: "+newLeft+", newRight: "+newRight);

		double theCurVal = 0;
		for(double j=newLeft; j<curTime; j+=resolution) {
			double hour = j;
			try {
				theCurVal = pointMap.get(round(hour));
				Log.d(DEBUG, "hr:"+hour+", val:"+theCurVal);
				GraphViewData gv = new GraphViewData(hour, theCurVal);
				graphViewList.add(gv);
			}
			catch (NullPointerException e){
				Log.d(DEBUG, "can't find hr: "+hour);
			}
		}
	
		
		//printPointMap(pointMap);
		

		GraphViewData[] gViews = new GraphViewData[graphViewList.size()];
		graphViewList.toArray(gViews);

		Log.d(DEBUG, "gViews size: "+gViews.length);

		GraphViewData gv = new GraphViewData(newLeft, 0);
		zViews[0] = gv;
		GraphViewData gv2 = new GraphViewData(newRight, 0);
		zViews[1] = gv2;

		GraphViewData gv3 = new GraphViewData(newLeft, highestVal*1.5);
		tViews[0] = gv3;
		GraphViewData gv4 = new GraphViewData(newRight, highestVal*1.5);
		tViews[1] = gv4;
			

		GraphViewSeries topSeries = new GraphViewSeries(tViews);  
		GraphViewSeries trendSeries = new GraphViewSeries(gViews);  
		GraphViewSeries zeroSeries = new GraphViewSeries(zViews);  

		GraphView graphView = new LineGraphView(  
				this // context  
				, "Trend" // heading  
				);  

		graphView.addSeries(topSeries); // data
		graphView.addSeries(trendSeries); // data  
		graphView.addSeries(zeroSeries); // data  


		//graphView.setViewPort(stock.getPoints().get(stock.getPoints().size()-1).x-0.015, 0.015);  
		graphView.setScrollable(true);  
		// optional - activate scaling / zooming  
		graphView.setScalable(true);  


		// ((LineGraphView) graphView).setDrawBackground(true);

		ll.removeAllViews();
		ll.addView(graphView);
		
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setGroupingSeparator(',');
		df.setDecimalFormatSymbols(dfs);
		
		((TextView)findViewById(R.id.port_text_credits)).setText(df.format(theCurVal));
	}

	//TODO fake points algorithm. last point is virtual.
	private void fakePoints(HashMap<Double, Long> pointMap, long stockId, int depth) {


		for(double i=0; i<24; i++) {
			Long val0 = pointMap.get(round(i));
			
			if(val0==null) continue;
			
			Long val1 = pointMap.get(round(i+1));

			//Log.d(DEBUG, i+": "+val0+", "+(i+1)+": "+val1);
			if(val1 != null){
				fillPointMap(i, val0, i+1, val1, stockId, pointMap, 10, depth);
			}
		}
	}

	public void fillPointMap(double x1, long y1, double x2, long y2, long stockId, HashMap<Double, Long> pointMap, int end, int depth) {
		DecimalFormat hashFormat = new DecimalFormat("0000000000");
		String hashStr = x1+","+y1+","+(x2)+","+y2+","+stockId;
		long myHash = ((long)hashStr.hashCode()) + ((long)2147483647) + ((long)1);
		String myHashStr = hashFormat.format(myHash);
		Log.d(DEBUG, "hash: "+myHashStr+", depth:"+depth+", from "+x1+" to "+x2);

		double m = (y2 - y1+0.0)/(x2-x1+0.0); //slope
		long b = y1;

		//Log.d(DEBUG, "m="+m+", b="+b);

		double deltax = ((x2-x1)/(0.0+myHashStr.length()));
		double xpos = x1;
		for(int j=0; j<end; j++) {
			xpos += deltax;

			char c = myHashStr.charAt(j);
			long newVal = calcInTrend(c, m, b, deltax, depth);

			if(Math.abs(Math.round(xpos) - xpos) < 0.0001) {
				// do nothin
			}
			else {
				pointMap.put(round(xpos), newVal);
			}
			

			//Log.d(DEBUG, "added new point: ("+xpos+", "+pointMap.get(round(xpos))+")");

			int hournow = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);

			if(depth>0 && x1>=hournow) {
				//Log.d(DEBUG+"2", "at depth "+depth+", pointmap1: "+pointMap.get(round(xpos-deltax))+", pointmap2: "+pointMap.get(round(xpos)));
				fillPointMap(xpos-deltax, pointMap.get(round(xpos-deltax)), xpos, pointMap.get(round(xpos)), stockId, pointMap, 10, depth-1);
			}

		}

	}

	
	Timer trendTimer = new Timer();
	Timer portTimer = new Timer();
	public class FakePointsTask extends AsyncTask<Void, Void, Void> {

		HashMap<Double, Long> pointMap;
		long stockId;
		int chartId;
		int depth;
		int leftBound;
		int rightBound;

		public FakePointsTask(HashMap<Double, Long> pointMap, int chartId, long stockId, int depth, int leftBound, int rightBound) {
			this.pointMap = pointMap;
			this.stockId = stockId;
			this.chartId = chartId;
			this.depth = depth;
			this.leftBound = leftBound;
			this.rightBound = rightBound;
		}
		@Override
		protected Void doInBackground(Void... params) {
			fakePoints(pointMap, stockId, depth);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			Log.d(DEBUG, "done async fake!");
			Log.d(DEBUG, "pointMap: "+pointMap);
			
			if(stockId>=0) trendPointMap = pointMap;
			else portPointMap = pointMap;
			
			graphPortfolio(pointMap, chartId, stockId, leftBound, rightBound);
			
			/*
			if(stockId>=0) {
				trendTimer = new Timer();
				trendTimer.schedule(new UpdateGraphTask(stockId, leftBound, rightBound), 0, 3600);
			}
			else {
				portTimer = new Timer();
				portTimer.schedule(new UpdateGraphTask(stockId, leftBound, rightBound), 0, 3600);
			}*/
		}

	}

	public double round(double value) {

		DecimalFormat roundf = new DecimalFormat("#.###");
		return Double.parseDouble(roundf.format(value));
		//return (double)Math.round(value * 100000) / 100000;
	}

	public long calcInTrend(char c, double m, long b, double deltax, int depth){
		if(c=='0') {
			return (long) ((m*deltax + b) * 0.96);
		}
		else if(c=='1') {
			return (long) ((m*deltax + b) * 0.98);
		}
		else if(c=='2') {
			return (long) ((m*deltax + b) * 0.98);
		}
		else if(c=='3') {
			return (long) ((m*deltax + b) * 0.98);
		}
		else if(c=='4') {
			return (long) ((m*deltax + b) * 1.00);
		}
		else if(c=='5') {
			return (long) ((m*deltax + b) * 1.02);
		}
		else if(c=='6') {
			return (long) ((m*deltax + b) * 1.02);
		}
		else if(c=='7') {
			return (long) ((m*deltax + b) * 1.02);
		}
		else if(c=='8') {
			return (long) ((m*deltax + b) * 1.02);
		}
		else if(c=='9') {
			return (long) ((m*deltax + b) * 1.04);
		}
		else return -1;
	}
	String oldCredits;
	String oldDelta;
	String oldTopTitle;
	String oldBottomTitle;

	public void trendClicked(long stockId) {
		
		portTimer.cancel();
		
		dayTabClicked(null);
		
		currentStockId = stockId;
		Trend t = ((MyApplication) getApplication()).trendMap.get(stockId);

		portfolioScreen = false;
		oldCredits = ((TextView)findViewById(R.id.port_text_credits)).getText().toString();
		((TextView)findViewById(R.id.port_text_credits)).setText(t.getCurrentHourVal()+"");
		oldDelta = ((TextView)findViewById(R.id.port_text_percent)).getText().toString();
		long currentVal = t.getCurrentHourVal();
		long zeroVal = t.getZeroHourVal();
		double percent = (currentVal-zeroVal+0.0)/(zeroVal+0.0);
		((TextView)findViewById(R.id.port_text_percent)).setText(df.format(percent)+"%");
		oldTopTitle = ((TextView)findViewById(R.id.port_text_toptitle)).getText().toString();
		((TextView)findViewById(R.id.port_text_toptitle)).setText(t.getName()+"");
		oldBottomTitle = ((TextView)findViewById(R.id.port_text_bottomtitle)).getText().toString();
		((TextView)findViewById(R.id.port_text_bottomtitle)).setText("Top Shareholders");

		findViewById(R.id.port_linear_trends).setVisibility(View.GONE);
		findViewById(R.id.port_linear_trends2).setVisibility(View.VISIBLE);

		findViewById(R.id.chart).setVisibility(View.GONE);
		findViewById(R.id.chart2).setVisibility(View.VISIBLE);

		findViewById(R.id.port_rel_trend_bio).setVisibility(View.VISIBLE);

		graphTrend(stockId);
		new GetShareholderTask().execute(stockId);

		((TextView)findViewById(R.id.port_text_trendname)).setText(t.getName());
		((TextView)findViewById(R.id.port_text_trenddesc)).setText(t.getDesc());

	}

	public void portfolioClicked(View v) {
		currentStockId = -1;
		
		gotoTrendFromTrade();

		portfolioScreen = true;
		((TextView)findViewById(R.id.port_text_credits)).setText(oldCredits);
		((TextView)findViewById(R.id.port_text_percent)).setText(oldDelta);
		((TextView)findViewById(R.id.port_text_toptitle)).setText(oldTopTitle);
		((TextView)findViewById(R.id.port_text_bottomtitle)).setText(oldBottomTitle);

		findViewById(R.id.port_linear_trends).setVisibility(View.VISIBLE);
		findViewById(R.id.port_linear_trends2).setVisibility(View.GONE);

		findViewById(R.id.chart).setVisibility(View.VISIBLE);
		findViewById(R.id.chart2).setVisibility(View.GONE);

		findViewById(R.id.port_rel_trend_bio).setVisibility(View.GONE);

        findViewById(R.id.port_btn_port).setBackgroundResource(R.drawable.btn_portfolio_pressed);
	}
	
	public void friendsClicked(View v) {
		Intent i = new Intent(PortfolioActivity.this, FriendsActivity.class);
		startActivity(i);
	}

	public void graphTrend(long stockId) {
		Trend t = ((MyApplication) getApplication()).trendMap.get(stockId);
		String hourlyPointList = t.getHourlyPointList();
		String [] hourlyPointListArr = hourlyPointList.split(";");
		HashMap<Double, Long> pointMap = new HashMap<Double, Long>();
		for(int i=0; i<hourlyPointListArr.length-1; i+=2) {
			int hour = Integer.parseInt(hourlyPointListArr[i]);
			long val = Long.parseLong(hourlyPointListArr[i+1]);
			pointMap.put(round((double) hour), val);
		}

		int lastHr = (hourlyPointListArr.length-1)/2;
		long lastVal = Long.parseLong(hourlyPointListArr[hourlyPointListArr.length-1]);
		double trend = t.getTrend();

		lastHr++;
		pointMap.put(round((double) lastHr), (long) (lastVal + trend));

		new FakePointsTask(pointMap, R.id.chart2, stockId, 2, 0, 24).execute();

	}


	private class GetShareholderTask extends AsyncTask<Long, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Long... stockId) {
			JSONObject jo = TrendsAPI.getInstance().topShareholders(stockId[0]);
			return jo;
		}

		@Override
		protected void onPostExecute(JSONObject jo) {
			try {
				JSONArray ja = jo.getJSONArray("data");

				LinearLayout ll = (LinearLayout)PortfolioActivity.this.findViewById(R.id.port_linear_trends2);
				ll.removeAllViews();

				// TODO: sort in order!


				for(int i=0; i<ja.length(); i++) {
					JSONObject jao = ja.getJSONObject(i);
					String name = jao.getString("name");
					String userId = jao.getString("userId");
					int numShares = Integer.parseInt(jao.getString("numShares"));

					View v = getLayoutInflater().inflate(R.layout.shareholderitem, null);
					((TextView)v.findViewById(R.id.shareholderitem_text_name)).setText(name);
					((TextView)v.findViewById(R.id.shareholderitem_text_numShares)).setText(numShares+"");

                    String myTag = userId +";"+ name;

					v.setTag(myTag);

					v.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String vUserId = (String) v.getTag();
							Log.d(DEBUG, "tag: "+vUserId);
							// TODO: go to user profile

                            Intent i = new Intent(PortfolioActivity.this, ProfileActivity.class);
                            i.putExtra("id", vUserId);
                            Log.d(DEBUG, "Launching Portfolio Activity: "+vUserId);
                            startActivity(i);
						}
					});

					v.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if(event.getAction() == MotionEvent.ACTION_DOWN) {
								v.findViewById(R.id.trenditem_rel_main).setBackgroundColor(getResources().getColor(R.color.blueselect));
								((TextView)v.findViewById(R.id.shareholderitem_text_name)).setTextColor(getResources().getColor(R.color.blueshine));
								((TextView)v.findViewById(R.id.shareholderitem_text_numShares)).setTextColor(getResources().getColor(R.color.blueshine));
							}
							if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
								v.findViewById(R.id.trenditem_rel_main).setBackgroundColor(0xffffffff);
								((TextView)v.findViewById(R.id.shareholderitem_text_name)).setTextColor(getResources().getColor(R.color.dblue));
								((TextView)v.findViewById(R.id.shareholderitem_text_numShares)).setTextColor(getResources().getColor(R.color.dblue));
							}

							return false;
						}
					});

					ll.addView(v);

				}
			} catch (JSONException e) {
				Log.d(DEBUG, "update err: "+e.getMessage());
			}
		}
	}

	
	class UpdateGraphTask extends TimerTask {

		long stockId;
		int leftBound;
		int rightBound;

		public UpdateGraphTask(long stockId, int leftBound, int rightBound) {
			this.stockId = stockId;
			this.leftBound = leftBound;
			this.rightBound = rightBound;
		}

		public void run() {
			Log.d(DEBUG, "tick");
			runOnUiThread(new Runnable() {
				public void run() {
					
					if(graphMode=='n') {
						leftBound = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);
						rightBound = leftBound+2;
					}
					
					
					if(stockId>=0)
						graphPortfolio(trendPointMap, R.id.chart2, stockId, leftBound, rightBound);
					else 
						graphPortfolio(portPointMap, R.id.chart, stockId, leftBound, rightBound);
				}
			});

		}
	}

	@Override
	public void onBackPressed() {
		if(portfolioScreen) {
			super.onBackPressed();
		}
		else if(tradeScreen){
			gotoTrendFromTrade();
		}
		else {
			portfolioClicked(null);
			trendTimer.cancel();
		}
	}

	public void tradeButtonClicked(View v) {
		Log.d(DEBUG, "trade button clicked");
		tradeScreen = true;
		portfolioScreen = false;
		findViewById(R.id.port_rel_trend_trade).setVisibility(View.VISIBLE);
		//findViewById(R.id.port_rel_trends).setVisibility(View.GONE);

		
		
		// TODO SHARES OWNED!
		long credits = ((MyApplication) PortfolioActivity.this.getApplication()).credits;
		((TextView)findViewById(R.id.port_text_credits_mycredits_num)).setText(credits+"");


		EditText et = ((EditText)findViewById(R.id.port_edit_shares));
		et.setText("");
		TextWatcher stw = new ShareTextWatcher(currentStockId);
		et.addTextChangedListener(stw);
	}

	public void gotoTrendFromTrade() {
		tradeScreen = false;
		portfolioScreen = false;
		findViewById(R.id.port_rel_trend_trade).setVisibility(View.GONE);
		//findViewById(R.id.port_rel_trends).setVisibility(View.VISIBLE);
	}

	public class ShareTextWatcher implements TextWatcher {

		long stockId;
		public ShareTextWatcher(long stockId) {
			this.stockId = stockId;
		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				Log.d(DEBUG, "textwatcher:"+s.toString());
				int shares = Integer.parseInt(s.toString());
				Trend t = ((MyApplication) PortfolioActivity.this.getApplication()).trendMap.get(stockId);
				long cost = (shares*t.getCurrentVal());

				((TextView)findViewById(R.id.port_text_total_num)).setText(cost+"");
			}
			catch (Exception e){
				Log.d(DEBUG, "textwatcher err: "+e.getMessage());
				if(s.toString().equals("")) {
					((TextView)findViewById(R.id.port_text_total_num)).setText(0+"");
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

	}


	public void nowTabClicked(View v) {
		findViewById(R.id.port_text_now).setBackgroundResource(R.drawable.now_selected);
		findViewById(R.id.port_text_day).setBackgroundResource(R.drawable.day_0);
		findViewById(R.id.port_text_week).setBackgroundResource(R.drawable.week_0);
		findViewById(R.id.port_text_all).setBackgroundResource(R.drawable.all_0);
		graphMode = 'n';
		
		int chartId;
		if(findViewById(R.id.chart).getVisibility() == View.VISIBLE) {
			chartId = R.id.chart;
		}
		else chartId = R.id.chart2;
		
		//Trend t = ((MyApplication) getApplication()).trendMap.get(currentStockId);
		//long y1 = t.getCurrentHourVal();
		//long y2 = (long) (y1+t.getTrend());	
		
		double x1 = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);
		double x2 = x1+1;
		

		//HashMap<Double, Long> pointMap = new HashMap<Double, Long>();
		
		//pointMap.put(round(x1), y1);
		//pointMap.put(round(x2), y2);

		//new FakePointsTask(pointMap, chartId, currentStockId, 2, (int)x1, (int)x2+1).execute();
		
		if(currentStockId >= 0)
			graphPortfolio(trendPointMap, chartId, currentStockId, (int)x1, (int)x2+1);
		else
			graphPortfolio(portPointMap, chartId, currentStockId, (int)x1, (int)x2+1);
	}

	public void dayTabClicked(View v) {
		findViewById(R.id.port_text_now).setBackgroundResource(R.drawable.now_1);
		findViewById(R.id.port_text_day).setBackgroundResource(R.drawable.day_selected);
		findViewById(R.id.port_text_week).setBackgroundResource(R.drawable.week_1);
		findViewById(R.id.port_text_all).setBackgroundResource(R.drawable.all_1);
		
		graphMode = 'd';
		
		
		int chartId;
		if(findViewById(R.id.chart).getVisibility() == View.VISIBLE) {
			chartId = R.id.chart;
		}
		else chartId = R.id.chart2;
		
		if(currentStockId >= 0)
			graphPortfolio(trendPointMap, chartId, currentStockId, 0, 24);
		else
			graphPortfolio(portPointMap, chartId, currentStockId, 0, 24);
	}
	
	public void weekTabClicked(View v) {
		findViewById(R.id.port_text_now).setBackgroundResource(R.drawable.now_2);
		findViewById(R.id.port_text_day).setBackgroundResource(R.drawable.day_2);
		findViewById(R.id.port_text_week).setBackgroundResource(R.drawable.week_selected);
		findViewById(R.id.port_text_all).setBackgroundResource(R.drawable.all_2);
	}

	public void allTabClicked(View v) {
		Log.d(DEBUG+"2", "clicked");
		findViewById(R.id.port_text_now).setBackgroundResource(R.drawable.now_3);
		findViewById(R.id.port_text_day).setBackgroundResource(R.drawable.day_3);
		findViewById(R.id.port_text_week).setBackgroundResource(R.drawable.week_3);
		findViewById(R.id.port_text_all).setBackgroundResource(R.drawable.all_selected);
	}

	public void buyClicked(View v) {
		
	}
	
	public void sellClicked(View v) {
		
	}
	
}