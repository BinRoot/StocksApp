package com.stocksapp;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StockPage extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stockmain);


		String firstName = (String) getIntent().getExtras().get("firstName");
		String id = (String) getIntent().getExtras().get("id");

		
		LinearLayout ll = (LinearLayout) findViewById(R.id.chart);
		
		float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
		String[] verlabels = new String[] { "2", "1", "0" };
		String[] horlabels = new String[] { "445", "446", "447", "448" };
		GraphView graphView = new GraphView(this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);
		
		ll.addView(graphView);
	}

	
}
