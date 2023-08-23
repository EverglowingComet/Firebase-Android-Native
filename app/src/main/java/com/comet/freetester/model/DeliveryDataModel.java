package com.comet.freetester.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.comet.freetester.data.Delivery;
import com.comet.freetester.data.GalleryItem;
import com.comet.freetester.data.User;
import com.comet.freetester.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveryDataModel {
    public ArrayList<Delivery> deliveryList = new ArrayList<>();
    public ArrayList<User> userList = new ArrayList<>();
    public ArrayList<GalleryItem> galleryList = new ArrayList<>();

    public static DeliveryDataModel sInstance = null;

    public static DeliveryDataModel getInstance(String uid) {
        if (sInstance == null || sInstance.user == null || !sInstance.user.uid.equals(uid)) {
            sInstance = new DeliveryDataModel(uid);
        }

        return sInstance;
    }
    public static DeliveryDataModel getInstance() {
        return sInstance;
    }

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseFunctions functions = FirebaseFunctions.getInstance();

    public static void clearInstance() {
        sInstance = null;
    }

    public boolean inProgress = false;
    public boolean isConnected = false;
    public String idToken;
    public User user;
    private ValueEventListener connectivityListener;

    public DeliveryDataModel(String uid) {
        user = new User(uid);

        connectivityListener = FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isConnected = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isConnected = false;
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        idToken = task.getResult().getToken();
                    } else {
                        idToken = null;
                    }
                }
            });
        }
    }

    public ChildEventListener listenDb(String path, boolean blockAddReload, FirebaseDatabaseListener listener) {
        return database.getReference(path).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!inProgress && !blockAddReload) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!inProgress) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (!inProgress) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void cancelDbListen(ChildEventListener listener, String path) {
        database.getReference(path).removeEventListener(listener);
    }

    public void loadUser(final FirebaseDatabaseListener listener) {
        database.getReference("user").child(user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    userList = new ArrayList<>();
                    user = null;

                    if (map != null) {
                        user = User.fromMap(map);
                    }
                    userList.add(user);

                    if (user != null) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(null);
                    }
                } catch (Exception ex) {
                    listener.onFailure(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError);
            }
        });
    }

    public void saveUser(final FirebaseDatabaseListener listener) {
        database.getReference("user").child(user.uid).setValue(user.getDataMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onSuccess();
                } else {
                    listener.onFailure(databaseError);
                }
            }
        });
    }

    public void deliveryQuery(FirebaseDatabaseListener listener) {
        Map<String, Object> data = new HashMap<>();

        functions.getHttpsCallable("delivery-deliveryQuery").call(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HttpsCallableResult result = task.getResult();
                HashMap<String, Object> data1 = Utils.getDataMap(result);

                boolean success = Utils.checkSuccess(data1);
                if (!success) {
                    listener.onFailure(null);
                    return;
                }

                deliveryList = new ArrayList<>();
                HashMap<String, Object> map = Utils.getDataMapForKey(data1, "deliveries");
                if (map != null) {
                    for (String id : map.keySet()) {
                        HashMap<String, Object> item = Utils.getDataMapForKey(map, id);
                        deliveryList.add(Delivery.fromMap(item));
                    }
                }

                listener.onSuccess();
            } else {
                Exception exception = task.getException();
                if (exception != null) exception.printStackTrace();
                listener.onFailure(null);
            }

        });
    }

    public void galleryQuery(FirebaseDatabaseListener listener) {
        Map<String, Object> data = new HashMap<>();

        functions.getHttpsCallable("delivery-galleryQuery").call(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HttpsCallableResult result = task.getResult();
                HashMap<String, Object> data1 = Utils.getDataMap(result);

                boolean success = Utils.checkSuccess(data1);
                if (!success) {
                    listener.onFailure(null);
                    return;
                }

                galleryList = new ArrayList<>();
                HashMap<String, Object> map = Utils.getDataMapForKey(data1, "galleryList");
                if (map != null) {
                    for (String id : map.keySet()) {
                        HashMap<String, Object> item = Utils.getDataMapForKey(map, id);
                        galleryList.add(GalleryItem.fromMap(item));
                    }
                }

                userList = new ArrayList<>();
                map = Utils.getDataMapForKey(data1, "userList");
                if (map != null) {
                    for (String id : map.keySet()) {
                        HashMap<String, Object> item = Utils.getDataMapForKey(map, id);
                        userList.add(User.fromMap(item));
                    }
                }

                listener.onSuccess();
            } else {
                Exception exception = task.getException();
                if (exception != null) exception.printStackTrace();
                listener.onFailure(null);
            }

        });
    }

    public String generateId(String path) {
        return database.getReference(path).push().getKey();
    }

    public void uploadImageFileToStorage(final String id, final String type, final Uri uri, final FirebaseStorageListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference riversRef = storageRef.child("image/" + type + "/"+ id + ".image");

        riversRef.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                if (task.getException() != null) throw task.getException();
            }

            // Continue with the task to get the download URL
            return riversRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Uri downloadUri = task.getResult();

                if (downloadUri != null) {
                    listener.onSuccess(downloadUri);
                } else {
                    listener.onFailure(null);
                }
            } else {
                listener.onFailure(null);
            }
        });

    }

    public @Nullable Delivery getDelivery(@Nullable String id) {
        for (Delivery item : deliveryList) {
            if (id != null && id.equals(item.id)) {
                return item;
            }
        }
        return null;
    }

    public @Nullable GalleryItem getGalleryItem(@Nullable String id) {
        for (GalleryItem item : galleryList) {
            if (id != null && id.equals(item.id)) {
                return item;
            }
        }
        return null;
    }

    public @Nullable User getUser(@Nullable String uid) {
        for (User item : userList) {
            if (uid != null && uid.equals(item.uid)) {
                return item;
            }
        }
        return null;
    }

    public void submitDeliveryPhoto(Delivery delivery, Uri uri, FirebaseDatabaseListener listener) {
        Map<String, Object> data = new HashMap<>();
        data.put("deliveryId", delivery.id);
        data.put("deliveryPhotoUri", uri.toString());

        functions.getHttpsCallable("delivery-submitDeliveryPhoto").call(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HttpsCallableResult result = task.getResult();
                delivery.deliveryPhotoUri = uri.toString();
                delivery.delivered = true;
                listener.onSuccess();
            } else {
                Exception exception = task.getException();
                if (exception != null) exception.printStackTrace();
                listener.onFailure(null);
            }

        });
    }

    public void submitGallery(GalleryItem galleryItem, FirebaseDatabaseListener listener) {
        Map<String, Object> data = new HashMap<>();
        data.put("galleryItem", galleryItem.getDataMap());

        functions.getHttpsCallable("delivery-submitGallery").call(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HttpsCallableResult result = task.getResult();
                HashMap<String, Object> dict = (HashMap) result.getData();

                boolean success = (boolean) dict.get("success");
                if (!success) {
                    listener.onFailure(null);
                    return;
                }

                HashMap<String, Object> map = (HashMap<String, Object>) dict.get("update");
                if (map != null) {
                    GalleryItem update = GalleryItem.fromMap(map);

                    GalleryItem original = getGalleryItem(update.id);
                    if (original != null) {
                        galleryList.remove(original);
                    }
                    galleryList.add(update);
                }

                listener.onSuccess();
            } else {
                Exception exception = task.getException();
                if (exception != null) exception.printStackTrace();
                listener.onFailure(null);
            }

        });
    }

}
