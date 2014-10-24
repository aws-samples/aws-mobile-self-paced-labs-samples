package com.amazon.example.snake.aws;

import java.io.File;
import java.io.FileInputStream;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.mobileconnectors.s3.transfermanager.*;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class S3UploadTask extends AsyncTask<String, Long, S3TaskResult>
		implements ProgressListener {

	public interface UploadTaskFinishedListener {
		void onTaskFinished(); // If you want to pass something back to the
								// listener add a param to this method
	}

	// This is the progress bar you want to update while the task is in progress
	private final ProgressDialog progressBar;

	// This is the listener that will be told when this task is finished
	private final UploadTaskFinishedListener finishedListener;

	private final TransferManager manager;

	private long total = 0;

	private long filesize = 0;
	
	private int percentProgress = 0;

	/**
	 * A Loading task that will load some resources that are necessary for the
	 * app to start
	 * 
	 * @param progressBar
	 *            - the progress bar you want to update while the task is in
	 *            progress
	 * @param finishedListener
	 *            - the listener that will be told when this task is finished
	 */
	public S3UploadTask(ProgressDialog progressBar,
			UploadTaskFinishedListener finishedListener) {
		this.progressBar = progressBar;
		this.finishedListener = finishedListener;

		manager = AWSClientManager.getManager();
	}

	@Override
	protected void onPreExecute() {
		progressBar.setCancelable(true);
		progressBar.show();
	}

	@Override
	protected S3TaskResult doInBackground(String... params) {
		S3TaskResult result = new S3TaskResult();
		Log.i("TransferTask", "Starting task with url: " + params[0].toString());
		if (createBucket(params[0])) {
			result = uploadResources(params[0]);
		}
		// Perhaps you want to return something to your post execute
		return result;
	}

	private boolean createBucket(String bucketname) {
		Bucket newBucket = manager.getAmazonS3Client().createBucket(AWSClientManager.S3_BUCKET_NAME);

		if (newBucket != null)
			return true;
		else {
			return false;
		}

	}


	private S3TaskResult uploadResources(String fileToUpload) {

		Upload up = null;
		S3TaskResult result = new S3TaskResult();
		manager.setConfiguration(new TransferManagerConfiguration());
		File file = new File(fileToUpload);
		filesize = file.length();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(filesize);
		
		try {
			FileInputStream input = new FileInputStream(file);
			up = manager.upload(AWSClientManager.S3_BUCKET_NAME, file.getName(), input, metadata);
			up.addProgressListener(this);
			up.waitForCompletion();

		} catch (Exception e) {
			result.setErrorMessage(e.getMessage());
			Log.e("uploadResources", e.getMessage());
			e.printStackTrace();
			// You can poll your transfer's status to check its progress
			if (up.isDone() == false) {
				System.out.println("Transfer: " + up.getDescription());
				System.out.println("  - State: " + up.getState());
				System.out.println("  - Progress: "
						+ up.getProgress().getBytesTransferred());
			}
		}

		return result;

	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
		if (filesize != 0)
			percentProgress = (int) ((values[0] * 100) / filesize);
		progressBar.setProgress(percentProgress); // This is ran on the UI
													// thread so
		// it is ok to update our progress
		// bar ( a UI view ) here
		Log.i("uploaded:", percentProgress + "%");
	}

	// From AsyncTask, runs on UI thread when background is finished
	@Override
	protected void onPostExecute(S3TaskResult result) {
		super.onPostExecute(result);
		
		if (result.getErrorMessage() != null) {
			progressBar.setMessage("Error:" + result.getErrorMessage());
		}
		else
		{
			progressBar.setMessage("Done");
		}
		
		progressBar.dismiss();
		finishedListener.onTaskFinished();

	}

	// From ProgressListener, publish progress to AsyncTask
	// as this is still running in background
	@Override
	public void progressChanged(ProgressEvent pe) {
		total += pe.getBytesTransferred();
		publishProgress(total);
		Log.i("bytestranferred:", total + "bytes");

	}

}
