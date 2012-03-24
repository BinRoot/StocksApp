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

		TextView tv = (TextView)findViewById(R.id.text_yo);
		tv.setText(firstName+", "+id);

		GraphicalView mChartView = null;
		//mChartView.repaint();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			XYMultipleSeriesDataset xyset = new XYMultipleSeriesDataset();
			XYSeries xys = new XYSeries("My Stock");
			xys.add(0, 0);
			xys.add(1, 1);
			xys.add(2, 2);
			xys.add(3, 7);
			xys.add(4, 6);
			xyset.addSeries(xys);
			
			XYMultipleSeriesRenderer xymr = new XYMultipleSeriesRenderer();
			xymr.setAxesColor(Color.BLACK);
			mChartView = ChartFactory.getLineChartView(this, xyset, xymr);
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			
		} else {
			mChartView.repaint();
		}
	}
}
