aws-mobile-self-paced-labs-samples
==================================

Snake Game Android - SPL 48
============================================
This Self Paced Lab highlights the use of The Snake Game, which is simple mobile game for Android devices where the snake eats the apples and gets longer and longer as it eats them. It serves as a sample app to show how to use different AWS Mobile Services together. It uses the AWS Android SDK and number of AWS Mobile Services:
   * Amazon Cognito Identity Broker (Amazon, Facebook and Google+) to authenticate users and also unauthenticated identities
   * Amazon Cognito Sync to sync game preferences (Level of the game)
   * Amazon S3 to upload screenshot to S3 (Game over screen with high scores)
   * Amazon S3 to download game assets dynamically and updates the UI
   * Amazon Mobile Analytics to start and stop session (also custom events)