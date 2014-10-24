package com.amazon.example.snake.aws;


import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.Dataset.SyncCallback;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;


public class CognitoSyncTask{

    CognitoSyncManager cognitosync;
    CognitoCachingCredentialsProvider cognito;
    Activity callingActivity;

    public CognitoSyncTask(Activity callingActivity) {

        cognitosync = AWSClientManager.getCognitoSync();
        cognito = AWSClientManager.getCognito();
        this.callingActivity = callingActivity;
    }


    void printDataset() {
        Dataset dataset = AWSClientManager.getDataset();
        for (Record record : dataset.getAllRecords()) {
            Log.i("sync", record.toString());
        }
    }
    public void doSync() {

        ProgressDialog dialog = ProgressDialog.show(callingActivity,
                "Syncing", "Please wait");
        Dataset dataset = AWSClientManager.getDataset();
        printDataset();

        dataset.synchronize(new SimpleCallback(dialog));
    }

    private class SimpleCallback implements SyncCallback {
        private ProgressDialog dialog;

        private SimpleCallback(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onSuccess(Dataset dataset, final List<Record> newRecords) {
            printDataset();
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Log.i("Sync", String.format("%d records synced", newRecords.size()));
                    Toast.makeText(callingActivity,
                        "Sync Successful!", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onFailure(final DataStorageException dse) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Log.e("Sync", "failed: " + dse);
                    Toast.makeText(callingActivity,
                        "Sync Failed due to\n" + dse.getMessage(), Toast.LENGTH_LONG)
                        .show();
                    dse.printStackTrace();
                }
            });
        }

        @Override
        public boolean onConflict(final Dataset dataset,
                final List<SyncConflict> conflicts) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Log.i("Sync onConflict",
                        String.format("%s records in conflict", conflicts.size()));
                    List<Record> resolvedRecords = new ArrayList<Record>();
                    for (SyncConflict conflict : conflicts) {
                        Log.i("Sync onConflict",
                            String.format("remote: %s; local: %s",
                                conflict.getRemoteRecord(),
                                conflict.getLocalRecord()));
                        /* resolve by taking remote records */
                        resolvedRecords.add(conflict.resolveWithRemoteRecord());
                    }
                    dataset.resolve(resolvedRecords);
                    Toast.makeText(
                            callingActivity,
                            String.format(
                                "%s records in conflict. Resolve by taking remote records",
                                conflicts.size()),
                            Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }

        @Override
        public boolean onDatasetDeleted(Dataset arg0, String arg1) { return false; }

        @Override
        public boolean onDatasetsMerged(Dataset arg0, List<String> arg1) { return false; }
    }
}
