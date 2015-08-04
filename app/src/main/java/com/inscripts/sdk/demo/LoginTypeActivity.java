package com.inscripts.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.inscripts.cometchat_sdkdemo.R;
import com.inscripts.helper.Keys.SharedPreferenceKeys;
import com.inscripts.helper.SharedPreferenceHelper;

public class LoginTypeActivity extends ActionBarActivity {

	private Button usernameBtn;/* ,useridBtn, guestBtn; */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_type);
		/*
		 * useridBtn = (Button) findViewById(R.id.buttonLoginWithUserid);
		 * guestBtn = (Button) findViewById(R.id.buttonLoginWithGuest);
		 */
		usernameBtn = (Button) findViewById(R.id.buttonLoginWithUsername);

		usernameBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginTypeActivity.this, LoginActivity.class);
				SharedPreferenceHelper.save(SharedPreferenceKeys.LOGIN_TYPE, "2");
				startActivity(intent);
			}
		});
		/*
		 * useridBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { Intent intent = new
		 * Intent(LoginTypeActivity.this, LoginActivity.class);
		 * SharedPreferenceHelper.save(SharedPreferenceKeys.LOGIN_TYPE, "1");
		 * startActivity(intent); } });
		 * 
		 * 
		 * 
		 * guestBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(LoginTypeActivity.this, LoginActivity.class);
		 * SharedPreferenceHelper.save(SharedPreferenceKeys.LOGIN_TYPE, "3");
		 * startActivity(intent); } });
		 */
	}

}
