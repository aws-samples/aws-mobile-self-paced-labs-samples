Running the Snake Game App Sample
============================================
Snake Game is simple mobile game for Android devices where the snake eats the apples and gets longer and longer as it eats them. It serves as a sample app to show how to use different AWS Mobile Services together. It uses the AWS Android SDK and number of AWS Mobile Services:
   * Amazon Cognito Identity Broker (Amazon, Facebook and Google+) to authenticate users and also unauthenticated identities
   * Amazon Cognito Sync to sync game preferences (Level of the game)
   * Amazon S3 to upload screenshot to S3 (Game over screen with high scores)
   * Amazon S3 to download game assets dynamically and updates the UI to “Christmas theme” or FIFA Theme
   * Amazon DynamoDB to GET highScore
   * Amazon DynamoDB to PUT last score
   * Amazon Mobile Analytics to start and stop session (also custom events)
   * Amazon Kinesis to capture Logcat data (Work in progress)The Snake Game App uses the 

1. Import the project Snake Game - AWS into Eclipse
   * Go to File -> Import.  Import Wizard will open.
   * Select General -> Existing Projects into Workspace.  Click Next.
   * In Select root directory, browse to samples directory.  List of all samples projects will appear.
   * Select the 'Snake Game - AWS' project to import
   * Click Finish.

2. Copy the AWS SDK for Android jar into the libs directory for the project. Include the following jars under libs/debug directory (for release purposes, you can include the release jars under libs/release directory)
   * aws-android-sdk-X.X.X-core.debug.jar
   * extras/aws-android-sdk-X.X.X-cognito.debug.jar		
	+ For example:
	+ aws-android-sdk-2.1.0-kinesis.jar
	+ aws-android-sdk-2.1.0-cognito.jar		
	+ aws-android-sdk-2.1.0-mobileanalytics.jar
	+ aws-android-sdk-2.1.0-core.jar			
	+ aws-android-sdk-2.1.0-s3.jar
	+ aws-android-sdk-2.1.0-ddb-mapper.jar		
	+ aws-android-sdk-2.1.0-sns.jar
	+ aws-android-sdk-2.1.0-ddb.jar			

3. Update the path to the FacebookSDK or google-play-services_lib in project.properties. By default it points to the one included in the repo so if you just cloned the project and didn't move any of the directories this should not be necessary. (While Facebook Login is not required to run the app, the SDK is still required to build). 

4. Update your App configuration for Cognito:
   * Make sure you have an identity pool created and configured at https://console.aws.amazon.com/cognito/ and you downloaded the starter code at the last step of the wizard.
   * Open CognitoSyncClientManager.java
   * Update "AWS_ACCOUNT_ID", "IDENTITY_POOL_ID", "UNAUTH_ROLE_ARN", and "AUTH_ROLE_ARN" with the values from the starter code.
   * At this point you can run the sample if you have the support of unauthenticated identity configured in the identity pool.
     + Go to Project ->  Clean.
     + Go to Project ->  Build All.
     + Go to Run -> Run.
   * To support Facebook Login and Login with Amazon, continue with step 4 and step 5.

5. To add support for Facebook Login (Optional)
   * Follow the instructions at https://developers.facebook.com/docs/android/getting-started/ to create a Facebook app
     + For "Package Name", enter com.amazonaws.cognito.sync.demo
     + For "Class Name", enter com.amazonaws.cognito.sync.demo.MainActivity
   * Make sure your identity pool is configured to support Facebook login by entering the Facebook app ID at https://console.aws.amazon.com/cognito/ from the previous step.
   * Import the Facebook SDK into Eclipse following https://developers.facebook.com/docs/android/getting-started/
   * Link to the Facebook SDK project and configure the Facebook app ID
     + Open the strings.xml file located in res/values
     + Update "facebook_app_id" with the app ID of the app you created
     + Open project properties and under Android remove the placeholder Facebook library "path/to/facebook/sdk" and add "FacebookSDK"
   * At this point you can run the sample with Facebook Login.

6. To add support for Login with Amazon. (Optional)
   * Follow the instructions at https://login.amazon.com/android to register a new application
     + For "Label", enter Cognito sync demo
     + For "Package Name", enter com.amazonaws.cognito.sync.demo
   * Make sure your identity pool is configured to support Login with Amazon by entering the Client ID at https://console.aws.amazon.com/cognito/ from the previous step.
   * Copy and paste the API key amzn_api_key in strings.xml
   * If this isn't configured properly, the "Login with Amazon" button will be disabled in the sample app.
    
7. To add support for Google+ Signin. (Optional)
   * Follow the instructions at https://code.google.com/apis/console to create a new Project
   * Create a new Client ID under APIs & Auth > Credentials
      + Select "Service Account"
   * Make sure your identity pool is configured to support Login with Amazon by entering the Client ID at https://console.aws.amazon.com/cognito/ from the previous step.
   * Copy and paste the client id to google_client_id key in strings.xml
   * If this isn't configured properly, the "Login with Amazon" button will be disabled in the sample app.
