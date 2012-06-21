package com.stocksapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import API.TrendsAPI;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FriendsActivity extends Activity {

	final String DEBUG = "Friends";
	FriendsAdapter fa = new FriendsAdapter();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		((Button)findViewById(R.id.friends_btn_friends)).setPressed(true);
		((Button)findViewById(R.id.friends_btn_friends)).setClickable(false);
		
		String userId = "FB"+((MyApplication)this.getApplication()).facebookID;
		
		new GetFriendsTask().execute(userId);
		
		ListView lv = (ListView) findViewById(R.id.friends_list);
		lv.setAdapter(fa);
	}
	
	public class FriendsAdapter extends BaseAdapter {

		ArrayList<Friend> friendList;
		
		public FriendsAdapter() {
			friendList = new ArrayList<Friend>();
		}
		
		@Override
		public int getCount() {
			return friendList.size();
		}
		
		public void addItem(Friend f) {
			friendList.add(f);
		}

		@Override
		public Friend getItem(int pos) {
			return friendList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			View v = convertView;

			if(v==null) {
				v = getLayoutInflater().inflate(R.layout.friendsitem, null);
			}
			
			Friend f = friendList.get(pos);
			
			((TextView)v.findViewById(R.id.friendsitem_text_name)).setText(f.name);
			((TextView)v.findViewById(R.id.friendsitem_text_net)).setText(f.net+"");

            // TODO: display profile pic
            new GetPic(((ImageView)v.findViewById(R.id.friendsitem_img_pic)), f.id).execute();

            v.setTag(f);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Friend vFriend = (Friend)v.getTag();

                    //Log.d(DEBUG, "tag: "+vFriendId);
                    // TODO: go to trade screen

                    friendClicked(vFriend);
                }
            });

            v.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {

                        v.findViewById(R.id.friendsitem_rel_main).setBackgroundColor(getResources().getColor(R.color.blueselect));
                        ((TextView)v.findViewById(R.id.friendsitem_text_name)).setTextColor(getResources().getColor(R.color.blueshine));
                        ((TextView)v.findViewById(R.id.friendsitem_text_net)).setTextColor(getResources().getColor(R.color.blueshine));
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.findViewById(R.id.friendsitem_rel_main).setBackgroundColor(0xffffffff);
                        ((TextView)v.findViewById(R.id.friendsitem_text_name)).setTextColor(getResources().getColor(R.color.dblue));
                        ((TextView)v.findViewById(R.id.friendsitem_text_net)).setTextColor(getResources().getColor(R.color.dblue));
                    }

                    return false;
                }
            });


			return v;
		}
		
		@Override
		public void notifyDataSetChanged() {
			Collections.sort(friendList, new Comparator<Friend>() {
				@Override
				public int compare(Friend lhs, Friend rhs) {
					if (lhs.net > rhs.net) return -1;
					else if (lhs.net < rhs.net) return 1;
					else return 0;
				}
			});
			
			super.notifyDataSetChanged();
		}
		
		
	}

    private void friendClicked(Friend vFriend) {
         // TODO: launch new activity
        Intent i = new Intent(FriendsActivity.this, ProfileActivity.class);
        i.putExtra("id", vFriend.id+";"+vFriend.name);
        i.putExtra("net", vFriend.net);
        startActivity(i);
        this.finish();
    }

    public void portfolioClicked(View v) {
         finish();
	}
	
	public void discoverClicked(View v) {
		
	}

    public class GetPic extends AsyncTask<Void, Void, Bitmap> {

        ImageView iv = null;
        long userId;

        public GetPic(ImageView iv, String userId) {
            this.iv = iv;
            this.userId = Integer.parseInt(userId.substring(2));
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            URL img_value = null;
            try {
                img_value = new URL("http://graph.facebook.com/"+userId+"/picture?type=large");
                Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                return mIcon1;
            }
            catch (Exception e) {
                Log.d(DEBUG, "err: "+e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            iv.setImageBitmap(b);
        }
    }

	public class GetFriendsTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... userId) {
			JSONObject jo = TrendsAPI.getInstance().getFriends(userId[0]);
			return jo;
		}
		
		@Override
		public void onPostExecute(JSONObject jo) {
			ArrayList<Friend> friendSortList = new ArrayList<FriendsActivity.Friend>();
			try {
				JSONArray ja = jo.getJSONArray("data");
				for(int i=0; i<ja.length(); i++) {
					JSONObject jao = ja.getJSONObject(i);
					String userId = jao.getString("id");
					String userName = jao.getString("name");
					long netVal = jao.getLong("net");
					fa.addItem(new Friend(userName, userId, netVal));
				}
				fa.notifyDataSetChanged();
				
				
			} catch (JSONException e) {
				Log.d(DEBUG, "getfriends task err: "+e.getMessage());
			}
		
		}
	}
	
	public class Friend {
		
		public String name;
		public String id;
		public long net;
		
		public Friend(String name, String id, long net) {
			this.name = name;
			this.id = id;
			this.net = net;
		}
		
		
	}
	
}