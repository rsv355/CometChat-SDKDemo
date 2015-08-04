package com.inscripts.sdk.demo;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.inscripts.adapter.BuddylistAdapter;
import com.inscripts.cometchat_sdkdemo.R;
import com.inscripts.helper.Keys.SharedPreferenceKeys;
import com.inscripts.helper.SharedPreferenceHelper;
import com.inscripts.pojo.SingleUser;

public class UsersListActivity extends ActionBarActivity implements OnItemClickListener {

	private ListView usersListView;
	// private static ArrayAdapter<String> adapter;
	private static BuddylistAdapter adapter;

	/* List for the simple adapter */
	private static ArrayList<String> list;

	/* For mapping userId and name */
	private static ArrayList<SingleUser> usersList = new ArrayList<SingleUser>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_list);

		getSupportActionBar().setTitle("User list");

		usersListView = (ListView) findViewById(R.id.listViewUsers);
		list = new ArrayList<String>();

		usersListView.setOnItemClickListener(this);
		populateList();
		adapter = new BuddylistAdapter(this, usersList);
		usersListView.setAdapter(adapter);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (list.size() <= 0) {
			populateList();
		}
	}

	public static void populateList() {
		try {
			if (null != list && null != usersList && null != adapter) {
				JSONObject onlineUsers;

				if (SharedPreferenceHelper.contains(SharedPreferenceKeys.USERS_LIST)) {
					onlineUsers = new JSONObject(SharedPreferenceHelper.get(SharedPreferenceKeys.USERS_LIST));
				} else {
					onlineUsers = new JSONObject();
				}

				Iterator<String> keys = onlineUsers.keys();
				list.clear();
				usersList.clear();
				while (keys.hasNext()) {
					JSONObject user = onlineUsers.getJSONObject(keys.next().toString());
					String username = user.getString("n");
					list.add(username);
					usersList
							.add(new SingleUser(username, user.getInt("id"), user.getString("m"), user.getString("s")));
				}
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SingleUser user = usersList.get(arg2);

		Intent intent = new Intent(this, SingleChatActivity.class);
		intent.putExtra("user_id", user.getId());
		intent.putExtra("user_name", user.getName());
		startActivity(intent);
	}
}