package com.inscripts.sdk.demo;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat_sdkdemo.R;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class SingleChatActivity extends ActionBarActivity {

	private long friendId;
	private String friendName;
	private ListView listview;
	private EditText messageField;
	private ImageView sendButton;
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
		sendButton = (ImageView) findViewById(R.id.submit_btn);



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

		ListView lv = (ListView) findViewById(R.id.lv);
		final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.listview_row_layout);
		lv.setAdapter(mAdapter);

		final EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.editTextChatMessage);
		final View rootView = findViewById(R.id.relativeBottomArea);

		final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
		final ImageView submitButton = (ImageView) findViewById(R.id.submit_btn);

		// Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
		final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

		//Will automatically set size according to the soft keyboard size
		popup.setSizeForSoftKeyboard();

		//If the emoji popup is dismissed, change emojiButton to smiley icon
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
			}
		});

		//If the text keyboard closes, also dismiss the emoji popup
		popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

			@Override
			public void onKeyboardOpen(int keyBoardHeight) {

			}

			@Override
			public void onKeyboardClose() {
				if(popup.isShowing())
					popup.dismiss();
			}
		});

		//On emoji clicked, add it to edittext
		popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				if (emojiconEditText == null || emojicon == null) {
					return;
				}

				int start = emojiconEditText.getSelectionStart();
				int end = emojiconEditText.getSelectionEnd();
				if (start < 0) {
					emojiconEditText.append(emojicon.getEmoji());
				} else {
					emojiconEditText.getText().replace(Math.min(start, end),
							Math.max(start, end), emojicon.getEmoji(), 0,
							emojicon.getEmoji().length());
				}
			}
		});

		//On backspace clicked, emulate the KEYCODE_DEL key event
		popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(
						0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				emojiconEditText.dispatchKeyEvent(event);
			}
		});

		// To toggle between text keyboard and emoji keyboard keyboard(Popup)
		emojiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//If popup is not showing => emoji keyboard is not visible, we need to show it
				if(!popup.isShowing()){

					//If keyboard is visible, simply show the emoji popup
					if(popup.isKeyBoardOpen()){
						popup.showAtBottom();
						changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
					}

					//else, open the text keyboard first and immediately after that show the emoji popup
					else{
						emojiconEditText.setFocusableInTouchMode(true);
						emojiconEditText.requestFocus();
						popup.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
					}
				}

				//If popup is showing, simply dismiss it to show the undelying text keyboard
				else{
					popup.dismiss();
				}
			}
		});

		//On submit, add the edittext text to listview and clear the edittext
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String newText = emojiconEditText.getText().toString();
				emojiconEditText.getText().clear();
				mAdapter.add(newText);
				mAdapter.notifyDataSetChanged();

			}
		});








	}


	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
		iconToBeChanged.setImageResource(drawableResourceId);
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
