package com.comet.freetester.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;

public interface FirebaseDatabaseListener {
    public void onSuccess();
    public void onFailure(@Nullable DatabaseError error);
}
