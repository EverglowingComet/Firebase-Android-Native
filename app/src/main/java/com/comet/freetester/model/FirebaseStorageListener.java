package com.comet.freetester.model;

import android.net.Uri;

import com.google.firebase.database.DatabaseError;

public interface FirebaseStorageListener {
    public void onSuccess(Uri uri);
    public void onFailure(DatabaseError error);
}
