package com.stocksapp;

import Model.Stock;
import android.app.Application;

import com.facebook.android.Facebook;

public class MyApplication extends Application {

    public Facebook facebook;
    public String facebookName;
    public String facebookID;
    public Stock stock; // the currently selected stock on interest
    int credits;
    
    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
       	this.facebook = facebook;
    }
    
}