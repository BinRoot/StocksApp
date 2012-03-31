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
import org.json.JSONObject;

import android.util.Log;

public class DataAPI {
	private static DataAPI dataAPI;
	private final String myURL = "http://mycompanyAPI.com"; 

	public static DataAPI getInstance() {
		if (dataAPI == null) {
			dataAPI = new DataAPI();
		}
		return dataAPI;
	}
	
	
	
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
		    
		    Iterator iter = params.entrySet().iterator();
	
		    JSONObject holder = new JSONObject();
		    
		    while(iter.hasNext()) {
		        Map.Entry pairs = (Map.Entry)iter.next();
		        String key = (String)pairs.getKey();
		        Map m = (Map)pairs.getValue();   
	
		        JSONObject data = new JSONObject();
	
		        Iterator iter2 = m.entrySet().iterator();
		        while(iter2.hasNext()) {
		            Map.Entry pairs2 = (Map.Entry)iter2.next();
		            data.put((String)pairs2.getKey(), (String)pairs2.getValue());
		        }
	
		        holder.put(key, data);
		    }
	
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
}
