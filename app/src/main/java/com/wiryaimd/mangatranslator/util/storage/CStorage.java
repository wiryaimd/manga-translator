package com.wiryaimd.mangatranslator.util.storage;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

public class CStorage {

    private static final String TAG = "CStorage";

    private StorageReference storageRef;

    public interface Listener{
        void success(String url);
    }

    public CStorage() {
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void uploadImg(Bitmap bitmap, Listener listener){
        String fileName = "mngTranslator-" + UUID.randomUUID().toString() + ".jpg";
        final StorageReference ref = storageRef.child("mangaTranslator/" + fileName);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

        UploadTask uploadTask = ref.putBytes(stream.toByteArray());
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @NonNull
            @NotNull
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw Objects.requireNonNull(task.getException());
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().toString();
                    listener.success(url);
                }
            }
        });

    }
}
