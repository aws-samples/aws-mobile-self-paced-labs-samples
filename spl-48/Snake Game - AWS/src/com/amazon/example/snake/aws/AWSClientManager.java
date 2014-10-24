package com.amazon.example.snake.aws;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;

public class AWSClientManager {
	
	private static TransferManager manager = null;
	private static CognitoSyncManager cognitosync = null;
    private static CognitoCachingCredentialsProvider provider = null; 
    private static MobileAnalyticsManager analytics = null;
    
	public static final String AWS_ACCOUNT_ID = "";
	public static final String COGNITO_POOL_ID = "";
	public static final String COGNITO_ROLE_AUTH = "";
	public static final String COGNTIO_ROLE_UNAUTH = "";
	public static final String S3_BUCKET_NAME = "";
	public static final String COGNITO_SYNC_DATASET_NAME = "";
    

	public static void init(Context context)
	{
        provider = new CognitoCachingCredentialsProvider(context, 
                AWS_ACCOUNT_ID, COGNITO_POOL_ID, COGNTIO_ROLE_UNAUTH,
                COGNITO_ROLE_AUTH, Regions.US_EAST_1);

        //initialize the Cognito Sync Client
        
        //initialize the Other Clients
		manager = new TransferManager(provider);
		analytics = MobileAnalyticsManager.getOrCreateInstance(context, "App_ID_Here", Regions.US_EAST_1, provider);
	
	}
	
	public static Dataset getDataset()
	{
        return cognitosync.openOrCreateDataset(COGNITO_SYNC_DATASET_NAME);
	}
	
	
	public static TransferManager getManager() {
		if (manager == null) {
            throw new IllegalStateException("client not initialized yet");
        }
		return manager;
	}
	
	
	 /**
     * Sets the login so that you can use authorized identity.
     * 
     * @param providerName the name of 3rd identity provider
     * @param token openId token
     */
    public static void addLogins(String providerName, String token) {
        if (provider == null) {
            throw new IllegalStateException("client not initialized yet");
        }

        Map<String, String> logins = new HashMap<String, String>();
        logins.put(providerName, token);
        provider.setLogins(logins);
       
    }

    /**
     * Gets the singleton instance of the CognitoClient. init() must be call
     * prior to this.
     * 
     * @return an instance of CognitoClient
     */
    public static CognitoSyncManager getCognitoSync() {
        if (cognitosync == null) {
            throw new IllegalStateException("client not initialized yet");
        }
        return cognitosync;
    }
    
    public static CognitoCachingCredentialsProvider getCognito() {
        if (provider == null) {
            throw new IllegalStateException("client not initialized yet");
        }
        return provider;
    }
    
	 public static MobileAnalyticsManager getAnalytics() {
	        if (analytics == null) {
	            throw new IllegalStateException("client not initialized yet");
	        }
	        return analytics;
	 }

}
