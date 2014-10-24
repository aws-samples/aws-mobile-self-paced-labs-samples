package com.amazon.example.snake;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazon.example.snake.R;
import com.amazon.example.snake.aws.AWSClientManager;
import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;

//facebook
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

//Google Play Services
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class LoginActivity extends Activity implements Session.StatusCallback,
		OnConnectionFailedListener, ConnectionCallbacks {

	private ImageButton btnLogin;
	private ImageButton btnFBLogin;
	private ImageButton btnGoogleLogin;
	private Button btnPlay;

	private enum LoginType {
		NONE, AWS, FACEBOOK, GOOGLE

	};

	private LoginType loginRequestType;
	private AmazonAuthorizationManager authManager;

	private static final String[] APP_SCOPES = { "profile" };
	private static final String LOG_TAG = "LoginActivity";

	// Google SignIn Variables
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient googleAPIClient;
	private ConnectionResult connectionResult;
	private boolean intentInProgress;
	private boolean signInClicked;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Log.d(LOG_TAG, "onCreate");

		// initialize
		AWSClientManager.init(this);
		initGoogleApliClient();

		// Initialize Login Request Type
		loginRequestType = LoginType.NONE;

		//Add Your Login with Amazon Button Here
		
		//Add Your Login With Facebook Button Here
		
		//Add Your Login with Google Button Here
		
		//Add Your UnAuthenticated Identity Button Here
				

	}
	
	private void initGoogleApliClient(){
		
		googleAPIClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(
				(GoogleApiClient.ConnectionCallbacks) this)
		.addOnConnectionFailedListener(
				(GoogleApiClient.OnConnectionFailedListener) this)
		.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
	}

	private void startGameActivity(boolean withIdentity) {
		Intent intent = new Intent(LoginActivity.this,
				SplashScreenActivity.class);
		if (withIdentity)
			intent.putExtra("withIdentity", withIdentity);
		startActivity(intent);
	}

	// Amazon Activity - Start

	private class AuthListener implements AuthorizationListener {

		@Override
		public void onSuccess(Bundle response) {
			Log.d(LOG_TAG, "Auth succeeded, getting token");
			authManager.getToken(APP_SCOPES, new TokenListener());
		}

		@Override
		public void onError(AuthError ae) {
			Log.e(LOG_TAG, "AuthError during authorization", ae);
			runOnUiThread(new Runnable() {
				public void run() {
					setResult(Activity.RESULT_CANCELED, null);
					finish();
				}
			});
		}

		@Override
		public void onCancel(Bundle cause) {
			Log.e(LOG_TAG, "User cancelled authorization");
			runOnUiThread(new Runnable() {
				public void run() {
					setResult(Activity.RESULT_CANCELED, null);
					finish();
				}
			});
		}
	}

	private class TokenListener implements AuthorizationListener {

		@Override
		public void onSuccess(Bundle response) {
			String authzToken = response
					.getString(AuthzConstants.BUNDLE_KEY.TOKEN.val);
			Log.d(LOG_TAG, "amazon token: " + authzToken);
			//Add Amazon Access Token to Cognito Sessions Provider
			
			startGameActivity(true);
		}

		@Override
		public void onError(AuthError ae) {
			Log.e(LOG_TAG, ae.getMessage(), ae);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setResult(Activity.RESULT_CANCELED, null);
					finish();
				}
			});
		}

		@Override
		public void onCancel(Bundle cause) {
			Log.e(LOG_TAG, "ProfileListener cancelled");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setResult(Activity.RESULT_CANCELED, null);
					finish();
				}
			});
		}
	}

	// Amazon Activity - END

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Since both Facebook and Google need to obtain data before
		// Activity exits and returns in onActivityResult
		// Added LoginType inspection below
		switch (loginRequestType) {
		case FACEBOOK:
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
			break;

		case GOOGLE:
			if (requestCode == RC_SIGN_IN) {
				
				if (resultCode != RESULT_OK) {
					signInClicked = false;
				}

				intentInProgress = false;

				if (!googleAPIClient.isConnecting()) {
					googleAPIClient.connect();
				}

				break;
			}
		default:
			break;

		}
	}

	// Facebook Activity - Start
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (session.isOpened()) {
			setFacebookSession(session);
			// make request to the /me API
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user object
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						Toast.makeText(LoginActivity.this,
								"Hello " + user.getName(), Toast.LENGTH_LONG)
								.show();
					}
				}
			}).executeAsync();
		}
	}

	private void setFacebookSession(Session session) {
		new RefreshCredentialsTask(session).execute();
	}

	/**
	 * A task to refresh credentials after adding facebook logins
	 */
	/*
	 * Added Google login to RefreshCredentials Overloaded constructor
	 */
	class RefreshCredentialsTask extends AsyncTask<Void, Void, Void> {

		//ProgressDialog dialog;
		Session session;
		LoginType identityType;
		Context context = null;

		public RefreshCredentialsTask(Session session) {
			this.session = session;
			this.identityType = LoginType.FACEBOOK;
		}

		public RefreshCredentialsTask(LoginType identityType, Context context) {
			this.identityType = identityType;
			this.context = context;
		}

		public RefreshCredentialsTask(LoginType identityType) {
			this.identityType = identityType;
		}

		@Override
		protected void onPreExecute() {
		
		}

		@Override
		protected Void doInBackground(Void... params) {

			
			Log.i(LOG_TAG, "google clientid: " + getString(R.string.google_client_id));

			String accessToken = null;
			switch (identityType) {
			
			case GOOGLE:
				try {
					accessToken = GoogleAuthUtil.getToken(context,
							Plus.AccountApi.getAccountName(googleAPIClient),
							"audience:server:client_id:" + getString(R.string.google_client_id));
					
				} catch (UserRecoverableAuthException e) {
					// This error is recoverable, so we could fix this
					// by displaying the intent to the user.
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}
				Log.i(LOG_TAG, "google token: " + accessToken);
				//Add Google Access Token to Cognito Sessions Provider
				
				break;
			case FACEBOOK:
				Log.i(LOG_TAG, "facebook token: " + session.getAccessToken());
				//Add Facebook Session Token to Cognito Sessions Provider
				
				break;
			default:
				break;

			}

			startGameActivity(false);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			btnFBLogin.setVisibility(View.GONE);
		}
	}

	// Facebook Activity - END

	// Google Plus Connection Methods - Start
	@Override
	protected void onStart() {
		super.onStart();
		/*
		 * In Google+ documentation the Google API Client should connect on
		 * start, however, moving this to the OnClick since we have multiple
		 * identity providers. If it is desired that Google+ connect by default;
		 * uncomment code below
		 */

		// _googleAPIClient.connect();

	}

	@Override
	protected void onStop() {
		super.onStop();

		if (googleAPIClient.isConnected()) {
			googleAPIClient.disconnect();
		}

	};

	@Override
	public void onConnected(Bundle arg0) 
	{	
		signInClicked = false;
		
		// Retrieve the oAuth 2.0 access token.
		final Context context = this.getApplicationContext();
		
		new RefreshCredentialsTask(loginRequestType, context).execute();
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult connResult) {
		Log.i(LOG_TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
				+ connResult.getErrorCode());
		if (!intentInProgress) {
			connectionResult = connResult;

			if (signInClicked) {
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		googleAPIClient.connect();

	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (connectionResult.hasResolution()) {
			try {
				intentInProgress = true;
				startIntentSenderForResult(connectionResult.getResolution()
						.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				Log.i(LOG_TAG,
						"Sign in intent could not be sent: "
								+ e.getLocalizedMessage());
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				intentInProgress = false;
				googleAPIClient.connect();
			}
		}
	}

	// Google Plus Connection Methods - End
}