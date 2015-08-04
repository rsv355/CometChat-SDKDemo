package com.inscripts.sdk.demo;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat_sdkdemo.R;

public class SingleChatActivity extends ActionBarActivity {

	private long friendId;
	private String friendName;
	private ListView listview;
	private EditText messageField;
	private Button sendButton;
	private ArrayList<String> messageList;
	private ArrayAdapter<String> adapter;
	private CometChat cometchat;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Intent intent = getIntent();
		friendId = intent.getLongExtra("user_id", 0);
		friendName = intent.getStringExtra("user_name");

		/* Get the singleton CometChat instance for use. */
		cometchat = CometChat.getInstance(getApplicationContext());
		getSupportActionBar().setTitle("Chat with " + friendName);

		messageList = new ArrayList<String>();
		listview = (ListView) findViewById(R.id.listViewChatMessages);
		adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, messageList);
		listview.setAdapter(adapter);

		messageField = (EditText) findViewById(R.id.editTextChatMessage);
		sendButton = (Button) findViewById(R.id.buttonSendMessage);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final String message = messageField.getText().toString().trim();
				if (message.length() > 0) {
					messageField.setText("");

					/* Send a message to the current user */
					cometchat.sendMessage(String.valueOf(friendId), message, new Callbacks() {

						@Override
						public void successCallback(JSONObject response) {
							messageList.add("Me: " + message);
							adapter.notifyDataSetChanged();
							listview.setSelection(adapter.getCount() - 1);
						}

						@Override
						public void failCallback(JSONObject response) {
							Toast.makeText(SingleChatActivity.this, "Error in sending message", Toast.LENGTH_SHORT)
									.show();
						}
					});
				} else {
					Toast.makeText(SingleChatActivity.this, "Blank message", Toast.LENGTH_SHORT).show();
				}
			}
		});

		/* Receiver for updating messages. */
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int senderId = intent.getIntExtra("user_id", 0);
				if (0 != senderId && senderId == friendId) {
					String message = intent.getStringExtra("message");
					adapter.add(friendName + ": " + message);
					adapter.notifyDataSetChanged();
					listview.setSelection(adapter.getCount() - 1);
				}
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(receiver, new IntentFilter("NEW_SINGLE_MESSAGE"));
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
}
