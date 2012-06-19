package API;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stocksapp.MyApplication;
import com.stocksapp.PortfolioActivity;

import Model.Trend;
import android.app.Activity;
import android.util.Log;

public class TrendsAPI {
	private static TrendsAPI trendsAPI;
	public static TrendsAPI getInstance() {
		if (trendsAPI == null) {
			trendsAPI = new TrendsAPI();
		}
		return trendsAPI;
	}
	
	String DEBUG = "TrendsAPI";
	String APIurl = "http://trendsapi.appspot.com";
	
	public long login(String userId, String userName) {
		HashMap<String, String> attrs = new HashMap<String, String>();
		attrs.put("id", userId);
		attrs.put("name", userName);
		String result = postData(APIurl+"/user", attrs);
		return Long.parseLong(result);
	}
	
	// {data:[{"name":"Android","currentHourVal":50,"zeroHourVal":50,"numShares":3,"trend":0.0,"picture":"pic01"}, {}]}
	public void checkStocks(String userId, Activity a) {
		Log.d(DEBUG, "checkStocks started");
		HashMap<String, String> attrs = new HashMap<String, String>();
		attrs.put("userId", userId);
		attrs.put("op", "r");
		String result = postData(APIurl+"/stock", attrs);
		Log.d(DEBUG, "result: "+result);
		try {
			JSONObject jo = new JSONObject(result);
			JSONArray ja = jo.getJSONArray("data");
			for(int i=0; i<ja.length(); i++) {
				JSONObject joa = ja.getJSONObject(i);
				String name = joa.getString("name");
				long id = joa.getLong("id");
				long currentHourVal = joa.getLong("currentHourVal");
				long zeroHourVal = joa.getLong("zeroHourVal");
				long numShares = joa.getLong("numShares");
				double trend = joa.getDouble("trend");
				String picture = joa.getString("picture");
				
				Log.d(DEBUG, "-- "+name);
				
				Trend t = ((MyApplication) a.getApplication()).trendMap.get(id);
				if(t==null) t = new Trend();
				t.setName(name);
				t.setId(id);
				t.setCurrentHourVal(currentHourVal);
				t.setZeroHourVal(zeroHourVal);
				t.setNumShares(numShares);
				t.setTrend(trend);
				t.setPicture(picture);
				((MyApplication) a.getApplication()).trendMap.put(id, t);
			}
		} catch (JSONException e) {
			Log.d(DEBUG,"checkStocks err: "+e.getMessage());
		}
	}
	
	public void pingStock(long stockId, Activity a) {
		Log.d(DEBUG, "pingStocks started");
		HashMap<String, String> attrs = new HashMap<String, String>();
		attrs.put("stockId", stockId+"");
		String result = postData(APIurl+"/ping", attrs);
		Log.d(DEBUG, "ping result: "+result);
		try {
			JSONObject jo = new JSONObject(result);
			
			String hourlyPointList = jo.getString("pointList");
			long currentHourVal = jo.getLong("currentHourVal");
			double trend = jo.getDouble("trend");
			
			Trend t = ((MyApplication) a.getApplication()).trendMap.get(stockId);
			if(t==null) t = new Trend();
			t.setHourlyPointList(hourlyPointList);
			t.setCurrentHourVal(currentHourVal);
			t.setTrend(trend);
			((MyApplication) a.getApplication()).trendMap.put(stockId, t);
		}
		catch(Exception e) {
			Log.d(DEBUG, "ping err: "+e.getMessage());
		}
	}
	
	
	public JSONObject topShareholders(long stockId) {
		Log.d(DEBUG, "pingStocks started");
		HashMap<String, String> attrs = new HashMap<String, String>();
		attrs.put("stockId", stockId+"");
		String result = postData(APIurl+"/leaderboard", attrs);
		Log.d(DEBUG, "ping result: "+result);
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		}
		catch(Exception e) {
			Log.d(DEBUG, "topshareholder err: "+e.getMessage());
		}
		
		return null;
	}
	
	public JSONObject getFriends(String userId) {
		Log.d(DEBUG, "getFriends started");
		HashMap<String, String> attrs = new HashMap<String, String>();
		attrs.put("userId", userId);
		String result = postData(APIurl+"/leaderboard", attrs);
		Log.d(DEBUG, "ping result: "+result);
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		}
		catch(Exception e) {
			Log.d(DEBUG, "getfriends err: "+e.getMessage());
		}
		
		return null;
	}
	
	
	
	public String postData(String url, HashMap<String, String> attributes) {
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(attributes.size());
	        for(String key : attributes.keySet()) {
	        	nameValuePairs.add(new BasicNameValuePair(key, attributes.get(key)));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        HttpResponse response = httpclient.execute(httppost);
	        String responseBody = EntityUtils.toString(response.getEntity());
	        return responseBody;
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }

	    return null;
	} 


	private String getData(String urlStr)
	{
	    InputStream is = null;
	    try {
	    	URL connectURL = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

	        is = conn.getInputStream(); 
	        // scoop up the reply from the server
	        int ch; 
	        StringBuffer sb = new StringBuffer(); 
	        while( ( ch = is.read() ) != -1 ) { 
	            sb.append( (char)ch ); 
	        } 
	        return sb.toString(); 
	    }
	    catch(Exception e) {
	       Log.e(DEBUG, "biffed it getting HTTPResponse");
	    }
	    finally 
	    {
	        try {
	        if (is != null)
	            is.close();
	        } catch (Exception e) {}
	    }

	    return "";
	}
}
