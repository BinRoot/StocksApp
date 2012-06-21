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

    int rankIndex[] = {0, 500, 1000, 2000, 3500, 6000, 10000};
    String rankVal[] = {"The Fool", "The Analyst", "Rank 1", "Rank 2", "Rank 3", "Rank 4", "Rank 5"};
    String finalRank = "CEO";
    
    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
       	this.facebook = facebook;
    }

    public String getRankValForNet(long net) {
        for(int i=0; i<rankIndex.length; i++) {
            if(net < rankIndex[i]) {
                 return rankVal[i];
            }
        }
        return finalRank;
    }

    public int getNextRankIndex(long net) {
        for(int i=0; i<rankIndex.length; i++) {
            if(net > rankIndex[i]) {
                 try {
                     int rankI = rankIndex[i+1];
                     return rankI;
                 }
                 catch (Exception e) {
                     return 1000000;
                 }
            }
        }

        return -1;
    }

    public int getCurRankIndex(long net) {
        for(int i=0; i<rankIndex.length; i++) {
            if(net > rankIndex[i]) {
                return rankIndex[i];
            }
        }

        return -1;
    }

}