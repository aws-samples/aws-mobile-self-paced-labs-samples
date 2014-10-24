package com.amazon.example.snake;


import com.amazon.example.snake.aws.AWSClientManager;
import com.amazon.example.snake.aws.CognitoSyncTask;

import com.amazon.example.snake.game.*;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazonaws.mobileconnectors.cognito.*;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.*;
import com.facebook.Session;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SnakeGameActivity extends Activity implements GameStateListener {
	private int startingSpeed = 500;
	private int startingLevel = 1;
	private boolean gameIsInitializing = false;
	private MobileAnalyticsManager analytics = null;

	private GameView gameView = null;
	private GameController gameController = GameController.CONTROLLER;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_snake);

		this.gameView = (GameView) this.findViewById(R.id.snake_view);

		analytics = AWSClientManager.getAnalytics();

		String directory = getIntent().getStringExtra("resourceDirectory");

		if (directory != null) {
			this.gameView.loadBitmaps(directory);
			this.gameView.loadSounds(directory);
		} else { // Load default bitmaps and sounds
			this.gameView.loadBitmaps();
			this.gameView.loadSounds();
		}

		CognitoSyncTask tsk = new CognitoSyncTask(SnakeGameActivity.this);
		tsk.doSync();

		this.gameView.setOnTouchListener(new SwipeListener(this.gameView));
		this.gameController.setGameStateListener(this);

		if (!gameIsInitializing) {
			gameController.setStartingGameLevel(startingLevel, startingSpeed);
			gameController.start();
		}

		this.gameController.getGameStateListener().onGameInitialized();
	}

	public void onResume() {
		super.onResume();

		// Notify the AmazonInsights SDK that a session resume happened in this
		// Android activity.
		// Be sure to include this in every activity's onResume.
		analytics.getSessionClient().resumeSession();

	}

	@Override
	public void onPause() {
		super.onPause();

		// Notify the AmazonInsights SDK that a session pause happened in this
		// Android activity.
		// Be sure to include this in every activity's onPause.
		analytics.getSessionClient().pauseSession();

		EventClient eventClient = analytics.getEventClient();
		eventClient.submitEvents();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.game_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.newGame).setEnabled(true);

		return true;
	}

	public void startGame() {
		int gameLevel;
		int gameSpeed;
		// cognito sync
		Dataset dataset = AWSClientManager.getDataset();
		String gameLevelString = dataset.get("gameLevel");
		String gameSpeedString = dataset.get("gameSpeed");

		Log.i("SnakeGameActivity", "Starting game with gameLevel:"
				+ gameLevelString);
		if (null != gameLevelString || null != gameSpeedString) {
			gameLevel = Integer.parseInt(gameLevelString);
			gameSpeed = Integer.parseInt(gameSpeedString);

		} else {
			gameLevel = startingLevel;
			gameSpeed = startingSpeed;
		}

		gameController.setStartingGameLevel(gameLevel, gameSpeed);
		gameController.startGame();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.newGame) {
			startGame();
		}
		
		if (item.getItemId() == R.id.logout) {
			logoutAndUnlink();
		}

		if (item.getItemId() == R.id.licenseAgreement) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://developer.amazon.com/sdk/pml.html"));
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	private void logoutAndUnlink() {
		
		//logout/unlink and wipe local dataset
		//new CognitoUnlinkTask().execute();	
	
		Session session = Session.openActiveSessionFromCache(SnakeGameActivity.this);
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
        
        AmazonAuthorizationManager mAuthManager = new AmazonAuthorizationManager(this, Bundle.EMPTY);
        if (mAuthManager != null) {
            mAuthManager.clearAuthorizationState(null);
        }
        
        AWSClientManager.getCognitoSync().wipeData();


		Toast.makeText(gameView.getContext(), "Logout Successful",
				Toast.LENGTH_SHORT).show();
		
		
	}

	@Override
	public void onGameInitialized() {
		if (this.startingSpeed > 0 && this.startingLevel > 0) {
			gameIsInitializing = true;
			Toast.makeText(gameView.getContext(), "Game initialized!",
					Toast.LENGTH_SHORT).show();
		} else {
			gameIsInitializing = false;
		}
	}

	@Override
	public void onGameStarted(GameLevel gameLevel) {
		Toast.makeText(gameView.getContext(),
				"Starting game " + gameLevel + "!", Toast.LENGTH_SHORT).show();

		EventClient eventClient = analytics.getEventClient();
		AnalyticsEvent visitEvent = eventClient.createEvent("speedSet");
		eventClient.recordEvent(visitEvent);

	}

	@Override
	public void onGameFinished(GameLevel gameLevel) {
		Toast.makeText(
				gameView.getContext(),
				"Finished game " + gameLevel + ". Score = "
						+ gameLevel.getScore() + "!", Toast.LENGTH_SHORT)
				.show();

		// implementing cognito sync
		Dataset dataset = AWSClientManager.getDataset();
		dataset.put("gameLevel", gameLevel + "");
		dataset.put("gameSpeed", startingSpeed + "");

		Intent intent = new Intent(SnakeGameActivity.this,
				GameOverScreenActivity.class);
		intent.putExtra("highLevel", gameLevel.getLevelNumber());
		intent.putExtra("highScore", gameLevel.getScore());
		startActivity(intent);
	}

	@Override
	public void onLevelStarted(GameLevel gameLevel) {
		Toast.makeText(
				gameView.getContext(),
				"Starting level " + gameLevel + ". Score = "
						+ gameLevel.getScore() + "!", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onLevelFinished(GameLevel gameLevel) {
		Toast.makeText(
				gameView.getContext(),
				"Finished level " + gameLevel + ". Score = "
						+ gameLevel.getScore() + "!", Toast.LENGTH_SHORT)
				.show();

		if (gameLevel.getLevelNumber() == 2) {
			EventClient eventClient = analytics.getEventClient();
			AnalyticsEvent conversionEvent = eventClient
					.createEvent("reachedLevel2");
			eventClient.recordEvent(conversionEvent);
		}

		// Record game level completion as a custom analytics event
		EventClient eventClient = analytics.getEventClient();
		AnalyticsEvent event = eventClient.createEvent("levelComplete")
				.withMetric("snakeLength", (double) gameLevel.getGrowLength())
				.withMetric("snakeSpeed", (double) gameLevel.getSpeed())
				.withMetric("level", (double) gameLevel.getLevelNumber());
		eventClient.recordEvent(event);

	}

}
