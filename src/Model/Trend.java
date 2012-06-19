package Model;

import java.util.Date;

public class Trend {
	private long id;
	private String name;
	private String desc;
	private long initial;
	private long openingDayVal;
	private long currentHourVal;
	private Date lastTransaction;
	private String shareHolderList;
	private String hourlyPointList;
	private String dailyPointList;
	private double trend;
	private String historyList;
	private String picture;
	private long buys;
	private long sells;
	private long zeroHourVal;
	private long numShares;
	boolean modified = false;
	
	private long currentVal;
	
	public long getCurrentVal() {
		return currentVal;
	}
	public void setCurrentVal(long currentVal) {
		this.currentVal = currentVal;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
		modified = true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		modified = true;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
		modified = true;
	}
	public long getInitial() {
		return initial;
	}
	public void setInitial(long initial) {
		this.initial = initial;
		modified = true;
	}
	public long getOpeningDayVal() {
		return openingDayVal;
	}
	public void setOpeningDayVal(long openingDayVal) {
		this.openingDayVal = openingDayVal;
		modified = true;
	}
	public long getCurrentHourVal() {
		return currentHourVal;
	}
	public void setCurrentHourVal(long currentHourVal) {
		this.currentVal = currentHourVal;
		this.currentHourVal = currentHourVal;
		modified = true;
	}
	public Date getLastTransaction() {
		return lastTransaction;
	}
	public void setLastTransaction(Date lastTransaction) {
		this.lastTransaction = lastTransaction;
		modified = true;
	}
	public String getShareHolderList() {
		return shareHolderList;
	}
	public void setShareHolderList(String shareHolderList) {
		this.shareHolderList = shareHolderList;
		modified = true;
	}
	public String getHourlyPointList() {
		return hourlyPointList;
	}
	public void setHourlyPointList(String hourlyPointList) {
		this.hourlyPointList = hourlyPointList;
		modified = true;
	}
	public String getDailyPointList() {
		return dailyPointList;
	}
	public void setDailyPointList(String dailyPointList) {
		this.dailyPointList = dailyPointList;
		modified = true;
	}
	public double getTrend() {
		return trend;
	}
	public void setTrend(double trend) {
		this.trend = trend;
		modified = true;
	}
	public String getHistoryList() {
		return historyList;
	}
	public void setHistoryList(String historyList) {
		this.historyList = historyList;
		modified = true;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
		modified = true;
	}
	public long getBuys() {
		return buys;
	}
	public void setBuys(long buys) {
		this.buys = buys;
		modified = true;
	}
	public long getSells() {
		return sells;
	}
	public void setSells(long sells) {
		this.sells = sells;
		modified = true;
	}
	public long getZeroHourVal() {
		return zeroHourVal;
	}
	public void setZeroHourVal(long zeroHourVal) {
		this.zeroHourVal = zeroHourVal;
		modified = true;
	}
	public long getNumShares() {
		return numShares;
	}
	public void setNumShares(long numShares) {
		this.numShares = numShares;
		modified = true;
	}
	public boolean isModified() {
		boolean tmp = modified;
		modified = false;
		return tmp;	
	}
	public void setModified(boolean modified) {
		this.modified = modified;
		modified = true;
	}
	
	public double getDayPercent() {
		return 100*(currentHourVal - zeroHourVal+0.0)/(zeroHourVal+0.0);
	}
	
	
}
