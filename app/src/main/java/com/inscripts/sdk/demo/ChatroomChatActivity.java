package com.inscripts.sdk.demo;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.cometchat_sdkdemo.R;
import com.inscripts.utils.Logger;

public class ChatroomChatActivity extends ActionBarActivity {

	private String chatroomName, chatroomId;
	private EditText input;
	private ImageView sendbtn;
	private CometChatroom cometChatroom;
	private ArrayList<String> messageList;
	private ArrayAdapter<String> adapter;
	private ListView chatListView;
	private BroadcastReceiver customReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Intent intent = getIntent();

		if (intent.hasExtra("cName")) {
			chatroomName = intent.getStringExtra("cName");
		}
		if (intent.hasExtra("chatroomid")) {
			chatroomId = intent.getStringExtra("chatroomid");
		}

		getSupportActionBar().setTitle(chatroomName);

		input = (EditText) findViewById(R.id.editTextChatMessage);
		sendbtn = (ImageView) findViewById(R.id.submit_btn);


		cometChatroom = CometChatroom.getInstance(getApplicationContext());

		chatListView = (ListView) findViewById(R.id.listViewChatMessages);
		messageList = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messageList);
		chatListView.setAdapter(adapter);

		sendbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

		customReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.hasExtra("Newmessage")) {
					String message = intent.getStringExtra("from") + ": " + intent.getStringExtra("Message");
					messageList.add(message);
					adapter.notifyDataSetChanged();
					chatListView.setSelection(messageList.size() - 1);
				}
			}
		};
	}

	private void sendMessage() {
		final String message = input.getText().toString().trim();
		if (!TextUtils.isEmpty(message)) {
			input.setText("");
			/*
			 * Send message to active chatroom.
			 */
			cometChatroom.sendMessage(message, new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					messageList.add("Me: " + message);
					adapter.notifyDataSetChanged();
					chatListView.setSelection(adapter.getCount() - 1);
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("send message fail = " + response);
				}
			});
		}
	}


	@Override
	public void onStart() {
		super.onStart();
		if (customReceiver != null) {
			registerReceiver(customReceiver, new IntentFilter("Chatroom_message"));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (customReceiver != null) {
			unregisterReceiver(customReceiver);
		}
	}
}
