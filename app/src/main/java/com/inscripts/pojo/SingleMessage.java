package com.inscripts.pojo;

public class SingleMessage {

	private String message, username;

	public SingleMessage(String message, String username) {
		this.message = message;
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
