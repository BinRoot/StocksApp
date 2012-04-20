package Model;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.PointF;

public class Stock {
	int currentValue;
	int id;
	String name;
	int openingPrice;
	int purchasePrice;
	int parValue;
	
	int shareCount = 0;
	
	double percentChangeByLastHour;
	double percentChangeAllTime;
	
	ArrayList<PointF> points = new ArrayList<PointF>();
	
	public Stock() {
		
	}
	
	public Stock(String name, int currentValue,
			double percentChangeByLastHour,
			double percentChangeByLastDay,
			double percentChangeByLastWeek,
			double percentChangeAllTime) {
		this.name = name;
		this.currentValue = currentValue;
		this.percentChangeByLastHour = percentChangeByLastHour;
		this.percentChangeAllTime = percentChangeAllTime;
	}
	
	public void setPoints(ArrayList<PointF> points) {
		this.points = points;
	}
	
	public void resetPoints() {
		points = new ArrayList<PointF>();
	}
	
	public ArrayList<PointF> getPoints() {
		return points;
	}
	
	public void setParValue(int parValue) {
		this.parValue = parValue;
	}
	
	public int getParValue() {
		return parValue;
	}
	
	public int getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPercentChangeByLastHour() {
		return percentChangeByLastHour;
	}
	public void setPercentChangeByLastHour(double percentChangeByLastHour) {
		this.percentChangeByLastHour = percentChangeByLastHour;
	}
	public double getPercentChangeAllTime() {
		return percentChangeAllTime;
	}
	public void setPercentChangeByLastMonth(double percentChangeByLastMonth) {
		this.percentChangeAllTime = percentChangeAllTime;
	}

	public int getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(int openingPrice) {
		this.openingPrice = openingPrice;
	}

	public int getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(int purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public void setPercentChangeAllTime(double percentChangeAllTime) {
		this.percentChangeAllTime = percentChangeAllTime;
	}

	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}
	
	public int getShareCount() {
		return shareCount;
	}
	
	
}
