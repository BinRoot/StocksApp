package com.stocksapp;

import android.app.Application;

import com.facebook.android.Facebook;

public class MyApplication extends Application {

    private Facebook facebook;

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
       	this.facebook = facebook;
    }
}