package com.stocksapp;

import java.util.HashMap;

import Model.Trend;
import android.app.Application;

import com.facebook.android.Facebook;

public class MyApplication extends Application {

    public Facebook facebook;
    public String facebookName;
    public String facebookID;
    public Trend stock; // the currently selected stock of interest
    public HashMap<Long, Trend> trendMap = new HashMap<Long, Trend>();
    Long credits;
    
    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
       	this.facebook = facebook;
    }
    
}