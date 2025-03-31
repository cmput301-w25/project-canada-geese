package com.example.canada_geese.Workers;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.canada_geese.Managers.OfflineDatabase;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.Models.PendingMoodEvent;
import com.example.canada_geese.Managers.DatabaseManager;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoodSyncWorker extends Worker {
    public static final String WORK_NAME = "MoodSyncWork";
    public MoodSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        OfflineDatabase db = OfflineDatabase.getInstance(getApplicationContext());
        List<PendingMoodEvent> pending = db.pendingMoodDao().getAll();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || pending.isEmpty()) return Result.success();

        for (PendingMoodEvent e : pending) {
            try {
                ArrayList<String> uploadedUrls = new ArrayList<>();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                for (String path : e.imagePaths) {
                    File file = new File(path);
                    byte[] data = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        data = Files.readAllBytes(file.toPath());
                    }

                    StorageReference ref = storageRef.child("mood_images/" + user.getUid() + "/" + file.getName());

                    Uri downloadUrl = Tasks.await(ref.putBytes(data)
                            .continueWithTask(task -> ref.getDownloadUrl()));
                    uploadedUrls.add(downloadUrl.toString());
                }

                MoodEventModel mood = new MoodEventModel(
                        e.emotion, e.description, new Date(e.timestamp), e.emoji,
                        e.color, e.isPrivate, e.hasLocation, e.latitude, e.longitude
                );
                mood.setImageUrls(uploadedUrls);
                mood.setSocialSituation(e.socialSituation);
                mood.setUserId(user.getUid());

                DatabaseManager.getInstance().addMoodEvent(mood);
                db.pendingMoodDao().delete(e);

            } catch (Exception ex) {
                ex.printStackTrace();
                return Result.retry(); // if one fails, retry later
            }
        }

        return Result.success();
    }

    public static void scheduleSync(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(MoodSyncWorker.class)
                .build();

        workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP, // or REPLACE depending on your needs
                syncRequest
        );
    }
}
