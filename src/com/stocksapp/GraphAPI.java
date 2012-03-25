package com.stocksapp;

import java.util.ArrayList;
import org.json.JSONObject;
import android.graphics.PointF;


public class GraphAPI {
	private JSONObject pairs;
	private float offset = .10f;
	ArrayList<PointF> parsedPairs; 
	static GraphAPI graphAPI;
	
	public static GraphAPI getInstance() {
		if(graphAPI == null) {
			graphAPI = new GraphAPI();
		}
		return graphAPI;
	}
	
	public void setParsedPair(ArrayList<PointF> parsedPairs) {
		this.parsedPairs = parsedPairs;
	}
	
	public String[] getVarLabels(){
		String[] result = new String[8];
		float maxY = 0;

		for(PointF p : parsedPairs)
			if(p.y > maxY)
				maxY = p.y;

		float spacing = (maxY + offset*maxY)/8;

		for(int i = 0; i < 8; i++)
			result[i] = "" + (maxY + offset*maxY - spacing*i);

		return result;
	}

	public String[] getHorLabels(){
		//Returns all x coordinates for now, fix later
		String[] result = new String[parsedPairs.size()];
		int i = 0;
		for(PointF p : parsedPairs){
			result[i] = "" + p.x;
			i++;
		}

		return result;
	}

	public float[] getValues(){
		//Returns all x coordinates for now, fix later
		float[] result = new float[parsedPairs.size()];
		int i = 0;
		for(PointF p : parsedPairs){
			result[i] = p.y;
			i++;
		}

		return result;
	}

}
