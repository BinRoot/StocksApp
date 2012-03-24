package com.stocksapp;

import java.util.Date;

public class Stock {
	int currentValue;
	int id;
	String name;
	int totalShares;
	Date created, updated;
	String category;
	int outstandingShares;
	double percentChangeByLastHour;
	double percentChangeByLastDay;
	double percentChangeByLastWeek;
	double percentChangeByLastMonth;
	
	public Stock(String name, int currentValue,
			double percentChangeByLastHour,
			double percentChangeByLastDay,
			double percentChangeByLastWeek,
			double percentChangeByLastMonth) {
		this.name = name;
		this.currentValue = currentValue;
		this.percentChangeByLastDay = percentChangeByLastDay;
		this.percentChangeByLastHour = percentChangeByLastHour;
		this.percentChangeByLastMonth = percentChangeByLastMonth;
		this.percentChangeByLastWeek = percentChangeByLastWeek;
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
	public double getPercentChangeByLastDay() {
		return percentChangeByLastDay;
	}
	public void setPercentChangeByLastDay(double percentChangeByLastDay) {
		this.percentChangeByLastDay = percentChangeByLastDay;
	}
	public double getPercentChangeByLastWeek() {
		return percentChangeByLastWeek;
	}
	public void setPercentChangeByLastWeek(double percentChangeByLastWeek) {
		this.percentChangeByLastWeek = percentChangeByLastWeek;
	}
	public double getPercentChangeByLastMonth() {
		return percentChangeByLastMonth;
	}
	public void setPercentChangeByLastMonth(double percentChangeByLastMonth) {
		this.percentChangeByLastMonth = percentChangeByLastMonth;
	}

	
}
