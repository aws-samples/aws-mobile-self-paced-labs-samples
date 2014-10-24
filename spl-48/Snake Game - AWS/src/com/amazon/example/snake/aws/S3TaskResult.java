package com.amazon.example.snake.aws;

import android.net.Uri;

public class S3TaskResult {
	Uri uri = null;
	String errorMessage = null;

	// Getters and setters
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}