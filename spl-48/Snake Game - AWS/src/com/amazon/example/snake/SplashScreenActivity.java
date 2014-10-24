package com.amazon.example.snake;

import com.amazon.example.snake.aws.S3DownloadTask;
import com.amazon.example.snake.aws.S3DownloadTask.DownloadTaskFinishedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreenActivity extends Activity implements
		DownloadTaskFinishedListener {

	private ProgressBar progressBar;
	private TextView progressBarText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the splash screen
		setContentView(R.layout.activity_splash_screen);
		// Find the progress bar
		progressBar = (ProgressBar) findViewById(R.id.activity_splash_progress_bar);
		progressBarText = (TextView) findViewById(R.id.activity_splash_progress_bar_text);

		boolean withIdentity = getIntent().getBooleanExtra("withIdentity",
				false);
		if (withIdentity) {
			// Start your loading
			new S3DownloadTask(progressBar, progressBarText, this)
					.execute("Archive.zip"); // Pass in whatever you need a url
												// is just an example we don't
												// use it in this tutorial
		}
		else
			completeSplash(false);
	}

	private void completeSplash(boolean success) {
		startApp(success);
		finish(); // Don't forget to finish this Splash Activity so the user
					// can't return to it!
	}

	private void startApp(boolean success) {
		Intent intent = new Intent(SplashScreenActivity.this,
				SnakeGameActivity.class);
		if (success)
			intent.putExtra("resourceDirectory", getApplicationContext()
					.getExternalFilesDir(null) + "/downloads");
		startActivity(intent);
	}

	@Override
	public void onTaskFinished(boolean success) {
		completeSplash(success);
	}

}