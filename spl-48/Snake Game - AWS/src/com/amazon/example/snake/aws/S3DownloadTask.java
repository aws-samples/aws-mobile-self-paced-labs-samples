package com.amazon.example.snake.aws;

import java.io.File;

import com.amazon.example.snake.helpers.UnZipper;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class S3DownloadTask extends AsyncTask<String, Long, S3TaskResult>
		implements ProgressListener {

	// If you want to pass something back to the
	// listener add a param to this method
	public interface DownloadTaskFinishedListener {
		void onTaskFinished(boolean success);
	}

	// This is the progress bar you want to update while the task is in progress
	private final ProgressBar progressBar;
	private final TextView progressBarText;
	private final String downloadDirectory;

	// This is the listener that will be told when this task is finished
	private final DownloadTaskFinishedListener finishedListener;

	private final TransferManager manager;

	// 0 = checking existing files... 1 = downloading 2 = unpacking 3 =
	// loading...
	private int downloadTaskState = 0;

	private long total = 0;

	private long filesize = 0;

	private int percentProgress = 0;

	private boolean forceDownload = true;

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
	public S3DownloadTask(ProgressBar progressBar, TextView progressBarText,
			DownloadTaskFinishedListener finishedListener) {
		this.progressBar = progressBar;
		this.finishedListener = finishedListener;
		this.progressBarText = progressBarText;
		this.downloadDirectory = progressBar.getContext().getExternalFilesDir(
				null)
				+ "/downloads";

		manager = AWSClientManager.getManager();
	}

	@Override
	protected void onPreExecute() {
		progressBar.setMax(100);
	}

	protected S3TaskResult doInBackground(String... params) {

		Log.i("doInBackground", "Starting download task with file: "
				+ params[0].toString());
		S3TaskResult result = new S3TaskResult();

		changeState(0);

		if (isNewResourceAvailable(params[0]) || forceDownload) {

			changeState(1);

			result = downloadResources(params[0]);

			if (result.getErrorMessage() == null) {
				changeState(2);
				if (forceDownload) {
					UnZipper d = new UnZipper(downloadDirectory + "/"
							+ params[0], downloadDirectory + "/");
					d.unzip();
				}
			}

		}
		changeState(3);
		return result;
	}

	private boolean isNewResourceAvailable(String filename) {

		File file = new File(downloadDirectory, filename);

		// TODO: partial downloaded file
		if (!file.exists()) {
			return true;
		}

		try {

			ObjectMetadata metadata = manager.getAmazonS3Client()
					.getObjectMetadata(AWSClientManager.S3_BUCKET_NAME,
							filename);
			long remoteLastModified = metadata.getLastModified().getTime();

			filesize = metadata.getContentLength();

			if (file.lastModified() < remoteLastModified) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			Log.e("isNewResourceAvailable", e.getMessage());
			e.printStackTrace();
		}

		return true;

	}

	private void changeState(int state) {
		downloadTaskState = state;
		publishProgress(0L);
	}

	private S3TaskResult downloadResources(String fileToDownload) {

		S3TaskResult result = new S3TaskResult();
		Download down = null;
		File file = new File(downloadDirectory, fileToDownload);
		try {

			down = manager.download(AWSClientManager.S3_BUCKET_NAME,
					fileToDownload, file);
			down.addProgressListener(this);
			down.waitForCompletion();

		} catch (Exception e) {

			result.setErrorMessage(e.getMessage());
			Log.e("downloadResources", e.getMessage());
		}

		return result;

	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);

		switch (downloadTaskState) {
		case 0:
			progressBarText.setText("checking existing files..");
			percentProgress = 2;
			break;
		case 1:
			progressBarText.setText("downloading...");
			if (filesize != 0)
				percentProgress = 2 + (int) ((values[0] * 70) / filesize);
			break;
		case 2:
			progressBarText.setText("unpacking...");
			percentProgress = 92;
			break;
		case 3:
			progressBarText.setText("loading...");
			percentProgress += 8;
			break;
		}

		progressBar.setProgress(percentProgress); // This is ran on the UI
													// thread so

		// it is ok to update our progress
		// bar ( a UI view ) here
		Log.i("downloaded:", percentProgress + "%");
	}

	// From AsyncTask, runs on UI thread when background is finished
	protected void onPostExecute(S3TaskResult result) {
		super.onPostExecute(result);

		if (result.getErrorMessage() != null) {
			progressBarText.setText("Error:" + result.getErrorMessage());
			finishedListener.onTaskFinished(false);
		} else
			finishedListener.onTaskFinished(forceDownload);

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
