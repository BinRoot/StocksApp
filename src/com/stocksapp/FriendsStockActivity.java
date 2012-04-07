package com.stocksapp;

import java.util.ArrayList;

import Model.Person;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.Facebook;

public class FriendsStockActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		ListView friendsActivityList = (ListView) findViewById(R.id.list_friends_activity);
		FriendsActivityAdapter fActivityAdapter = new FriendsActivityAdapter();
		fActivityAdapter.addActivityList("Dave bought Jeremy Li +400");
		fActivityAdapter.addActivityList("Phil sold Lady Gaga -250");
		fActivityAdapter.addActivityList("Jeremy bought Hunger Games +60");
		fActivityAdapter.addActivityList("Bob bought Hunger Games +35");
		
		friendsActivityList.setAdapter(fActivityAdapter);
		
		fActivityAdapter.notifyDataSetChanged();
		
		
		ListView friendsMyList = (ListView) findViewById(R.id.list_friends_my);
		FriendsMyAdapter fMyAdapter = new FriendsMyAdapter();
		fMyAdapter.addMyFriendList("Joe", 123);
		fMyAdapter.addMyFriendList("Bob", 73);
		fMyAdapter.addMyFriendList("Nick", 93);
		fMyAdapter.addMyFriendList("Carly", 104);
		fMyAdapter.addMyFriendList("Lewis", 168);
		fMyAdapter.notifyDataSetChanged();
		
		friendsMyList.setAdapter(fMyAdapter);
		
	}
	
	public class FriendsActivityAdapter extends BaseAdapter {

		ArrayList<String> activityList;
		
		public FriendsActivityAdapter() {
			activityList = new ArrayList<String>();
		}
		
		public void addActivityList(String s) {
			activityList.add(s);
		}
		
		@Override
		public int getCount() {
			return activityList.size();
		}

		@Override
		public String getItem(int position) {
			return activityList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if(v==null) {
				v = getLayoutInflater().inflate(R.layout.friendsactivityitem, null);
			}
			
			((TextView)v.findViewById(R.id.text_friendsactivitylist)).setText(activityList.get(position));
			
			return v;
		}
		
	}
	
	
	public class FriendsMyAdapter extends BaseAdapter {

		ArrayList<Person> myFriendsList;
		
		public FriendsMyAdapter() {
			myFriendsList = new ArrayList<Person>();
		}
		
		public void addMyFriendList(String s, int points) {
			myFriendsList.add(new Person(s, points));
		}
		
		@Override
		public int getCount() {
			return myFriendsList.size();
		}

		@Override
		public Person getItem(int position) {
			return myFriendsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if(v==null) {
				v = getLayoutInflater().inflate(R.layout.friendsmyitem, null);
			}
			
			((TextView)v.findViewById(R.id.text_friendsmylist)).setText(myFriendsList.get(position).getName());
			((TextView)v.findViewById(R.id.text_friendspoints)).setText(myFriendsList.get(position).getPoints()+"");

			
			return v;
		}

		
		
	}
	
	public void myFriendsClicked(View v) {
		(findViewById(R.id.list_friends_activity)).setVisibility(View.GONE);
		(findViewById(R.id.list_friends_my)).setVisibility(View.VISIBLE);
		(findViewById(R.id.linear_friends_textheader)).setVisibility(View.VISIBLE);
	
	
		(findViewById(R.id.button_friends_activity)).setBackgroundResource(R.drawable.tab_friendactivity_deselected);
		(findViewById(R.id.button_friends_my)).setBackgroundResource(R.drawable.tab_myfriends_selected);
		
	}
	
	public void myActivityClicked(View v) {
		(findViewById(R.id.list_friends_activity)).setVisibility(View.VISIBLE);
		(findViewById(R.id.list_friends_my)).setVisibility(View.GONE);
		(findViewById(R.id.linear_friends_textheader)).setVisibility(View.GONE);
		
		
		(findViewById(R.id.button_friends_activity)).setBackgroundResource(R.drawable.tab_friendactivity_selected);
		(findViewById(R.id.button_friends_my)).setBackgroundResource(R.drawable.tab_myfriends_deselected);
	}
	
	public void portfolioClicked(View v) {
		Intent i = new Intent(FriendsStockActivity.this, StockActivity.class);
		startActivity(i);
		FriendsStockActivity.this.finish();
	}
	
	public void discoverClicked(View v) {
		Intent i = new Intent(FriendsStockActivity.this, DiscoverActivity.class);
		startActivity(i);
		FriendsStockActivity.this.finish();
	}
	
}
