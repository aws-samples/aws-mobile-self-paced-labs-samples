package com.amazon.example.snake;

import com.amazon.example.snake.aws.CognitoSyncTask;
import com.amazon.example.snake.aws.S3UploadTask;
import com.amazon.example.snake.aws.S3UploadTask.UploadTaskFinishedListener;
import com.amazon.example.snake.helpers.Screenshotter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class GameOverScreenActivity extends Activity implements
		UploadTaskFinishedListener{

	private TextView gameOverText;
	int highLevel;
	int highScore;
	int lastLevel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over_screen);
		setTitle(R.string.activity_game_over_title);

		gameOverText = (TextView) findViewById(R.id.gameover_content);

		highLevel = getIntent().getIntExtra("highLevel", 0);
		highScore = getIntent().getIntExtra("highScore", 0);
		
		gameOverText.setText("Your High Level : " + highLevel
				+ "\n Your High Score : " + highScore
				+ "\n Your Last Record Level was : " + lastLevel);

		Button btn_new = (Button) findViewById(R.id.gameover_new_game_button);

		btn_new.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				
				Intent callIntent = new Intent(Intent.ACTION_CALL); 
				callIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				callIntent.setClass(GameOverScreenActivity.this,SnakeGameActivity.class);
				startActivity(callIntent);
			}
		});
		
		
		Button btn_resume = (Button) findViewById(R.id.gameover_resume_game_button);

		btn_resume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			CognitoSyncTask tsk = new CognitoSyncTask(GameOverScreenActivity.this);
			tsk.doSync();
				
			}
		});
		
		//Add Upload Screenshot to S3 Button Here
		
	}

	@Override
	public void onTaskFinished() {
		finish();
	}
	

}
