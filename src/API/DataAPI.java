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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DataAPI {
	private static DataAPI dataAPI;
	private final String APIURL = "http://api.trend.swithfriends.com"; 
	final static String APILEVEL = "/1.0";
	final static String DEBUG = "DataAPI";
	

	final static boolean DEBUG_MODE = true;

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
	 * @return POST result (0 = success), (1 = no content, already exists)
	 */
	public int usersPOST(String facebookID, String facebookName) {
		// TODO: change back to normal after testing
		if(!DEBUG_MODE) {
			return 0; // success	
		}
		else {
			JSONObject jo = new JSONObject();
			JSONObject joNest = new JSONObject();
			try {
				joNest.put("facebook_id", facebookID);
				joNest.put("name", facebookName);
				jo.put("user", joNest);
			} catch (JSONException e) {	}
			
			//postMap.put("facebook_id", facebookID);
			//postMap.put("name", facebookName);

			String result = doPOST(APIURL+"/api"+APILEVEL+"/users", jo);
			return 0; // TODO: fix this
		}
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

		if(!DEBUG_MODE) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("created_at", "2012-03-25T00:24:45Z");
				jo.put("credits", 999);
				jo.put("updated_at", "2012-03-25T00:24:45Z");
			}
			catch (Exception e) {};
			return jo;
		}
		else {
			String result = doGET(APIURL+"/api"+APILEVEL+"/users/"+facebookID);
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d("DataAPI", "usersGET err: "+e.getMessage());
				return null;
			}
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

		if(!DEBUG_MODE) {
			JSONObject jo = new JSONObject();
			try {
				JSONArray ja = new JSONArray();
				JSONObject jao1 = new JSONObject();
				jao1.put("name", "Ron Paul");
				jao1.put("id", 1);
				jao1.put("opening_price", 50);
				jao1.put("current_price", 60);
				jao1.put("purchase_price", 55);
				ja.put(jao1);
				JSONObject jao3 = new JSONObject();
				jao3.put("name", "LA Lakers Basketball Team");
				jao3.put("id", 3);
				jao3.put("opening_price", 200);
				jao3.put("current_price", 311);
				jao3.put("purchase_price", 151);
				ja.put(jao3);
				JSONObject jao5 = new JSONObject();
				jao5.put("name", "iPhone 5");
				jao5.put("id", 4);
				jao5.put("opening_price", 400);
				jao5.put("current_price", 500);
				jao5.put("purchase_price", 430);
				ja.put(jao5);
				jo.put("stock", ja);
			}
			catch (Exception e) {};
			return jo;
		}
		else {
			Log.d(DEBUG, "trying to do GET Portfolio: "+APIURL+"/api"+APILEVEL+"/portfolio/"+facebookID);
			String result = doGET(APIURL+"/api"+APILEVEL+"/portfolios/"+facebookID);
			Log.d(DEBUG, "success, result is: "+result);
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d("DataAPI", "portfolioGET err: "+e.getMessage());
				return null;
			}
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
	public JSONObject portfolioPOST(String facebookID, String stockID, int quantity) {

		if(DEBUG_MODE) {
			return null; // TODO: fix this
		}
		else {
			HashMap postMap = new HashMap();
			postMap.put("id", stockID);
			postMap.put("quantity", quantity);

			String result = doPOST(APIURL+"/api"+APILEVEL+"/portfolios/"+facebookID+"/sell", postMap);
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d(DEBUG, "portfolio POST: "+e.getMessage());
				return null;
			}
		}
		
		
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
	 *  { "id": 432890432890, "stock_id": 12, "price": 444, "shares": 9 }
	 * HTTP 404 <br>
	 * If the stock doesn’t exist <br>
	 * HTTP 403 <br>
	 * If the user doesn’t have enough credits or not enough shares on the market <br>
	 * @param facebookID
	 * @param stockID
	 * @param quantity
	 * @return POST result
	 */
	public JSONObject marketPOST(String facebookID, int stockID, int quantity) {
		
		if(!DEBUG_MODE) {
			return null; 
		}
		else {
			
			String result = "[default]";
			
			try {
				JSONObject joPOST = new JSONObject();
				
				JSONObject joELS = new JSONObject();
				joELS.put("stock_id", stockID);
				joELS.put("quantity", quantity);
				joELS.put("facebook_id", facebookID);
				
				joPOST.put("order", joELS);
				
				Log.d(DEBUG, "POSTing to "+APIURL+"/api"+APILEVEL+"/market/"+stockID+"/buy");
				Log.d(DEBUG, "with data "+joPOST.toString());
				
				result = doPOST(APIURL+"/api"+APILEVEL+"/market/"+stockID+"/buy", joPOST);
				
				Log.d(DEBUG, "success! result: "+result);
			}
			catch (Exception e) {
				Log.d(DEBUG, "json market post err: "+e.getMessage());
			}
			
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d(DEBUG, "market POST: "+e.getMessage());
				return null;
			}
		}
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
		
		if(!DEBUG_MODE) {
			JSONObject jo = new JSONObject();
			try {
				JSONArray ja = new JSONArray();
				JSONObject jao1 = new JSONObject();
				jao1.put("name", "Ron Paul");
				jao1.put("id", 1);
				jao1.put("opening_price", 50);
				jao1.put("current_price", 60);
				jao1.put("purchase_price", 55);
				ja.put(jao1);
				JSONObject jao2 = new JSONObject();
				jao2.put("name", "Charlie Sheen");
				jao2.put("id", 2);
				jao2.put("opening_price", 9);
				jao2.put("current_price", 5);
				jao2.put("purchase_price", 8);
				ja.put(jao2);
				JSONObject jao3 = new JSONObject();
				jao3.put("name", "Red Sox");
				jao3.put("id", 3);
				jao3.put("opening_price", 200);
				jao3.put("current_price", 311);
				jao3.put("purchase_price", 151);
				ja.put(jao3);
				JSONObject jao4 = new JSONObject();
				jao4.put("name", "Taylor Swift");
				jao4.put("id", 4);
				jao4.put("opening_price", 100);
				jao4.put("current_price", 107);
				jao4.put("purchase_price", 120);
				ja.put(jao4);
				JSONObject jao5 = new JSONObject();
				jao5.put("name", "iPhone 5");
				jao5.put("id", 4);
				jao5.put("opening_price", 400);
				jao5.put("current_price", 500);
				jao5.put("purchase_price", 430);
				ja.put(jao5);
				jo.put("stock", ja);
			}
			catch (Exception e) {};
			return jo;
		}
		else {
			String result = doGET(APIURL+"/api"+APILEVEL+"/market");
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d("DataAPI", "marketGET err: "+e.getMessage());
				return null;
			}
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
	public JSONObject performanceGET(int stockID) {
		
		if(DEBUG_MODE) {
			JSONObject jo = new JSONObject();
			try {
				if(stockID==1) {
					
				}
				jo.put("graph", "daily");
				JSONArray ja = new JSONArray();
				
				if(stockID==1) {
					JSONObject jao1 = new JSONObject();
					jao1.put("date", 100);
					jao1.put("value", 300);
					ja.put(jao1);
					
					JSONObject jao2 = new JSONObject();
					jao2.put("date", 105);
					jao2.put("value", 305);
					ja.put(jao2);
					
					JSONObject jao3 = new JSONObject();
					jao3.put("date", 110);
					jao3.put("value", 310);
					ja.put(jao3);
					
					JSONObject jao4 = new JSONObject();
					jao4.put("date", 130);
					jao4.put("value", 280);
					ja.put(jao4);
					
					JSONObject jao5 = new JSONObject();
					jao5.put("date", 140);
					jao5.put("value", 290);
					ja.put(jao5);
					
					JSONObject jao6 = new JSONObject();
					jao6.put("date", 145);
					jao6.put("value", 275);
					ja.put(jao6);
				}
				else {
					JSONObject jao1 = new JSONObject();
					jao1.put("date", 100);
					jao1.put("value", 305);
					ja.put(jao1);
					
					JSONObject jao2 = new JSONObject();
					jao2.put("date", 105);
					jao2.put("value", 300);
					ja.put(jao2);
					
					JSONObject jao3 = new JSONObject();
					jao3.put("date", 110);
					jao3.put("value", 315);
					ja.put(jao3);
					
					JSONObject jao4 = new JSONObject();
					jao4.put("date", 130);
					jao4.put("value", 275);
					ja.put(jao4);
					
					JSONObject jao5 = new JSONObject();
					jao5.put("date", 140);
					jao5.put("value", 295);
					ja.put(jao5);
					
					JSONObject jao6 = new JSONObject();
					jao6.put("date", 145);
					jao6.put("value", 270);
					ja.put(jao6);
				}
				
				jo.put("values", ja);
			}
			catch (Exception e) {}
			return jo;
		}
		else {
			String result = doGET(APIURL+"/api"+APILEVEL+"/performance/"+stockID+"/daily");
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d("DataAPI", "performanceGET err: "+e.getMessage());
				return null;
			}
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
		
		if(DEBUG_MODE) {
			JSONObject jo = new JSONObject();
			try {
				JSONArray ja = new JSONArray();
				
				JSONObject jao1 = new JSONObject();
				jao1.put("name", "Charles Feduke");
				jao1.put("net", 300);
				ja.put(jao1);
				
				JSONObject jao2 = new JSONObject();
				jao2.put("name", "Nishant Shukla");
				jao2.put("value", 9001);
				ja.put(jao2);
				
				JSONObject jao3 = new JSONObject();
				jao2.put("name", "David Kapp");
				jao2.put("value", 200);
				ja.put(jao3);
				
				JSONObject jao4 = new JSONObject();
				jao2.put("name", "Greta N");
				jao2.put("value", 400);
				ja.put(jao4);
				
				JSONObject jao5 = new JSONObject();
				jao2.put("name", "Pete Blair");
				jao2.put("value", 500);
				ja.put(jao5);
				
				JSONObject jao6 = new JSONObject();
				jao2.put("name", "Phil Conein");
				jao2.put("value", 600);
				ja.put(jao6);
				
				jo.put("investors", ja);
			}
			catch (Exception e) {}
			return jo;
		}
		else {
			String result = doGET(APIURL+"/api"+APILEVEL+"/leaderboard");
			try {
				JSONObject jo = new JSONObject(result);
				return jo;
			} catch (JSONException e) {
				Log.d("DataAPI", "leaderboardGET err: "+e.getMessage());
				return null;
			}
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
			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Content-type", "application/json");
			String response = httpclient.execute(httpget, responseHandler);

			return response;
		}
		catch (Exception e) {
			Log.d("DataAPI", "doGET err: "+e.getMessage());
			return null;
		}
	}

	
	public static String doPOST(String path, JSONObject holder) {
		try {
			Log.d(DEBUG, "in doPOST, path is "+path+", with data "+holder.toString());
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(path);
			
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
			Log.d(DEBUG, "success, the response is: "+response);
			return response;
		}
		catch (Exception e) {
			Log.d("DataAPI", "doPOST err: "+e.getMessage());
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
