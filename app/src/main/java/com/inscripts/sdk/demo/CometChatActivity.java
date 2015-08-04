package com.inscripts.sdk.demo;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.callbacks.SubscribeCallbacks;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat_sdkdemo.R;
import com.inscripts.enums.Languages;
import com.inscripts.helper.Keys.SharedPreferenceKeys;
import com.inscripts.helper.SharedPreferenceHelper;
import com.inscripts.utils.Logger;

public class CometChatActivity extends ActionBarActivity {

	public static final ArrayList<String> logs = new ArrayList<String>();

	/* Modify the URL to point to the site you desire. */
	private static final String SITE_URL = "http://192.168.0.159/";

	/* Change this value to a valid user ID on the above site. */
	public static final String USER_ID = "6";

	private static boolean isSubscribed = false;

	private CometChat cometchat;
	public static String myId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial);

		/*
		 * Initializing the core CometChat instance. This is a singleton and
		 * hence can be called and used anywhere.
		 */

		cometchat = CometChat.getInstance(getApplicationContext());

		/*
		 * This function will set/reset the development mode so that you can
		 * view the logs for the request and response You will get
		 * 
		 * "CC_SDK_LOG:URL" logs which will specifiy the url for which a request
		 * is sent You wukk get
		 * 
		 * "CC_SDK_LOG:RESPONSE" logs which will specify the response of request
		 * sent
		 */
		 CometChat.setDevelopmentMode(false);

		final SubscribeCallbacks subCallbacks = new SubscribeCallbacks() {

			@Override
			public void onMessageReceived(JSONObject receivedMessage) {
				LogsActivity.addToLog("One-On-One onMessageReceived");
				try {
					Logger.debug("onMessageReceived: " + receivedMessage);
					/* Messagetype 3 is announcemnet */
					Intent intent = new Intent();
					intent.setAction("NEW_SINGLE_MESSAGE");

					/* Send a broadcast to SingleChatActivity */
					intent.putExtra("user_id", receivedMessage.getInt("from"));
					intent.putExtra("message", receivedMessage.getString("message"));
					sendBroadcast(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(JSONObject errorResponse) {
				LogsActivity.addToLog("One-On-One onError");
				Logger.debug("Some error: " + errorResponse);
			}

			@Override
			public void gotProfileInfo(JSONObject profileInfo) {
				LogsActivity.addToLog("One-On-One gotProfileInfo");
				Logger.debug("Profile info: " + profileInfo);
				JSONObject j = profileInfo;
				try {
					myId = j.getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				cometchat.setTranslateLanguage(Languages.Spanish, new Callbacks() {

					@Override
					public void successCallback(JSONObject response) {
						Logger.error("response success = " + response);
					}

					@Override
					public void failCallback(JSONObject response) {
						Logger.error("response fail = " + response);
					}
				});
				/*
				 * cometchat.getOnlineUsers(new Callbacks() {
				 * 
				 * @Override public void successCallback(JSONObject response) {
				 * Logger.debug("online users =" + response.toString());
				 * 
				 * }
				 * 
				 * @Override public void failCallback(JSONObject response) {
				 * 
				 * } });
				 */
				sendRandomImage();
			}

			@Override
			public void gotOnlineList(JSONObject onlineUsers) {
				try {
					LogsActivity.addToLog("One-On-One gotOnlineList");
					/* Store the list for later use. */
					SharedPreferenceHelper.save(SharedPreferenceKeys.USERS_LIST, onlineUsers.toString());
					Logger.error("Got online list " + onlineUsers);
					UsersListActivity.populateList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			

			@Override
			public void gotAnnouncement(JSONObject announcement) {

			}
		};

		cometchat.isCometChatInstalled(SITE_URL, new Callbacks() {

			@Override
			public void successCallback(JSONObject response) {
				try {
					Logger.debug("in success " + response.getString("cometchat_url"));
				} catch (Exception e) {
				}
			}

			@Override
			public void failCallback(JSONObject response) {
				Logger.debug("in fail " + response);
			}
		});

		if (CometChat.isLoggedIn()) {
			cometchat.subscribe(true, subCallbacks);
		}
	}

	public void buttonClick(View view) {
		switch (view.getId()) {
		case R.id.buttonOpenOneOnOne:
			startActivity(new Intent(this, UsersListActivity.class));
			break;
		case R.id.buttonOpenChatrooms:
			startActivity(new Intent(this, ChatroomListActivity.class));
			break;
		case R.id.buttonOpenLogs:
			startActivity(new Intent(this, LogsActivity.class));
			break;
		case R.id.buttonLogout:
			cometchat.logout(new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					SharedPreferenceHelper.removeKey(SharedPreferenceKeys.IS_LOGGEDIN);
					SharedPreferenceHelper.removeKey(SharedPreferenceKeys.USER_NAME);
					SharedPreferenceHelper.removeKey(SharedPreferenceKeys.PASSWORD);
					SharedPreferenceHelper.removeKey(SharedPreferenceKeys.LOGIN_TYPE);
					SharedPreferenceHelper.removeKey(SharedPreferenceKeys.USERS_LIST);
					SharedPreferenceHelper.removeKey(SharedPreferenceKeys.CHATROOMS_LIST);
					// SharedPreferenceHelper.removeKey(SharedPreferenceKeys.API_KEY);
					startActivity(new Intent(CometChatActivity.this, UrlScreenActivity.class));
					finish();
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("logout failed");
				}
			});

			break;
		default:
			break;
		}
	}

	/**
	 * Sends a random image from the gallery to user 5.
	 */
	@SuppressWarnings("deprecation")
	private void sendRandomImage() {
		try {
			String[] projection = new String[] { MediaColumns.DATA, };

			Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			Cursor cur = managedQuery(images, projection, "", null, "");

			final ArrayList<String> imagesPath = new ArrayList<String>();
			if (cur.moveToFirst()) {
				int dataColumn = cur.getColumnIndex(MediaColumns.DATA);
				do {
					imagesPath.add(cur.getString(dataColumn));
				} while (cur.moveToNext());
			}

			// Logger.debug(imagesPath.toString());

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(imagesPath.get(1), options);

			cometchat.sendImage(new File(imagesPath.get(1)), "1", new Callbacks() {
				// cometchat.sendImage(bitmap, "5", new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					Logger.debug("Success: " + response);
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("Fail: " + response);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendRandomVideo() {
		try {
			String[] projection = new String[] { MediaColumns.DATA, };

			Uri videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			Cursor cur = managedQuery(videos, projection, "", null, "");

			final ArrayList<String> imagesPath = new ArrayList<String>();
			if (cur.moveToFirst()) {
				int dataColumn = cur.getColumnIndex(MediaColumns.DATA);
				do {
					imagesPath.add(cur.getString(dataColumn));
				} while (cur.moveToNext());
			}

			cometchat.sendVideo(new File(imagesPath.get(2)), "110", new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					Logger.debug("Success: " + response);
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("Fail: " + response);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
