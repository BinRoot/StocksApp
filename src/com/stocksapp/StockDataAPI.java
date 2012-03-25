package com.stocksapp;

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

import android.util.Log;

public class StockDataAPI {
	private static StockDataAPI stockeAPI;

	public static StockDataAPI getInstance() {
		if (stockeAPI == null) {
			stockeAPI = new StockDataAPI();
		}
		return stockeAPI;
	}
	
	// 03-24 21:18:56.144: D/API(1496): result: {"created_at":"2012-03-25T01:18:56Z","credits":1000,"facebook_id":"123456","id":4,"updated_at":"2012-03-25T01:18:56Z"}

	public String postID(String facebook_id) {
		HashMap<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("facebook_id", facebook_id);
		
		String result = postData("http://furious-fire-1313.herokuapp.com/users", attrMap);
		Log.d("API", "result: "+result);
		
		return result;
	}
	
	
	// {"created_at":"2012-03-25T01:18:56Z","credits":1000,"facebook_id":"123456","updated_at":"2012-03-25T01:18:56Z"}
	
	public String getID(String facebook_id) {
		//HashMap<String, String> attrMap = new HashMap<String, String>();
		//attrMap.put("user", facebook_id);
		
		String result = getData("http://furious-fire-1313.herokuapp.com/users/"+facebook_id);
		Log.d("API", "result: "+result);
		
		return result;
	}
	
	public String postData(String url, HashMap<String, String> attributes) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(attributes.size());
	        for(String key : attributes.keySet()) {
	        	nameValuePairs.add(new BasicNameValuePair(key, attributes.get(key)));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
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
	    try 
	    {
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
	    catch(Exception e)
	    {
	       Log.e("API", "biffed it getting HTTPResponse");
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
