package com.inscripts.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inscripts.cometchat_sdkdemo.R;
import com.inscripts.pojo.Chatroom;

public class ChatroomlistAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private ArrayList<Chatroom> chatroomList;

	public ChatroomlistAdapter(Context context, ArrayList<Chatroom> chatroomList) {
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.chatroomList = chatroomList;
	}

	@Override
	public int getCount() {
		return chatroomList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return chatroomList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private static class ViewHolder {
		TextView chatroomName;
		ImageView chatroomProtectedIcon;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View vi = view;
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			vi = inflator.inflate(R.layout.custom_chatroomlist_item, null);
			holder.chatroomName = (TextView) vi.findViewById(R.id.textViewChatroomName);
			holder.chatroomProtectedIcon = (ImageView) vi.findViewById(R.id.imageViewPasswordProtectedIcon);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		Chatroom chatroom = chatroomList.get(position);
		String type = chatroom.getType();
		if (type.equals("1")) {
			holder.chatroomProtectedIcon.setVisibility(View.VISIBLE);
		} else {
			holder.chatroomProtectedIcon.setVisibility(View.GONE);
		}
		holder.chatroomName.setText(chatroom.getChatroomName());
		return vi;
	}

}
