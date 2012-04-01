package Model;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.PointF;

public class Stock {
	int currentValue;
	int id;
	String name;
	int totalShares;
	Date created, updated;
	String category;
	int outstandingShares;
	double percentChangeByLastHour;
	double percentChangeAllTime;
	
	ArrayList<PointF> points = new ArrayList<PointF>();
	
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
	
	public ArrayList<PointF> getPoints() {
		return points;
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
	public int getTotalShares() {
		return totalShares;
	}
	public void setTotalShares(int totalShares) {
		this.totalShares = totalShares;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getOutstandingShares() {
		return outstandingShares;
	}
	public void setOutstandingShares(int outstandingShares) {
		this.outstandingShares = outstandingShares;
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

	
}
