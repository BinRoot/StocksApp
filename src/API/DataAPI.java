package API;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DataAPI {
	private static DataAPI dataAPI;
	private final String APIURL = "http://mycompanyAPI.com"; 
	final static String DEBUG = "DataAPI";

	public static DataAPI getInstance() {
		if (dataAPI == null) {
			dataAPI = new DataAPI();
		}
		return dataAPI;
	}
	
	/**
	 * <b>POST</b> /api/users <br>
	 * <b>Client</b>: JSON { “facebook_id”:”blah”, “name”:”their facebook name” } <br>
	 * <b>Server</b>: 
	 *	Creates a user record only if the user does not exist <br>
	 *	Assigns the new user record a set # of credits <br>
	 *	HTTP 201 (created) <br>
	 *	HTTP 204 (no content; already exists) <br>
	 *	#HTTP 403 (forbidden) JSON { “code”:some_integer, “message”:”some message” } <br>
	 * @param facebookID
	 * @param facebookName
	 * @return POST result
	 */
	public String usersPOST(String facebookID, String facebookName) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("facebook_id", facebookID);
		postMap.put("name", facebookName);
		
		return doPOST(APIURL+"/api/users", postMap);
	}
	
	/**
	 * <b>GET</b> /api/users/:facebook_id <br>
	 * <b>Client</b>: query param facebook_id literally substituted for :facebook_id in the URL above <br>
	 * <b>Server</b>: <br>
	 * HTTP 200 <br>
	 * {"created_at":"2012-03-25T00:24:45Z","credits":1000,"updated_at":"2012-03-25T00:24:45Z"} <br>
	 * HTTP 404, empty <br>
	 * when user does not exist
	 * 
	 * @param facebookID
	 * @return GET result {"created_at":"2012-03-25T00:24:45Z","credits":1000,"updated_at":"2012-03-25T00:24:45Z"}
	 */
	public JSONObject usersGET(String facebookID) {
		String result = doGET(APIURL+"/api/users/"+facebookID);
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		} catch (JSONException e) {
			Log.d("DataAPI", "usersGET err: "+e.getMessage());
			return null;
		}
	}
	
	
	
	/**
	 * <b>GET</b> /api/portfolio/:facebook_id <br>
	 * <b>Client</b>:  querystring parameter <br>
	 * <b>Server</b>: <br>
	 * HTTP 200 <br>
	 * Array of Stock structures: <br>
	 * Stock: name, id, opening_price, current_price, purchase_price <br>
	 * @param facebookID 
	 * @return GET result
	 */
	public JSONObject portfolioGET(String facebookID) {
		String result = doGET(APIURL+"/api/portfolio/"+facebookID);
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		} catch (JSONException e) {
			Log.d("DataAPI", "portfolioGET err: "+e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * <b>POST</b> /api/portfolio/:facebook_id/sell <br>
	 * <b>Client</b>: sell stocks they own <br>
	 * SellOrder structure < Order <br>
	 * { “id”:12, “quantity”:44 } <br>
	 * <b>Server</b>:
	 * HTTP 200 <br>
	 * Transaction structure <br>
	 * id, stock_id, price, shares <br>
	 * HTTP 404 <br>
	 * If the user doesn’t have that stock <br>
	 * HTTP 403 <br>
	 * If the user doesn’t have sufficient stock <br>
	 * @param facebookID
	 * @param stockID
	 * @param quantity
	 * @return POST result
	 */
	public String portfolioPOST(String facebookID, String stockID, int quantity) {
		HashMap postMap = new HashMap();
		postMap.put("id", stockID);
		postMap.put("quantity", quantity);
		// test if this properly converts to JSON
		
		//TODO: turn into JSON?
		return doPOST(APIURL+"/api/portfolio/"+facebookID+"/sell", postMap);
	}
	
	/**
	 * <b>POST</b> /api/market/:stock_id/buy <br>
	 * <b>Client</b>: buy stocks available on the market <br>
 	 * BuyOrder structure < Order <br>
	 * { “id”:12, “quantity”:44, “facebook_id”:”blah” } <br>
	 * <b>Server</b>: <br>
	 * HTTP 200 <br>
	 * Transaction <br>
	 * id, stock_id, price, shares <br>
	 * HTTP 404 <br>
	 * If the stock doesn’t exist <br>
	 * HTTP 403 <br>
	 * If the user doesn’t have enough credits or not enough shares on the market <br>
	 * @param facebookID
	 * @param stockID
	 * @param quantity
	 * @return POST result
	 */
	public String marketPOST(String facebookID, String stockID, int quantity) {
		HashMap postMap = new HashMap();
		postMap.put("id", stockID);
		postMap.put("quantity", quantity);
		postMap.put("facebookID", facebookID);
		// test if this properly converts to JSON
		
		//TODO: turn into JSON?
		return doPOST(APIURL+"/api/market/"+stockID+"/buy", postMap);
	}
	
	/**
	 * <b>GET</b> /api/market <br>
	 * <b>Client</b>: wants a list of all the available stocks <br>
	 * <b>Server</b>: <br>
	 * HTTP 200 <br>
	 * { “stocks”:[ { “id”:492, “name”:”blah”, “current_price”:444}, … ] } <br>
	 * @return GET result JSONObject 
	 */
	public JSONObject marketGET() {
		String result = doGET(APIURL+"/api/market");
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		} catch (JSONException e) {
			Log.d("DataAPI", "marketGET err: "+e.getMessage());
			return null;
		}
	}
	
	/**
	 * <b>GET</b> /api/performance/:stock_id/daily <br>
	 * <b>Client</b>: wants the performance of a stock for the previous 24 hours <br>
	 * <b>Server</b>: <br>
	 * HTTP 200 <br>
	 * Array with 0..24 values with hourly averages for the stock <br>
	 * { “graph”:”daily”, “values”:[ { “date”, epoch_millis_since_epoch, “value”: 4400 }, …]} <br>
	 * HTTP 404 <br>
	 * Stock doesn’t exist <br>
	 * @param stockID
	 * @return GET result JSONObject
	 */
	public JSONObject performanceGET(String stockID) {
		String result = doGET(APIURL+"/api/performance/"+stockID+"/daily");
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		} catch (JSONException e) {
			Log.d("DataAPI", "performanceGET err: "+e.getMessage());
			return null;
		}
	}
	
	/**
	 * <b>GET</b> /api/leaderboard <br>
	 * <b>Client</b>: wants the leaderboard <br>
	 * <b>Server</b>: this returns all the users and their net worth <br>
	 * { “investors”: [ { “name”:”Charles Feduke”, “net”:3200 }, … ] }
	 * @return GET result JSONObject
	 */
	public JSONObject leaderboardGET() {
		String result = doGET(APIURL+"/api/leaderboard");
		try {
			JSONObject jo = new JSONObject(result);
			return jo;
		} catch (JSONException e) {
			Log.d("DataAPI", "leaderboardGET err: "+e.getMessage());
			return null;
		}
	}
	
	
	
	
	/*
	 * general HTTP GET and POST methods below
	 */
	
	public static String doGET(String path) {
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(path);
			ResponseHandler responseHandler = new BasicResponseHandler();
		    String response = httpclient.execute(httpget, responseHandler);
		    return response;
		}
		catch (Exception e) {
			Log.d("DataAPI", "doGET err: "+e.getMessage());
			return null;
		}
	}
	
	public static String doPOST(String path, Map params) {
		try {
		    DefaultHttpClient httpclient = new DefaultHttpClient();
		    HttpPost httpost = new HttpPost(path);
		    
		    JSONObject holder = mapToJSON(params);
	
		    //passes the results to a string builder/entity
		    StringEntity se = new StringEntity(holder.toString());
	
		    //sets the post request as the resulting string
		    httpost.setEntity(se);
		    //sets a request header so the page receving the request will know what to do with it
		    httpost.setHeader("Accept", "application/json");
		    httpost.setHeader("Content-type", "application/json");
	
		    //Handles what is returned from the page 
		    ResponseHandler responseHandler = new BasicResponseHandler();
		    String response = httpclient.execute(httpost, responseHandler);
		    return response;
		}
		catch (Exception e) {
			Log.d("DataAPI", "doPOST err: "+e.getMessage());
			return null;
		}
	}
	
	public static JSONObject mapToJSON(Map params) {
		try {
			JSONObject holder = new JSONObject();
			for(Object key : params.keySet()) {
				holder.put(key.toString(), params.get(key));
			}
		    return holder;
		}
		catch (Exception e) {
			Log.d("DataAPI", "doPOST err: "+e.getMessage());
			return null;
		}
	}
}
